package org.wipqueue.queue;

import org.wipqueue.WipConsumer;
import org.wipqueue.WipStrategy;

import java.util.concurrent.TimeUnit;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 * Time: 11:40
 */
public class WipQueueThread<T,R> extends WipRunner {
    private final WipQueueImpl<T,R> queue;
    private final WipConsumer<T,R> consumer;
    private final int pollTimeMillis;
    private final WipStrategy strategy;

    public WipQueueThread(WipStrategy strategy, WipQueueImpl<T,R> queue, WipConsumer<T,R> consumer) {
        this(strategy, queue, consumer, 500);
    }

    public WipQueueThread(WipStrategy strategy, WipQueueImpl<T,R> queue, WipConsumer<T,R> consumer, int pollTimeMillis) {
        this.strategy = strategy;
        this.queue = queue;
        this.consumer = consumer;
        this.pollTimeMillis = pollTimeMillis;
    }

    protected void doThisRepeatedly() {
        try {
            WorkItem<T,R> workItem = queue.poll(pollTimeMillis, TimeUnit.MILLISECONDS);
            if(workItem == null || workItem.future.isCancelled()) {
                return;
            }
            try {
                workItem.start();
                strategy.itemAboutToBeConsumed(workItem);
                R result = consumer.consume(workItem.workItem);
                workItem.future.setResult(result);
                workItem.end();
                strategy.itemConsumedSuccessfully(workItem);
            } catch (Exception e) {
                workItem.future.throwError(e);
                workItem.end();
                strategy.itemConsumedWithError(e, workItem);
            }
        } catch (InterruptedException e) {
            /// not sure yet...
        }
    }

}
