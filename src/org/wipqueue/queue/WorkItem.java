package org.wipqueue.queue;

/**
 * copyright Tiest Vilee 2012
 * Date: 29/03/2012
 * Time: 22:59
 */
public class WorkItem<T, R> {
    public final T workItem;
    public final WipFuture<R> future;
    public final long createTime;
    private long totalTime = -1;
    private long waitTime = -1;

    public WorkItem(T workItem, WipFuture<R> future) {
        this.workItem = workItem;
        this.future = future;
        createTime = System.currentTimeMillis();
    }

    public void start() {
        waitTime = System.currentTimeMillis() - createTime;
    }

    public void end() {
        totalTime = System.currentTimeMillis() - createTime;
    }

    public long waitTime() {
        return waitTime;
    }

    public long processTime() {
        return totalTime = waitTime;
    }

    public long totalTime() {
        return totalTime;
    }
}
