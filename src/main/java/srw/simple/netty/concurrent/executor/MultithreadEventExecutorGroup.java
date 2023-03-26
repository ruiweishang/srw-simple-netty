package srw.simple.netty.concurrent.executor;

import srw.simple.netty.concurrent.DefaultPromise;
import srw.simple.netty.concurrent.Future;
import srw.simple.netty.concurrent.Promise;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对应的Netty类：io.netty.util.concurrent.MultithreadEventExecutorGroup
 * EventExecutor线程池的抽象实现类
 *
 * @author shangruiwei
 * @date 2023/3/19 18:03
 */
public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {

    // 线程池
    private final EventExecutor[] children;
    private final Set<EventExecutor> readonlyChildren;
    // TODO 实现结束，并释放资源
    // private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    // 简化实现，不提供策略了
    // private final EventExecutorChooserFactory.EventExecutorChooser chooser;

    private final AtomicInteger idx = new AtomicInteger();

    /**
     * @param nThreads
     * @param args     具体的EventExecutorGroup创建EventExecutor时，透传的参数
     */
    public MultithreadEventExecutorGroup(int nThreads, Object... args) {
        children = new EventExecutor[nThreads];

        ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();

        for (int i = 0; i < nThreads; i++) {
            try {
                children[i] = newChild(executor, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Set<EventExecutor> childrenSet = new LinkedHashSet<EventExecutor>(children.length);
        Collections.addAll(childrenSet, children);
        readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }

    @Override
    public EventExecutor next() {
        return children[idx.getAndIncrement() & children.length - 1];
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return readonlyChildren.iterator();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        for (EventExecutor l : children) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
        return terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        // TODO
        return null;
//        return terminationFuture;
    }

    /**
     * 让具体的业务实现具体的EventExecutor，比如：Nio会创建NioEventLoop
     *
     * @param executor
     * @param args
     * @return
     * @throws Exception
     */
    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;
}
