package gr.forth.ics.graph;

import java.util.HashMap;
import java.util.Map;
import gr.forth.ics.util.Args;

//TODO: Create tests, for directions etc
import java.util.Collections;

/**
 * This class provides a simple utility to create copies of graphs. Also provides, upon request,
 * maps to navigate from a copied node/edge to each copy counterpart, and/or the reverse.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class GraphCloner {
    private final boolean copyData;
    private final Direction nodeMapDirection;
    private final Direction edgeMapDirection;
    private Map<Node, Node> nodeMap;
    private Map<Edge, Edge> edgeMap;
    
    /**
     * Creates a GraphCloner.
     *
     * @param copyData if true, copies (shallow copy) internal node and edge data
     *  (via {@link gr.forth.ics.graph.Tuple#copyInto(gr.forth.ics.graph.Tuple) Tuple.copyInto(Tuple)} method).
     * @param nodeMapDirection specifies what the node map return value of {@link #getNodeMap()} should have.
     *   Possible values:
     *   <li>null : node map will be null
     *   <li>Direction.OUT : each source node will be a key, with its copy counterpart as value.
     *   <li>Direction.IN : each copied node will be a key, with its source counterpart as value.
     *   <li>Direction.EITHER : Both Direction.IN and Direction.OUT, as above, combined in the same map.
     *
     * @param edgeMapDirection specifies what the edge map return value of {@link #getEdgeMap()} should have.
     *   Possible values:
     *   <li>null : edge map will be null
     *   <li>Direction.OUT : each source edge will be a key, with its copy counterpart as value.
     *   <li>Direction.IN : each copied edge will be a key, with its source counterpart as value.
     *   <li>Direction.EITHER : Both Direction.IN and Direction.OUT, as above, combined in the same map.
     */
    public GraphCloner(boolean copyData, Direction nodeMapDirection, Direction edgeMapDirection) {
        this.copyData = copyData;
        this.nodeMapDirection = nodeMapDirection;
        this.edgeMapDirection = edgeMapDirection;
    }
    
    /**
     * Copies the specified graph, into the supplied graph instance, and returns the copy.
     *
     * @param source the graph to be copied (must be non-null)
     * @param copy the graph into which to create the copy (must be non-null)
     * @return the graph copy (same as second parameter)
     */
    public Graph copy(InspectableGraph source, Graph copy) {
        Args.notNull(source, copy);
        if (edgeMapDirection != null) {
            edgeMap = new HashMap<Edge, Edge>();
        } else {
            edgeMap = null;
        }
        nodeMap = new HashMap<Node, Node>();
        for (Node n : source.nodes()) {
            Node cn = copy.newNode();
            if (copyData) {
                n.copyInto(cn);
            }
            nodeMap.put(n, cn);
            if (nodeMapDirection != null && nodeMapDirection.isIn()) {
                nodeMap.put(cn, n);
            }
        }
        for (Edge e : source.edges()) {
            Node cn1 = nodeMap.get(e.n1());
            Node cn2 = nodeMap.get(e.n2());
            Edge ce = copy.newEdge(cn1, cn2);
            if (copyData) {
                e.copyInto(ce);
            }
            if (edgeMapDirection != null) {
                if (edgeMapDirection.isOut()) {
                    edgeMap.put(e, ce);
                }
                if (edgeMapDirection.isIn()) {
                    edgeMap.put(ce, e);
                }
            }
        }
        if (nodeMapDirection == null) {
            nodeMap = null;
        }
        return copy;
    }
    
    /**
     * Returns a modifiable map with nodes, the semantics of which is defined in the
     * {@link #GraphCloner(boolean, gr.forth.ics.graph.Direction, gr.forth.ics.graph.Direction) GraphCopier} constructor.
     */
    public Map<Node, Node> getNodeMap() {
        return nodeMap;
    }
    
    /**
     * Returns a modifiable map with edges, the semantics of which is defined in the
     * {@link #GraphCloner(boolean, gr.forth.ics.graph.Direction, gr.forth.ics.graph.Direction) GraphCopier} constructor.
     */
    public Map<Edge, Edge> getEdgeMap() {
        return edgeMap;
    }
    
    public static class GraphMapper {
        private final Map<Node, Node> nodeMap;
        private final Map<Edge, Edge> edgeMap;
        
        private GraphMapper(Map<Node, Node> nodeMap,
                Map<Edge, Edge> edgeMap) {
            this.nodeMap = Collections.unmodifiableMap(nodeMap);
            this.edgeMap = Collections.unmodifiableMap(edgeMap);
        }
        
        public Map<Node, Node> getNodeMap() {
            return nodeMap;
        }
        
        public Map<Edge, Edge> getEdgeMap() {
            return edgeMap;
        }
    }
}
