package srw.simple.netty.channel.handler;

/**
 * @author shangruiwei
 * @date 2023/3/26 19:11
 */
public interface ChannelInboundHandler extends ChannelHandler {

    void channelRegistered(ChannelHandlerContext ctx) throws Exception;

    void channelActive(ChannelHandlerContext ctx) throws Exception;

    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;
}
