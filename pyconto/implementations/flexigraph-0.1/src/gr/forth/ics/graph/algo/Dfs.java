package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import java.util.*;
import gr.forth.ics.util.Args;

//TODO: can be implemented on top of a GraphTraversal?
//TODO: assign tree number, then call template. So, subclass to find connected components
//TODO: kanonika i execute 8a eprepe na dinei ena antikeimeno me apotelesmata
//TODO: kapoia static me8odos gia na ekteleitai o DFS wste na pairneis katey8eian to apotelesma - xwris na exeis antikeimeno DFS
/**
 *
 */
public class Dfs extends AbstractSearch {
    private static enum EdgeType {
        treeEdge, forwardEdge, backEdge, crossEdge;
    }
    
    private Object EDGE_INFO;
    private Object NODE_INFO;
    
    private int time;

    private final Direction direction;
    
    public Dfs(InspectableGraph graph, Direction direction) {
        this(graph, null, direction);
    }
    
    public Dfs(InspectableGraph graph, Node startNode, Direction direction) {
        super(graph, startNode);
        Args.notNull(direction);
        this.direction = direction;
    }
    
    private void initKeys() {
        EDGE_INFO = new Object();
        NODE_INFO = new Object();
    }
    
    protected void executeImpl() {
        initKeys();
        time = 0;
        boolean exit = dfs(startNode);
        if (!exit) {
            for (Node n : graph.nodes()) {
                if (isUnexplored(n)) {
                    exit = dfs(n);
                    if (exit) {
                        break;
                    }
                }
            }
        }
    }
    
    protected boolean dfs(Node start) {
        Object tree = new Object();
        incTreeNumber();
        markNodeTree(start, tree);
        if (visitNewTree(start)) {
            return true;
        }
        Stack stack = new Stack();
        stack.push(start.asPath(), start, graph.edges(start, direction).iterator());
        outLoop:
            while (!stack.isEmpty()) {
                ExecutionPoint executionPoint = stack.pop();
                Node root = executionPoint.node;
                Iterator<Edge> iterator = executionPoint.iterator;
                NodeInfo rootInfo = getNode(root);
                if (rootInfo == null || rootInfo.justCreated) {
                    Edge parent = (rootInfo == null ? null : rootInfo.parent);
                    rootInfo = new NodeInfo(time++, parent);
                    rootInfo.justCreated = false;
                    markNode(root, rootInfo);
                    markNodeTree(root, tree);
                    if (visitPre(executionPoint.parent)) {
                        return true;
                    }
                }
                while (iterator.hasNext()) {
                    Edge e = iterator.next();
                    Path currentPath = executionPoint.parent.append(e.asPath(executionPoint.parent.tailNode()));
                    currentPath = storePath(currentPath);
                    if (getEdge(e) != null) { //prevents loop-backs for undirected graphs
                        continue;
                    }
                    Node other = e.opposite(root);
                    NodeInfo otherInfo = getNode(other);
                    if (otherInfo == null) { //unexplored
                        markEdge(e, EdgeType.treeEdge);
                        if (visitTreeEdge(currentPath)) {
                            return true;
                        }
                        //store current execution point, store next point and continue;
                        stack.push(executionPoint.parent, root, iterator);
                        stack.push(currentPath, other, graph.edges(other, direction).iterator());
                        otherInfo = new NodeInfo(time, e);
                        markNode(other, otherInfo);
                        continue outLoop;
                    } else if (otherInfo.doneVisiting == false) { //back edge
                        markEdge(e, EdgeType.backEdge);
                        if (visitBackEdge(currentPath)) {
                            return true;
                        }
                    } else {
                        if (otherInfo.time.endTime < rootInfo.time.startTime) {
                            markEdge(e, EdgeType.crossEdge);
                            if (visitCrossEdge(currentPath)) {
                                return true;
                            }
                        } else {
                            markEdge(e, EdgeType.forwardEdge);
                            if (visitForwardEdge(currentPath)) {
                                return true;
                            }
                        }
                    }
                }
                rootInfo.time.endTime = time++;
                if (visitPost(executionPoint.parent)) {
                    return true;
                }
                rootInfo.doneVisiting = true;
            }
            return false;
    }
    
   
   
    /** Return true to break */
    protected boolean visitTreeEdge(Path path) { return false; }
    
        /** Return true to break */
    protected boolean visitPre(Path path) { return false; }
    
    /** Return true to break */
    protected boolean visitPost(Path path) { return false; }
    
    /** Return true to break */
    protected boolean visitForwardEdge(Path path) { return false; }
    
    /** Return true to break */
    protected boolean visitBackEdge(Path path) { return false; }
    
    /** Return true to break */
    protected boolean visitCrossEdge(Path path) { return false; }
    
    private NodeInfo getNode(Node node) {
        return (NodeInfo)node.get(NODE_INFO);
    }
    
    private void markEdge(Edge edge, EdgeType type) {
        edge.putWeakly(EDGE_INFO, type);
    }
    
    private EdgeType getEdge(Edge e) {
        return (EdgeType)e.get(EDGE_INFO);
    }
    
    private void markNode(Node node, NodeInfo info) {
        node.putWeakly(NODE_INFO, info);
    }
    
    public boolean isTreeEdge(Edge e) {
        return isType(e, EdgeType.treeEdge);
    }
    
    public boolean isCrossEdge(Edge e) {
        return isType(e, EdgeType.crossEdge);
    }
    
    public boolean isForwardEdge(Edge e) {
        return isType(e, EdgeType.forwardEdge);
    }
    
    public boolean isBackEdge(Edge e) {
        return isType(e, EdgeType.backEdge);
    }
    
    private boolean isType(Edge e, EdgeType type) {
        EdgeType edgeType = getEdge(e);
        if (edgeType == null) {
            return false;
        }
        return edgeType == type;
    }
    
    public boolean isUnexplored(Node node) {
        return getNode(node) == null;
    }
    
    public boolean isVisited(Node node) {
        NodeInfo info = getNode(node);
        if (info == null) {
            return false;
        }
        return info.doneVisiting;
    }
    
    public boolean isVisiting(Node node) {
        NodeInfo info = getNode(node);
        if (info == null) {
            return false;
        }
        return !info.doneVisiting;
    }
    
    public Time getTime(Node node) {
        NodeInfo info = getNode(node);
        if (info == null) {
            return null;
        }
        return info.time;
    }
    
    public Node getParent(Node node) {
        Edge parent = getParentEdge(node);
        if (parent == null) {
            return null;
        }
        return parent.opposite(node);
    }
    
    public Edge getParentEdge(Node node) {
        NodeInfo info = getNode(node);
        if (info == null) {
            return null;
        }
        return info.parent;
    }
    
    private static class NodeInfo {
        final Time time;
        boolean doneVisiting = false;
        boolean justCreated = true;
        final Edge parent;
        
        NodeInfo(int time) {
            this(time, null);
        }
        
        NodeInfo(int time, Edge parent) {
            this.time = new Time(time);
            this.parent = parent;
        }
    }
    
    public static class Time {
        int startTime = -1;
        int endTime = -1;
        
        Time(int start) {
            this.startTime = start;
        }
        
        public int getStart() {
            return startTime;
        }
        
        public int getFinish() {
            return endTime;
        }
        
        public String toString() {
            return "[" + startTime + ".." + endTime + "]";
        }
    }
    
    private static class Stack {
        private final LinkedList<ExecutionPoint> list = new LinkedList<ExecutionPoint>();
        
        void push(Path current, Node node, Iterator<Edge> iterator) {
            list.addLast(new ExecutionPoint(current, node, iterator));
        }
        
        ExecutionPoint pop() {
            return list.removeLast();
        }
        
        boolean isEmpty() {
            return list.isEmpty();
        }
    }
    
    private static class ExecutionPoint {
        final Node node;
        final Iterator<Edge> iterator;
        final Path parent;
        
        ExecutionPoint(Path parent, Node node, Iterator<Edge> iterator) {
            this.parent = parent;
            this.node = node;
            this.iterator = iterator;
        }
    }
}
