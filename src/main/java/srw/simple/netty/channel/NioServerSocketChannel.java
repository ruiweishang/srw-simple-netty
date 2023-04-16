package srw.simple.netty.channel;

import srw.simple.netty.buffer.ByteBufAllocator;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;
import srw.simple.netty.utils.LogUtil;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;

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
    protected void doWrite(ByteBuffer in) throws Exception {

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
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public Channel flush() {
        return null;
    }

    @Override
    public boolean isActive() {
        ServerSocketChannel ch = (ServerSocketChannel) javaChannel();
        return ch.isOpen() && ch.socket().isBound();
    }

    private final class NioMessageUnsafe extends AbstractUnsafe {

        private final List<Object> readBuf = new ArrayList<Object>();

        @Override
        public void read() {
            final ChannelPipeline pipeline = pipeline();

            SocketChannel ch = null;
            do {
                try {
                    // accept新的client链接
                    LogUtil.log(this.getClass(), String.format("Thread:%s ServerSocketChannel accept一个SocketChannel", Thread.currentThread().getName()));
                    ch = ((ServerSocketChannel) javaChannel()).accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (ch != null) {
                    // 将accept新的client链接构建NioSocketChannel对象，并缓存在NioMessageUnsafe的readBuf中
                    readBuf.add(new NioSocketChannel(NioServerSocketChannel.this, ch));
                }
            } while (ch != null);

            for (int i = 0; i < readBuf.size(); i++) {
                pipeline.fireChannelRead(readBuf.get(i));
            }

            pipeline.fireChannelReadComplete();
        }
    }
}
