package org.wipqueue.strategy;

import org.junit.Test;
import org.wipqueue.WipStrategy;
import org.wipqueue.exception.QueueAboveLwmException;
import org.wipqueue.exception.QueueFullException;

import static org.junit.Assert.fail;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 16:33
 */
public class MaxItemsWithLowWaterMarkStrategyTest {
    @Test
    public void shouldDoNothingForConsumerEvents() {
        WipStrategy strategy = new MaxItemsWithLowWaterMarkStrategy(5);

        strategy.itemConsumedSuccessfully(null);
        strategy.itemConsumedWithError(new RuntimeException("my exception"), null);
    }

    @Test
    public void givenQueueIsNotFullButAboveLowWaterMarkWhenItemIsAddedThenItemIsOk() {
        WipStrategy strategy = new MaxItemsWithLowWaterMarkStrategy(5);

        strategy.addingWorkItem(null, 6);
    }

    @Test
    public void givenQueueIsFullWhenItemAddedThenRejectItem() {
        WipStrategy strategy = new MaxItemsWithLowWaterMarkStrategy(5);

        try {
            strategy.itemAddedButQueueFull();
            fail("Should have thrown a WorkItemRejectedException");
        } catch (QueueFullException e) {
            // ???
        }
    }

    @Test
    public void givenQueueHasBeenFullButStillHigherThanLowWaterMarkWhenItemAddedThenItemIsRejected() {
        WipStrategy strategy = new MaxItemsWithLowWaterMarkStrategy(5);

        try {
            strategy.itemAddedButQueueFull();
        } catch (QueueFullException e) { /* don't care */ }

        try {
            // when
            strategy.addingWorkItem(null, 6);
            fail("Should have thrown a WorkItemRejectedException");
        } catch (QueueAboveLwmException e) {
            // ???
        }
    }


    @Test
    public void givenQueueHasBeenFullButNowEqualToLowWaterMarkWhenItemAddedThenItemIsOk() {
        WipStrategy strategy = new MaxItemsWithLowWaterMarkStrategy(5);

        try {
            strategy.itemAddedButQueueFull();
        } catch (QueueFullException e) { /* don't care */ }

        // when
        strategy.addingWorkItem(null, 5);
    }

    @Test
    public void givenQueueHasBeenFullButGoneBelowLowWaterMarkAndThenBackUpALittleWhenItemAddedThenItemIsOk() {
        WipStrategy strategy = new MaxItemsWithLowWaterMarkStrategy(5);

        try {
            strategy.itemAddedButQueueFull();
        } catch (QueueFullException e) { /* don't care */ }

        strategy.addingWorkItem(null, 5);

        // when
        strategy.addingWorkItem(null, 6);
    }
}
