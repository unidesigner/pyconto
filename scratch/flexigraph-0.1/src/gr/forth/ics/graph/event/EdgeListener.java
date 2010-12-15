package gr.forth.ics.graph.event;

public interface EdgeListener extends OperationListener {
    void edgeAdded(GraphEvent e);
    
    void edgeRemoved(GraphEvent e);
    
    /** return true to veto */
    void edgeToBeAdded(GraphEvent e);
    
    /** return true to veto */
    void edgeToBeRemoved(GraphEvent e);
    
    void edgeReordered(GraphEvent e); //only event.edge() is defined
}
