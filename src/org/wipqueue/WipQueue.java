package org.wipqueue;

import org.wipqueue.queue.WipQueueImpl;

import java.util.concurrent.Future;

/**
 * copyright Tiest Vilee 2012
 * Date: 01/04/2012
 * Time: 00:14
 */
public class WipQueue<T, R> {

    private final WipQueueImpl<T, R> delegate;

    public WipQueue(WipQueueImpl<T, R> delegate) {
        this.delegate = delegate;
    }

    /**
     * Adds a workItem to this queue, returning a Future that can be used to wait
     * on a result from the processing of the workItem
     * @param workItem The item that is passed to a consumer for consumption
     * @return A Future that will, at some point, contain the value that is the result
     *          of consuming the workItem.
     */
    public Future<R> put(T workItem) {
        return delegate.put(workItem);
    }


    /**
     * Waits for the consumers of this thread to start. Not necessary, but nice.
     */
    public WipQueue<T,R> waitToStart() throws InterruptedException {
        delegate.waitToStart();
        return this;
    }
}
