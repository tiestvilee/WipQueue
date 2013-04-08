package org.wipqueue;

import org.junit.Before;
import org.junit.Test;
import org.wipqueue.consumerthread.WipConsumerThreadFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 */
public class WipQueueAcceptanceTest {

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
    public void givenEmptyQueueWhenItemAddedConsumerShouldConsume() throws Exception {
        WipQueue<String, String> queue = new WipQueueBuilder<String, String>(new ConsumerFactoryStub(consumer)).build().waitToStart();

        queue.put("Workitem");

        assertThat(consumer.received, contains("Workitem"));

    }

    @Test
    public void givenNoJobsConsumerShouldWait() throws Exception {
        // this test can't fail, so is a bad test.
        WipQueue<String, String> queue = new WipQueueBuilder<String, String>(new ConsumerFactoryStub(consumer)).build().waitToStart();

        queue.put("Workitem");
        consumer.freeUpAndWaitFor(1);
        Thread.sleep(50);
        queue.put("Workitem");
        consumer.freeUpAndWaitFor(1);

        assertThat(consumer.received.size(), is(2));
    }

    @Test
    public void givenSlowConsumerWhenItemAddedThenItemShouldBeQueuedWhenFirstConsumerFinishesThenNewItemShouldBeConsumed() throws Exception {
        // given
        WipQueue<String, String> queue = new WipQueueBuilder<String, String>(new ConsumerFactoryStub(consumer)).build().waitToStart();

        consumer.nextIsSlow();
        queue.put("slow job");

        // when
        queue.put("queued job");

        // then
        assertThat(consumer.received.size(), is(0));

        // when
        consumer.freeUpAndWaitFor(2);

        // then
        assertThat(consumer.received, contains("slow job", "queued job"));
    }

    @Test
    public void givenQueuedItemWhenItemIsFinishedThenFutureReturns() throws Exception {
        // given
        WipQueue<String, String> queue = new WipQueueBuilder<String, String>(new ConsumerFactoryStub(consumer)).build().waitToStart();

        consumer.nextIsSlow();
        Future<String> future = queue.put("slow job");
        assertThat(future.isDone(), is(false));

        // when
        consumer.freeUpAndWaitFor(1);
        Thread.sleep(10);

        assertThat(future.isDone(), is(true));
        assertThat(future.get(), is("slow job-done"));
    }

    @Test
    public void givenQueuedItemWhenItemIsCancelledThenItemNeverExecutes() throws Exception {
        // but it stays on queue until it is "not" executed

        // given
        WipQueue<String, String> queue = new WipQueueBuilder<String, String>(new ConsumerFactoryStub(consumer)).build().waitToStart();

        consumer.nextIsSlow();
        queue.put("slow job");
        Future<String> future = queue.put("queued job");

        // when
        future.cancel(false);
        consumer.freeUpAndWaitFor(1);
        Thread.yield();

        //then
        assertThat(future.isDone(), is(true));
        assertThat(future.isCancelled(), is(true));
        assertThat(consumer.received.size(), is(1));
    }

    private class ConsumerStub implements WipConsumer<String, String> {
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

    private class LockStepConsumer implements WipConsumer<String, String> {
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

    private class ConsumerFactoryStub implements WipConsumerFactory {
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
