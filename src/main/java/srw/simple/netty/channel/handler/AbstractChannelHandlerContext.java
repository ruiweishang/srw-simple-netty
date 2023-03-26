package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.Channel;
import srw.simple.netty.channel.DefaultChannelPipeline;
import srw.simple.netty.channel.eventloop.ChannelFuture;
import srw.simple.netty.concurrent.executor.EventExecutor;

import java.net.SocketAddress;
import java.util.Map;

/**
 * @author shangruiwei
 * @date 2023/3/26 18:45
 */
public abstract class AbstractChannelHandlerContext implements ChannelHandlerContext {

    public volatile AbstractChannelHandlerContext next;
    public volatile AbstractChannelHandlerContext prev;

//    private final DefaultChannelPipeline pipeline;
//    final EventExecutor executor;

//    protected AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor,
//                                            String name, Class<? extends ChannelHandler> handlerClass) {
//        this.pipeline = pipeline;
//        this.executor = executor;
//    }
}
