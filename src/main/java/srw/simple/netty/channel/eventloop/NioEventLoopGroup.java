package srw.simple.netty.channel.eventloop;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.concurrent.Future;

import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 对应Netty类：io.netty.channel.nio.NioEventLoopGroup
 * Nio的Channel的线程池
 *
 * @author shangruiwei
 * @date 2023/3/19 18:04
 */
public class NioEventLoopGroup extends MultithreadEventLoopGroup {

    public NioEventLoopGroup(int nThreads) {
        // 注释：selectStrategyFactory, RejectedExecutionHandlers.reject()，这两个参数省略了，简单实现
        super(nThreads, null, SelectorProvider.provider());
    }

    @Override
    protected EventLoop newChild(Executor executor, Object... args) throws Exception {
        return new NioEventLoop(this);
    }
}
