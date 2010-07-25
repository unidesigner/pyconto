package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.path.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import gr.forth.ics.util.Args;

/**
 * Offers biconnectivity analysis for graphs.
 * 
 * <p>(Revised from an online version of Andrew Schwerin).
 *
 * @author Andrew Schwerin
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Biconnectivity {
    private final Object DISCOVERER = new Object();
    private final Object MARKING_NODE = new Object();
    private final Object IS_CUT_NODE = new Object();
    
    private final InspectableGraph graph;
    private final Dfs dfs;
    private final Graph cycleGraph;
    private final Clusterer connectedComponents;

    private final int rootsCount;
    
    private Biconnectivity(InspectableGraph graph) {
        Args.notNull(graph);
        final List<Node> roots = new LinkedList<Node>();
        this.graph = graph;
        dfs = new Dfs(graph, Direction.EITHER) {
            @Override protected boolean visitNewTree(Node v) {
                roots.add(v);
                return false;
            }
            
            @Override protected boolean visitTreeEdge(Path path) {
                Node to = path.tailNode();
                Edge e = path.tailEdge();
                to.putWeakly(DISCOVERER, e);
                return false;
            }
        };
        cycleGraph = new PrimaryGraph();

        dfs.execute();
        this.rootsCount = roots.size();

        for (Node n : roots) {
            preorderTraverse(n);
        }

        connectedComponents = Clusterers.connectedComponents(cycleGraph);

        for(Node n : graph.nodes()) {
            computeCutVertex(n);
        }
    }
    
    /**
     * Executes the biconnectivity analyzer on the given graph.
     * This algorithm is of {@code O(|N| + |E|)} time complexity.
     * 
     * @return a Biconnectivity that can be queried to access the results of the algorithm execution
     */
    public static Biconnectivity execute(InspectableGraph graph) {
        return new Biconnectivity(graph);
    }
    
    /**
     * Returns the biconnected component to which the given Edge belongs.
     * Different components are guaranteed to be represented by distinct objects.
     * 
     * @param e an edge in the graph upon which the algorithm was executed
     * @return an object representing the component to which the Edge e belongs, or
     * {@code null} if the edge was not present in the graph when the algorithm was executed
     * @exception IllegalArgumentException if the given edge does not belong to
     *      the graph upon which the algorithm was executed
     */
    public Object componentOf(Edge e) throws IllegalArgumentException {
        if(!graph.containsEdge(e)) {
            throw new IllegalArgumentException("Edge " + e + " does not belong to the graph upon which this algorithm was most recently executed");
        }
        Node nodeMarking = nodeMarking(e);
        if (nodeMarking == null) {
            return null;
        }
        return connectedComponents.findClusterOf(nodeMarking);
    }
    
    /**
     * Returns whether the specified node is a cut node, that is, a node that if it is removed,
     * the graph becomes disconnected.
     * 
     * @param v the node to check whether it is a cut node
     * @return whether the specified node is a cut node, that is, a node that if it is removed,
     *      the graph becomes disconnected
     * @throws IllegalArgumentException if the specified node does not belong to the
     *      graph upon which the algorithm executed
     */
    public boolean isCutNode(Node v) throws IllegalArgumentException {
        if(!graph.containsNode(v)) {
            throw new IllegalArgumentException("Node " + v + " does not belong to the graph upon which this algorithm was most recently executed");
        }
        
        return v.getBoolean(IS_CUT_NODE);
    }
    
    private void computeCutVertex(Node v) {
        Iterator<Edge> edges = graph.edges(v).iterator();
        if (edges.hasNext()) {
            Object component = componentOf(edges.next());
            while(edges.hasNext()) {
                if (!component.equals( componentOf(edges.next()))) {
                    v.putWeakly(IS_CUT_NODE, true);
                    return;
                }
            }
        }
        v.putWeakly(IS_CUT_NODE, false);
    }
    
    private void preorderTraverse(Node v) {
        LinkedList<Edge> discoveryEdges = new LinkedList<Edge>();
        
        for (Edge e : graph.edges(v)) {
            // Not only must e be a tree edge, but v must be the parent
            // and opposite(v, e) must be the child.
            if (dfs.isTreeEdge(e)
                && dfs.getParent(e.opposite(v)) == v) {
                discoveryEdges.addFirst(e);
            } else if (dfs.isBackEdge(e) && !isMarked(e)) {
                Node vE = cycleGraph.newNode(e);
                mark(e, vE);
                Node u = e.opposite(v);
                while (u != v) {
                    Edge discoversU = discoverer(u);
                    if (isMarked(discoversU)) {
                        cycleGraph.newEdge(nodeMarking(discoversU), vE, null);
                        break;
                    } else {
                        Node vDU = cycleGraph.newNode(discoversU);
                        mark(discoversU, vDU);
                        cycleGraph.newEdge(vE, vDU, null);
                    }
                    u = discoversU.opposite(u);
                } // while
            } // else if is back edge
        }
        for (Edge e : discoveryEdges) {
            preorderTraverse( e.opposite(v) );
            
            // If it's not marked yet, it's a bridge.  The following code
            // interprets bridge edges as biconnected components
            if (!isMarked(e)) {
                mark(e, cycleGraph.newNode(e));
            }
        }
    }
    
    private void mark(Edge e, Node vE) {
        e.putWeakly(MARKING_NODE, vE);
    }
    
    private boolean isMarked(Edge e) {
        return e.has(MARKING_NODE);
    }
    
    private Node nodeMarking(Edge e) {
        return e.getNode(MARKING_NODE);
    }
    
    private Edge discoverer(Node v) {
        return v.getEdge(DISCOVERER);
    }
    
    public InspectableGraph getGraph() {
        return graph;
    }

    /**
     * Returns the number of (one-)connected components found when the biconnectivity
     * algorithm ran.
     *
     * @return the number of (one-)connected components found when the biconnectivity
     * algorithm ran
     */
    public int componentsCount() {
        return rootsCount;
    }
}

