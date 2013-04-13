package org.wipqueue;

import org.wipqueue.queue.WipRunner;
import org.wipqueue.utils.SynchronisationPoint;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * copyright Tiest Vilee 2012
 * Date: 13/04/2013
 * Time: 21:36
 */
public class TimeoutConsumer<T,R> extends WipRunner implements WipConsumer<T,R> {

    private final WipConsumer<T, R> delegate;
    private final long timeoutMillis;
    private final KillOrStop killOrStop;
    private final AtomicReference<T> workItem = new AtomicReference<T>();
    private final AtomicReference<R> result = new AtomicReference<R>();
    private final AtomicReference<Exception> exception = new AtomicReference<Exception>();
    private Thread delegateThread;
    private SynchronisationPoint completedJob;
    private SynchronisationPoint newJob;

    public TimeoutConsumer(WipConsumer<T,R> delegate, long timeoutMillis, KillOrStop killOrStop) {
        this.delegate = delegate;
        this.timeoutMillis = timeoutMillis;
        this.killOrStop = killOrStop;
        startConsumerThread();
    }

    public synchronized R consume(T workItem) throws Exception {
        waitUntilStarted();


        this.workItem.set(workItem);

        newJob.reached();
        if(completedJob.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
            completedJob = new SynchronisationPoint();
            if(exception.get() != null) {
                throw exception.get();
            }
            return this.result.get();
        }
        killOrStop.doIt(this);
        startConsumerThread();
        throw new TimeoutException("Timed out trying to consume " + workItem);
    }

    private void startConsumerThread() {
        completedJob = new SynchronisationPoint();
        newJob = new SynchronisationPoint();
        delegateThread = new Thread(this);
        delegateThread.start();
    }

    @Override
    protected void doThisRepeatedly() {
        try {
            newJob.await();
            newJob = new SynchronisationPoint();
            result.set(delegate.consume(workItem.get()));
        } catch (Exception e) {
            exception.set(e);
        } finally {
            completedJob.reached();
        }
    }

    public enum KillOrStop {
        KILL { // The nasty, but effective, way of stopping a thread
            @Override
            public void doIt(WipRunner runner) {
                runner.kill();
            }
        }, STOP { // the correct, but hard, way of stopping a thread - see http://forward.com.au/javaProgramming/HowToStopAThread.html
            @Override
            public void doIt(WipRunner runner) {
                runner.stop();
            }
        };

        public abstract void doIt(WipRunner runner);
    }
}
