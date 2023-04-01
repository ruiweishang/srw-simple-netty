package srw.simple.netty.channel;

import srw.simple.netty.channel.handler.ChannelHandler;

/**
 * @author shangruiwei
 * @date 2023/3/26 11:36
 */
public interface ChannelPipeline extends ChannelInboundInvoker, ChannelOutboundInvoker {

    ChannelPipeline addLast(ChannelHandler... handlers);

    Channel channel();
}
