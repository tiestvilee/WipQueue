package org.wipqueue.consumerthread;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wipqueue.WipConsumer;
import org.wipqueue.WipConsumerFactory;
import org.wipqueue.queue.WipQueueImpl;
import org.wipqueue.queue.WipQueueThread;
import org.wipqueue.queue.WipQueueThreadFactory;
import org.wipqueue.WipStrategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2012
 * Time: 16:45
 */
public class ThrottledConsumerThreadFactoryTest {

//    private WipConsumerFactory<String, String> consumerFactory = new WipConsumerFactory<String, String>() {
//        public WipConsumer newConsumer() {
//            return new WipConsumer<String, String>() {
//                public String consume(String workItem) {
//                    return null;  //To change body of implemented methods use File | Settings | File Templates.
//                }
//            };
//        }
//    };
    @Mock
    private WipStrategy strategy;
    @Mock
    private WipQueueImpl<String, String> queue;
    @Mock
    private WipQueueThreadFactory<String, String> wipQueueThreadFactory;
    @Mock
    private WipQueueThread<String, String> wipQueueThread;



    private WipConsumer<String,String> consumer = new WipConsumer<String, String>() {
        public String consume(String workItem) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    private WipConsumerFactory<String, String> consumerFactory = new WipConsumerFactory<String, String>() {
        public WipConsumer<String, String> newConsumer() {
            return consumer;
        }
    };

    @Before
    public void setup() {
        initMocks(this);

        given(wipQueueThreadFactory.newAndRunningWipQueueThread(strategy, queue, consumer)).willReturn(wipQueueThread);
    }

    @Test
    public void shouldCreateAndRunABunchOfConsumers() {
        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 0, 3);

        factory.startConsumers(queue, strategy, 3);

        verify(wipQueueThreadFactory, times(3)).newAndRunningWipQueueThread(strategy, queue, consumer);
        assertThat(factory.consumerCount(), is(3));
    }

    @Test
    public void shouldCreateAndRunANewConsumer() {
        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 0, 3);

        assertThat(factory.newConsumer(queue, strategy), is(true));

        verify(wipQueueThreadFactory).newAndRunningWipQueueThread(strategy, queue, consumer);
        assertThat(factory.consumerCount(), is(1));
    }

    @Test
    public void shouldNotCreateMoreThanMaximumConsumers() {
        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 0, 3);
        factory.newConsumer(queue, strategy);
        factory.newConsumer(queue, strategy);
        factory.newConsumer(queue, strategy);

        assertThat(factory.newConsumer(queue, strategy), is(false));
        assertThat(factory.consumerCount(), is(3));
    }

    @Test
    public void shouldNotCreateMoreThanMaximumConsumersInBulk() {
        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 0, 3);

        assertThat(factory.startConsumers(queue, strategy, 5), is(3));
        assertThat(factory.consumerCount(), is(3));
    }

//    @Test
//    public void givenAConsumerIsCurrentlyBeingCreatedWhenNewConsumerIsCalledThenNothingShouldHappen() {
//        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 1000);
//
//        given(wipQueueThread.isStarted()).willReturn(false); // will never start
//        factory.newConsumer(queue, strategy);
//
//        factory.newConsumer(queue, strategy);
//
//        verify(wipQueueThreadFactory, times(1)).newAndRunningWipQueueThread(strategy, queue, consumer);
//        assertThat(factory.consumerCount(), is(1));
//    }

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

    @Test
    public void shouldStopClient() {
        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 0, 3);

        given(wipQueueThread.isStarted()).willReturn(true); // will immediately start
        factory.newConsumer(queue, strategy);

        factory.stopConsumer();

        verify(wipQueueThread).stop();
        assertThat(factory.consumerCount(), is(0));
    }

    @Test
    public void shouldNotStopClientIfAtMinimum() {
        WipConsumerThreadFactoryDefault<String, String> factory = new WipConsumerThreadFactoryDefault<String, String>(consumerFactory, wipQueueThreadFactory, 3, 6);

        factory.startConsumers(queue, strategy, 3);

        assertThat(factory.stopConsumer(), is(false));
        assertThat(factory.consumerCount(), is(3));
    }

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
