package srw.simple.netty.channel.eventloop;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.concurrent.executor.SingleThreadEventExecutor;
import srw.simple.netty.utils.ObjectUtil;

import java.util.concurrent.ThreadFactory;

/**
 * @author shangruiwei
 * @date 2023/3/19 18:05
 */
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    protected SingleThreadEventLoop(EventLoopGroup parent) {
        super(parent);
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup) super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        // 新建一个ChannelPromise，使用promise机制，不阻塞main线程，在另外一个线程，执行register逻辑
        // main线程可以使用ChannelPromise对象，获取register的执行结果
        DefaultChannelPromise channelPromise = new DefaultChannelPromise(channel, this);
        return register(channelPromise);
    }

    @Override
    public ChannelFuture register(final ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }
}
