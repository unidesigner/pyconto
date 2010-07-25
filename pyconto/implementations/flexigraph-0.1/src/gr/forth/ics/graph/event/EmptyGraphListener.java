package gr.forth.ics.graph.event;

public abstract class EmptyGraphListener implements GraphListener {
    public void preEvent() { }
    public void postEvent() { }
    
    public void nodeToBeAdded(GraphEvent e) { }
    public void nodeAdded(GraphEvent e) { }
    public void nodeToBeRemoved(GraphEvent e) { }
    public void nodeRemoved(GraphEvent e) { }
    public void nodeReordered(GraphEvent e) { }
    
    public void edgeToBeAdded(GraphEvent e) { }
    public void edgeAdded(GraphEvent e) { }
    public void edgeToBeRemoved(GraphEvent e) { }
    public void edgeRemoved(GraphEvent e) { }
    public void edgeReordered(GraphEvent e) { }
}
