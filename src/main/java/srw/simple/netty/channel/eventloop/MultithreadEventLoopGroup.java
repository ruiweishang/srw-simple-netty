package srw.simple.netty.channel.eventloop;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.concurrent.executor.MultithreadEventExecutorGroup;

import java.util.concurrent.Executor;

/**
 * 对应的Netty类：io.netty.channel.MultithreadEventLoopGroup
 * Channel业务的线程池，是EventExecutorGroup的一种具体实现
 *
 * @author shangruiwei
 * @date 2023/3/19 18:03
 */
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    protected MultithreadEventLoopGroup(int nThreads, Object... args) {
        super(nThreads, args);
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return next().register(channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return next().register(promise);
    }

    @Override
    protected abstract EventLoop newChild(Executor executor, Object... args) throws Exception;

}
