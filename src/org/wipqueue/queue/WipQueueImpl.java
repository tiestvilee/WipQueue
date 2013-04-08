package org.wipqueue.queue;

import org.wipqueue.consumerthread.WipConsumerThreadFactory;
import org.wipqueue.WipStrategy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 * Time: 11:14
 */
public class WipQueueImpl<T, R> {
    private final WipConsumerThreadFactory<T, R> consumerThreadFactory;
    private final WipStrategy wipStrategy;
    private final BlockingQueue<WorkItem<T, R>> queue;

    public WipQueueImpl(WipConsumerThreadFactory<T, R> consumerThreadFactory, WipStrategy wipStrategy, int maxQueuedItems, int startingConsumerCount) {
        this.consumerThreadFactory = consumerThreadFactory;
        this.wipStrategy = wipStrategy;
        queue = new ArrayBlockingQueue<WorkItem<T, R>>(maxQueuedItems);

        consumerThreadFactory.startConsumers(this, wipStrategy, startingConsumerCount);
    }

    public WipQueueImpl<T,R> waitToStart() throws InterruptedException {
        consumerThreadFactory.waitToStart();
        return this;
    }


    public Future<R> put(T workItem) {
        wipStrategy.addingWorkItem(this, queue.size());
        WipFuture<R> future = new WipFuture<R>();
        boolean wasAdded;
        try {
            wasAdded = queue.offer(new WorkItem<T, R>(workItem, future), 0, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            wasAdded = false;
        }

        if( ! wasAdded) {
            wipStrategy.itemAddedButQueueFull();
        }

        return future;
    }

    public WorkItem<T,R> poll(int time, TimeUnit unit) throws InterruptedException {
        return queue.poll(time, unit);
    }

    public int size() {
        return queue.size();
    }

    public void createConsumer() {
        consumerThreadFactory.newConsumer(this, wipStrategy);
    }

    public void killConsumer() {
        consumerThreadFactory.stopConsumer();
    }

    public int runningConsumerCount() {
        return consumerThreadFactory.consumerCount();
    }
}
