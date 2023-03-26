package srw.simple.netty;

import srw.simple.netty.bootstrap.ServerBootstrap;
import srw.simple.netty.business.TestLogChannelHandler;
import srw.simple.netty.channel.NioServerSocketChannel;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.EventLoopGroup;
import srw.simple.netty.channel.eventloop.NioEventLoopGroup;

/**
 * Server的测试启动类
 *
 * @author shangruiwei
 * @date 2023/3/19 18:13
 */
public class NettyServerApplication {

    private static final int PORT = 8001;

    public static void main(String[] args) {
        /**
         * 注释：EventLoopGroup是Channel工作的线程池，Netty启动后，大部分的工作会生成task，添加到EventLoopGroup中的某个具体的EventLoop，
         * 最终会被EventLoop的线程调度执行
         */
        // 作为Server，bossGroup用于接收client的连接，并负责处理多路复用器(selector)中的各种就绪事件
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // workGroup用于处理某个和Client连接的各种读写和业务逻辑，比如：编解码
        EventLoopGroup workGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = (ServerBootstrap) ServerBootstrap.create()
                    // Netty使用建造者模式，配置各种属性
                    .group(bossGroup, workGroup)
                    .childHandler(new TestLogChannelHandler())
                    .channel(NioServerSocketChannel.class)
                    .handler(new TestLogChannelHandler());

            // bind就会启动服务了
            ChannelFuture bindChannelFuture = serverBootstrap.bind(PORT).sync();

            // 等待Server的Channel关闭，所以启动后主线程会阻塞在这里，而不会结束
            bindChannelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅结束，释放占用的资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
