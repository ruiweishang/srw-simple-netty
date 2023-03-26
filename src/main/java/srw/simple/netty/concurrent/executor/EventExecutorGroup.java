package srw.simple.netty.concurrent.executor;

import srw.simple.netty.concurrent.Future;

import java.lang.Iterable;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 对应Netty类：io.netty.util.concurrent.EventExecutorGroup
 * 简化实现，不继承：ScheduledExecutorService
 *
 * @author shangruiwei
 * @date 2023/3/13 11:25
 */
public interface EventExecutorGroup extends ExecutorService, Iterable<EventExecutor> { //extends ScheduledExecutorService, Iterable<EventExecutor> {

    /**
     * 优雅关闭，释放资源
     *
     * @return
     */
    Future<?> shutdownGracefully();

    /**
     * 优雅关闭，释放资源
     *
     * @param quietPeriod
     * @param timeout
     * @param unit
     * @return
     */
    Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

    /**
     * 终止的Future
     *
     * @return
     */
    Future<?> terminationFuture();

    /**
     * 返回线程池组中的一个EventExecutor
     *
     * @return
     */
    EventExecutor next();

    @Override
    Iterator<EventExecutor> iterator();

    @Override
    Future<?> submit(Runnable task);

    @Override
    <T> Future<T> submit(Runnable task, T result);

    @Override
    <T> Future<T> submit(Callable<T> task);
}
