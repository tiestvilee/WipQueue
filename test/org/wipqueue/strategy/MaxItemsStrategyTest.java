package org.wipqueue.strategy;

import org.junit.Test;
import org.wipqueue.WipStrategy;
import org.wipqueue.exception.QueueFullException;

import static org.junit.Assert.fail;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 16:33
 */
public class MaxItemsStrategyTest {
    @Test
    public void shouldDoNothingForMostEvents() {
        WipStrategy strategy = new MaxItemsStrategy();

        strategy.addingWorkItem(null, 0);
        strategy.itemConsumedSuccessfully(null);
        strategy.itemConsumedWithError(new RuntimeException("my exception"), null);
    }

    @Test
    public void shouldThrowExceptionIfQueueFull() {
        WipStrategy strategy = new MaxItemsStrategy();

        try {
            strategy.itemAddedButQueueFull();
            fail("Should have thrown a WorkItemRejectedException");
        } catch (QueueFullException e) {
            // ???
        }
    }
}
