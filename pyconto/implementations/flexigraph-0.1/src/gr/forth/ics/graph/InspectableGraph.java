package gr.forth.ics.graph;

import gr.forth.ics.graph.event.EdgeListener;
import gr.forth.ics.graph.event.GraphListener;
import gr.forth.ics.graph.event.NodeListener;
import java.util.List;
import gr.forth.ics.util.ExtendedListIterable;

/**
 * A read-only graph. A graph G = {N, E} is a set of nodes and edges. Each edge connects exactly two
 * nodes that also belong to the same graph. That is, no edge can connect to a node that is not part
 * of the graph.
 * <p>
 * Node and edge iterators returned reflect the most current state of the graph. That is, if a node
 * iteration is in progress and a node, which has not been iterated yet, is removed, it will not
 * appear at all. Similarly, a new node may or may not appear in an iteration that started before the
 * new node addition. It is guaranteed that removed elements will not (re-)appear in iterations,
 * after their removals (assuming single-threaded execution). Elements may, or may not, be removed
 * through the iterators, depending on the graph implementation.
 * <p>
 * All iterator-style methods (e.g., {@link #nodes()}, {@link #edges()}), actually return {@link gr.forth.ics.util.ExtendedListIterable}
 * instances, to provide a richer set of operations, beyond simple iteration. Note that these methods
 * return subclasses of <code>Iterable</code>, so they can be used in the enhanced for-loop:<BR>
 * <code>
 * for (Node node : graph.nodes()) {<BR>
 * ...<BR>
 * }<BR>
 * </code>
 * <p>
 * Each graph can be decorated with arbitrary key-value entries through the {@link #tuple()} method.
 * <BR>
 * Listeners can be attached to any graph, to receive events of interest, such as additions/removals
 * of nodes/edges, etc. 
 * 
 * @see Node
 * @see Edge
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface InspectableGraph {
    /**
     * Returns nodeCount() == 0. Note that a graph with no nodes, cannot possibly have any edge.
     */
    boolean isEmpty();
    
    /**
     * Returns the number of nodes contained in this graph (and returned by {@link #nodes() nodes} method).
     */
    int nodeCount();
    
    /**
     * Returns the number of edge contained in this graph (and returned by {@link #edges() edges} method).
     */
    int edgeCount();
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all edges of this graph.
     */
    ExtendedListIterable<Edge> edges();
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all nodes of this graph.
     */
    ExtendedListIterable<Node> nodes();
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all
     * edges that contain the specified node. This is equivalent to <code>edges(node, Direction.EITHER)</code>
     *
     * @param node the node that all the returned edges must contain
     * @throws IllegalArgumentException when !containsNode(node)
     */
    ExtendedListIterable<Edge> edges(Node node);
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all
     * edges that contain the specified node at the specified direction. That is, return every edge <i>e</i>,
     * when direction is:
     * <li><code>Direction.OUT</code>, such that <code>e.n1() == node</code></li>
     * <li><code>Direction.IN</code>, such that <code>e.n2() == node</code></li>
     * <li><code>Direction.EITHER</code>, such that <code>e.isIncident(node)</code></li>
     *
     * <BR>
     * @param node the node that all the returned edges must contain
     * @param direction specifies the direction of the returned edge, relatively to the specified node
     * @throws IllegalArgumentException when !containsNode(node)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     */
    ExtendedListIterable<Edge> edges(Node node, Direction direction);
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all
     * edges that connect the specified nodes, in either direction. This is equivalent to
     * <code>edges(n1, n2, Direction.EITHER)</code>.
     *
     * @throws IllegalArgumentException when !containsNode(n1) or !containsNode(n2)
     */
    ExtendedListIterable<Edge> edges(Node n1, Node n2);
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all
     * edges that connect the specified nodes, in the specified direction. That is, return every
     * edge <i>e</i>, when direction is:
     * <li><code>Direction.OUT</code>, such that <code>e.n1() == n1 && e.n2() == n2</code></li>
     * <li><code>Direction.IN</code>, such that <code>e.n2() == n1 && e.n1() == n2</code></li>
     * <li><code>Direction.EITHER</code>, such that <code>e.isIncident(n1) && e.isIncident(n2)</code></li>
     *
     * @throws IllegalArgumentException when !containsNode(n1) or !containsNode(n2)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     */
    ExtendedListIterable<Edge> edges(Node n1, Node n2, Direction direction);
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all
     * nodes that are neighbors with the specified node. Two nodes are "neighbors" if there exists
     * an edge connecting the two. Actually, for every incident edge, a neighbor is reported, so in
     * the case of parallel edges, neighbors are reported multiple times.
     *
     * This is equivalent to <code>adjacentNodes(node, Direction.EITHER)</code>
     *
     * @see #adjacentNodes(Node, Direction)
     * @throws IllegalArgumentException when !containsNode(node)
     */
    ExtendedListIterable<Node> adjacentNodes(Node node);
    
    /**
     * Returns an {@link gr.forth.ics.util.ExtendedListIterable ExtendedListIterable} over all
     * nodes that are neighbors with the specified node, in the specified direction.
     * Two nodes are "neighbors" if there exists an edge connecting the two. Actually, for
     * every incident edge, a neighbor is reported, so in the case of parallel edges,
     * neighbors are reported multiple times.
     *
     * See {@link #edges(Node, Direction) edges(Node, Direction)} method for the semantics of
     * the Direction parameter.
     *
     * @param node the node of which the neighbors are returned
     * @param direction the direction in which to seek neighbors
     *
     * @throws IllegalArgumentException when !containsNode(node)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     */
    ExtendedListIterable<Node> adjacentNodes(Node node, Direction direction);
    
    /**
     * Returns whether there exists an edge connecting the specified nodes. This is equivalent to
     * <code>areAdjacent(n1, n2, Direction.EITHER)</code>
     *
     * @see #areAdjacent(Node, Node, Direction)
     * @throws IllegalArgumentException when !containsNode(n1) or !containsNode(n2)
     */
    boolean areAdjacent(Node n1, Node n2);
    
    /**
     * Returns whether there exists an edge connecting the specified nodes, with the specified direction.
     * That is, to return true there must be an edge <i>e</i> such that:
     * <li>If direction is <code>Direction.OUT</code>, <code>e.n1() == n1 && e.n2() == n2</code></li>
     * <li>If direction is <code>Direction.IN</code>, <code>e.n2() == n1 && e.n1() == n2</code></li>
     * <li>If direction is <code>Direction.EITHER</code>, <code>e.isIncident(n1) && e.isIncident(n2)</code></li>
     *
     * @see #areAdjacent(Node, Node, Direction)
     * @throws IllegalArgumentException when !containsNode(n1) or !containsNode(n2)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     */
    boolean areAdjacent(Node n1, Node n2, Direction direction);
    
    /**
     * Convenient method that returns an edge of this graph.
     *
     * @see #edges()
     * @throws java.util.NoSuchElementException when edgeCount() == 0
     */
    Edge anEdge();
    
    /**
     * Convenient method that returns an edge of this graph, incident to the specified node.
     *
     * @see #edges(Node)
     * @throws gr.forth.ics.util.NullArgumentException when node is null
     * @throws java.util.NoSuchElementException when no such edge exists
     */
    Edge anEdge(Node node);
    
    /**
     * Convenient method that returns an edge of this graph, incident to the specified node,
     * at the specified direction.
     *
     * @see #edges(Node, Direction)
     * @throws IllegalArgumentException when !containsNode(node)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     * @throws java.util.NoSuchElementException when no such edge exists
     */
    Edge anEdge(Node node, Direction direction);
    
    /**
     * Convenient method that returns an edge of this graph, incident to the specified nodes.
     *
     * @see #edges(Node, Node)
     * @throws IllegalArgumentException when !containsNode(n1) or !containsNode(n2)
     * @throws java.util.NoSuchElementException when no such edge exists
     */
    Edge anEdge(Node n1, Node n2);
    
    /**
     * Convenient method that returns an edge of this graph, incident to the specified nodes,
     * at the specified direction.
     *
     * @see #edges(Node, Node, Direction)
     * @throws IllegalArgumentException when !containsNode(n1) or !containsNode(n2)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     * @throws java.util.NoSuchElementException when no such edge exists
     */
    Edge anEdge(Node n1, Node n2, Direction direction);
    
    /**
     * Convenient method that returns a node of this graph.
     *
     * @see #nodes()
     * @throws java.util.NoSuchElementException when nodeCount() == 0
     */
    Node aNode();
    
    /**
     * Convenient method that returns a node of this graph that is neighbor to the specified node.
     *
     * @see #adjacentNodes(Node)
     * @throws IllegalArgumentException when !containsNode(node)
     * @throws java.util.NoSuchElementException when no such node exists
     */
    Node aNode(Node neighbor);
    
    /**
     * Convenient method that returns a node of this graph that is neighbor to the specified node,
     * in the specified direction.
     *
     * @see #adjacentNodes(Node, Direction)
     * @throws IllegalArgumentException when !containsNode(node)
     * @throws gr.forth.ics.util.NullArgumentException when direction is null
     * @throws java.util.NoSuchElementException when no such node exists
     */
    Node aNode(Node neighbor, Direction direction);
    
    /**
     * Returns whether this graph contains the specified edge. If edge is null, false is returned.
     */
    boolean containsEdge(Edge edge);
    
    /**
     * Returns whether this graph contains the specified node. If node is null, false is returned.
     */
    boolean containsNode(Node node);
    
    /**
     * Returns the in-degree of the specified node.
     * @throws IllegalArgumentException when !containsNode(node)
     */
    int inDegree(Node node);
    
    /**
     * Returns the out-degree of the specified node.
     * @throws IllegalArgumentException when !containsNode(node)
     */
    int outDegree(Node node);
    
    /**
     * Returns the degree of the specified node.
     * @throws IllegalArgumentException when !containsNode(node)
     */
    int degree(Node node);
    
    /**
     * Returns the degree of the specified node, depending on direction:
     * <li><code>Direction.OUT</code>: equivalent to outDegree(node)</li>
     * <li><code>Direction.IN</code>: equivalent to inDegree(node)</li>
     * <li><code>Direction.EITHER</code>: equivalent to degree(node)</li>
     *
     * @throws IllegalArgumentException when !containsNode(node)
     * @throws NullArgumentException when direction == null
     */
    int degree(Node node, Direction direction);
    
    /**
     * Adds a GraphListener to this graph. If listener is null, nothing happens.
     * @see GraphListener
     */
    void addGraphListener(GraphListener listener);
    
    /**
     * Removes the specified GraphListener from this graph. If listener is null, nothing happens.
     * @see GraphListener
     */
    void removeGraphListener(GraphListener listener);
    
    /**
     * Adds a NodeListener to this graph. If listener is null, nothing happens.
     * @see NodeListener
     */
    void addNodeListener(NodeListener listener);
    
    /**
     * Removes the specified NodeListener from this graph. If listener is null, nothing happens.
     * @see NodeListener
     */
    void removeNodeListener(NodeListener listener);
    
    /**
     * Adds an EdgeListener to this graph. If listener is null, nothing happens.
     * @see EdgeListener
     */
    void addEdgeListener(EdgeListener listener);
    
    /**
     * Removes the specified EdgeListener from this graph. If listener is null, nothing happens.
     * @see EdgeListener
     */
    void removeEdgeListener(EdgeListener listener);
    
    /**
     * Returns a read-only view of the currently registered node listeners.
     * @see NodeListener
     * @see GraphListener
     */
    List<NodeListener> getNodeListeners();
    
    /**
     * Returns a read-only view of the currently registered edge listeners.
     * @see EdgeListener
     * @see GraphListener
     */
    List<EdgeListener> getEdgeListeners();
    
    /**
     * Returns the (single) tuple object associated with this graph. Subsequence calls always
     * return the same object.
     */
    Tuple tuple();
    
    /**
     * Provides hints to the graph implementation about desired runtime performance. May be ignored
     * by the implementation.
     * 
     * @see Hint
     */
    void hint(Hint hint);
}
