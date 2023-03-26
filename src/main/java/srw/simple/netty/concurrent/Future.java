package srw.simple.netty.concurrent;

/**
 * 对应的Netty类：io.netty.util.concurrent.Future
 * Java的Future，提供了获取另外一个线程执行结果的能力，netty的Future在其基础上扩展了监听器，即：addListener，实现了事件监听机制
 * 需要注意的是，addListener可能发生在任务已经完成后，所以，addListener后，要校验任务是否完成，如果完成需要直接listener
 *
 * @author shangruiwei
 * @date 2023/3/12 10:00
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

    /**
     * 是否成功了
     *
     * @return
     */
    boolean isSuccess();

    /**
     * 添加监听器
     *
     * @param listener
     * @return
     */
    Future<V> addListener(GenericFutureListener<Future<V>> listener);

    /**
     * 移除监听器
     *
     * @param listener
     * @return
     */
    Future<V> removeListener(GenericFutureListener<Future<V>> listener);

    /**
     * 不阻塞调用线程方式，获取结果（即使还未执行完，也会返回null）
     *
     * @return
     */
    V getNow();

    /**
     * 阻塞调用线程，直到执行完，可以获取结果
     *
     * @return
     * @throws InterruptedException
     */
    Future<V> sync() throws InterruptedException;
}
