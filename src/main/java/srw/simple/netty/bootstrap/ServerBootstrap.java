package srw.simple.netty.bootstrap;

import srw.simple.netty.business.ServerChannelHandler;
import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.ChannelPipeline;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.DefaultChannelPromise;
import srw.simple.netty.channel.eventloop.EventLoopGroup;
import srw.simple.netty.channel.handler.ChannelHandler;
import srw.simple.netty.channel.handler.ChannelHandlerContext;
import srw.simple.netty.channel.handler.ChannelInboundHandler;
import srw.simple.netty.concurrent.Future;
import srw.simple.netty.concurrent.GenericFutureListener;
import srw.simple.netty.concurrent.executor.EventExecutor;
import srw.simple.netty.utils.LogUtil;
import srw.simple.netty.utils.ObjectUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

/**
 * 对应的Netty类：io.netty.bootstrap.ServerBootstrap
 * 作为服务启动类，提供了建造者模式（builder），并在bind方法，封装了创建Channel和注册（register）逻辑
 * 让用户以一种超简单的方式，创建一个Server
 *
 * @author shangruiwei
 * @date 2023/3/19 18:12
 */
public class ServerBootstrap extends AbstractBootstrap {

    private volatile EventLoopGroup childGroup;
    private volatile ChannelHandler childHandler;

    /**
     * 创建
     *
     * @return
     */
    public static ServerBootstrap create() {
        return new ServerBootstrap();
    }

    /**
     * EventLoopGroup的建造器
     * parentGroup用于接收client的连接，并负责处理多路复用器(selector)中的各种就绪事件
     * childGroup用于处理某个和Client连接的各种读写和业务逻辑，比如：编解码
     *
     * @param parentGroup
     * @param childGroup
     * @return
     */
    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        this.childGroup = ObjectUtil.checkNotNull(childGroup, "childGroup");
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        this.childHandler = ObjectUtil.checkNotNull(childHandler, "childHandler");
        return this;
    }

    void init(Channel channel) {
//        setChannelOptions(channel, newOptionsArray(), logger);
//        setAttributes(channel, newAttributesArray());

        // Server的处理管道
        ChannelPipeline p = channel.pipeline();

        // 将handler添加到pipeline，并添加一个可以创建SocketChannel的handler：ServerBootstrapAcceptor
        if (super.handler != null) {
            LogUtil.log(this.getClass(), "初始化server channel，将handler添加到pipeline中");
            p.addLast(super.handler);
        }
        p.addLast(new ServerChannelHandler());

        // 添加
        LogUtil.log(this.getClass(), "初始化server channel，将ServerBootstrapAcceptor添加到pipeline中（此handler用来在server accept一个client，在创建一个channel后，在server channel的readChannel的初始化client channel）");
        p.addLast(new ServerBootstrapAcceptor(channel, childGroup, childHandler));
    }

    /**
     * Server绑定指定的端口，并返回主线程ChannelFuture，主线程可用ChannelFuture.sync等待bind异步执行完成
     *
     * @return
     */
    public ChannelFuture bind(int port) {
        SocketAddress localAddress = new InetSocketAddress(port);

        // 创建并初始化一个Channel，并将Channel注册到EventLoop中
        final ChannelFuture regFuture = initAndRegister();

        // bind
        return doBind(regFuture, localAddress);
    }

    private ChannelFuture doBind(ChannelFuture regFuture, final SocketAddress localAddress) {
        // 刚才创建的Channel的引用
        final Channel channel = regFuture.channel();
        // 找一个Executor执行bind
//        EventExecutor eventExecutor = group.next();
//        DefaultChannelPromise bindPromise = new DefaultChannelPromise(channel, eventExecutor);
        DefaultChannelPromise bindPromise = new DefaultChannelPromise(channel, null);
        // 将bind的处理，添加到listener，这样，一定是等注册好了，才会执行bind
        regFuture.addListener(new GenericFutureListener<Future<Void>>() {
            @Override
            public void operationComplete(Future<Void> future) {
                // 注释：将bind作为task添加很重要，作为task添加，那会先执行register的所有逻辑后，才会执行bind
                channel.eventLoop().execute(() -> {
                    // 判断注册是否成功了
                    if (regFuture.isSuccess()) {
                        channel.bind(localAddress, bindPromise);
                    } else {
                        System.out.println("注册失败，不能bind");
                        // TODO bindPromise.setFailure(regFuture.cause());
                    }
                });
            }
        });

        return bindPromise;
    }

    private static class ServerBootstrapAcceptor implements ChannelInboundHandler {

        private final EventLoopGroup childGroup;
        private final ChannelHandler childHandler;

        ServerBootstrapAcceptor(
                final Channel channel, EventLoopGroup childGroup, ChannelHandler childHandler) {
            this.childGroup = childGroup;
            this.childHandler = childHandler;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            LogUtil.log(this.getClass(), "ServerBootstrapAcceptor channelRead");
            final Channel child = (Channel) msg;

//            child.pipeline().addLast(childHandler);

            childGroup.register(child);
            // TODO 处理注册失败
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        }
    }
}
