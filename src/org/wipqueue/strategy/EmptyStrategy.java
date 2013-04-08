package org.wipqueue.strategy;

import org.wipqueue.WipStrategy;
import org.wipqueue.queue.WorkItem;
import org.wipqueue.queue.WipQueueImpl;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 22:27
 */
public class EmptyStrategy implements WipStrategy {
    public void addingWorkItem(WipQueueImpl queue, int queueSize) {
        // do nothing
    }

    public void itemAboutToBeConsumed(WorkItem workItem) {
        // do nothing
    }

    public void itemConsumedSuccessfully(WorkItem workItem) {
        // do nothing
    }

    public void itemConsumedWithError(Exception thrownException, WorkItem workItem) {
        // do nothing
    }

    public void itemAddedButQueueFull() {
        // do nothing
    }
}
