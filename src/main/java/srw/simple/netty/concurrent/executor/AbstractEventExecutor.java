package srw.simple.netty.concurrent.executor;

import srw.simple.netty.concurrent.DefaultPromise;
import srw.simple.netty.concurrent.Future;
import srw.simple.netty.concurrent.Promise;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author shangruiwei
 * @date 2023/3/19 17:29
 */
public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {

    private final EventExecutorGroup parent;
    private final Collection<EventExecutor> selfCollection = Collections.<EventExecutor>singleton(this);

    static final long DEFAULT_SHUTDOWN_QUIET_PERIOD = 2;
    static final long DEFAULT_SHUTDOWN_TIMEOUT = 15;

    protected AbstractEventExecutor() {
        this(null);
    }

    protected AbstractEventExecutor(EventExecutorGroup parent) {
        this.parent = parent;
    }

    @Override
    public EventExecutor next() {
        return this;
    }

    @Override
    public EventExecutorGroup parent() {
        return parent;
    }

    @Override
    public boolean inEventLoop() {
        return inEventLoop(Thread.currentThread());
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new DefaultPromise<V>(this);
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return selfCollection.iterator();
    }

    @Override
    public Future<?> submit(Runnable task) {
        return (Future<?>) super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return (Future<T>) super.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return (Future<T>) super.submit(task);
    }

    @Override
    public Future<?> shutdownGracefully() {
        return shutdownGracefully(DEFAULT_SHUTDOWN_QUIET_PERIOD, DEFAULT_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    @Deprecated
    public void shutdown() {

    }

    @Override
    @Deprecated
    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.emptyList();
    }
}
