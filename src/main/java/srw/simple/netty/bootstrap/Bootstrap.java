package srw.simple.netty.bootstrap;

import srw.simple.netty.business.ClientChannelHandler;
import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.ChannelPipeline;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.DefaultChannelPromise;
import srw.simple.netty.concurrent.Future;
import srw.simple.netty.concurrent.GenericFutureListener;
import srw.simple.netty.concurrent.executor.EventExecutor;
import srw.simple.netty.utils.LogUtil;
import srw.simple.netty.utils.ObjectUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author shangruiwei
 * @date 2023/3/19 18:12
 */
public class Bootstrap extends AbstractBootstrap {

    private volatile SocketAddress remoteAddress;

    void init(Channel channel) {
//        setChannelOptions(channel, newOptionsArray(), logger);
//        setAttributes(channel, newAttributesArray());

        ChannelPipeline p = channel.pipeline();

        LogUtil.log(this.getClass(), "初始化channel，将handler添加到pipeline中");
        p.addLast(handler);
        p.addLast(new ClientChannelHandler());
    }

    public ChannelFuture connect(String inetHost, int inetPort) {
        // return connect(InetSocketAddress.createUnresolved(inetHost, inetPort));
        return connect(new InetSocketAddress(inetHost, inetPort));
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
        return doResolveAndConnect(remoteAddress, localAddress());
    }

    private ChannelFuture doResolveAndConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
        // 创建并初始化一个Channel，并将Channel注册到EventLoop中
        final ChannelFuture regFuture = initAndRegister();

        // connect
        return this.doResolveAndConnect(regFuture, remoteAddress, localAddress);
    }

    private ChannelFuture doResolveAndConnect(ChannelFuture regFuture, final SocketAddress remoteAddress, final SocketAddress localAddress) {
        // 刚才创建的Channel的引用
        final Channel channel = regFuture.channel();
        // 找一个Executor执行connect
        DefaultChannelPromise connectPromise = new DefaultChannelPromise(channel, channel.eventLoop());

        // 将connect的处理，添加到listener，这样，一定是等注册好了，才会执行connect
        regFuture.addListener(new GenericFutureListener<Future<Void>>() {
            @Override
            public void operationComplete(Future<Void> future) {
                // 注释：将connect作为task添加很重要，作为task添加，那会先执行register的所有逻辑后，才会执行connect
                channel.eventLoop().execute(() -> {
                    LogUtil.log(this.getClass(), "reg operationComplete");
                    // 判断注册是否成功了
                    if (regFuture.isSuccess()) {
                        if (localAddress == null) {
                            channel.connect(remoteAddress, connectPromise);
                        } else {
                            channel.connect(remoteAddress, localAddress, connectPromise);
                        }
                    } else {
                        System.out.println("注册失败，不能connect");
                        // TODO bindPromise.setFailure(regFuture.cause());
                    }
                });
            }
        });

        return connectPromise;
    }

}
