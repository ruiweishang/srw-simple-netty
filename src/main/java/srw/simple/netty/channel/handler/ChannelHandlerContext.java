package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.ChannelInboundInvoker;
import srw.simple.netty.channel.ChannelOutboundInvoker;
import srw.simple.netty.concurrent.executor.EventExecutor;

/**
 * 对应的Netty类：io.netty.channel.ChannelHandlerContext
 * 此类维护了ChannelHandler的链表关系，提供获取上一个或者下一个ChannelHandler，又继承了ChannelInboundInvoker, ChannelOutboundInvoker
 * 提供了执行上一个或者下一个的合适的ChannelHandler，比如：Inbound事件，就只执行Inbound的ChannelHandler
 *
 * @author shangruiwei
 * @date 2023/3/26 11:39
 */
public interface ChannelHandlerContext extends ChannelInboundInvoker, ChannelOutboundInvoker {

    /**
     * 返回context封装的ChannelHandler
     *
     * @return
     */
    ChannelHandler handler();

    /**
     * Return the {@link Channel} which is bound to the {@link ChannelHandlerContext}.
     */
    Channel channel();

    /**
     * Returns the {@link EventExecutor} which is used to execute an arbitrary task.
     */
    EventExecutor executor();
}
