package srw.simple.netty.business;

import srw.simple.netty.channel.eventloop.DefaultChannelPromise;
import srw.simple.netty.channel.handler.ChannelHandlerContext;
import srw.simple.netty.channel.handler.ChannelInboundHandler;
import srw.simple.netty.utils.LogUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author shangruiwei
 * @date 2023/4/12 08:44
 */
public class ServerChannelHandler implements ChannelInboundHandler {
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            ByteBuffer readBuffer = (ByteBuffer) msg;
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            LogUtil.log(this.getClass(), String.format("server 收到的信息：%s", new String(bytes, StandardCharsets.UTF_8)));
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
}
