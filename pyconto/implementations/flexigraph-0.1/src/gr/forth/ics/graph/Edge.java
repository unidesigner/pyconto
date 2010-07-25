package gr.forth.ics.graph;

import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Accessor;
import gr.forth.ics.graph.Tuple;

//Exceptions are thrown on invalid parameters
/**
 * An edge that connects two {@link Node nodes}. The incident nodes of an edge can neither be changed
 * nor be reordered. This immutability means that is it safe for an edge to be shared between
 * {@link InspectableGraph graphs}.
 * 
 * @see Graph
 * @see Node
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Edge extends Tuple {
    /**
     * Returns the first incident node of this edge.
     */
    Node n1();
    
    /**
     * Returns the first incident node of this edge.
     */
    Node n2();
    
    /**
     * Returns true if the specified node is either n1() or n2().
     */
    boolean isIncident(Node node);
    
    /**
     * Returns the opposite incident node of the specified node of this edge.
     *
     * @return <li>n1(), if specified node is n2(),
     *          <li>n2(), if specified node is n1()
     * @throws RuntimeException if the supplied node is neither n1() nor n2().
     */
    Node opposite(Node node);
    
    /**
     * Returns whether n1() == n2().
     */
    boolean isSelfLoop();
    
    /**
     * Returns true if this edge shares at least one incident node with the specified node.
     * @throws NullArgumentException when the supplied edge is null.
     */
    boolean isIncident(Edge other);
    
    /**
     * Equivalent to calling {@link #getIntersection(boolean, Edge) getIntersection(true, other)}.
     */
    Node getIntersection(Edge other);
    
    /**
     * Returns a node that both this edge and the supplied edge contain. If no such node exists, null
     * is returned.
     * @param startFromN1 if true, and both n1() and n2() are eligible, return n1(), or else return n2()
     * @param other the edge to be tested against this edge.
     * @throws NullArgumentException when other is null
     */
    Node getIntersection(boolean startFromN1, Edge other);
    
    /**
     * Returns true if the this edge and the supplied edge, are incident and form a directed path
     * when put together.
     * @see #testOrientation(Edge)
     * @throws NullArgumentException when other is null
     */
    boolean areParallel(Edge other);
    
    /**
     * Tests the relative orientation of this edge towards a supplied edge. This is a finer-grained
     * version of the {@link #areParallel(Edge) areParallel} method. If the two edges have no
     * common node, or one of the edges are self-loops then Orientation.UNDEFINED is returned. If the
     * edges form a directed path, then Orientation.SAME is returned. Finally, if nothing of this holds,
     * that is, the edges are connected but do not form a directed path, Orientation.OPPOSITE is returned. 
     *
     * @param other the other edge to test the orientation with
     * @throws NullArgumentException when other is null
     * @return <li>Orientation.UNDEFINED, if <code>!isIncident(other) || isSelfLoop() || other.isSelfLoop()</code>,
     *          <li>Orientation.SAME, if <code>n2() == other.n1() || n1() == other.n2()</code>
     *          <li>Orientation.OPPOSITE, if <code>n1() == other.n1() || n2() == other.n2()</code>
     */
    Orientation testOrientation(Edge other);

    /**
     * Returns this edge as a {@link gr.forth.ics.graph.path.Path Path}.
     * 
     * @see gr.forth.ics.graph.path.Path Path
     */
    Path asPath();
    
    /**
     * Returns this edge as a {@link gr.forth.ics.graph.path.Path Path}, with the specified node as a head node.
     *
     * @param headNode a node that belongs to this edge, where the returned path will start from
     * @return a path that starts at the specified node, with this edge as its only edge
     * @see gr.forth.ics.graph.path.Path Path
     */
    Path asPath(Node headNode);
    
    /**
     * The kinds of relationship that two edges may have.
     *
     * @see #testOrientation
     */
    enum Orientation {
        SAME, OPPOSITE, UNDEFINED
    }
}
