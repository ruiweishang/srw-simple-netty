package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.ChannelInboundInvoker;
import srw.simple.netty.channel.ChannelOutboundInvoker;
import srw.simple.netty.channel.ChannelPipeline;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;
import srw.simple.netty.utils.ObjectUtil;

import java.net.SocketAddress;

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

    public DefaultChannelPipeline(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, "channel");

        tail = new TailContext(this);
        head = new HeadContext(this);

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler... handlers) {
        return null;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public ChannelInboundInvoker fireChannelRegistered() {
        AbstractChannelHandlerContext.invokeChannelRegistered(head);
        return this;
    }

    @Override
    public ChannelInboundInvoker fireChannelActive() {
        AbstractChannelHandlerContext.invokeChannelActive(head);
        return this;
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
        return tail.bind(socketAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return tail.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return tail.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelOutboundInvoker read() {
        tail.read();
        return this;
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

        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, "TAIL_NAME", TailContext.class);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public int executionMasks() {
            return 0;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) {

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

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
        public ChannelHandler handler() {
            return null;
        }
    }

    final class HeadContext extends AbstractChannelHandlerContext implements ChannelOutboundHandler, ChannelInboundHandler {

        private final Channel.Unsafe unsafe;

        HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, "HEAD_NAME", HeadContext.class);
            unsafe = pipeline.channel().unsafe();
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public int executionMasks() {
            return 0;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) {
            // 调用初始化添加更多的handler
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelActive();

            // 注册read事件
            channel.read();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

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
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelHandler handler() {
            return null;
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            unsafe.bind(localAddress, promise);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            unsafe.connect(remoteAddress, localAddress, promise);
        }

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            unsafe.beginRead();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            unsafe.write(msg, promise);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            unsafe.flush();
        }
    }
}
