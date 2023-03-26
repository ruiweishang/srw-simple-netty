package srw.simple.netty.channel;

import srw.simple.netty.channel.eventloop.*;
import srw.simple.netty.utils.ObjectUtil;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * 对应Netty类：AbstractNioMessageChannel,AbstractNioChannel,AbstractChannel
 * 简单实现，层级没有分很清楚，对应多个类
 *
 * @author shangruiwei
 * @date 2023/3/26 17:24
 */
public abstract class AbstractChannel implements Channel {

    private final Channel parent;
    final Unsafe unsafe;
    private final DefaultChannelPipeline pipeline;

    private volatile SocketAddress localAddress;
    private volatile SocketAddress remoteAddress;
    private volatile EventLoop eventLoop;

//    private final DefaultChannelPromise closeFuture = new DefaultChannelPromise();

    // Java的Nio类
    private final SelectableChannel ch;
    protected final int readInterestOp;
    volatile SelectionKey selectionKey;

    protected AbstractChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        this.parent = parent;
        unsafe = newUnsafe();
        pipeline = new DefaultChannelPipeline(this);

        this.ch = ch;
        this.readInterestOp = readInterestOp;

        try {
            ch.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    /**
     * 创建Unsafe，交给子类实现
     *
     * @return
     */
    protected abstract Unsafe newUnsafe();

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return pipeline.bind(localAddress, promise);
    }

    protected abstract class AbstractUnsafe implements Unsafe {

        @Override
        public final void register(EventLoop eventLoop, final ChannelPromise promise) {
            AbstractChannel.this.eventLoop = eventLoop;

            // main线程调用register，所以将register作为任务添加到EventLoop中，并直接返回不阻塞main线程
            eventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        }

        private void register0(ChannelPromise promise) {
            try {
                // 使用Java的Nio类，并注册到selector中，并把ServerSocketChannel对象attachment
                selectionKey = ch.register(((NioEventLoop) eventLoop).selector(), 0, this);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
            // 设置注册成功，设置后，main线程就能获取到注册成功的结果了
            promise.trySuccess();

            // 执行注册的pipeline
            pipeline.fireChannelRegistered();
        }
    }


}
