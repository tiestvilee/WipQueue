package org.wipqueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeoutException;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.wipqueue.TimeoutConsumer.KillOrStop.KILL;
import static org.wipqueue.TimeoutConsumer.KillOrStop.STOP;
import static org.wipqueue.queue.WipQueueImplTest.ConsumerStub;
import static org.wipqueue.queue.WipQueueImplTest.LockStepConsumer;

/**
 * copyright Tiest Vilee 2012
 * Date: 13/04/2013
 * Time: 21:55
 */
public class TimeoutConsumerTest {

    @Mock
    private LockStepConsumer consumer = new LockStepConsumer();

    @Before
    public void setup() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldCallDelegateAndReturnResult() throws Exception {
        TimeoutConsumer<String, String> timeoutConsumer = new TimeoutConsumer<String, String>(
            consumer, 1000, STOP);
        consumer.next();

        String result = timeoutConsumer.consume("consume this");

        assertThat(result, is("consume this-done2"));
    }

    @Test
    public void shouldCallDelegateAndRethrowException() throws Exception {
        TimeoutConsumer<String, String> timeoutConsumer = new TimeoutConsumer<String, String>(
            consumer, 1000, STOP);
        consumer.nextWithError();

        try {
            timeoutConsumer.consume("consume this");
            fail("should have thrown an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("an error"));
        }
    }

    @Test
    public void shouldGiveUpIfItTimesOut() throws Exception {

        TimeoutConsumer<String, String> timeoutConsumer = new TimeoutConsumer<String, String>(
            consumer, 10, KILL);

        try {
            timeoutConsumer.consume("consume this");
            fail("Should have timed out");
        } catch (TimeoutException e) {

        }
    }

    @Test
    public void ifTimedOutThenConsumerThreadIsStopped() throws Exception {
        ConsumerStub consumer = new ConsumerStub();
        TimeoutConsumer<String, String> timeoutConsumer = new TimeoutConsumer<String, String>(
            consumer, 10, KILL);

        consumer.nextIsSlow();
        try {
            timeoutConsumer.consume("consume this");
            fail("Should have timed out");
        } catch (TimeoutException e) {
            consumer.freeUpAndWaitFor(0);
            Thread.sleep(1);
            assertThat(consumer.received.size(), is(0));
        }
    }
}
