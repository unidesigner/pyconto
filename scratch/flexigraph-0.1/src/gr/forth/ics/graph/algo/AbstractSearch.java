package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Args;

public abstract class AbstractSearch {
    protected final InspectableGraph graph;
    protected Node startNode;
    
    private int treeCount;
    
    private Object NODE_TREE_INFO;
    
    public AbstractSearch(InspectableGraph graph) {
        this(graph, null);
    }
    
    public AbstractSearch(InspectableGraph graph, Node startNode) {
        Args.notNull(graph);
        this.graph = graph;
        this.startNode = startNode;
    }
    
    public Node getStartNode() {
        return startNode;
    }
    
    public void setStartNode(Node node) {
        this.startNode = node;
    }
    
    public InspectableGraph getGraph() {
        return graph;
    }
    
    public final void execute() {
        NODE_TREE_INFO = new Object();
        if (startNode == null) {
            if (graph.nodeCount() == 0) {
                return;
            }
            startNode = graph.aNode();
        }
        Args.isTrue("Start node not contained in graph", graph.containsNode(startNode));
        treeCount = 0;
        executeImpl();
    }
    
    protected abstract void executeImpl();
    
    protected void incTreeNumber() {
        treeCount++;
    }
    
    public int getComponentCount() {
        return treeCount;
    }
    
    protected void markNodeTree(Node node, Object treeIdentifier) {
        node.putWeakly(NODE_TREE_INFO, treeIdentifier);
    }
    
    //can override to truncate path stored
    protected Path storePath(Path path) {
        return path;
    }
    
    /** Return true to break */
    protected boolean visitNewTree(Node node) { return false; }
    
    public Object getComponentIdentifier(Node node) {
        return node.get(NODE_TREE_INFO);
    }
}
