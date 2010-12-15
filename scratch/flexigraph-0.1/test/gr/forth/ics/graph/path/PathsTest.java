package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Direction;
import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.Graphs;

public class PathsTest extends TestCase {
    
    public PathsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PathsTest.class);
        
        return suite;
    }

    public void testFind() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2", "3", "4");
        //1 <-- 2 --> 3 --> 4
        g.newEdge(n[1], n[0]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[3]);
        
        assertEquals("[2-->1]", Paths.findPath(g, n[1], n[0], Direction.OUT).toString());
        assertEquals("[2-->3-->4]", Paths.findPath(g, n[1], n[3], Direction.OUT).toString());
        
        assertNull(Paths.findPath(g, n[0], n[1], Direction.OUT));
        assertNull(Paths.findPath(g, n[3], n[2], Direction.OUT));
        assertNull(Paths.findPath(g, n[3], n[1], Direction.OUT));
        
        InspectableGraph ug = Graphs.undirected(g);
        assertEquals("[1<--2-->3]", Paths.findPath(ug, n[0], n[2], Direction.OUT).toString());
        assertEquals("[3<--2-->1]", Paths.findPath(ug, n[2], n[0], Direction.OUT).toString());
        assertEquals("[4<--3<--2-->1]", Paths.findPath(ug, n[3], n[0], Direction.OUT).toString());
    }
}
