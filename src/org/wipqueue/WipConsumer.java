package org.wipqueue;

/**
 * This is the actual thing that does the consuming of the workitems from
 * the queue.
 *
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 * Time: 11:15
 */
public interface WipConsumer<T, R> {
    /**
     * Consume an item of work and return a result
     *
     * @param workItem the item of work to perform
     * @return the result of processing the workItem
     */
    R consume(T workItem) throws Exception;
}
