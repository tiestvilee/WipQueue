package org.wipqueue.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wipqueue.WipStrategy;
import org.wipqueue.queue.WipQueueImpl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 16:33
 */
public class CreateConsumerStrategyTest {

    @Mock
    WipQueueImpl queue;

    @Before
    public void setUp() {
        initMocks(this);

        given(queue.runningConsumerCount()).willReturn(4);
    }

    @Test
    public void shouldDoNothingForSomeEvents() {
        WipStrategy strategy = new CreateConsumerStrategy(3, 5, 10, 30);

        strategy.itemAddedButQueueFull();
    }

    @Test
    public void givenQueueSizeBelowLowWaterMarkThenKillClient() {
        WipStrategy strategy = new CreateConsumerStrategy(3, 5, 10, 30);

        strategy.addingWorkItem(queue, 10);

        verify(queue, never()).createConsumer();
        verify(queue).killConsumer();
    }

    @Test
    public void whenQueueSizeAboveLowWaterMarkAndBelowHighWaterMarkThenDoNothing() {
        WipStrategy strategy = new CreateConsumerStrategy(3, 5, 10, 30);

        strategy.addingWorkItem(queue, 11);
        strategy.addingWorkItem(queue, 30);

        verify(queue, never()).createConsumer();
        verify(queue, never()).killConsumer();
    }

    @Test
    public void whenQueueSizeAboveHighWaterMarkThenCreateNewConsumer() {
        WipStrategy strategy = new CreateConsumerStrategy(3, 5, 10, 30);

        strategy.addingWorkItem(queue, 31);

        verify(queue).createConsumer();
        verify(queue, never()).killConsumer();
    }

    @Test
    public void givenMaxClientsWhenQueueSizeAboveHighWaterMarkThenDoNothing() {
        WipStrategy strategy = new CreateConsumerStrategy(3, 5, 10, 30);

        given(queue.runningConsumerCount()).willReturn(5);

        strategy.addingWorkItem(queue, 31);

        verify(queue, never()).createConsumer();
        verify(queue, never()).killConsumer();
    }

    @Test
    public void givenMinClientsWhenQueueSizeBelowLowWaterMarkThenDoNothing() {
        WipStrategy strategy = new CreateConsumerStrategy(3, 5, 10, 30);

        given(queue.runningConsumerCount()).willReturn(3);

        strategy.addingWorkItem(queue, 10);

        verify(queue, never()).createConsumer();
        verify(queue, never()).killConsumer();
    }

}
