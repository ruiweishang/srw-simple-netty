package srw.simple.netty.channel;

import srw.simple.netty.buffer.ByteBufAllocator;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;
import srw.simple.netty.channel.eventloop.EventLoop;

import java.net.SocketAddress;

/**
 * @author shangruiwei
 * @date 2023/3/13 11:09
 */
public interface Channel extends ChannelOutboundInvoker {

    Unsafe unsafe();

    EventLoop eventLoop();

    /**
     * 返回Channel的Pipeline
     *
     * @return
     */
    ChannelPipeline pipeline();

    ByteBufAllocator alloc();

    @Override
    Channel read();

    @Override
    Channel flush();

    ChannelFuture closeFuture();

    boolean isActive();

    interface Unsafe {

        void register(EventLoop eventLoop, ChannelPromise promise);

        void bind(SocketAddress localAddress, ChannelPromise promise);

        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        void beginRead();

        void write(Object msg, ChannelPromise promise);

        void flush();
    }
}
