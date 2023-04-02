package srw.simple.netty.channel.handler;

import srw.simple.netty.channel.eventloop.ChannelPromise;

import java.net.SocketAddress;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author shangruiwei
 * @date 2023/4/1 17:16
 */
public class ChannelHandlerMask {

    static final int MASK_EXCEPTION_CAUGHT = 1;
    static final int MASK_CHANNEL_REGISTERED = 1 << 1;
    static final int MASK_CHANNEL_UNREGISTERED = 1 << 2;
    static final int MASK_CHANNEL_ACTIVE = 1 << 3;
    static final int MASK_CHANNEL_INACTIVE = 1 << 4;
    static final int MASK_CHANNEL_READ = 1 << 5;
    static final int MASK_CHANNEL_READ_COMPLETE = 1 << 6;
    static final int MASK_USER_EVENT_TRIGGERED = 1 << 7;
    static final int MASK_CHANNEL_WRITABILITY_CHANGED = 1 << 8;
    static final int MASK_BIND = 1 << 9;
    static final int MASK_CONNECT = 1 << 10;
    static final int MASK_DISCONNECT = 1 << 11;
    static final int MASK_CLOSE = 1 << 12;
    static final int MASK_DEREGISTER = 1 << 13;
    static final int MASK_READ = 1 << 14;
    static final int MASK_WRITE = 1 << 15;
    static final int MASK_FLUSH = 1 << 16;

    static final int MASK_ONLY_INBOUND = MASK_CHANNEL_REGISTERED |
            MASK_CHANNEL_UNREGISTERED | MASK_CHANNEL_ACTIVE | MASK_CHANNEL_INACTIVE | MASK_CHANNEL_READ |
            MASK_CHANNEL_READ_COMPLETE | MASK_USER_EVENT_TRIGGERED | MASK_CHANNEL_WRITABILITY_CHANGED;
    private static final int MASK_ALL_INBOUND = MASK_EXCEPTION_CAUGHT | MASK_ONLY_INBOUND;
    static final int MASK_ONLY_OUTBOUND = MASK_BIND | MASK_CONNECT | MASK_DISCONNECT |
            MASK_CLOSE | MASK_DEREGISTER | MASK_READ | MASK_WRITE | MASK_FLUSH;
    private static final int MASK_ALL_OUTBOUND = MASK_EXCEPTION_CAUGHT | MASK_ONLY_OUTBOUND;

    static int mask(Class<? extends ChannelHandler> clazz) {
        return mask0(clazz);
    }

    private static int mask0(Class<? extends ChannelHandler> handlerType) {
        int mask = MASK_EXCEPTION_CAUGHT;
        try {
            if (ChannelInboundHandler.class.isAssignableFrom(handlerType)) {
                mask |= MASK_ALL_INBOUND;
            }

            if (ChannelOutboundHandler.class.isAssignableFrom(handlerType)) {
                mask |= MASK_ALL_OUTBOUND;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mask;
    }

}
