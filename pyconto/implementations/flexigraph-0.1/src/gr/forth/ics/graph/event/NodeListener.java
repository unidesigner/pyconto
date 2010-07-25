package gr.forth.ics.graph.event;

public interface NodeListener extends OperationListener {
    void nodeAdded(GraphEvent e);
    void nodeRemoved(GraphEvent e);
    void nodeToBeAdded(GraphEvent e);
    void nodeToBeRemoved(GraphEvent e) ;
    void nodeReordered(GraphEvent e);
}
