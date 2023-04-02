package srw.simple.netty.channel.handler;

/**
 * 对应的Netty类：io.netty.channel.ChannelHandler,io.netty.channel.AbstractChannelHandlerContext,io.netty.channel.ChannelHandlerContext
 * 简单实现，去掉了ChannelHandlerContext的概念，直接在ChannelHandler维护一个双向链表
 *
 * @author shangruiwei
 * @date 2023/3/26 11:37
 */
public interface ChannelHandler {

    /**
     * Gets called after the {@link ChannelHandler} was added to the actual context and it's ready to handle events.
     */
    void handlerAdded(ChannelHandlerContext ctx) throws Exception;
}
