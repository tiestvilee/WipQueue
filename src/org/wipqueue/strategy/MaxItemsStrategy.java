package org.wipqueue.strategy;

import org.wipqueue.exception.QueueFullException;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 16:32
 */
public class MaxItemsStrategy extends EmptyStrategy {
    public void itemAddedButQueueFull() {
        throw new QueueFullException("Work Item not added as the queue was full");
    }
}
