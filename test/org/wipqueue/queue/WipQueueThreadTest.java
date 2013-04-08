package org.wipqueue.queue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wipqueue.WipConsumer;
import org.wipqueue.WipStrategy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2013
 * Time: 10:42
 */
public class WipQueueThreadTest  {
    @Mock
    private WipConsumer<String, String> consumer;
    @Mock
    private WipQueueImpl<String, String> queue;
    @Mock
    private WipStrategy strategy;

    private String input = "incoming";
    private String output = "outgoing";
    private WipFuture<String> future = new WipFuture<String>();
    private WorkItem<String, String> workItem = new WorkItem<String, String>(input, future);
    private WipQueueThread<String,String> queueThread;
    private RuntimeException thrownException;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(queue.poll(anyInt(), (TimeUnit) any())).thenReturn(workItem);
        when(consumer.consume(input)).thenReturn(output);

        queueThread = new WipQueueThread<String, String>(strategy, queue, consumer);
    }

    @Test
    public void shouldConsumeItemFromQueueAndTriggerItemAboutToBeConsumedEvent() throws Exception {

        queueThread.doThisRepeatedly();

        verify(strategy).itemAboutToBeConsumed(workItem);
        assertThat(0 < workItem.waitTime(), is(true));
    }

    @Test
    public void shouldConsumeItemFromQueueAndTriggerItemConsumedSuccessfullyEvent() throws Exception {

        queueThread.doThisRepeatedly();

        verify(consumer).consume(input);
        verify(strategy).itemConsumedSuccessfully(workItem);
        assertThat(future.isDone(), is(true));
        assertThat(future.isCancelled(), is(false));
        assertThat(future.get(), is(output));
        assertThat(0 <= workItem.waitTime(), is(true));
        assertThat(workItem.waitTime() <= workItem.totalTime(), is(true));
    }

    @Test
    public void shouldConsumeItemFromQueueAndTriggerItemConsumedWithErrorEvent() throws Exception {
        thrownException = new RuntimeException("my exception");
        when(consumer.consume(input)).thenThrow(thrownException);

        queueThread.doThisRepeatedly();

        verify(strategy).itemConsumedWithError(thrownException, workItem);
        assertThat(future.isDone(), is(true));
        assertThat(future.isCancelled(), is(false));
        assertThat(0 <= workItem.waitTime(), is(true));
        assertThat(workItem.waitTime() <= workItem.totalTime(), is(true));
        try {
            future.get();
        } catch (ExecutionException e) {
            assertThat(e.getCause().getMessage(), is("my exception"));
        }
    }

    @Test
    public void shouldNotConsumeCancelledItems() throws Exception {
        future.cancel(false);

        queueThread.doThisRepeatedly();

        verify(consumer, never()).consume((String) any());
        // should we have an event for this?
    }

}
