package org.wipqueue.queue;

import org.wipqueue.utils.SynchronisationPoint;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2012
 * Time: 14:05
 */
public abstract class WipRunner implements Runnable {
    private final SynchronisationPoint started   = new SynchronisationPoint();
    private final SynchronisationPoint finished  = new SynchronisationPoint();
    private final AtomicBoolean        keepGoing = new AtomicBoolean(true);
    private Thread whoToInterrupt;

    public void run() {
        whoToInterrupt = Thread.currentThread();
        started.reached();
        try {
            while(keepGoing.get()) {
                doThisRepeatedly();
            }
        } finally {
            finished.reached();
        }
    }

    protected abstract void doThisRepeatedly();

    public void waitUntilStarted() throws InterruptedException {
        started.await();
    }

    public void stop() {
        keepGoing.set(false);
        whoToInterrupt.interrupt();
    }

    public void waitUntilFinished() throws InterruptedException {
        finished.await();
    }

    public boolean isStarted() {
        return started.hasBeenReached();
    }

    public boolean isFinished() {
        return finished.hasBeenReached();
    }

    public void kill() {
        whoToInterrupt.stop();
        finished.reached();
    }
}
