package srw.simple.netty.channel;

import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;

import java.net.SocketAddress;

/**
 * @author shangruiwei
 * @date 2023/3/19 18:15
 */
public interface ChannelOutboundInvoker {

    ChannelFuture bind(SocketAddress socketAddress, ChannelPromise promise);

    ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise);

    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

    ChannelOutboundInvoker read();

    ChannelFuture write(Object msg, ChannelPromise promise);

    ChannelOutboundInvoker flush();
}
