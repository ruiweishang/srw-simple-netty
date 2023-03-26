package srw.simple.netty.concurrent.executor;

import srw.simple.netty.concurrent.Promise;

/**
 * @author shangruiwei
 * @date 2023/3/13 11:24
 */
public interface EventExecutor extends EventExecutorGroup {

    /**
     * 返回自己
     * 注释：EventExecutor继承了EventExecutorGroup，但是EventExecutor不是线程池，如果调用next方法，得返回自己
     *
     * @return
     */
    @Override
    EventExecutor next();

    /**
     * 返回EventExecutorGroup
     *
     * @return
     */
    EventExecutorGroup parent();

    /**
     * 判断当前线程是否是EventExecutor的执行线程
     *
     * @return
     */
    boolean inEventLoop();

    /**
     * 判断thread是否是EventExecutor的执行线程
     *
     * @param thread
     * @return
     */
    boolean inEventLoop(Thread thread);

    /**
     * 返回一个新建的promise
     * 注释：newPromise,newProgressivePromise,newSucceededFuture,newFailedFuture，这些方法并不是必须提供的，完全可以自己手动新建promise和future
     *
     * @param <V>
     * @return
     */
    <V> Promise<V> newPromise();

//    <V> ProgressivePromise<V> newProgressivePromise();
//    <V> Future<V> newSucceededFuture(V result);
//    <V> Future<V> newFailedFuture(Throwable cause);
}
