package gr.forth.ics.graph.event;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.util.Args;
import java.lang.ref.WeakReference;

/**
 * A graph listener with a corresponing delegate listener (which actually processes all graph events),
 * which does not prevent the latter of being reclaimed by the garbage collector. This class offers a
 * safe way to create listeners to graph that update external structures in response to graph modifications,
 * without making such listeners the source of memory leaks; note that a graph strongly references its
 * current list of listeners, so if a normal listener is attached to a graph, it cannot be reclaimed (nor whatever
 * it references) before the graph itself is reclaimed, even if it is unreachable from anywhere else.
 *
 * <p>A WeakListener automatically deregisters itself from a graph when it finds that the delegate listener
 * is garbage collected.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public final class WeakListener implements GraphListener {
    private final InspectableGraph targetGraph;
    private final WeakReference<GraphListener> delegateListenerRef;
    private GraphListener delegateListener;
    private int eventCount;

    /**
     * Creates a WeakListener and attaches it to a graph. The WeakListener delegates all events it
     * receives to a specified listener. It will <em> not prevent the specified listener from being garbage
     * collected, i.e. it does not strongly reference it. The WeakListener will automatically remove itself
     * from the graph when the delegate listener is garbage collected.
     *
     * <p>If you create a structure that needs to update itself dynamically by responding to graph events,
     * but you want the structure itself to be reclaimable before the graph (simply by removing references
     * to it), you need to use a WeakListener to receive the events. That is because directly registering
     * a listener to a graph creates a strong reference to it, thus the graph indirectly strongly references
     * the structure, so it cannot be reclaimed unless the listener is explicitly removed from the graph.
     * Using a WeakListener does not require explicitly removing it from the graph.
     *
     * <p>It is guaranteed that the delegate listener <em>cannot</em> be garbage collected
     * <em>during</em> a graph event processing.
     *
     * @param graph the graph the events of which the listener is interested in
     * @param delegateListener the listener that will handle the events coming from the specified graph
     */
    public static WeakListener createAndAttach(InspectableGraph graph, GraphListener delegateListener) {
        WeakListener listener = new WeakListener(graph, delegateListener);
        graph.addGraphListener(listener);
        return listener;
    }

    private WeakListener(InspectableGraph targetGraph, GraphListener delegateListener) {
        Args.notNull(targetGraph, "graph");
        Args.notNull(delegateListener, "listener");
        this.targetGraph = targetGraph;
        this.delegateListenerRef = new WeakReference<GraphListener>(delegateListener);
    }

    @Override public void preEvent() {
        if (eventCount == 0) {
            delegateListener = delegateListenerRef.get();
            if (delegateListener == null) {
                targetGraph.removeGraphListener(this);
                return;
            }
        }
        eventCount++;
        delegateListener.preEvent();
    }

    @Override public void postEvent() {
        delegateListener.postEvent();
        eventCount--;
        if (eventCount == 0) {
            delegateListener = null;
            if (delegateListenerRef.get() == null) {
                targetGraph.removeGraphListener(this);
                return;
            }
        }
    }

    public void nodeAdded(GraphEvent e) {
        delegateListener.nodeAdded(e);
    }

    public void nodeRemoved(GraphEvent e) {
        delegateListener.nodeRemoved(e);
    }

    public void nodeToBeAdded(GraphEvent e) {
        delegateListener.nodeToBeAdded(e);
    }

    public void nodeToBeRemoved(GraphEvent e) {
        delegateListener.nodeToBeRemoved(e);
    }

    public void nodeReordered(GraphEvent e) {
        delegateListener.nodeReordered(e);
    }

    public void edgeAdded(GraphEvent e) {
        delegateListener.edgeAdded(e);
    }

    public void edgeRemoved(GraphEvent e) {
        delegateListener.edgeRemoved(e);
    }

    public void edgeToBeAdded(GraphEvent e) {
        delegateListener.edgeToBeAdded(e);
    }

    public void edgeToBeRemoved(GraphEvent e) {
        delegateListener.edgeToBeRemoved(e);
    }

    public void edgeReordered(GraphEvent e) {
        delegateListener.edgeReordered(e);
    }
}
