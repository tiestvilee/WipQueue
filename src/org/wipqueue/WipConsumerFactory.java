package org.wipqueue;

/**
 * This interface must be implemented to create consumers for the queue. They
 * are then used, maybe with the WipQueueBuilder, to create a WipQueue.
 *
 * copyright Tiest Vilee 2012
 * Date: 30/03/2012
 * Time: 19:25
 */
public interface WipConsumerFactory<T,R> {
    /**
     * Creates a consumer for consuming work items from the queue
     */
    WipConsumer<T,R> newConsumer();
}
