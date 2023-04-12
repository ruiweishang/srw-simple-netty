package srw.simple.netty.business;

import srw.simple.netty.channel.eventloop.ChannelPromise;
import srw.simple.netty.channel.handler.ChannelHandler;
import srw.simple.netty.channel.handler.ChannelHandlerContext;
import srw.simple.netty.channel.handler.ChannelInboundHandler;
import srw.simple.netty.channel.handler.ChannelOutboundHandler;
import srw.simple.netty.utils.LogUtil;

import java.net.SocketAddress;

/**
 * @author shangruiwei
 * @date 2023/3/26 14:50
 */
public class TestLogChannelHandler implements ChannelInboundHandler, ChannelOutboundHandler {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.log("channelRegistered");
        // 继续执行下一个handler
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.log("channelActive");
        // 继续执行下一个handler
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.log("channelRead");
        // 继续执行下一个handler
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.log("channelReadComplete");
        // 继续执行下一个handler
        ctx.fireChannelReadComplete();
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        this.log("bind");
        // 继续执行下一个handler
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        this.log("connect");
        // 继续执行下一个handler
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        this.log("read");
        // 继续执行下一个handler
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.log("write");
        // 继续执行下一个handler
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.log("flush");
        // 继续执行下一个handler
        ctx.flush();
    }

    private void log(String content) {
        LogUtil.log(this.getClass(), String.format("thread:%s %s", Thread.currentThread().getName(), content));
    }
}
