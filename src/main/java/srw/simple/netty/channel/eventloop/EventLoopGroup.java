package srw.simple.netty.channel.eventloop;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.concurrent.executor.EventExecutorGroup;

/**
 * 对应的Netty类：io.netty.channel.EventLoopGroup
 * <p>
 * 本接口是channel对EventExecutor能力的集成
 * 注释：在做任何项目（不管是技术中间件，还是业务项目）想要扩展性强，就必须拆解需求和分层实现
 * EventExecutor这层是对Executor的扩展，不含有业务含义
 * 现在channel要使用promise和listener机制，可以在EventExecutor基础上扩展，所以：
 * 定义的EventLoopGroup，继承了EventExecutorGroup，并且定义了register方法，注册channel
 *
 * @author shangruiwei
 * @date 2023/3/19 17:34
 */
public interface EventLoopGroup extends EventExecutorGroup {

    @Override
    EventLoop next();

    /**
     * 将channel的register方法，作为任务注册到EventExecutorGroup线程池中的某个EventExecutor中
     *
     * @param channel
     * @return
     */
    ChannelFuture register(Channel channel);

    /**
     * 将channel的register方法，作为任务注册到EventExecutorGroup线程池中的某个EventExecutor中
     *
     * @param channelPromise
     * @return
     */
    ChannelFuture register(ChannelPromise channelPromise);
}
