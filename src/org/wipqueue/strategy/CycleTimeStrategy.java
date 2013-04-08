package org.wipqueue.strategy;

import org.wipqueue.queue.WorkItem;
import org.wipqueue.exception.CycleTimeTooLongException;
import org.wipqueue.queue.WipQueueImpl;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * copyright Tiest Vilee 2012
 * Date: 08/04/2012
 * Time: 22:43
 */
public class CycleTimeStrategy extends EmptyStrategy {
    private final int maxCycleTime;
    private final AtomicBoolean cycleTimeTooLong = new AtomicBoolean(false);
    long lastCycleTime = -1;

    public CycleTimeStrategy(int maxCycleTime) {
        this.maxCycleTime = maxCycleTime;
    }

    public void addingWorkItem(WipQueueImpl queue, int queueSize) {
        if(cycleTimeTooLong.get()) {
            throw new CycleTimeTooLongException(
                String.format("Last consumed item took %s seconds to consume, but shouldn't take longer than %s", lastCycleTime, maxCycleTime));
        }
    }

    public void itemConsumedSuccessfully(WorkItem workItem) {
        testCycleTime(workItem.totalTime());
    }

    private void testCycleTime(long cycleTime) {
        if(cycleTime > maxCycleTime) {
            cycleTimeTooLong.set(true);
            lastCycleTime = cycleTime;
        }
    }

    public void itemConsumedWithError(Exception thrownException, WorkItem workItem) {
        testCycleTime(workItem.totalTime());
    }

    public void itemAddedButQueueFull() {
    }
}
