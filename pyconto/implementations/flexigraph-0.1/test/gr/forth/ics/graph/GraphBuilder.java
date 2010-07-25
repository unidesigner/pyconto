package gr.forth.ics.graph;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class GraphBuilder {
    private final Graph graph;
    
    public GraphBuilder(Graph graph) {
        Args.notNull(graph);
        this.graph = graph;
    }
    
    public Graph getGraph() {
        return graph;
    }
    
    public Edge[] newEdges(Node ... nodes) {
        return newEdges(false, nodes);
    }
    
    /**
     * Equivalent to {@link #newEdges(boolean, Node[]) newPath(false, nodes)}.
     */
    public Edge[] newEdges(boolean closedPath, Node ... nodes) {
        Edge[] e = new Edge[nodes.length - (closedPath ? 0 : 1)];
        int pos = 0;
        for (int i = 1; i < nodes.length; i++) {
            e[pos++] = graph.newEdge(nodes[i - 1], nodes[i]);
        }
        if (closedPath && nodes.length > 0) {
            e[pos++] = graph.newEdge(nodes[nodes.length - 1], nodes[0]);
        }
        return e;
    }
    
    public NodeBuilder newNodes(Object ... values) {
        return new NodeBuilder(graph.newNodes(values));
    }
    
    public NodeBuilder newNodes(int count) {
        return new NodeBuilder(graph.newNodes(count));
    }
    
    /**
     * Equivalent to {@link #newPath(boolean, Node[]) newPath(false, nodes)}.
     */
    public Path newPath(Node ... nodes) {
        return newPath(false, nodes);
    }
    
    //if nodes.length == 0, null is returned
    public Path newPath(boolean closedPath, Node ... nodes) {
        Args.notNull((Object[])nodes);
        if (nodes.length == 0) {
            return null;
        }
        Path result = nodes[0].asPath();
        for (int i = 1; i < nodes.length; i++) {
            result = result.append(graph.newEdge(nodes[i - 1], nodes[i]).asPath());
        }
        if (closedPath && nodes.length > 0) {
            result = result.append(graph.newEdge(nodes[nodes.length - 1], nodes[0]).asPath());
        }
        return result;
    }
    
    public class NodeBuilder {
        final Node[] nodes;
        
        private NodeBuilder(Node[] nodes) {
            this.nodes = nodes;
        }
        
        public Node[] getNodes() {
            return nodes;
        }
        
        public NodeBuilder newPath(int ... indices) {
            return newPath(false, indices);
        }
        
        public NodeBuilder newPath(boolean closedPath, int ... indices) {
            Node last = null;
            for (int i : indices) {
                Node cur = nodes[i];
                if (last != null) {
                    graph.newEdge(last, cur);
                }
                last = cur;
            }
            if (closedPath && nodes.length > 0) {
                graph.newEdge(nodes[nodes.length - 1], nodes[0]);
            }
            return this;
        }
        
        public NodeBuilder newEdge(int n1, int n2) {
            graph.newEdge(nodes[n1], nodes[n2]);
            return this;
        }
        
        public NodeBuilder newEdges(int[][] pairsArray) {
            for (int[] pair : pairsArray) {
                graph.newEdge(nodes[pair[0]], nodes[pair[1]]);
            }
            return this;
        }
    }
}