package org.wipqueue;

import org.junit.Test;
import org.wipqueue.queue.WipFuture;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 * Time: 23:16
 */
public class WipFutureTest {

    @Test
    public void givenAFutureWhenIncompleteThenHasNoResult() {
        WipFuture<String> future = new WipFuture<String>();

        assertThat(future.isDone(), is(false));
        assertThat(future.isCancelled(), is(false));
    }

    @Test
    public void givenAFutureWhenCompleteThenResultIsAvailable() throws Exception {
        WipFuture<String> future = new WipFuture<String>();

        future.setResult("result");

        assertThat(future.isDone(), is(true));
        assertThat(future.isCancelled(), is(false));
        assertThat(future.get(), is("result"));
        assertThat(future.get(1, TimeUnit.MILLISECONDS), is("result"));
    }

    @Test
    public void givenAFutureWhenRequestingResultThenWillBlockUntilAvailable() throws Exception {
        final WipFuture<String> future = new WipFuture<String>();
        final CountDownLatch threadStarted = new CountDownLatch(1);

        new Thread(new Runnable() {
            public void run() {
                    threadStarted.countDown();
                    Thread.yield();
                    future.setResult("result");
            }
        }).start();

        threadStarted.await();
        assertThat(future.get(), is("result"));
    }

    @Test
    public void givenAFutureWhenRequestingResultThenWillBlockForShortPeriod() throws Exception {
        final WipFuture<String> future = new WipFuture<String>();

        try {
            future.get(1, TimeUnit.MILLISECONDS);
            fail("Should have timed out");
        } catch (TimeoutException e) {
        }
    }

    @Test
    public void givenAFutureThatFailedWhenRequestingResultThenWillThrowException() throws Exception {
        final WipFuture<String> future = new WipFuture<String>();

        future.throwError(new RuntimeException("an error"));
        try {
            future.get();
            fail("Should have had an execution exception");
        } catch (ExecutionException e) {
            assertThat(e.getCause().getMessage(), is("an error"));
        }
    }

    @Test
    public void givenAFutureWhenCancellingAndNotYetSetThenCancelledIsTrue() throws Exception {
        final WipFuture<String> future = new WipFuture<String>();

        // when
        boolean success = future.cancel(false);

        // then
        assertThat(success, is(true));
        assertThat(future.isCancelled(), is(true));
        assertThat(future.isDone(), is(true));

        try {
            future.get();
            fail("Should have thrown a Cancellation Exception");
        } catch (CancellationException e) {
        }

        try {
            future.get(1, TimeUnit.MILLISECONDS);
            fail("Should have thrown a Cancellation Exception");
        } catch (CancellationException e) {
        }
    }

    @Test
    public void givenAFutureWhenCancellingAndAlreadySetThenCancelledIsFalse() throws Exception {
        final WipFuture<String> future = new WipFuture<String>();
        future.setResult("result");

        // when
        boolean success = future.cancel(false);

        // then
        assertThat(success, is(false));
        assertThat(future.isCancelled(), is(false));
        assertThat(future.isDone(), is(true));

        assertThat(future.get(), is("result"));
        assertThat(future.get(1, TimeUnit.MILLISECONDS), is("result"));
    }

    @Test
    public void cannotCancelAFutureMidFlight() {
        WipFuture<String> future = new WipFuture<String>();
        try {
            future.cancel(true);
            fail("Should have thrown an unsuppoertedException");
        } catch(UnsupportedOperationException e) {
            // pass
        }
    }
}
