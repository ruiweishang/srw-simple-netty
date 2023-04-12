package srw.simple.netty.business;

import srw.simple.netty.channel.eventloop.DefaultChannelPromise;
import srw.simple.netty.channel.handler.ChannelHandlerContext;
import srw.simple.netty.channel.handler.ChannelInboundHandler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author shangruiwei
 * @date 2023/4/12 08:36
 */
public class ClientChannelHandler implements ChannelInboundHandler {


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap("hello world".getBytes(StandardCharsets.UTF_8));
        ctx.write(byteBuffer, new DefaultChannelPromise(ctx.channel(), ctx.channel().eventLoop()));
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
}
