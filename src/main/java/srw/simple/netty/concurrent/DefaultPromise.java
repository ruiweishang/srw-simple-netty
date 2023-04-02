package srw.simple.netty.concurrent;

import srw.simple.netty.concurrent.executor.EventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 对应的Netty类：io.netty.util.concurrent.DefaultPromise
 * Future和Promise的实现类
 *
 * @author shangruiwei
 * @date 2023/3/13 11:20
 */
public class DefaultPromise<V> implements Promise<V> {

    /**
     * 原子类，提供cas设置result变量
     */
    private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, "result");

    /**
     * 任务执行成功的标识
     */
    private static final Object SUCCESS = new Object();

    /**
     * 任务执行未完成的标识
     */
    private static final Object UNCANCELLABLE = new Object();

    /**
     * 任务执行结果
     */
    private volatile Object result;

    /**
     * 任务执行用的Executor
     */
    private final EventExecutor executor;

    /**
     * 等待线程的数量
     */
    private short waiters;

    /**
     * listener递归的最大深度
     */
    private static final Integer MAX_LISTENER_STACK_DEPTH = Integer.valueOf(System.getProperty("io.netty.defaultPromise.maxListenerStackDepth", "8"));

    /**
     * EventExecutor执行过程中的listener递归深度
     */
    private static final ThreadLocal<Integer> FutureListenerStackDepthThreadLocal =
            new ThreadLocal<Integer>();

    /**
     * 监听器
     * 注释：Netty定义的是Object类型，原因是要支持两种类型：GenericFutureListener,DefaultFutureListeners
     * TODO 没分析Netty这样做的原因，应该只需要支持DefaultFutureListeners就行了呀，Spring也是这样做的，会使用ApplicationEventMulticaster统一处理
     */
    private List<GenericFutureListener<Future<V>>> listeners = new ArrayList<>();

    public DefaultPromise(EventExecutor eventExecutor) {
        this.executor = eventExecutor;
    }

    @Override
    public boolean isSuccess() {
        Object result = this.result;
        return result != null && result != UNCANCELLABLE;
    }

    @Override
    public Future<V> addListener(GenericFutureListener<Future<V>> listener) {
        if (listener != null) {
            synchronized (this) {
                // 线程安全的添加listener
                listeners.add(listener);
            }
            if (this.isDone()) {
                // 此情况是：任务已经执行完了，才添加listener，所以立即执行listener
                this.notifyListeners();
            }
        }
        return this;
    }

    @Override
    public Future<V> removeListener(GenericFutureListener<Future<V>> listener) {
        synchronized (this) {
            if (listeners != null && listeners.size() > 0) {
                listeners.remove(listener);
            }
        }
        return this;
    }

    @Override
    public V getNow() {
        // 注释：在很多中间件的代码里都能看到此情况，将一个变量赋值给一个方法里的局部变量，比如 Object result = this.result;
        // 我的理解是：DefaultPromise的result变量是volatile类型，整个方法流程里，只需要在赋值一瞬间的值就行，不需要每次使用volatile类型
        // 这样做的好处是：局部变量的性能高于volatile变量，另外，如果多次使用DefaultPromise的result，需要处理多次读的不一致
        Object result = this.result;
        // 注释：简化实现了，没有加异常状态（CauseHolder）
        if (result == SUCCESS || result == UNCANCELLABLE) {
            return null;
        }
        return (V) result;
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        if (isDone()) {
            return this;
        }
        synchronized (this) {
            while (!isDone()) {
                wait();
            }
        }
        return this;
    }

    @Override
    public Promise<V> setSuccess(V result) {
        return null;
    }

    @Override
    public boolean trySuccess(V result) {
        return setValue0(result != null ? result : SUCCESS);
    }

    @Override
    public Promise<V> setFailure(Throwable throwable) {
        // TODO
        return null;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        // TODO
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO
        return false;
    }

    @Override
    public boolean isCancelled() {
        // TODO
        return false;
    }

    @Override
    public boolean isDone() {
        // result不是UNCANCELLABLE，就是完成了
        return result != null && result != UNCANCELLABLE;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (!this.isDone()) {
            // 如果未执行完，就阻塞当前线程
            synchronized (this) {
                // 双重检查
                while (!this.isDone()) {
                    // 阻塞
                    waiters += 1;
                    wait();
                }
            }
        }

        // 执行完毕
        Object result = this.result;

        if (result == SUCCESS || result == UNCANCELLABLE) {
            // 此情况是创建的promise/future没有返回值，返回null即可
            return null;
        }

        return (V) result;
        // TODO 为了实现简单，异常情况，暂时不处理
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // TODO
        return null;
    }

    /**
     * 返回本promise的EventExecutor
     *
     * @return
     */
    protected EventExecutor executor() {
        return executor;
    }

    private void notifyListeners() {
        EventExecutor executor = executor();
        if (executor.inEventLoop()) {
            // 如果当前线程是EventExecutor的执行线程，那判断一下调用的栈的深度，如果太深，就listener作为task添加到EventExecutor中
            Integer stackDepth = FutureListenerStackDepthThreadLocal.get();
            if (stackDepth == null) {
                stackDepth = 0;
            }
            if (stackDepth < MAX_LISTENER_STACK_DEPTH) {
                // 如果调用栈的深度不深，就立即执行listener
                FutureListenerStackDepthThreadLocal.set(stackDepth + 1);
                try {
                    notifyListenersNow();
                } finally {
                    FutureListenerStackDepthThreadLocal.set(stackDepth);
                }
            }
        }

        // 如果当前线程不是EventExecutor的执行线程，或者调用栈的深度已经很深，就将listener作为task添加到EventExecutor中
        safeExecute(executor, this::notifyListenersNow);
    }

    private void notifyListenersNow() {
        List<GenericFutureListener<Future<V>>> tempListeners = null;

        synchronized (this) {
            // 注释：首先listener只需要执行一次，执行后，从listeners中清除
            // 加上synchronized，将还未执行的listener取出，赋值给tempListeners，并清空listeners
            if (listeners != null && listeners.size() > 0) {
                tempListeners = listeners;
            }

            // 清空listeners
            listeners = new ArrayList<>();
        }

        if (tempListeners != null && tempListeners.size() > 0) {
            for (GenericFutureListener<Future<V>> tempListener : tempListeners) {
                tempListener.operationComplete(this);
            }
        }
    }

    private static void safeExecute(EventExecutor executor, Runnable task) {
        try {
            executor.execute(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean setValue0(Object objResult) {
        if (RESULT_UPDATER.compareAndSet(this, null, objResult) ||
                RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, objResult)) {
            synchronized (this) {
                if (waiters > 0) {
                    notifyAll();
                }

                notifyListeners();
            }
            return true;
        }
        return false;
    }

}
