package srw.simple.netty;

import srw.simple.netty.bootstrap.Bootstrap;
import srw.simple.netty.business.TestLogChannelHandler;
import srw.simple.netty.channel.NioSocketChannel;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.EventLoopGroup;
import srw.simple.netty.channel.eventloop.NioEventLoopGroup;

/**
 * Client的测试启动类
 *
 * @author shangruiwei
 * @date 2023/3/26 14:21
 */
public class NettyClientApplication {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8001;

    public static void main(String[] args) {
        // group用于连接Server后的各种读写和业务逻辑，比如：编解码
        EventLoopGroup group = new NioEventLoopGroup(8);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TestLogChannelHandler());

            // connect会连接Server，等于启动了Client服务
            ChannelFuture f = bootstrap.connect(HOST, PORT).sync();

            // 等待channel关闭，所以主线程会阻塞在这里，而不会结束
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅结束，释放占用的资源
            group.shutdownGracefully();
        }
    }
}
