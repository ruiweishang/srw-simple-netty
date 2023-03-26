package srw.simple.netty.concurrent;

/**
 * 对应的Netty类：io.netty.util.concurrent.Promise
 * 可设置结果的Future
 * 注释：个人理解：Future参与的执行机制，可以分为三步，1：执行，即：execute，2：设置结果，3：执行listener
 * Java的Future的实现类是：FutureTask，他将第一和第二步都合并到FutureTask中执行了，并不区分第一，第二步
 * Netty对执行过程做了进一步的抽象，执行，设置执行结果分开了，所以才有了Promise的出现
 * 而在Promise设置执行结果后，也可以自动调用已注册的Listener，实现对调用者工作的简化
 *
 * @author shangruiwei
 * @date 2023/3/13 10:26
 */
public interface Promise<V> extends Future<V> {

    /**
     * 设置执行成功的结果，并执行Listener
     *
     * @param result
     * @return
     */
    Promise<V> setSuccess(V result);

    /**
     * 尝试设置执行成功的结果，并执行Listener
     *
     * @param result
     * @return
     */
    boolean trySuccess(V result);

    /**
     * 设置执行异常的结果，并执行Listener
     *
     * @param throwable
     * @return
     */
    Promise<V> setFailure(Throwable throwable);

    /**
     * 尝试设置执行异常的结果，并执行Listener
     *
     * @param cause
     * @return
     */
    boolean tryFailure(Throwable cause);
}
