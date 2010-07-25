package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.layout.ArithmeticTestCase;
import gr.forth.ics.graph.Graph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.GraphBuilder;
import gr.forth.ics.graph.GraphBuilder.NodeBuilder;

public abstract class AbstractMetricsTest extends ArithmeticTestCase {
    public AbstractMetricsTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        return suite;
    }
    
    protected Node[] createGraph2(Graph g) {
        GraphBuilder gb = new GraphBuilder(g);
        Node[] n = gb.newNodes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
            .newPath(5, 6, 8, 7, 5, 4, 0, 1, 9, 3, 0)
            .newEdge(0, 2).newEdge(9, 10)
            .getNodes();
        return n;
    }
    
    protected Node[] createGraph(Graph g) {
        GraphBuilder gb = new GraphBuilder(g);
        NodeBuilder nb = gb.newNodes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18);
        Node[] nodes = nb.getNodes();
        nb.newEdges(new int[][] {
            { 0, 2 },
            { 0, 11 },
            { 0, 13 },
            { 1, 0 },
            { 1, 11 },
            { 1, 6 },
            { 2, 0 },
            { 2, 12 },
            { 2, 16 },
            { 2, 17 },
            { 3, 4 },
            { 3, 5 },
            { 3, 10 },
            { 4, 3 },
            { 4, 8 },
            { 4, 10 },
            { 5, 3 },
            { 5, 4 },
            { 5, 8 },
            { 6, 1 },
            { 6, 11 },
            { 6, 15 },
            { 7, 3 },
            { 7, 5 },
            { 7, 8 },
            { 8, 4 },
            { 8, 7 },
            { 8, 11 },
            { 9, 3 },
            { 9, 4 },
            { 9, 8 },
            { 9, 12 },
            { 10, 4 },
            { 10, 7 },
            { 10, 13 },
            { 11, 0 },
            { 11, 1 },
            { 11, 6 },
            { 11, 13 },
            { 12, 4 },
            { 12, 6 },
            { 12, 17 },
            { 13, 0 },
            { 13, 11 },
            { 13, 14 },
            { 14, 1 },
            { 14, 6 },
            { 14, 11 },
            { 15, 1 },
            { 15, 6 },
            { 15, 14 },
            { 16, 1 },
            { 16, 2 },
            { 16, 17 },
            { 17, 1 },
            { 17, 2 },
            { 17, 16 }
        });
        return nodes;
    }
}
