package srw.simple.netty.channel;

import srw.simple.netty.channel.eventloop.*;
import srw.simple.netty.channel.handler.DefaultChannelPipeline;
import srw.simple.netty.utils.LogUtil;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.TimeUnit;

/**
 * 对应Netty类：AbstractNioMessageChannel,AbstractNioChannel,AbstractChannel
 * 简单实现，层级没有分很清楚，对应多个类
 *
 * @author shangruiwei
 * @date 2023/3/26 17:24
 */
public abstract class AbstractChannel implements Channel {

    private final Channel parent;
    /**
     * Unsafe从名字就能看出，是不对外暴漏，用户不需要不应该使用的。
     * 源码中的注释是：Unsafe operations that should never be called from user-code. These methods
     * are only provided to implement the actual transport, and must be invoked from an I/O thread except for the
     * 个人理解是：Netty试图将底层通信相关的全部封装，不管使用TCP，还是UDP，不需要关注底层如何通信，只需要使用对应的Channel类
     * 所以设计了Unsafe，并且在AbstractChannel中只定义了unsafe，具体的Unsafe，要具体的子类来实现
     */
    final Unsafe unsafe;
    private final DefaultChannelPipeline pipeline;

    private volatile SocketAddress localAddress;
    private volatile SocketAddress remoteAddress;
    private volatile EventLoop eventLoop;

    private final DefaultChannelPromise closeFuture;

    // Java的Nio类
    private final SelectableChannel ch;
    protected final int readInterestOp;
    volatile SelectionKey selectionKey;

    protected AbstractChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        this.parent = parent;
        // 让具体的实现子类，创建和实现Unsafe
        unsafe = newUnsafe();
        pipeline = new DefaultChannelPipeline(this);

        this.ch = ch;
        this.readInterestOp = readInterestOp;

        try {
            ch.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        closeFuture = new DefaultChannelPromise(this, null);
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    @Override
    public EventLoop eventLoop() {
        return eventLoop;
    }

    @Override
    public Channel read() {
        pipeline.read();
        return this;
    }

    protected SelectableChannel javaChannel() {
        return ch;
    }

    /**
     * 创建Unsafe，交给子类实现
     *
     * @return
     */
    protected abstract Unsafe newUnsafe();

    protected abstract void doWrite(ByteBuffer in) throws Exception;

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return pipeline.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return pipeline.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return pipeline.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelFuture closeFuture() {
        return closeFuture;
    }

    protected void doBeginRead() throws Exception {
        // Channel.read() or ChannelHandlerContext.read() was called
        final SelectionKey selectionKey = this.selectionKey;
        if (!selectionKey.isValid()) {
            return;
        }

        final int interestOps = selectionKey.interestOps();
        if ((interestOps & readInterestOp) == 0) {
            selectionKey.interestOps(interestOps | readInterestOp);
        }
    }

    public abstract class AbstractUnsafe implements Unsafe {

        private volatile ByteBuffer outboundBuffer = ByteBuffer.allocate(1000);

        @Override
        public final void register(EventLoop eventLoop, final ChannelPromise promise) {
            // 简化了代码实现，不做是否已经注册的校验了，直接当作main线程调用，所以直接使用eventLoop调用
            AbstractChannel.this.eventLoop = eventLoop;

            // main线程调用register，所以将register作为任务添加到EventLoop中，并直接返回不阻塞main线程
            eventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        }

        @Override
        public void bind(SocketAddress localAddress, ChannelPromise promise) {
            try {
                ((ServerSocketChannel) ch).bind(localAddress, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // active的条件是：ServerSocketChannel isOpen and ServerSocketChannel.socket().isBound
            pipeline.fireChannelActive();
        }

        @Override
        public final void connect(
                final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
            if (localAddress != null) {
                try {
                    ((SocketChannel) ch).bind(localAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                ((SocketChannel) ch).connect(remoteAddress);
                // 向selector注册Connect事件，使用NioEventLoop处理，NioEventLoop会调用unsafe.finishConnect，会执行active事件
                selectionKey.interestOps(SelectionKey.OP_CONNECT);

                // 简化处理，设置connect成功
                promise.trySuccess();

//                // Schedule connect timeout.
//                int connectTimeoutMillis = config().getConnectTimeoutMillis();
//                if (connectTimeoutMillis > 0) {
//                    connectTimeoutFuture = eventLoop().schedule(new Runnable() {
//                        @Override
//                        public void run() {
//                            ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
//                            if (connectPromise != null && !connectPromise.isDone()
//                                    && connectPromise.tryFailure(new ConnectTimeoutException(
//                                    "connection timed out: " + remoteAddress))) {
//                                close(voidPromise());
//                            }
//                        }
//                    }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
//                }
//
//                promise.addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(ChannelFuture future) throws Exception {
//                        if (future.isCancelled()) {
//                            if (connectTimeoutFuture != null) {
//                                connectTimeoutFuture.cancel(false);
//                            }
//                            connectPromise = null;
//                            close(voidPromise());
//                        }
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void beginRead() {
            try {
                doBeginRead();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public final void write(Object msg, ChannelPromise promise) {
            outboundBuffer = ((ByteBuffer) msg);
        }

        @Override
        public final void flush() {
            try {
                doWrite(outboundBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void finishConnect() {
            try {
                ((SocketChannel) ch).finishConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            pipeline().fireChannelActive();
        }

        public abstract void read();

        public void forceFlush() {
            flush();
        }

        private void register0(ChannelPromise promise) {
            try {
                // 使用Java的Nio类，并注册到selector中，并把ServerSocketChannel对象attachment
                selectionKey = ch.register(((NioEventLoop) eventLoop).selector(), 0, AbstractChannel.this);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }

            // TODO 这里执行ChannelHandler的handlerAdded是可以对pipeline添加ChannelHandler，比如：ChannelInitializer之所以能添加多个就是此机制
            // pipeline.invokeHandlerAddedIfNeeded();

            LogUtil.log(this.getClass(), "register到selector成功");
            // 设置注册成功，设置后，main线程就能获取到注册成功的结果了
            promise.trySuccess();

            // 执行注册的pipeline
            pipeline.fireChannelRegistered();

            // 这里也省略了isActive后，pipeline.fireChannelActive()的实现，看NioServerSocketChannel的isActive方法，需要Java的
            // channel是open，并且socket是绑定的，所以此处正常不会active
            // NioServerSocketChannel的isActive方法内容：isOpen() && javaChannel().socket().isBound();
        }
    }


}
