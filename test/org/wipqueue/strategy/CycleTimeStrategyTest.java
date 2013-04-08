package org.wipqueue.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wipqueue.WipStrategy;
import org.wipqueue.queue.WorkItem;
import org.wipqueue.exception.CycleTimeTooLongException;
import org.wipqueue.queue.WipQueueImpl;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 16:33
 */
public class CycleTimeStrategyTest {

    @Mock
    WipQueueImpl queue;

    @Mock
    private WorkItem workItem;

    @Before
    public void setUp() {
        initMocks(this);

        given(queue.runningConsumerCount()).willReturn(4);
    }

    @Test
    public void shouldDoNothingForSomeEvents() {
        WipStrategy strategy = new CycleTimeStrategy(1000);

        strategy.itemAddedButQueueFull();
    }

    @Test
    public void givenMostRecentlyConsumedItemTookLongerThanMaximumCycleTimeThenRejectNextAddedItems() {
        WipStrategy strategy = new CycleTimeStrategy(1000);
        given(workItem.totalTime()).willReturn(1010L);

        strategy.itemConsumedSuccessfully(workItem);

        try {
            strategy.addingWorkItem(queue, 10);
            fail("Should have thrown a CycleTimeTooLongException");
        } catch (CycleTimeTooLongException e) {
            // ???
        }
    }

    @Test
    public void givenMostRecentlyConsumedItemTookLessThanMaximumCycleTimeThenAcceptNextItem() {
        WipStrategy strategy = new CycleTimeStrategy(1000);
        given(workItem.totalTime()).willReturn(990L);

        strategy.itemConsumedSuccessfully(workItem);

        strategy.addingWorkItem(queue, 10);
    }

    @Test
    public void givenMostRecentlyConsumedItemWithErrorTookLongerThanMaximumCycleTimeThenRejectNextAddedItems() {
        WipStrategy strategy = new CycleTimeStrategy(1000);
        given(workItem.totalTime()).willReturn(1010L);

        strategy.itemConsumedWithError(new RuntimeException(), workItem);

        try {
            strategy.addingWorkItem(queue, 10);
            fail("Should have thrown a CycleTimeTooLongException");
        } catch (CycleTimeTooLongException e) {
            // ???
        }
    }

    @Test
    public void givenMostRecentlyConsumedItemWithErrorTookLessThanMaximumCycleTimeThenAcceptNextItem() {
        WipStrategy strategy = new CycleTimeStrategy(1000);
        given(workItem.totalTime()).willReturn(990L);

        strategy.itemConsumedWithError(new RuntimeException(), workItem);

        strategy.addingWorkItem(queue, 10);
    }


}
