package srw.simple.netty.concurrent.executor;

import srw.simple.netty.utils.ObjectUtil;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

import srw.simple.netty.concurrent.Future;

/**
 * @author shangruiwei
 * @date 2023/3/19 18:05
 */
public abstract class SingleThreadEventExecutor extends AbstractScheduledEventExecutor {

    private final Queue<Runnable> taskQueue;

    private volatile Thread thread;

    private final Executor executor;

    private final CountDownLatch threadLock = new CountDownLatch(1);
    private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
    //    private final boolean addTaskWakesUp;
//    private final int maxPendingTasks;
//    private final RejectedExecutionHandler rejectedExecutionHandler;

    protected SingleThreadEventExecutor(EventExecutorGroup parent) {
        super(parent);
//        this.addTaskWakesUp = addTaskWakesUp;
//        this.maxPendingTasks = Math.max(16, maxPendingTasks);
        this.executor = new ThreadPerTaskExecutor();
        taskQueue = new LinkedBlockingQueue<Runnable>(1000);
    }

    @Override
    public void execute(Runnable task) {
        execute0(task);
    }

    private void execute0(Runnable task) {
        ObjectUtil.checkNotNull(task, "task");
        execute(task, true);
    }

    private void execute(Runnable task, boolean immediate) {
        boolean inEventLoop = inEventLoop();
        // 投递任务
        taskQueue.offer(task);
        if (!inEventLoop) {
            // 主线程执行，会执行本逻辑，启动新线程执行
            startThread();
        }
    }

    private void startThread() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 启动新线程执行了
                SingleThreadEventExecutor.this.run();
            }
        });
    }

    protected boolean hasTasks() {
        assert inEventLoop();
        return !taskQueue.isEmpty();
    }

    protected boolean runAllTasks() {
        assert inEventLoop();

        Runnable task = null;

        do {
            task = taskQueue.poll();
            if (task != null) {
                task.run();
            }
        } while (task != null);

        return true;
    }

    @Override
    public boolean isShutdown() {
        // TODO
        return false;
    }

    @Override
    public boolean isTerminated() {
        // TODO
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        ObjectUtil.checkNotNull(unit, "unit");
        if (inEventLoop()) {
            throw new IllegalStateException("cannot await termination of the current thread");
        }

        threadLock.await(timeout, unit);

        return isTerminated();
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        // TODO
        return null;
    }

    @Override
    public Future<?> terminationFuture() {
        // TODO
        return null;
    }

    /**
     * 子类做具体实现
     */
    protected abstract void run();
}
