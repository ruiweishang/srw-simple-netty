package srw.simple.netty.channel.eventloop;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.concurrent.Future;
import srw.simple.netty.concurrent.GenericFutureListener;

/**
 * 对应Netty类：io.netty.channel.ChannelFuture
 * 是Channel业务的Future
 *
 * @author shangruiwei
 * @date 2023/3/19 17:49
 */
public interface ChannelFuture extends Future<Void> {

    /**
     * Future关联的Channel
     *
     * @return
     */
    Channel channel();

    /**
     * Channel业务，添加监听器
     *
     * @param listener
     * @return
     */
    @Override
    ChannelFuture addListener(GenericFutureListener<Future<Void>> listener);

    /**
     * Channel业务，移除监听器
     *
     * @param listener
     * @return
     */
    @Override
    ChannelFuture removeListener(GenericFutureListener<Future<Void>> listener);

    @Override
    ChannelFuture sync() throws InterruptedException;
}
