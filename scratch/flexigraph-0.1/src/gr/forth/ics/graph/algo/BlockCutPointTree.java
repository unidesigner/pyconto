package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.SecondaryGraph;
import gr.forth.ics.util.Args;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A block-cutpoint tree.
 *
 * <p>A block-cutpoint tree is derived from an arbitrary graph and defined as a tree (accessed with {@linkplain #get()} with:
 * <ul>
 * <li>one distinct node per biconnected component of the graph ({@linkplain #isBlock(Node)} returns true for such a node)
 * <li>one distinct node per cutpoint of the graph ({@linkplain #isCutNode(Node)} returns true for such a node)
 * <li>one edge for each adjacent pair of biconnected component and cut node, i.e. each cut node has one edge for each
 * biconnected component it belongs to (all edges connect exactly one block node and one cut node). The edges are
 * directed from the cut nodes towards the block nodes.
 * </ul>
 *
 * <p>Notice that cutpoint nodes are <em>shared</em> between the original graph and the block-cutpoint tree.
 *
 * @see <pre>J. Hopcroft and R. Tarjan. Efficient algorithms for graph manipulation. Comm. ACM, 16:372-378, 1973</pre>
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class BlockCutPointTree {
    private final InspectableGraph graph;
    private final SecondaryGraph blockCutPointTree;
    private final Object componentEdgesKey = new Object();
    private final Biconnectivity bicon;
    
    private BlockCutPointTree(InspectableGraph graph) {
        Args.notNull(graph);
        this.graph = graph;
        this.bicon = Biconnectivity.execute(graph);
        this.blockCutPointTree = new SecondaryGraph();
        Map<Object, Node> blocks = new HashMap<Object, Node>();
        for (Edge e : graph.edges()) {
            Object component = bicon.componentOf(e);
            Node block = blocks.get(component);
            Set<Edge> blockEdges;
            if (block == null) {
                blocks.put(component, block = blockCutPointTree.newNode());
                block.putWeakly(componentEdgesKey, blockEdges = new HashSet<Edge>());
                block.setValue(Collections.unmodifiableSet(blockEdges));
            } else {
                @SuppressWarnings("unchecked")
                Set<Edge> set = (Set<Edge>)block.get(componentEdgesKey);
                blockEdges = set;
            }
            //by now, "block" is a node corresponding to the block containing edge "e",
            //and "blockEdges" is the set of that block's edges.
            blockEdges.add(e);
        }

        for (Node n : graph.nodes()) {
            if (bicon.isCutNode(n)) {
                blockCutPointTree.adoptNode(n);
                Set<Object> neighborBlocks = new HashSet<Object>();
                for (Edge e : graph.edges(n)) {
                    neighborBlocks.add(bicon.componentOf(e));
                }
                for (Object neighborBlock : neighborBlocks) {
                    Node block = blocks.get(neighborBlock);
                    blockCutPointTree.newEdge(n, block);
                }
            }
        }
    }

    /**
     * Creates a block-cutpoint tree from a graph. The specified graph can be accessed from the
     * {@linkplain #getOriginalGraph()} method of the returned instance.
     *
     * @param graph the graph from which to create a block-cutpoint tree
     * @return a block-cutpoint tree based on the biconnected components and cut nodes of the specified graph
     */
    public static BlockCutPointTree execute(InspectableGraph graph) {
        return new BlockCutPointTree(graph);
    }

    /**
     * Returns the graph representing the block-cutpoint tree.
     *
     * @return the graph representing the block-cutpoint tree
     * @see BlockCutPointTree for the specification of a block-cutpoint tree and references
     */
    public InspectableGraph get() {
        return blockCutPointTree;
    }

    /**
     * Returns the graph instance from which this block-cutpoint tree was created.
     *
     * @return the graph instance from which this block-cutpoint tree was created
     */
    public InspectableGraph getOriginalGraph() {
        return graph;
    }

    /**
     * Returns whether the specified node corresponds to a block (biconnected component) of the original graph.
     * 
     * @param n a node of the block-cutpoint tree
     * @return whether the node corresponds to a block (biconnected component) of the original graph
     * @throws IllegalArgumentException if {@code cutNode} does not belong to the block-cutpoint tree
     */
    public boolean isBlock(Node n) {
        checkOwned(n);
        return n.has(componentEdgesKey);
    }

    /**
     * Returns the set of edges comprising the block (biconnected component) corresponding to a block node of the
     * block-cutpoint tree.
     *
     * @param blockNode a node of the block-cutpoint tree corresponding to a block node
     * @return the set of edges comprising the block (biconnected component) corresponding to a block node of the
     * block-cutpoint tree
     * @throws IllegalArgumentException if {@code blockNode} does not belong to the block-cutpoint tree or
     * is not a block node
     */
    public Set<Edge> getBlockEdges(Node blockNode) {
        @SuppressWarnings("unchecked")
        Set<Edge> edges = (Set<Edge>)blockNode.get(componentEdgesKey);
        if (edges == null) {
            throw new IllegalArgumentException("Not a block node");
        }
        return edges;
    }

    /**
     * Returns whether the specified node corresponds to a cutpoint of the original graph. If the
     * specified node is indeed a cutpoint, then it will be the same node instance as the cutpoint
     * in the original graph (i.e. cutpoint nodes are shared between the original graph and the block-cutpoint tree).
     *
     * @param n a node of the block-cutpoint tree
     * @return whether the node corresponds to a cutpoint of the original graph
     * @throws IllegalArgumentException if {@code cutNode} does not belong to the block-cutpoint tree
     */
    public boolean isCutNode(Node n) {
        return !isBlock(n);
    }

    private void checkOwned(Node n) {
        if (!blockCutPointTree.containsNode(n)) {
            throw new IllegalArgumentException("Not a node of the block-cutpoint tree: " + n);
        }
    }

    /**
     * Returns the result of the biconnectivity algorithm used to derive this block-cutpoint tree. It can be accessed to see for example
     * which nodes of the original graph (the one from which the block-cutpoint tree was derived) are cutpoints etc.
     *
     * @return the result of the biconnectivity algorithm used to derive this block-cutpoint tree
     */
    public Biconnectivity getBiconnectivity() {
        return bicon;
    }
}
