package srw.simple.netty.concurrent.executor;

/**
 * 对应Netty类：io.netty.util.concurrent.AbstractScheduledEventExecutor
 * 定时/延迟任务的执行，暂不支持
 *
 * @author shangruiwei
 * @date 2023/3/26 16:43
 */
public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {

    protected AbstractScheduledEventExecutor(EventExecutorGroup parent) {
        super(parent);
    }
}
