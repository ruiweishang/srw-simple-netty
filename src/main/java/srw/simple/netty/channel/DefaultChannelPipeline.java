package srw.simple.netty.channel;

import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;
import srw.simple.netty.channel.handler.*;
import srw.simple.netty.utils.ObjectUtil;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * 对应Netty的类：io.netty.channel.DefaultChannelPipeline
 *
 * @author shangruiwei
 * @date 2023/3/26 17:26
 */
public class DefaultChannelPipeline implements ChannelPipeline {

    // pipeline维护的ChannelHandler链表的头节点
    final AbstractChannelHandlerContext head;
    // pipeline维护的ChannelHandler链表的尾节点
    final AbstractChannelHandlerContext tail;

    private final Channel channel;
    private boolean firstRegistration = true;

    protected DefaultChannelPipeline(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, "channel");

        tail = new TailContext();
        head = new HeadContext();

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler... handlers) {
        return null;
    }

    @Override
    public Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelRegistered() {
        // 先用简单方式
        ((HeadContext) head).channelRegistered(head);
        // channel已注册的事件是Inbound，从head开始执行
        AbstractChannelHandlerContext next = head.next;
        if (next instanceof DefaultChannelHandlerContext) {
            ChannelHandler channelHandler = ((DefaultChannelHandlerContext) next).handler;
            if (channelHandler instanceof ChannelInboundHandler) {
                try {
                    ((ChannelInboundHandler) channelHandler).channelRegistered(next);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ((TailContext) tail).channelRegistered(tail);
        return this;
    }

    @Override
    public ChannelInboundInvoker fireChannelActive() {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireUserEventTriggered(Object event) {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelRead(Object msg) {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelReadComplete() {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelWritabilityChanged() {
        return null;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise promise) {
        //return tail.bind(socketAddress, promise);
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelOutboundInvoker read() {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelOutboundInvoker flush() {
        return null;
    }


    final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {

//        TailContext(DefaultChannelPipeline pipeline) {
//            super(pipeline, null, "TAIL_NAME", TailContext.class);
//        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) {

        }

//        @Override
//        public ChannelHandler handler() {
//            return this;
//        }
//
//        @Override
//        public void channelRegistered(ChannelHandlerContext ctx) {
//        }
//
//        @Override
//        public void channelUnregistered(ChannelHandlerContext ctx) {
//        }
//
//        @Override
//        public void channelActive(ChannelHandlerContext ctx) {
//            onUnhandledInboundChannelActive();
//        }
//
//        @Override
//        public void channelInactive(ChannelHandlerContext ctx) {
//            onUnhandledInboundChannelInactive();
//        }
//
//        @Override
//        public void channelWritabilityChanged(ChannelHandlerContext ctx) {
//            onUnhandledChannelWritabilityChanged();
//        }
//
//        @Override
//        public void handlerAdded(ChannelHandlerContext ctx) {
//        }
//
//        @Override
//        public void handlerRemoved(ChannelHandlerContext ctx) {
//        }
//
//        @Override
//        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
//            onUnhandledInboundUserEventTriggered(evt);
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//            onUnhandledInboundException(cause);
//        }
//
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            onUnhandledInboundMessage(ctx, msg);
//        }
//
//        @Override
//        public void channelReadComplete(ChannelHandlerContext ctx) {
//            onUnhandledInboundChannelReadComplete();
//        }
    }

    final class HeadContext extends AbstractChannelHandlerContext implements ChannelOutboundHandler, ChannelInboundHandler {

        private final Channel.Unsafe unsafe = null;

//        HeadContext(DefaultChannelPipeline pipeline) {
//            super(pipeline, null, "HEAD_NAME", HeadContext.class);
////            unsafe = pipeline.channel().unsafe();
//            unsafe = null;
//        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) {
            // 调用初始化添加更多的handler
        }
//
//        @Override
//        public ChannelHandler handler() {
//            return this;
//        }
//
//        @Override
//        public void handlerAdded(ChannelHandlerContext ctx) {
//            // NOOP
//        }
//
//        @Override
//        public void handlerRemoved(ChannelHandlerContext ctx) {
//            // NOOP
//        }
//
//        @Override
//        public void bind(
//                ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
//            unsafe.bind(localAddress, promise);
//        }
//
//        @Override
//        public void connect(
//                ChannelHandlerContext ctx,
//                SocketAddress remoteAddress, SocketAddress localAddress,
//                ChannelPromise promise) {
//            unsafe.connect(remoteAddress, localAddress, promise);
//        }
//
//        @Override
//        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
//            unsafe.disconnect(promise);
//        }
//
//        @Override
//        public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
//            unsafe.close(promise);
//        }
//
//        @Override
//        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
//            unsafe.deregister(promise);
//        }
//
//        @Override
//        public void read(ChannelHandlerContext ctx) {
//            unsafe.beginRead();
//        }
//
//        @Override
//        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
//            unsafe.write(msg, promise);
//        }
//
//        @Override
//        public void flush(ChannelHandlerContext ctx) {
//            unsafe.flush();
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//            ctx.fireExceptionCaught(cause);
//        }
//
//        @Override
//        public void channelRegistered(ChannelHandlerContext ctx) {
//            invokeHandlerAddedIfNeeded();
//            ctx.fireChannelRegistered();
//        }
//
//        @Override
//        public void channelUnregistered(ChannelHandlerContext ctx) {
//            ctx.fireChannelUnregistered();
//
//            // Remove all handlers sequentially if channel is closed and unregistered.
//            if (!channel.isOpen()) {
//                destroy();
//            }
//        }
//
//        @Override
//        public void channelActive(ChannelHandlerContext ctx) {
//            ctx.fireChannelActive();
//
//            readIfIsAutoRead();
//        }
//
//        @Override
//        public void channelInactive(ChannelHandlerContext ctx) {
//            ctx.fireChannelInactive();
//        }
//
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            ctx.fireChannelRead(msg);
//        }
//
//        @Override
//        public void channelReadComplete(ChannelHandlerContext ctx) {
//            ctx.fireChannelReadComplete();
//
//            readIfIsAutoRead();
//        }
//
//        private void readIfIsAutoRead() {
//            if (channel.config().isAutoRead()) {
//                channel.read();
//            }
//        }
//
//        @Override
//        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
//            ctx.fireUserEventTriggered(evt);
//        }
//
//        @Override
//        public void channelWritabilityChanged(ChannelHandlerContext ctx) {
//            ctx.fireChannelWritabilityChanged();
//        }
    }
}
