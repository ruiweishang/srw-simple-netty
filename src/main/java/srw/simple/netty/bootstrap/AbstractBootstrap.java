package srw.simple.netty.bootstrap;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.ChannelPipeline;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.channel.eventloop.DefaultChannelPromise;
import srw.simple.netty.channel.eventloop.EventLoopGroup;
import srw.simple.netty.channel.handler.ChannelHandler;
import srw.simple.netty.utils.LogUtil;
import srw.simple.netty.utils.ObjectUtil;

import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shangruiwei
 * @date 2023/3/26 11:44
 */
public abstract class AbstractBootstrap {

    volatile EventLoopGroup group;

    private volatile SocketAddress localAddress;

//    private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();
//    private final Map<AttributeKey<?>, Object> attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();

    volatile ChannelHandler handler;

    /**
     * Channel
     * 注释：这里做了简化，Netty使用先构造ChannelFactory，再用ChannelFactory创建Channel
     */
    private Class<? extends Channel> channelClass;

    final SocketAddress localAddress() {
        return localAddress;
    }

    public AbstractBootstrap group(EventLoopGroup group) {
        this.group = ObjectUtil.checkNotNull(group, "group");
        return this;
    }

    public AbstractBootstrap channel(Class<? extends Channel> channelClass) {
        this.channelClass = ObjectUtil.checkNotNull(channelClass, "channelClass");
        return this;
    }

    public AbstractBootstrap handler(ChannelHandler handler) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        return this;
    }

    /**
     * 创建channel
     *
     * @return
     */
    Channel createChannel() {
        try {
            LogUtil.log(this.getClass(), "创建channel");
            return channelClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 交给子类初始化
     *
     * @param channel
     * @throws Exception
     */
    abstract void init(Channel channel);

    final ChannelFuture initAndRegister() {
        // 创建channel
        Channel channel = createChannel();
        // 初始化channel
        init(channel);

        // 将channel注册到EventLoop中
        // 注释：group是线程池，group会调用next找一个具体点EventLoop处理register
        return this.group.register(channel);
    }
}
