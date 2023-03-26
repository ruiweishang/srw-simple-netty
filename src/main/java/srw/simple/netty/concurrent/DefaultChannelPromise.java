//package srw.simple.netty.concurrent;
//
//import srw.simple.netty.channel.Channel;
//import srw.simple.netty.concurrent.executor.EventExecutor;
//
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
///**
// * ChannelPromise的默认实现
// *
// * @author shangruiwei
// * @date 2023/3/13 11:15
// */
//public class DefaultChannelPromise extends DefaultPromise<Void> implements ChannelPromise {
//
//    private final Channel channel;
//
//    public DefaultChannelPromise(Channel channel, EventExecutor executor) {
//        super(executor);
//        this.channel = channel;
//    }
//
//    @Override
//    public Channel channel() {
//        return this.channel;
//    }
//
//    @Override
//    public ChannelPromise setSuccess() {
//        super.setSuccess(null);
//        return this;
//    }
//
//    @Override
//    public boolean trySuccess() {
//        super.trySuccess(null);
//        return false;
//    }
//
//    @Override
//    public ChannelPromise addListener(GenericFutureListener<Future<Void>> listener) {
//        super.addListener(listener);
//        return this;
//    }
//
//    @Override
//    public ChannelPromise removeListener(GenericFutureListener<Future<Void>> listener) {
//        super.removeListener(listener);
//        return this;
//    }
//
//    @Override
//    public ChannelPromise sync() throws InterruptedException {
//        super.sync();
//        return this;
//    }
//
//    @Override
//    public boolean isDone() {
//        return super.isDone();
//    }
//}
