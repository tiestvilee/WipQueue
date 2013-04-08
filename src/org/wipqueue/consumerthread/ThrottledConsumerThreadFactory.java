package org.wipqueue.consumerthread;

import org.wipqueue.queue.WipQueueImpl;
import org.wipqueue.WipStrategy;

/**
 * copyright Tiest Vilee 2012
 * Date: 01/04/2012
 * Time: 00:22
 */
public class ThrottledConsumerThreadFactory<T, R> implements WipConsumerThreadFactory<T, R> {

    private final WipConsumerThreadFactory<T, R> delegate;
    private final int delayInMillis;
    private long nextActionStartsAfter = 0;

    public ThrottledConsumerThreadFactory(WipConsumerThreadFactory<T,R> delegate, int delayInMillis) {
        this.delegate = delegate;
        this.delayInMillis = delayInMillis;
    }

    public int startConsumers(WipQueueImpl queue, WipStrategy wipStrategy, int startingConsumerCount) {
        if(canProceed()) {
            return delegate.startConsumers(queue, wipStrategy, startingConsumerCount);
        }
        return 0;
    }

    public void waitToStart() throws InterruptedException {
        delegate.waitToStart();
    }

    public boolean newConsumer(WipQueueImpl queue, WipStrategy wipStrategy) {
        if(canProceed()) {
            return delegate.newConsumer(queue, wipStrategy);
        }
        return false;
    }

    private synchronized boolean canProceed() {
        if(System.currentTimeMillis() < nextActionStartsAfter) {
            return false;
        }
        nextActionStartsAfter = System.currentTimeMillis() + delayInMillis;
        return true;
    }

    private boolean anyConsumersAreWaitingToStart() {
        return false;
    }

    public boolean stopConsumer() {
        if(canProceed()) {
            return delegate.stopConsumer();
        }
        return false;
    }

    public int consumerCount() {
        return delegate.consumerCount();
    }
}
