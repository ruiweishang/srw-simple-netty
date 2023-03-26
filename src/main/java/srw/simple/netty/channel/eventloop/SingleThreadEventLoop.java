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
        return register(new DefaultChannelPromise(channel, this));
    }

    @Override
    public ChannelFuture register(final ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }
}
