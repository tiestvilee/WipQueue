package org.wipqueue.queue;

import org.junit.Before;
import org.junit.Test;
import org.wipqueue.WipConsumer;
import org.wipqueue.WipConsumerFactory;
import org.wipqueue.consumerthread.WipConsumerThreadFactory;
import org.wipqueue.consumerthread.WipConsumerThreadFactoryDefault;
import org.wipqueue.WipStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 */
public class WipQueueImplTest {

    private ConsumerStub consumer;
    private LockStepConsumer lockStepConsumer;

    @Mock
    private WipStrategy wipStrategy;
    @Mock
    private WipConsumerThreadFactory consumerThreadFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        consumer = new ConsumerStub();
        lockStepConsumer = new LockStepConsumer();
    }

    @Test
    public void givenQueueWhenAddingWorkItemThenWipStrategyIsInvoked() throws Exception {
        WipQueueImpl<String, String> queue = new WipQueueImpl<String, String>(new WipConsumerThreadFactoryDefault(new ConsumerFactoryStub(lockStepConsumer), new WipQueueThreadFactory<String, String>(), 0, 3), wipStrategy, 100, 1).waitToStart();

        queue.put("slow job"); // is consumed
        lockStepConsumer.startedConsuming.await();

        queue.put("first item");

        queue.put("second item");

        verify(wipStrategy, times(2)).addingWorkItem(queue, 0);
        verify(wipStrategy).addingWorkItem(queue, 1);
    }

    @Test
    public void givenQueueIsFullWhenAddingWorkItemThenWipStrategyIsInvoked() throws Exception {
        WipQueueImpl<String, String> queue = new WipQueueImpl<String, String>(new WipConsumerThreadFactoryDefault(new ConsumerFactoryStub(lockStepConsumer), new WipQueueThreadFactory<String, String>(), 0, 3), wipStrategy, 5, 1).waitToStart();

        queue.put("first item");
        lockStepConsumer.startedConsuming.await();

        queue.put("job 1"); queue.put("job 2"); queue.put("job 3"); queue.put("job 4"); queue.put("job 5");

        queue.put("too many jobs");

        verify(wipStrategy).itemAddedButQueueFull();

        assertThat(queue.size(), is(5));
    }

    @Test
    public void delegateCreatingConsumerToFactory() throws Exception {
        WipQueueImpl<String, String> queue = new WipQueueImpl<String, String>(consumerThreadFactory, wipStrategy, 5, 1).waitToStart();

        queue.createConsumer();

        verify(consumerThreadFactory).newConsumer(queue, wipStrategy);
    }

    @Test
    public void delegateKillingConsumerToFactory() throws Exception {
        WipQueueImpl<String, String> queue = new WipQueueImpl<String, String>(consumerThreadFactory, wipStrategy, 5, 1).waitToStart();

        queue.killConsumer();

        verify(consumerThreadFactory).stopConsumer();
    }

    @Test
    public void delegateConsumerCountToFactory() throws Exception {
        WipQueueImpl<String, String> queue = new WipQueueImpl<String, String>(consumerThreadFactory, wipStrategy, 5, 1).waitToStart();
        given(consumerThreadFactory.consumerCount()).willReturn(678);

        assertThat(queue.runningConsumerCount(), is(678));
    }


    public static class ConsumerStub implements WipConsumer<String, String> {
        public final List<String> received = new ArrayList<String>();
        private CountDownLatch isBusy = new CountDownLatch(0);
        private CountDownLatch waitFor = new CountDownLatch(0);

        public String consume(String workItem) {
            try {
                isBusy.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            received.add(workItem);
            waitFor.countDown();
            return workItem + "-done";
        }

        public ConsumerStub nextIsSlow() {
            isBusy = new CountDownLatch(1);
            return this;
        }

        public void freeUpAndWaitFor(int howMany) throws InterruptedException {
            waitFor = new CountDownLatch(howMany);
            isBusy.countDown();
            waitFor.await();
        }
    }

    public static class LockStepConsumer implements WipConsumer<String, String> {
        CountDownLatch waitFor = new CountDownLatch(1);
        public CountDownLatch startedConsuming = new CountDownLatch(1);
        Set<Thread> seenThreads = new HashSet<Thread>();
        boolean nextWithError = false;

        public String consume(String workItem) {
            try {
                seenThreads.add(Thread.currentThread());
                startedConsuming.countDown();
                waitFor.await();
                waitFor = new CountDownLatch(1);
            } catch (InterruptedException e) {
                // don't care
            }
            if(nextWithError) {
                throw new RuntimeException("an error");
            }
            return workItem + "-done2";
        }

        public void next() throws Exception {
            waitFor.countDown();
            Thread.sleep(10);
        }

        public void nextWithError() throws Exception {
            nextWithError = true;
            next();
        }
    }

    public static class ConsumerFactoryStub implements WipConsumerFactory {
        private final WipConsumer<String, String> consumer;
        private int consumerCount = 0;

        public ConsumerFactoryStub(WipConsumer<String, String> consumer) {
            this.consumer = consumer;
        }

        public WipConsumer newConsumer() {
            consumerCount++;
            return consumer;
        }

        public void killConsumer() {
            consumerCount--;
        }

        public int runningConsumerCount() {
            return consumerCount;
        }
    }
}
