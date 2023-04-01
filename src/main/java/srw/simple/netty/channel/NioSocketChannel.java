package srw.simple.netty.channel;

import srw.simple.netty.buffer.ByteBufAllocator;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

/**
 * @author shangruiwei
 * @date 2023/3/26 15:34
 */
public class NioSocketChannel extends AbstractChannel {

    private static SocketChannel newChannel() {
        SocketChannel channel = null;
        try {
            channel = SelectorProvider.provider().openSocketChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public NioSocketChannel(Channel parent) {
        super(parent, newChannel(), SelectionKey.OP_READ);
    }

    @Override
    protected Unsafe newUnsafe() {
        return new NioSocketChannelUnsafe();
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
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise promise) {
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

    private final class NioSocketChannelUnsafe extends AbstractUnsafe {

        @Override
        public void beginRead() {

        }

        @Override
        public void write(Object msg, ChannelPromise promise) {

        }

        @Override
        public void flush() {

        }
    }
}
