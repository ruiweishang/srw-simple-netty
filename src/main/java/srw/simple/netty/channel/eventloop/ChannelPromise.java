package srw.simple.netty.channel.eventloop;

import srw.simple.netty.concurrent.Future;
import srw.simple.netty.concurrent.GenericFutureListener;
import srw.simple.netty.concurrent.Promise;

/**
 * 对应Netty类：io.netty.channel.ChannelPromise
 * Channel业务的Promise
 *
 * @author shangruiwei
 * @date 2023/3/19 17:55
 */
public interface ChannelPromise extends ChannelFuture, Promise<Void> {

//    @Override
//    ChannelPromise setSuccess(Void result);

    ChannelPromise setSuccess();

    boolean trySuccess();

    @Override
    ChannelPromise addListener(GenericFutureListener<Future<Void>> listener);

    @Override
    ChannelPromise removeListener(GenericFutureListener<Future<Void>> listener);
}
