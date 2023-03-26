//package srw.simple.netty.concurrent;
//
//import srw.simple.netty.channel.Channel;
//
///**
// * 专门用来给Channel用的Promise或者Future（因为类型确定是Channel了，所以泛型用Void来做占位符）
// *
// * @author shangruiwei
// * @date 2023/3/13 11:07
// */
//public interface ChannelPromise extends Promise<Void> {
//
//    /**
//     * 获取关联的Channel
//     *
//     * @return
//     */
//    Channel channel();
//
//    ChannelPromise setSuccess();
//
//    boolean trySuccess();
//
//    @Override
//    ChannelPromise addListener(GenericFutureListener<Future<Void>> listener);
//}
