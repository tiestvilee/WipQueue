package org.wipqueue.queue;

import org.wipqueue.WipConsumer;
import org.wipqueue.WipStrategy;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2012
 * Time: 22:57
 */
public class WipQueueThreadFactory<T,R> {

    private int pollTimeInMillis;

    public WipQueueThreadFactory() {
        this(500);
    }

    public WipQueueThreadFactory(int pollTimeInMillis) {
        this.pollTimeInMillis = pollTimeInMillis;
    }

    public WipQueueThread<T,R> newAndRunningWipQueueThread(WipStrategy strategy, WipQueueImpl<T, R> queue, WipConsumer<T, R> consumer) {
        WipQueueThread<T,R> queueThread = new WipQueueThread<T,R>(strategy, queue, consumer, pollTimeInMillis);
        new Thread(queueThread).start();
        return queueThread;
    }
}
