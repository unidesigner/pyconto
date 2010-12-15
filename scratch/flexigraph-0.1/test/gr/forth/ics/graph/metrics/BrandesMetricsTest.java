package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.Graphs;
import java.util.List;
import junit.framework.*;

public class BrandesMetricsTest extends AbstractMetricsTest {

    public BrandesMetricsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BrandesMetricsTest.class);

        return suite;
    }

    private InspectableGraph g;
    private Node[] n;
    private BrandesMetrics brandes;

    protected void setUp() {
        Graph dg = new PrimaryGraph();
        n = dg.newNodes(1, 2, 3, 4, 5, 6, 7);
        int[][] pairs = {
            { 0, 2 },
            { 1, 2 },
            { 2, 3 },
            { 3, 4 },
            { 4, 5 },
            { 4, 6 },
            { 5, 6 } };
        for (int[] pair : pairs) {
            dg.newEdge(n[pair[0]], n[pair[1]]);
        }
        InspectableGraph g = Graphs.undirected(dg);
        brandes = BrandesMetrics.execute(g, false);
    }

    public void testSimpleBetweeness() {
        NodeMetric b = brandes.getNodeBetweeness();
        assertEquals(0.0, b.getValue(n[0]));
        assertEquals(0.0, b.getValue(n[1]));
        assertEquals(0.0, b.getValue(n[5]));
        assertEquals(0.0, b.getValue(n[6]));
        assertEquals(18.0, b.getValue(n[2]));
        assertEquals(18.0, b.getValue(n[3]));
        assertEquals(16.0, b.getValue(n[4]));
    }

    public void testSimpleNormalizedBetweeness() {
        NodeMetric nb = brandes.getNormalizedNodeBetweeness();
        assertEquals(0.0, nb.getValue(n[0]));
        assertEquals(0.0, nb.getValue(n[1]));
        assertEquals(0.0, nb.getValue(n[5]));
        assertEquals(0.0, nb.getValue(n[6]));
        assertEquals(9.0 / 15.0, nb.getValue(n[2]));
        assertEquals(9.0 / 15.0, nb.getValue(n[3]));
        assertEquals(8.0 / 15.0, nb.getValue(n[4]));
    }

    public void testNormalizedBetweeness() {
        Graph g = new PrimaryGraph();
        Node[] n = createGraph(g);
        BrandesMetrics bm = BrandesMetrics.execute(g, true);
        double[] expected = {
            0.332, 0.122, 0.358, 0.065, 0.227, 0.007, 0.079, 0.013, 0.146,
            0.000, 0.089, 0.220, 0.250, 0.111, 0.016, 0.011, 0.002, 0.021
        };
        checkMetric(n, expected, bm.getNormalizedNodeBetweeness());
    }

    public void testNormalizedCloseness() {
        Graph g = new PrimaryGraph();
        Node[] n = createGraph(g);
        BrandesMetrics bm = BrandesMetrics.execute(g, true);
        double[] expected = {
            0.472, 0.486, 0.378, 0.293, 0.370, 0.239, 0.486, 0.239, 0.293,
            0.000, 0.283, 0.586, 0.298, 0.447, 0.354, 0.333, 0.293, 0.304
        };
        checkMetric(n, expected, bm.getNormalizedInCloseness());
    }

    public void testBridging() {
        Graph g = new PrimaryGraph();
        Node[] n = createGraph2(g);
        BrandesMetrics bm = BrandesMetrics.execute(g, false);
        double[] expected = {
            0.065,
            0.133,
            0.000,
            0.133,
            0.457,
            0.106,
            0.053,
            0.053,
            0.005,
            0.035,
            0.000
        };
        checkMetric(n, expected, bm.getBridging());
    }

    public void testEdgeBetweeness() {
        Graph g = new PrimaryGraph();
        createGraph(g);
        BrandesMetrics bm = BrandesMetrics.execute(g, true);
        double[] expected = {
            1176.25, 932.25, 342.6875, 924.75, 821.083, 241.333, 697.625, 553.333,
            23.0, 56.666, 549.250, 46.083, 289.333, 182.042, 482.208, 304.5,
            161.875, 495.5, 405.208, 143.417, 645.083, 38.75, 288.791, 68.5,
            708.458, 533.917, 32.75, 655.611, 0.0, 0.0, 0.0, 0.0, 532, 28.75,
            247.3125, 800.208, 185.083, 211.667, 226.854, 476.417, 190.25,
            51.333, 1234.0, 1044.917, 56.8125, 335.167, 356.167, 1102.417,
            125.583, 181.083, 41.646, 133.167, 741.417, 51.333, 128.25,
            733.333, 23.0
        };

        checkMetric(g.edges().drainToList(), expected, bm.getEdgeBetweeness());
    }

    private void checkMetric(Node[] n, double[] expected, NodeMetric m) {
        final double E = 0.001;
        for (int i = 0; i < n.length; i++) {
            assertNear(expected[i], m.getValue(n[i]), E);
        }
    }

    private void checkMetric(List<Edge> edges, double[] expected, EdgeMetric m) {
        final double E = 0.001;
        for (int i = 0; i < edges.size(); i++) {
            assertNear(expected[i], m.getValue(edges.get(i)), E);
        }
    }

}
