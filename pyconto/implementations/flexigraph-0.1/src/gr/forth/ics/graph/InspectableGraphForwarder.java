package gr.forth.ics.graph;

import gr.forth.ics.graph.event.EdgeListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.event.GraphEventSupport;
import gr.forth.ics.graph.event.GraphListener;
import gr.forth.ics.graph.event.NodeListener;

import gr.forth.ics.util.Args;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import gr.forth.ics.util.ExtendedListIterable;

public class InspectableGraphForwarder extends AbstractInspectableGraph implements InspectableGraph, Serializable {
    protected final InspectableGraph inspectableGraph;
    private final GraphEventSupport eventSupport = new GraphEventSupport();
    protected NodeListener nodeListener;
    protected EdgeListener edgeListener;
    
    public InspectableGraphForwarder(InspectableGraph delegate) {
        Args.notNull(delegate);
        this.inspectableGraph = delegate;
    }
    
    public InspectableGraph getDelegateGraph() {
        return inspectableGraph;
    }
    
    private void lazyInitNodeListener() {
        if (nodeListener == null) {
            nodeListener = new TrampolineNodeListener(this, inspectableGraph);
            inspectableGraph.addNodeListener(nodeListener);
        }
    }
    
    private void lazyInitEdgeListener() {
        if (edgeListener == null) {
            edgeListener = new TrampolineEdgeListener(this, inspectableGraph);
            inspectableGraph.addEdgeListener(edgeListener);
        }
    }
    
    public void addGraphListener(GraphListener listener) {
        eventSupport.addGraphListener(listener);
        lazyInitNodeListener();
        lazyInitEdgeListener();
    }
    
    public void removeGraphListener(GraphListener listener) {
        eventSupport.removeGraphListener(listener);
    }
    
    public void addNodeListener(NodeListener listener) {
        eventSupport.addNodeListener(listener);
        lazyInitNodeListener();
    }
    
    public void removeNodeListener(NodeListener listener) {
        eventSupport.removeNodeListener(listener);
    }
    
    public void addEdgeListener(EdgeListener listener) {
        eventSupport.addEdgeListener(listener);
        lazyInitEdgeListener();
    }
    
    public void removeEdgeListener(EdgeListener listener) {
        eventSupport.removeEdgeListener(listener);
    }
    
    public List<NodeListener> getNodeListeners() {
        return eventSupport.getNodeListeners();
    }
    
    public List<EdgeListener> getEdgeListeners() {
        return eventSupport.getEdgeListeners();
    }
    
    public ExtendedListIterable<Node> adjacentNodes(Node node, Direction direction) {
        return inspectableGraph.adjacentNodes(node, direction);
    }
    
    public int nodeCount() {
        return inspectableGraph.nodeCount();
    }
    
    public int edgeCount() {
        return inspectableGraph.edgeCount();
    }
    
    public ExtendedListIterable<Node> nodes() {
        return inspectableGraph.nodes();
    }
    
    public ExtendedListIterable<Edge> edges(Node node, Direction direction) {
        return inspectableGraph.edges(node, direction);
    }
    
    public ExtendedListIterable<Edge> edges(Node n1, Node n2, Direction direction) {
        return inspectableGraph.edges(n1, n2, direction);
    }
    
    public boolean containsEdge(Edge edge) {
        return inspectableGraph.containsEdge(edge);
    }
    
    public boolean containsNode(Node node) {
        return inspectableGraph.containsNode(node);
    }
    
    public int inDegree(Node node) {
        return inspectableGraph.inDegree(node);
    }
    
    public int outDegree(Node node) {
        return inspectableGraph.outDegree(node);
    }
    
    public ExtendedListIterable<Edge> edges() {
        return inspectableGraph.edges();
    }
    
    private static abstract class Detached {
        private final WeakReference<InspectableGraphForwarder> ref;
        private final InspectableGraph target;
        private InspectableGraphForwarder gd;
        protected GraphEventSupport eventSupport;
        
        private int depth = 0;
        
        Detached(InspectableGraphForwarder gd, InspectableGraph target) {
            this.ref = new WeakReference<InspectableGraphForwarder>(gd);
            this.target = target;
        }
        
        protected abstract void removeSelf(InspectableGraph target);
        protected abstract void preEventImpl();
        protected abstract void postEventImpl();
        
        public final void preEvent() {
            depth++;
            gd = ref.get();
            if (gd == null) {
                removeSelf(target);
                return;
            }
            eventSupport = gd.eventSupport;
            preEventImpl();
        }
        
        public final void postEvent() {
            depth--;
            postEventImpl();
            if (depth == 0) {
                eventSupport = null;
                gd = null;
            }
        }
        
        protected GraphEvent deriveEvent(GraphEvent e) {
            return new GraphEvent(ref.get(), e.getEventType(), e.getData());
        }
    }
    
    private static class TrampolineNodeListener extends Detached implements NodeListener {
        TrampolineNodeListener(InspectableGraphForwarder gd, InspectableGraph source) {
            super(gd, source);
        }
        
        public void nodeToBeRemoved(GraphEvent e) {
            eventSupport.fireNodeToBeRemoved(deriveEvent(e));
        }
        
        public void nodeToBeAdded(GraphEvent e) {
            eventSupport.fireNodeToBeAdded(deriveEvent(e));
        }
        
        public void nodeRemoved(GraphEvent e) {
            eventSupport.fireNodeRemoved(deriveEvent(e));
        }
        
        public void nodeAdded(GraphEvent e) {
            eventSupport.fireNodeAdded(deriveEvent(e));
        }
        
        public void nodeReordered(GraphEvent e) {
            //ignore this event type
        }
        
        protected void removeSelf(InspectableGraph target) {
            target.removeNodeListener(this);
        }
        
        protected void preEventImpl() {
            eventSupport.firePreNode();
        }
        
        protected void postEventImpl() {
            eventSupport.firePostNode();
        }
    }
    
    private static class TrampolineEdgeListener extends Detached implements EdgeListener {
        TrampolineEdgeListener(InspectableGraphForwarder gd, InspectableGraph source) {
            super(gd, source);
        }
        
        public void edgeToBeAdded(GraphEvent e) {
            eventSupport.fireEdgeToBeAdded(deriveEvent(e));
        }
        
        public void edgeReordered(GraphEvent e) {
            //ignore this event type
        }
        
        public void edgeRemoved(GraphEvent e) {
            eventSupport.fireEdgeRemoved(deriveEvent(e));
        }
        
        public void edgeAdded(GraphEvent e) {
            eventSupport.fireEdgeAdded(deriveEvent(e));
        }
        
        public void edgeToBeRemoved(GraphEvent e) {
            eventSupport.fireEdgeToBeRemoved(deriveEvent(e));
        }
        
        protected void removeSelf(InspectableGraph target) {
            target.removeEdgeListener(this);
        }
        
        protected void preEventImpl() {
            eventSupport.firePreEdge();
        }
        
        protected void postEventImpl() {
            eventSupport.firePostEdge();
        }
    }
}
