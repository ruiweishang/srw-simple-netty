package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.ChannelInboundInvoker;
import srw.simple.netty.concurrent.executor.EventExecutor;

/**
 * @author shangruiwei
 * @date 2023/3/26 21:55
 */
public class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {

    public final ChannelHandler handler;

    DefaultChannelHandlerContext(
            DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
        super(pipeline, executor, name, handler.getClass());
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return handler;
    }

    @Override
    public ChannelInboundInvoker fireUserEventTriggered(Object event) {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelRead(Object msg) {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelReadComplete() {
        return null;
    }

    @Override
    public ChannelInboundInvoker fireChannelWritabilityChanged() {
        return null;
    }
}
