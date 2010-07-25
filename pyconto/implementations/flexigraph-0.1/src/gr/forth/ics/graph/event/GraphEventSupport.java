package gr.forth.ics.graph.event;

import java.util.*;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.EventSupport;

public class GraphEventSupport {
    private static final Runnable NULL_RUNNABLE = new Runnable() { public void run() { } };
    private final EventSupport<NodeListener> nodeSupport = new EventSupport<NodeListener>();
    private final EventSupport<EdgeListener> edgeSupport = new EventSupport<EdgeListener>();
    private int listeners;
    
    private void calcListeners() {
        listeners = nodeSupport.getListenerCount() + edgeSupport.getListenerCount();
    }
    
    public boolean isEmpty() {
        return listeners == 0;
    }
    
    public void addEdgeListener(EdgeListener listener) {
        edgeSupport.addListener(listener);
        calcListeners();
    }
    
    public void addNodeListener(NodeListener listener) {
        nodeSupport.addListener(listener);
        calcListeners();
    }
    
    public void removeEdgeListener(EdgeListener listener) {
        edgeSupport.removeListener(listener);
        calcListeners();
    }
    
    public void removeNodeListener(NodeListener listener) {
        nodeSupport.removeListener(listener);
        calcListeners();
    }
    
    public void addGraphListener(GraphListener listener) {
        addEdgeListener(listener);
        addNodeListener(listener);
    }
    
    public void removeGraphListener(GraphListener listener) {
        if (listener == null) {
            return;
        }
        removeEdgeListener(listener);
        removeNodeListener(listener);
    }
    
    public void fire(GraphEvent e) {
        fire(e, e.getEventType(), NULL_RUNNABLE);
    }
    
    //throws npe
    public void fire(GraphEvent e, Runnable commandIfNoVeto) {
        fire(e, e.getEventType(), commandIfNoVeto);
    }
    
    //throws npe
    public void fire(GraphEvent e, GraphEvent.Type eventType, Runnable commandIfNoVeto) {
        if (listeners == 0) {
            commandIfNoVeto.run();
            return;
        }
        switch (eventType) {
            case NODE_ADDED: case NODE_REINSERTED:
                fireAddNode(e, commandIfNoVeto); break;
            case NODE_REMOVED:
                fireRemoveNode(e, commandIfNoVeto); break;
            case EDGE_ADDED: case EDGE_REINSERTED:
                fireAddEdge(e, commandIfNoVeto); break;
            case EDGE_REMOVED:
                fireRemoveEdge(e, commandIfNoVeto); break;
            case NODE_REORDERED:
                fireNodeReordered(e); break;
            case EDGE_REORDERED:
                fireEdgeReordered(e); break;
            default:
                throw new IllegalArgumentException("Unexpected event type: " + eventType);
        }
    }
    
    public void firePreEdge() {
        firePre(edgeSupport.getListeners());
    }
    
    public void firePostEdge() {
        firePost(edgeSupport.getListeners());
    }
    
    public void firePreNode() {
        firePre(nodeSupport.getListeners());
    }
    
    public void firePostNode() {
        firePost(nodeSupport.getListeners());
    }
    
    public void fireNodeReordered(GraphEvent e) {
        Args.isTrue(e.getEventType() == GraphEvent.Type.NODE_REORDERED);
        for (NodeListener listener : nodeSupport.getListeners()) {
            listener.nodeReordered(e);
        }
    }
    
    public void fireEdgeReordered(GraphEvent e) {
        Args.isTrue(e.getEventType() == GraphEvent.Type.EDGE_REORDERED);
        for (EdgeListener listener : edgeSupport.getListeners()) {
            listener.edgeReordered(e);
        }
    }
    
    public void fireNodeToBeAdded(GraphEvent e) {
        GraphEvent.Type eventType = e.getEventType();
        Args.isTrue(eventType == GraphEvent.Type.NODE_ADDED || eventType == GraphEvent.Type.NODE_REINSERTED);
        if (nodeSupport.isEmpty()) {
            return;
        }
        for (NodeListener listener : nodeSupport.getListeners()) {
            listener.nodeToBeAdded(e);
        }
    }
    
    private void fireAddNode(GraphEvent e, Runnable commandIfNoVeto) {
        firePreNode();
        try {
            Iterable<NodeListener> listeners = nodeSupport.getListeners();
            for (NodeListener listener : listeners) {
                listener.nodeToBeAdded(e);
            }
            commandIfNoVeto.run();
            for (NodeListener listener : listeners) {
                listener.nodeAdded(e);
            }
        } finally {
            firePostNode();
        }
    }
    
    public void fireNodeAdded(GraphEvent e) {
        GraphEvent.Type eventType = e.getEventType();
        Args.isTrue(eventType == GraphEvent.Type.NODE_ADDED || eventType == GraphEvent.Type.NODE_REINSERTED);
        if (nodeSupport.isEmpty()) {
            return;
        }
        for (NodeListener listener : nodeSupport.getListeners()) {
            listener.nodeAdded(e);
        }
    }
    
    public void fireNodeToBeRemoved(GraphEvent e) {
        Args.isTrue(e.getEventType() == GraphEvent.Type.NODE_REMOVED);
        if (nodeSupport.isEmpty()) {
            return;
        }
        for (NodeListener listener : nodeSupport.getListeners()) {
            listener.nodeToBeRemoved(e);
        }
    }
    
    private void fireRemoveNode(GraphEvent e, Runnable commandIfNoVeto) {
        firePreNode();
        try {
            Iterable<NodeListener> listeners = nodeSupport.getListeners();
            for (NodeListener listener : listeners) {
                listener.nodeToBeRemoved(e);
            }
            commandIfNoVeto.run();
            for (NodeListener listener : listeners) {
                listener.nodeRemoved(e);
            }
        } finally {
            firePostNode();
        }
    }
    
    public void fireNodeRemoved(GraphEvent e) {
        Args.isTrue(e.getEventType() == GraphEvent.Type.NODE_REMOVED);
        if (nodeSupport.isEmpty()) {
            return;
        }
        for (NodeListener listener : nodeSupport.getListeners()) {
            listener.nodeRemoved(e);
        }
    }
    
    public void fireEdgeToBeAdded(GraphEvent e) {
        GraphEvent.Type eventType = e.getEventType();
        Args.isTrue(eventType == GraphEvent.Type.EDGE_ADDED || eventType == GraphEvent.Type.EDGE_REINSERTED);
        if (edgeSupport.isEmpty()) {
            return;
        }
        for (EdgeListener listener : edgeSupport.getListeners()) {
            listener.edgeToBeAdded(e);
        }
    }
    
    private void fireAddEdge(GraphEvent e, Runnable commandIfNoVeto) {
        firePreEdge();
        try {
            Iterable<EdgeListener> listeners = edgeSupport.getListeners();
            for (EdgeListener listener : listeners) {
                listener.edgeToBeAdded(e);
            }
            commandIfNoVeto.run();
            for (EdgeListener listener : listeners) {
                listener.edgeAdded(e);
            }
        } finally {
            firePostEdge();
        }
    }
    
    public void fireEdgeAdded(GraphEvent e) {
        GraphEvent.Type eventType = e.getEventType();
        Args.isTrue(eventType == GraphEvent.Type.EDGE_ADDED || eventType == GraphEvent.Type.EDGE_REINSERTED);
        if (edgeSupport.isEmpty()) {
            return;
        }
        for (EdgeListener listener : edgeSupport.getListeners()) {
            listener.edgeAdded(e);
        }
    }
    
    public void fireEdgeToBeRemoved(GraphEvent e) {
        Args.isTrue(e.getEventType() == GraphEvent.Type.EDGE_REMOVED);
        if (edgeSupport.isEmpty()) {
            return;
        }
        for (EdgeListener listener : edgeSupport.getListeners()) {
            listener.edgeToBeRemoved(e);
        }
    }
    
    private void fireRemoveEdge(GraphEvent e, Runnable commandIfNoVeto) {
        firePreEdge();
        try {
            Iterable<EdgeListener> listeners = edgeSupport.getListeners();
            for (EdgeListener listener : listeners) {
                listener.edgeToBeRemoved(e);
            }
            commandIfNoVeto.run();
            for (EdgeListener listener : listeners) {
                listener.edgeRemoved(e);
            }
        } finally {
            firePostEdge();
        }
    }
    
    public void fireEdgeRemoved(GraphEvent e) {
        Args.isTrue(e.getEventType() == GraphEvent.Type.EDGE_REMOVED);
        if (edgeSupport.isEmpty()) {
            return;
        }
        for (EdgeListener listener : edgeSupport.getListeners()) {
            listener.edgeRemoved(e);
        }
    }
    
    private void firePre(Collection<? extends OperationListener> listeners) {
        for (OperationListener listener : listeners) {
            listener.preEvent();
        }
    }
    
    private void firePost(Collection<? extends OperationListener> listeners) {
        for (OperationListener listener : listeners) {
            listener.postEvent();
        }
    }
    
    public List<NodeListener> getNodeListeners() {
        return nodeSupport.getListeners();
    }
    
    public List<EdgeListener> getEdgeListeners() {
        return edgeSupport.getListeners();
    }
}
