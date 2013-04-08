package org.wipqueue.exception;

/**
 * copyright Tiest Vilee 2012
 * Date: 31/03/2012
 * Time: 22:36
 */
public class CycleTimeTooLongException extends WorkItemRejectedException {
    public CycleTimeTooLongException(String message) {
        super(message);
    }
}
