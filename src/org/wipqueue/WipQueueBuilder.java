package org.wipqueue;

import org.wipqueue.consumerthread.WipConsumerThreadFactory;
import org.wipqueue.consumerthread.WipConsumerThreadFactoryDefault;
import org.wipqueue.queue.WipQueueImpl;
import org.wipqueue.queue.WipQueueThreadFactory;
import org.wipqueue.strategy.EmptyStrategy;

/**
 * Used to build a WipQueue without too much concern about how it's strung together.
 *
 * copyright Tiest Vilee 2012
 * Date: 08/04/2012
 * Time: 23:27
 */
public class WipQueueBuilder<T,R> {
    private final WipConsumerFactory consumerFactory;

    private WipConsumerThreadFactory<T, R> consumerThreadFactory;
    private WipStrategy wipStrategy;
    private int maxQueuedItems;
    private int startingConsumerCount;
    private WipQueueThreadFactory<T, R> wipQueueThreadFactory;
    private int minConsumers;
    private int maxConsumers;


    WipQueueBuilder(WipConsumerFactory<T,R> consumerFactory) {
        this.consumerFactory = consumerFactory;
        wipStrategy = new EmptyStrategy();
        maxQueuedItems = 100;
        startingConsumerCount = 1;
        wipQueueThreadFactory = new WipQueueThreadFactory<T, R>(100);
        minConsumers = 1;
        maxConsumers = 1;
    }

    public WipQueueBuilder<T,R> withWipConsumerThreadFactory(WipConsumerThreadFactory consumerThreadFactory) {
        this.consumerThreadFactory = consumerThreadFactory;
        return this;
    }

    public WipQueueBuilder<T,R> withHeadStrategy(WipStrategy wipStrategy) {
        this.wipStrategy = wipStrategy;
        return this;
    }

    public WipQueueBuilder<T,R> withMaxItems(int maxQueuedItems) {
        this.maxQueuedItems = maxQueuedItems;
        return this;
    }

    public WipQueueBuilder<T,R> withStartingConsumerCount(int startingConsumerCount) {
        this.startingConsumerCount = startingConsumerCount;
        return this;
    }

    public WipQueueBuilder<T,R> withWipQueueThreadFactory(WipQueueThreadFactory<T,R> wipQueueThreadFactory) {
        this.wipQueueThreadFactory = wipQueueThreadFactory;
        return this;
    }

    public WipQueueBuilder<T,R> withMinConsumers(int minConsumers) {
        this.minConsumers = minConsumers;
        return this;
    }

    public WipQueueBuilder<T,R> withMaxConsumers(int maxConsumers) {
        this.maxConsumers = maxConsumers;
        return this;
    }

    public WipQueue<T,R> build() {
        if(consumerThreadFactory == null) {
            consumerThreadFactory = new WipConsumerThreadFactoryDefault<T, R>(consumerFactory, wipQueueThreadFactory, minConsumers, maxConsumers);
        }
        return new WipQueue<T, R>(
            new WipQueueImpl<T,R>(consumerThreadFactory, wipStrategy, maxQueuedItems, startingConsumerCount));
    }
}
