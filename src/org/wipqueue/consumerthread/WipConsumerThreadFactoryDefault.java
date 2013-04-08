package org.wipqueue.consumerthread;

import org.wipqueue.WipConsumerFactory;
import org.wipqueue.queue.WipQueueImpl;
import org.wipqueue.queue.WipQueueThread;
import org.wipqueue.queue.WipQueueThreadFactory;
import org.wipqueue.WipStrategy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * copyright Tiest Vilee 2012
 * Date: 01/04/2012
 * Time: 00:22
 */
public class WipConsumerThreadFactoryDefault<T, R> implements WipConsumerThreadFactory<T, R> {

    private final BlockingQueue<WipQueueThread<T, R>> queueThreads;
    private final WipConsumerFactory consumerFactory;
    private final WipQueueThreadFactory<T, R> wipQueueThreadFactory;
    private final int minimumConsumerCount;

    public WipConsumerThreadFactoryDefault(WipConsumerFactory consumerFactory, WipQueueThreadFactory<T, R> wipQueueThreadFactory, int min, int max) {
        this.consumerFactory = consumerFactory;
        this.wipQueueThreadFactory = wipQueueThreadFactory;
        this.queueThreads = new ArrayBlockingQueue<WipQueueThread<T, R>>(max);
        this.minimumConsumerCount = min;
    }

    public int startConsumers(WipQueueImpl queue, WipStrategy wipStrategy, int startingConsumerCount) {
        for(int i=0; i<startingConsumerCount; i++) {
            if( ! createConsumerThread(queue, wipStrategy)) {
                return i;
            }
        }
        return startingConsumerCount;
    }

    private boolean createConsumerThread(WipQueueImpl queue, WipStrategy wipStrategy) {
        try {
            return queueThreads.offer(
                wipQueueThreadFactory.newAndRunningWipQueueThread(wipStrategy, queue, consumerFactory.newConsumer()),
                0,
                TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void waitToStart() throws InterruptedException {
        for(WipQueueThread queueThread : queueThreads) {
            queueThread.waitUntilStarted();
        }
    }

    public boolean newConsumer(WipQueueImpl queue, WipStrategy wipStrategy) {
        return createConsumerThread(queue, wipStrategy);
    }

    Object stopLock = new Object();
    public boolean stopConsumer() {
        synchronized (stopLock) { // we don't want to accidentally kill multiple consumers, so have to lock down this behaviour
            if(queueThreads.size() <= minimumConsumerCount) {
                return false;
            }
            try {
                WipQueueThread<T, R> queueThread = queueThreads.poll(0, TimeUnit.MILLISECONDS);
                if(queueThread == null) {
                    return false;
                }
                queueThread.stop();
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        }
    }

    public int consumerCount() {
        return queueThreads.size();
    }
}
