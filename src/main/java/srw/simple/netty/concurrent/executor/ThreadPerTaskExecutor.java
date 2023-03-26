package srw.simple.netty.concurrent.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author shangruiwei
 * @date 2023/3/26 16:07
 */
public class ThreadPerTaskExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        // 注释：Netty使用了线程工厂创建，我就简化了
//        threadFactory.newThread(command).start();
        Executors.newSingleThreadExecutor().execute(command);
    }
}
