package srw.simple.netty.channel.eventloop;

import srw.simple.netty.channel.AbstractChannel;
import srw.simple.netty.channel.NioSocketChannel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author shangruiwei
 * @date 2023/3/19 18:05
 */
public class NioEventLoop extends SingleThreadEventLoop {

    /**
     * The NIO {@link Selector}.
     */
    private Selector selector;

    private final SelectorProvider provider;

    NioEventLoop(NioEventLoopGroup parent) {
        super(parent);
        this.provider = SelectorProvider.provider();
        try {
            selector = provider.openSelector();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Selector selector() {
        return selector;
    }

    @Override
    protected void run() {
        for (; ; ) {
            try {
                // 简单实现，有任务就先执行任务，任务执行完了，再处理就绪事件
                if (hasTasks()) {
                    runAllTasks();
                }
                // 阻塞到有就绪事件了
//                selector.select(3000);
                selector.select();
                processSelectedKeysPlain(selector.selectedKeys());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        // check if the set is empty and if so just return to not create garbage by
        // creating a new Iterator every time even if there is nothing to process.
        // See https://github.com/netty/netty/issues/597
        if (selectedKeys.isEmpty()) {
            return;
        }

        Iterator<SelectionKey> i = selectedKeys.iterator();
        for (; ; ) {
            final SelectionKey k = i.next();
            final Object a = k.attachment();
            i.remove();

            processSelectedKey(k, (AbstractChannel) a);

            if (!i.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey k, AbstractChannel ch) {
        final AbstractChannel.AbstractUnsafe unsafe = (AbstractChannel.AbstractUnsafe) ch.unsafe();

        try {
            int readyOps = k.readyOps();
            // We first need to call finishConnect() before try to trigger a read(...) or write(...) as otherwise
            // the NIO JDK channel implementation may throw a NotYetConnectedException.
            if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
                // remove OP_CONNECT as otherwise Selector.select(..) will always return without blocking
                // See https://github.com/netty/netty/issues/924
                int ops = k.interestOps();
                ops &= ~SelectionKey.OP_CONNECT;
                k.interestOps(ops);

                unsafe.finishConnect();
            }

            // Process OP_WRITE first as we may be able to write some queued buffers and so free memory.
            if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                // Call forceFlush which will also take care of clear the OP_WRITE once there is nothing left to write
                unsafe.forceFlush();
            }

            // Also check for readOps of 0 to workaround possible JDK bug which may otherwise lead
            // to a spin loop
            if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
                unsafe.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
