package org.wipqueue.queue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 * Time: 23:11
 */
public class WipFuture<T> implements Future<T>, Callable<T> {

    private final FutureTask<T> future = new FutureTask<T>(this);

    private T result = null;
    private Exception exception;

    public void setResult(T result) {
        this.result = result;
        future.run();
    }

    public void throwError(Exception e) {
        exception = e;
        future.run();
    }

    public boolean cancel(boolean mayInterruptWhileRunning) {
        if(mayInterruptWhileRunning) {
            throw new UnsupportedOperationException("Can't currently interrupt running Futures");
        }
        return future.cancel(mayInterruptWhileRunning);
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(l, timeUnit);
    }

    public T call() throws Exception {
        if(exception != null) {
            throw exception;
        }
        return (T) result;
    }
}
