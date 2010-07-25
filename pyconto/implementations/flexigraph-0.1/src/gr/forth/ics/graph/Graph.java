package gr.forth.ics.graph;


import java.util.Collection;

//TODO: event documentation
/**
 * A modifiable graph.
 *<p>
 * Graphs can be either <i>primary</i> or not (i.e., secondary). Node and edges can be contained to at most
 * one primary graph, but as many secondary graphs as needed. Primary graphs are more efficient than secondaries,
 * but secondaries are more flexible. Specifically, every node or edge can only be contained at most in
 * one primary graph, but they are allowed to be contained in arbitrarily many secondary graphs.
 *
 * Note that before a node is removed, its incident edges are removed (as the invariant of the edges
 * contained in this graph is that both nodes at their endings are always contained in the same graph).
 *
 * @see InspectableGraph
 * @see Node
 * @see Edge
 * @see gr.forth.ics.graph.concrete.PrimaryGraph
 * @see gr.forth.ics.graph.concrete.SecondaryGraph
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Graph extends InspectableGraph {
    /**
     * Creates and returns a new edge that start at node1 and ends at node2.
     * Equivalent to <code>newEdge(node1, node2, null)</code>.
     *
     * @throws IllegalArgumentException when !containsNode(node1) or !containsNode(node2)
     */
    Edge newEdge(Node node1, Node node2);
    
    /**
     * Creates and returns a new edge that start at node1 and ends at node2, with the specifed
     * value.
     *
     * @throws IllegalArgumentException when !containsNode(node1) or !containsNode(node2)
     */
    Edge newEdge(Node node1, Node node2, Object value);
    
    /**
     * Removes the specified edge, if it is currently contained to this graph. If the edge is null,
     * nothing happens.
     * @return whether the specified edge was indeed contained in this graph, and has been removed
     */
    boolean removeEdge(Edge edge);
    
    /**
     * Removes the specified edges. If the parameter is null, nothing happens, and zero is returned.
     * @return the number of edges that were contained in this graph and removed
     */
    int removeEdges(Iterable<Edge> edges);
    
    /**
     * Removes every edge of this graph.
     * @return the number of edges removed
     */
    int removeAllEdges();
    
    /**
     * Creates and returns a new node. Equivalent to <code>newNode(null)</code>.
     */
    Node newNode();
    
    /**
     * Creates and returns a new node, with the specified value.
     */
    Node newNode(Object value);
    
    /**
     * Creates the specified number of nodes, and return them as an array.
     * @throws IllegalArgumentException if count is less than zero
     */
    Node[] newNodes(int count);
    
    /**
     * Creates as many nodes as the number of arguments supplied and returns them as an array.
     * Each node's value is set to the respective argument.
     */
    Node[] newNodes(Object ... values);
    
    /**
     * Removes the specified node. If the node is null, nothing happens.
     * <BR><strong>Note:</strong> When a node is removed, all its incident edges are also removed.
     * @return whether the specified node was contained in this graph and was removed
     */
    boolean removeNode(Node node);
    
    /**
     * Removes the specified nodes. If the parameter is null, nothing happens, and zero is returned.
     * <BR><strong>Note:</strong> When a node is removed, all its incident edges are also removed.
     * @return the number of nodes that were contained in this graph and removed
     */
    int removeNodes(Iterable<Node> nodes);
    
    /**
     * Effectively clears the graph; removes all nodes and, thus, edges.
     * @return the number of nodes that were removed
     */
    int removeAllNodes();
    
    /**
     * Returns the OrderManager of this graph (optional method).
     * @see OrderManager
     */
    OrderManager getOrderManager();
    
    /**
     * Imports all the contents of the specified graph, into this graph. After the successful
     * execution of this method, the specified graph is empty, and all nodes and edges it contained
     * have been inserted to this graph.
     *
     * @throws gr.forth.ics.util.NullArgumentException if argument is null
     * @see #importGraph(Graph, Iterable)
     */
    void importGraph(Graph graph);
    
    //that do not belong to either graph
    /**
     * Imports a subpart of the specified graph, into this graph. The subpart is consisted of
     * the nodes specified, and <i>every edge that both its nodes are contained in the group</i>.
     * Every other edge, that connects a node to be imported and a node that will not be removed,
     * is removed and returned as a Collection. 
     *
     * @return a collection of edges that have one node that belongs to the declared nodes,
     * and one node that does not belong to them (i.e., <i>inter-edge</i>)
     * @throws gr.forth.ics.util.NullArgumentException if any argument is null
     */
    Collection<Edge> importGraph(Graph graph, Iterable<Node> nodes);
    
    /**
     * Returns whether this graph is <i>primary</i> or not.
     * @see Graph
     * @see gr.forth.ics.graph.concrete.PrimaryGraph
     * @see gr.forth.ics.graph.concrete.SecondaryGraph
     */
    boolean isPrimary();
    
    /**
     * Reinserts a node in this graph. <strong>Note: </strong>A node can belong at any time at most
     * to one <i>primary</i> graph. Whether a graph is primary may be obtained through the 
     * {@link #isPrimary()} method. So, it this graph is primary, the node must not belong to any
     * primary graph.
     *
     * @return true if the node is actually reinserted in the graph (i.e., it was not already there)
     * @throws IllegalArgumentException when node is already contained in a primary graph
     */
    boolean reinsertNode(Node n);
    
    /**
     * Reinserts an edge in this graph. Each node of the edge that do not belong to this graph is
     * reinserted to this graph prior to reinserting the edge itself. 
     * <strong>Note: </strong>An edge can belong at any time at most
     * to one <i>primary</i> graph. Whether a graph is primary may be obtained through the 
     * {@link #isPrimary()} method. So, it this graph is primary, the edge must not belong to any
     * primary graph.
     *
     * @return true if the edge is actually reinserted in the graph (i.e., it was not already there)
     * @throws IllegalArgumentException when the edge or either of its nodes is already contained in a primary graph
     */
    boolean reinsertEdge(Edge e);
    
    /**
     * Provides methods for reordering (in terms of iteration) the elements of a graph.
     */
    interface OrderManager {
        /**
         * Moves the specified node at front of other nodes.
         * @throws IllegalArgumentException if the node does not belong to the graph
         */
        void moveNodeToFront(Node node);
        
        /**
         * Moves the specified node at back of other nodes.
         * @throws IllegalArgumentException if the node does not belong to the graph
         */
        void moveNodeToBack(Node node);
        
        /**
         * Moves the specified node exactly before the target node.
         * @throws IllegalArgumentException when either node is not contained in the graph
         */
        void moveNodeBefore(Node node, Node beforeWhat);
        
        /**
         * Moves the specified node exactly after the target node.
         * @throws IllegalArgumentException when either node is not contained in the graph
         */
        void moveNodeAfter(Node node, Node afterWhat);
        
        /**
         * Moves the specified edge to the front of the edge list, on the source or the target
         * node, depending on the boolean argument.
         * @throws IllegalArgumentException if the edge does not belong to the graph
         */
        void moveEdgeToFront(Edge edge, boolean onSourceNode);
        
        /**
         * Moves the specified edge to the back of the edge list, on the source or the target
         * node, depending on the boolean argument.
         * @throws IllegalArgumentException if the edge does not belong to the graph
         */
        void moveEdgeToBack(Edge edge, boolean onSourceNode);
        
        /**
         * Moves the specified edge exactly before the target edge. These edges must exist in the
         * same edge list, that is:
         * <ul>
         * <li>if onSourceNode is true, edge.n1() == beforeWhat.n1() must be true</li>
         * <li>if onSourceNode is false, edge.n2() == beforeWhat.n2() must be true</li>
         * </ul>
         * @throws IllegalArgumentException when edges do not belong to the same edge list,
         * or they are not contained in the graph
         */
        void moveEdgeBefore(Edge edge, boolean onSourceNode, Edge beforeWhat);
        
        /**
         * Moves the specified edge exactly after the target edge. These edges must exist in the
         * same edge list, that is:
         * <ul>
         * <li>if onSourceNode is true, edge.n1() == beforeWhat.n1() must be true</li>
         * <li>if onSourceNode is false, edge.n2() == beforeWhat.n2() must be true</li>
         * </ul>
         * @throws IllegalArgumentException when edges do not belong to the same edge list,
         * or they are not contained in the graph
         */
        void moveEdgeAfter(Edge edge, boolean onSourceNode, Edge afterWhat);
    }
}
