package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.path.Traverser;
import gr.forth.ics.graph.path.Traverser.PathIterator;
import gr.forth.ics.util.Args;

/**
 * A breadth-search first graph traversal.
 */
public class Bfs extends AbstractSearch {
    private static enum EdgeType {
        treeEdge, crossEdge;
    }
    
    private Object EDGE_TYPE;
    private Object NODE_INFO;
    
    private Traverser bfsTraverser;

    private final Direction direction;
    
    public Bfs(InspectableGraph graph, Direction direction) {
        this(graph, null, Direction.OUT);
    }
    
    public Bfs(InspectableGraph graph, Node startNode, Direction direction) {
        super(graph, startNode);
        Args.notNull(direction);
        this.direction = direction;
    }
    
    private int layerCount;
    
    private void initKeys() {
        EDGE_TYPE = new Object();
        NODE_INFO = new Object();
    }
    
    protected void executeImpl() {
        initKeys();
        layerCount = 0;
        bfsTraverser = Traverser.newBfs().notRepeatingEdges().build();
        boolean exit = bfs(startNode);
        if (!exit) {
            for (Node n : graph.nodes()) {
                if (!isVisited(n)) {
                    exit = bfs(n);
                    if (exit) {
                        break;
                    }
                }
            }
        }
        bfsTraverser = null;
    }
    
    //return true to break
    protected boolean bfs(Node start) {
        Object tree = new Object();
        incTreeNumber();
        markNodeTree(start, tree);
        markNode(start, 0, null);
        if (visitNewTree(start)) {
            return true;
        }
        final boolean[] exit = new boolean[1];
        PathIterator iterator = bfsTraverser.traverse(graph, start, direction).iterator();
        while (iterator.hasNext()) {
            Path path = iterator.next();
            if (path.size() == 0) {
                continue;
            }
            Edge e = path.tailEdge();
            Node node = path.tailNode();

            if (isVisited(node)) {
                markEdge(e, EdgeType.crossEdge);
                if (visitCrossEdge(path)) {
                    return true;
                }
                iterator.skipExplorationOfLastPath();
                continue;
            } else {
                markEdge(e, EdgeType.treeEdge);
            }

            Node parent = e.opposite(node);
            int level = getLevel(parent) + 1;
            markNodeTree(node, getComponentIdentifier(parent));
            markNode(node, level, e);

            if (visitTreeEdge(path)) {
                return true;
            }
        }
        return false;
    }
    
    /** Return true to break */
    protected boolean visitTreeEdge(Path path) { return false; }
    
    /** Return true to break */
    protected boolean visitCrossEdge(Path path) { return false; }
    
    public boolean isTreeEdge(Edge e) {
        return isType(e, EdgeType.treeEdge);
    }
    
    public boolean isCrossEdge(Edge e) {
        return isType(e, EdgeType.crossEdge);
    }
    
    private boolean isType(Edge e, EdgeType type) {
        return e.get(EDGE_TYPE) == type;
    }
    
    public boolean isVisited(Node node) {
        return getNodeInfo(node) != null;
    }
    
    public Node getParent(Node node) {
        Edge parent = getParentEdge(node);
        if (parent == null) {
            return null;
        }
        return parent.opposite(node);
    }
    
    public Edge getParentEdge(Node node) {
        NodeInfo nodeInfo = getNodeInfo(node);
        if (nodeInfo == null) {
            throw new RuntimeException("Node has not been visited by this bfs (has bfs been executed?)");
        }
        return nodeInfo.parent;
    }
    
    public int getLayerCount() {
        return layerCount;
    }
    
    public int getLevel(Node node) {
        NodeInfo nodeInfo = getNodeInfo(node);
        if (nodeInfo == null) {
            throw new RuntimeException("Node has not been visited by this bfs (has bfs been executed?)");
        }
        return nodeInfo.level;
    }
    
    private NodeInfo getNodeInfo(Node node) {
        Args.notNull(node);
        return (NodeInfo)node.get(NODE_INFO);
    }
    
    private void markNode(Node node, int level, Edge parent) {
        layerCount = Math.max(layerCount, level);
        node.putWeakly(NODE_INFO, new NodeInfo(level, parent));
    }
    
    private void markEdge(Edge edge, EdgeType type) {
        edge.putWeakly(EDGE_TYPE, type);
    }
    
    private static class NodeInfo {
        final int level;
        final Edge parent;
        
        NodeInfo(int level, Edge parent) {
            this.level = level;
            this.parent = parent;
        }
    }
}