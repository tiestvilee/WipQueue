package org.wipqueue.queue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2012
 * Time: 10:38
 */
public class SynchronisationPoint {
    private final CountDownLatch latch = new CountDownLatch(1);

    public void await() throws InterruptedException {
        latch.await();
    }

    public void await(long time, TimeUnit units) throws InterruptedException {
        latch.await(time, units);
    }

    public void reached() {
        latch.countDown();
    }

    public boolean hasBeenReached() {
        return latch.getCount() == 0;
    }
}
