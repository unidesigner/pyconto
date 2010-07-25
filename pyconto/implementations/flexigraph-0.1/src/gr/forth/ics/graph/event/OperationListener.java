package gr.forth.ics.graph.event;

import java.util.EventListener;

/**
 * Supports nesting of consequtive graph events, by receiving callbacks when an event starts
 * and when in finishes. Between these calls, other events may be nested too.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public interface OperationListener extends EventListener {
    /**
     * Called before an event.
     */
    void preEvent();
    
    /**
     * Called after an event. This method will always be called, even if a listener threw an exception when handling this event.
     */
    void postEvent();
}
