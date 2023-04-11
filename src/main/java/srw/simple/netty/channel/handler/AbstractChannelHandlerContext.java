package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;
import srw.simple.netty.concurrent.executor.EventExecutor;
import srw.simple.netty.utils.ObjectUtil;

import java.net.SocketAddress;

import static srw.simple.netty.channel.handler.ChannelHandlerMask.*;

/**
 * @author shangruiwei
 * @date 2023/3/26 18:45
 */
public abstract class AbstractChannelHandlerContext implements ChannelHandlerContext {

    public volatile AbstractChannelHandlerContext next;
    public volatile AbstractChannelHandlerContext prev;


    private final int executionMask;

    private final DefaultChannelPipeline pipeline;
    final EventExecutor executor;
    private final String name;

    protected AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor,
                                            String name, Class<? extends ChannelHandler> handlerClass) {
        this.pipeline = pipeline;
        this.executor = executor;
        this.name = name;
        this.executionMask = mask(handlerClass);
    }

    @Override
    public Channel channel() {
        return pipeline.channel();
    }

    @Override
    public EventExecutor executor() {
        if (executor == null) {
            return channel().eventLoop();
        } else {
            return executor;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        invokeChannelRegistered(findContextInbound(MASK_CHANNEL_REGISTERED));
        return this;
    }

    @Override
    public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(localAddress, "localAddress");

        final AbstractChannelHandlerContext next = findContextOutbound(MASK_BIND);
        next.invokeBind(localAddress, promise);
//        EventExecutor executor = next.executor();
//        if (executor.inEventLoop()) {
//            next.invokeBind(localAddress, promise);
//        } else {
//            safeExecute(executor, new Runnable() {
//                @Override
//                public void run() {
//                    next.invokeBind(localAddress, promise);
//                }
//            }, promise, null, false);
//        }
        return promise;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return connect(remoteAddress, null, promise);
    }

    @Override
    public ChannelFuture connect(
            final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");

        final AbstractChannelHandlerContext next = findContextOutbound(MASK_CONNECT);
        next.invokeConnect(remoteAddress, localAddress, promise);

        return promise;
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        invokeChannelActive(findContextInbound(MASK_CHANNEL_ACTIVE));
        return this;
    }

    private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        try {
            ((ChannelOutboundHandler) handler()).connect(this, remoteAddress, localAddress, promise);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChannelHandlerContext read() {
        final AbstractChannelHandlerContext next = findContextOutbound(MASK_READ);
        next.invokeRead();

        return this;
    }

    private void invokeRead() {
        try {
            ((ChannelOutboundHandler) handler()).read(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChannelFuture write(final Object msg, final ChannelPromise promise) {
        write(msg, false, promise);

        return promise;
    }

    @Override
    public ChannelHandlerContext flush() {
        final AbstractChannelHandlerContext next = findContextOutbound(MASK_FLUSH);
        EventExecutor executor = next.executor();

        invokeFlush0();

        return this;
    }

    private void write(Object msg, boolean flush, ChannelPromise promise) {
        final AbstractChannelHandlerContext next = findContextOutbound(flush ?
                (MASK_WRITE | MASK_FLUSH) : MASK_WRITE);
        final Object m = msg;//pipeline.touch(msg, next);
        EventExecutor executor = next.executor();

        if (flush) {
            next.invokeWriteAndFlush(m, promise);
        } else {
            next.invokeWrite(m, promise);
        }
    }

    void invokeWrite(Object msg, ChannelPromise promise) {
        invokeWrite0(msg, promise);
    }

    void invokeWriteAndFlush(Object msg, ChannelPromise promise) {
        invokeWrite0(msg, promise);
        invokeFlush0();
    }

    private void invokeWrite0(Object msg, ChannelPromise promise) {
        try {
            ((ChannelOutboundHandler) handler()).write(this, msg, promise);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invokeFlush0() {
        try {
            ((ChannelOutboundHandler) handler()).flush(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRegistered();
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelRegistered();
                }
            });
        }
    }

    static void invokeChannelActive(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelActive();
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelActive();
                }
            });
        }
    }

    static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRead(msg);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelRead(msg);
                }
            });
        }
    }

    private void invokeChannelRead(Object msg) {
        try {
            ((ChannelInboundHandler) handler()).channelRead(this, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void invokeChannelReadComplete(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelReadComplete();
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelReadComplete();
                }
            });
        }
    }

    private void invokeChannelReadComplete() {
        try {
            ((ChannelInboundHandler) handler()).channelReadComplete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invokeChannelRegistered() {
        try {
            ((ChannelInboundHandler) handler()).channelRegistered(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invokeChannelActive() {
        try {
            ((ChannelInboundHandler) handler()).channelActive(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AbstractChannelHandlerContext findContextInbound(int mask) {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while (skipContext(ctx, mask, MASK_ONLY_INBOUND));
        return ctx;
    }

    private static boolean skipContext(
            AbstractChannelHandlerContext ctx, int mask, int onlyMask) {
        return (ctx.executionMask & (onlyMask | mask)) == 0 ||
                ((ctx.executionMask & mask) == 0);
    }

    private AbstractChannelHandlerContext findContextOutbound(int mask) {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while (skipContext(ctx, mask, MASK_ONLY_OUTBOUND));
        return ctx;
    }

    private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
        try {
            ((ChannelOutboundHandler) handler()).bind(this, localAddress, promise);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
