package srw.simple.netty.channel.handler;

/**
 * @author shangruiwei
 * @date 2023/3/26 21:55
 */
public class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {
    public final ChannelHandler handler;

    DefaultChannelHandlerContext(ChannelHandler handler) {
        this.handler = handler;
    }
}
