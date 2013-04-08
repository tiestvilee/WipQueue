package org.wipqueue.exception;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 22:25
 */
public class QueueFullException extends WorkItemRejectedException {
    public QueueFullException(String message) {
        super(message);
    }
}
