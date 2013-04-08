package org.wipqueue;

import org.wipqueue.queue.WorkItem;
import org.wipqueue.queue.WipQueueImpl;

/**
 * Strategies are used to interact with events from the queue.
 *
 * As events come in it is possible to react, throwing exceptions (see the exception package)
 * that the queue can use to modify its behaviour. Or the events could simply be recorded
 * for monitoring reasons.
 *
 * You may use the strategies in the strategy package, or your own.
 *
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 15:46
 */
public interface WipStrategy {
    /**
     * An item is about to be added to the queue
     * @param   queue   The queue the item is being added to - This is a WipQueueImpl and so exposes more
     *                  functionality than the normal WipQueue
     * @param   queueSize   The current size of the queue
     */
    public void addingWorkItem(WipQueueImpl queue, int queueSize);

    /**
     * an Item is about to be consumed
     * @param   workItem    The item about to be consumed
     */
    void itemAboutToBeConsumed(WorkItem workItem);

    /**
     * an Item has been successfully processed
     * @param   workItem    The item that was consumed
     */
    void itemConsumedSuccessfully(WorkItem workItem);

    /**
     * an Item has failed while being processed
     * @param   thrownException the exception that was thrown while processing the item
     * @param   workItem    The item that failed processing
     */
    void itemConsumedWithError(Exception thrownException, WorkItem workItem);

    /**
     * The queue was full so no item was added
     */
    void itemAddedButQueueFull();

}
