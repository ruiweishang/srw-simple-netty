package srw.simple.netty.channel;

import srw.simple.netty.channel.handler.ChannelHandler;

import java.util.Map;

/**
 * @author shangruiwei
 * @date 2023/3/26 11:36
 */
public interface ChannelPipeline extends ChannelInboundInvoker, ChannelOutboundInvoker, Iterable<Map.Entry<String, ChannelHandler>> {

    ChannelPipeline addLast(ChannelHandler... handlers);
}
