package srw.simple.netty.channel;

import srw.simple.netty.buffer.ByteBufAllocator;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

/**
 * 对应的Netty类：io.netty.channel.socket.nio.NioServerSocketChannel
 *
 * @author shangruiwei
 * @date 2023/3/26 14:26
 */
public class NioServerSocketChannel extends AbstractChannel {

    private static ServerSocketChannel newChannel() {
        ServerSocketChannel channel = null;
        try {
            channel = SelectorProvider.provider().openServerSocketChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public NioServerSocketChannel() {
        super(null, newChannel(), SelectionKey.OP_ACCEPT);
    }

    @Override
    protected Unsafe newUnsafe() {
        // NioServer使用的Unsafe
        return new NioMessageUnsafe();
    }

    @Override
    public Unsafe unsafe() {
        return super.unsafe;
    }

    @Override
    public ByteBufAllocator alloc() {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public Channel flush() {
        return null;
    }

    @Override
    public ChannelFuture closeFuture() {
        return null;
    }

    private final class NioMessageUnsafe extends AbstractUnsafe {

        @Override
        public void write(Object msg, ChannelPromise promise) {

        }

        @Override
        public void flush() {

        }
    }
}
