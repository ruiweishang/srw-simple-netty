package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.ChannelInboundInvoker;
import srw.simple.netty.channel.ChannelOutboundInvoker;

/**
 * 对应的Netty类：io.netty.channel.ChannelHandlerContext
 * 此类维护了ChannelHandler的链表关系，提供获取上一个或者下一个ChannelHandler，又继承了ChannelInboundInvoker, ChannelOutboundInvoker
 * 提供了执行上一个或者下一个的合适的ChannelHandler，比如：Inbound事件，就只执行Inbound的ChannelHandler
 *
 * @author shangruiwei
 * @date 2023/3/26 11:39
 */
public interface ChannelHandlerContext { //extends ChannelInboundInvoker, ChannelOutboundInvoker {
    
}
