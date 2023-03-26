package srw.simple.netty.concurrent;

import java.util.EventListener;

/**
 * 执行监听器（即：执行所有已经被add的Future中的Listener）
 * 注释：多提一下，观察者模式是非常常用的模式
 * Java提供了EventListener，EventObject
 * Netty和Spring也基于EventListener，EventObject提供了事件机制，比如：Spring的ApplicationListener和ApplicationEvent，就分别继承
 * 了EventListener，EventObject
 * 如果再将观察者模式的概念泛化，就是一种通知机制，联想常用的中间件我们可以想到什么？没错，就是MQ
 *
 * @author shangruiwei
 * @date 2023/3/12 10:04
 */
public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    /**
     * 执行监听器（即：执行所有已经被add的Future中的Listener）
     *
     * @param future
     */
    void operationComplete(F future);
}
