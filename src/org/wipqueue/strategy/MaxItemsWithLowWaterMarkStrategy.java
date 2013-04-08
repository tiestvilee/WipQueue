package org.wipqueue.strategy;

import org.wipqueue.queue.WipQueueImpl;
import org.wipqueue.exception.QueueAboveLwmException;
import org.wipqueue.exception.QueueFullException;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 22:30
 */
public class MaxItemsWithLowWaterMarkStrategy extends EmptyStrategy {

    private final int lowWaterMark;
    private boolean inFullState = false; // 'eventual consistency' - not thread-safe because not Terribly important (I hope)

    public MaxItemsWithLowWaterMarkStrategy(int lowWaterMark) {
        this.lowWaterMark = lowWaterMark;
    }

    @Override
    public void addingWorkItem(WipQueueImpl queue, int queueSize) {
        if(inFullState) {
            if(queueSize > lowWaterMark) {
                throw new QueueAboveLwmException("WorkItem not added as queue is full");
            } else {
                inFullState = false;
            }
        }
    }

    @Override
    public void itemAddedButQueueFull() {
        inFullState = true;
        throw new QueueFullException("WorkItem not added as queue is full");
    }
}
