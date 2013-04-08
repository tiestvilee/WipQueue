package org.wipqueue.consumerthread;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wipqueue.queue.WipQueueImpl;
import org.wipqueue.WipStrategy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2012
 * Time: 16:45
 */
public class WipConsumerThreadFactoryDefaultTest {

    @Mock
    private WipStrategy strategy;
    @Mock
    private WipQueueImpl<String, String> queue;
    @Mock
    private WipConsumerThreadFactory<String,String> consumerThreadFactory;


    ThrottledConsumerThreadFactory<String, String> factory;

    @Before
    public void setup() {
        initMocks(this);
        factory = new ThrottledConsumerThreadFactory<String, String>(consumerThreadFactory, 10);

        given(consumerThreadFactory.newConsumer((WipQueueImpl) any(), (WipStrategy) any())).willReturn(true);
    }

    @Test
    public void startConsumersShouldDelegate() {

        factory.startConsumers(queue, strategy, 3);

        verify(consumerThreadFactory).startConsumers(queue, strategy, 3);
    }

    @Test
    public void newConsumerShouldDelegate() {

        factory.newConsumer(queue, strategy);

        verify(consumerThreadFactory).newConsumer(queue, strategy);
    }

    @Test
    public void consumerCountShouldDelegate() {

        factory.consumerCount();

        verify(consumerThreadFactory).consumerCount();
    }

    @Test
    public void stopConsumerShouldDelegate() {

        factory.stopConsumer();

        verify(consumerThreadFactory).stopConsumer();
    }

    @Test
    public void waitToStartShouldDelegate() throws InterruptedException {

        factory.waitToStart();

        verify(consumerThreadFactory).waitToStart();
    }


    @Test
    public void givenAConsumerIsCurrentlyBeingCreatedWhenNewConsumerIsCalledThenNothingShouldHappen() {
        // given
        factory.newConsumer(queue, strategy);

        // when
        assertThat(factory.newConsumer(null, null), is(false));

        // then
        verify(consumerThreadFactory, times(1)).newConsumer(queue, strategy);
        verify(consumerThreadFactory, never()).newConsumer(null, null);
    }

    @Test
    public void givenManyConsumersAreCurrentlyBeingCreatedWhenNewConsumerIsCalledThenNothingShouldHappen() {
        // given
        factory.startConsumers(queue, strategy, 3);

        // when
        assertThat(factory.newConsumer(null, null), is(false));

        // then
        verify(consumerThreadFactory, times(1)).startConsumers(queue, strategy, 3);
        verify(consumerThreadFactory, never()).newConsumer(null, null);
    }

    @Test
    public void givenAConsumerHasRecentlyBeenCreatedWhenSufficientTimeHasPassedThenANewConsumerCanBeCreated() throws Exception {
        // given
        factory.newConsumer(queue, strategy);

        // when
        Thread.sleep(20);

        // then
        assertThat(factory.newConsumer(null, null), is(true));

        // then
        verify(consumerThreadFactory, times(1)).newConsumer(queue, strategy);
        verify(consumerThreadFactory, times(1)).newConsumer(null, null);
    }
//    @Test
//    public void givenManyConsumersAreCurrentlyBeingCreatedWhenNewConsumerIsCalledThenNothingShouldHappen() {
//        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 1000);
//
//        given(wipQueueThread.isStarted()).willReturn(false); // will never start
//        factory.startConsumers(queue, strategy, 5);
//
//        factory.newConsumer(queue, strategy);
//
//        verify(wipQueueThreadFactory, times(5)).newAndRunningWipQueueThread(strategy, queue, consumer);
//        assertThat(factory.consumerCount(), is(5));
//    }

//    @Test
//    public void givenAConsumerHasRecentlyBeenCreatedWhenNewConsumerIsCalledThenNothingShouldHappen() {
//        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 1000);
//
//        given(wipQueueThread.isStarted()).willReturn(true); // will immediately start
//        factory.newConsumer(queue, strategy);
//
//        factory.newConsumer(queue, strategy);
//
//        verify(wipQueueThreadFactory, times(1)).newAndRunningWipQueueThread(strategy, queue, consumer);
//        assertThat(factory.consumerCount(), is(1));
//    }


//    @Test
//    public void givenAConsumerHasRecentlyBeenStoppedWhenNewConsumerIsCalledThenNothingShouldHappen() throws InterruptedException {
//        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 5);
//
//        given(wipQueueThread.isStarted()).willReturn(true); // will immediately start
//        factory.newConsumer(queue, strategy);
//        Thread.sleep(10);
//
//        factory.stopConsumer();
//
//        // when
//        factory.newConsumer(queue, strategy);
//
//        verify(wipQueueThreadFactory, times(1)).newAndRunningWipQueueThread(strategy, queue, consumer); // only the first new
//        assertThat(factory.consumerCount(), is(0)); // the first was removed, but the second was added too soon after the stop
//    }

    @Test
    public void shouldWaitForConsumersToStart() {
        // pain
    }


}
