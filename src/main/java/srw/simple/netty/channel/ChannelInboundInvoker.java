package srw.simple.netty.channel;

/**
 * @author shangruiwei
 * @date 2023/3/26 11:35
 */
public interface ChannelInboundInvoker {

    ChannelInboundInvoker fireChannelRegistered();

    ChannelInboundInvoker fireChannelActive();

    ChannelInboundInvoker fireUserEventTriggered(Object event);

    ChannelInboundInvoker fireChannelRead(Object msg);

    ChannelInboundInvoker fireChannelReadComplete();

    ChannelInboundInvoker fireChannelWritabilityChanged();
}
