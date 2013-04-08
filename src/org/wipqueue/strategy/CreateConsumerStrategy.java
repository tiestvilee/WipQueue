package org.wipqueue.strategy;

import org.wipqueue.queue.WipQueueImpl;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 23:16
 */
public class CreateConsumerStrategy extends EmptyStrategy {
    private final int minClients;
    private final int maxClients;
    private final int lowWaterMark;
    private final int highWaterMark;

    public CreateConsumerStrategy(int minClients, int maxClients, int lowWaterMark, int highWaterMark) {
        this.minClients = minClients;
        this.maxClients = maxClients;
        this.lowWaterMark = lowWaterMark;
        this.highWaterMark = highWaterMark;
    }

    @Override
    public void addingWorkItem(WipQueueImpl queue, int queueSize) {
        if(queueSize > highWaterMark) {
            if(queue.runningConsumerCount() < maxClients) {
                queue.createConsumer();
            }
        } else if (queueSize <= lowWaterMark) {
            if(queue.runningConsumerCount() > minClients) {
                queue.killConsumer();
            }
        }
    }
}
