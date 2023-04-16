package srw.simple.netty.channel;

import srw.simple.netty.buffer.ByteBufAllocator;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.ChannelPromise;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;

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

    public NioSocketChannel() {
        super(null, newChannel(), SelectionKey.OP_READ);
    }

    public NioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket, SelectionKey.OP_READ);
    }

    @Override
    protected Unsafe newUnsafe() {
        return new NioSocketChannelUnsafe();
    }

    @Override
    protected void doWrite(ByteBuffer in) throws Exception {
        SocketChannel ch = (SocketChannel) javaChannel();
        in.flip();
//        byte[] bytes = new byte[in.remaining()];
//        in.get(bytes);
//        System.out.println("doWrite test " + new String(bytes, StandardCharsets.UTF_8));
        ch.write(in);
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
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public Channel flush() {
        return null;
    }

    @Override
    public boolean isActive() {
        SocketChannel ch = (SocketChannel) javaChannel();
        return ch.isOpen() && ch.isConnected();
    }

    private final class NioSocketChannelUnsafe extends AbstractUnsafe {

        @Override
        public void read() {
            final ChannelPipeline pipeline = pipeline();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            int n = 0;
            int MAX_NUM = 10;
            int num = 0;
            do {
                try {
                    n = ((SocketChannel) javaChannel()).read(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                num += 1;

                // 已经读到Buffer里了，触发channelRead事件，并且在channelRead的Handler里，自己判断是否已经读完了
                // 如果代码有问题，一直没有返回读完，最多读MAX_NUM次
                pipeline.fireChannelRead(byteBuffer);
            } while (n > 0 || num >= MAX_NUM);

            // 触发读完事件
            pipeline.fireChannelReadComplete();
        }
    }
}
