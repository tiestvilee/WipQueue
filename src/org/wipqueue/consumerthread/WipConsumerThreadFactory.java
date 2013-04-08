package org.wipqueue.consumerthread;

import org.wipqueue.WipStrategy;
import org.wipqueue.queue.WipQueueImpl;

/**
 * This factory starts a new consumer in a new Thread. Normally you wouldn't touch this, but it
 * is possible to implement your own versions
 *
 * copyright Tiest Vilee 2012
 * Date: 30/03/2012
 * Time: 19:25
 */
public interface WipConsumerThreadFactory<T,R> {
    /**
     * Starts a batch of consumers
     * @param   queue   the queue to the new consumers should listen on
     * @param   wipStrategy send and strategy events to this wipStrategy
     * @param   startingConsumerCount how many consumers to start with
     */
    int startConsumers(WipQueueImpl queue, WipStrategy wipStrategy, int startingConsumerCount);

    /**
     * waits for any threads to start
     */
    void waitToStart() throws InterruptedException;

    /**
     * creates a new consumer and its thread
     * @param   queue   The queue for the consumer to listen on
     * @param   wipStrategy   send any strategy events to this wipStrategy
     */
    boolean newConsumer(WipQueueImpl queue, WipStrategy wipStrategy);

    /**
     * stops a consumer (after it has finished processing its current payload
     */
    boolean stopConsumer();

    /**
     * number of consumers currently created - running or not
     */
    int consumerCount();

}
