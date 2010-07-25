package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.Arrays;
import junit.framework.*;

public class CompositeLocatorTest extends BasicLocatorTest {
    
    public CompositeLocatorTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CompositeLocatorTest.class);
        
        return suite;
    }
    
    protected Locator create() {
        return new CompositeLocator();
    }
    
    public void testComposite() {
        Locator nl1 = new BasicLocator();
        Locator nl2 = new BasicLocator();
        Node node1 = graph.newNode(1);
        Node node2 = graph.newNode(2);
        nl1.setLocation(node1, 5, 5);
        nl2.setLocation(node2, 10, 10);
        
        nl1.translate(-3, -3);
        nl2.translate(-7, -7);
        
        Locator composite = new CompositeLocator(nl1, nl2);
        
        assertEquals(new GPoint(2, 2), composite.getLocation(node1));
        assertEquals(new GPoint(3, 3), composite.getLocation(node2));
    }
    
    public void testTreeDemo() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3, 4);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[2], n[3]);
        g.newEdge(n[2], n[4]);
        CompositeLocator nl0 = new CompositeLocator();
        nl0.setLocation(n[0], GPoint.ZERO_POINT);
        assertEquals(GPoint.ZERO_POINT, nl0.getLocation(n[0]));
        
        Locator nl1 = new BasicLocator();
        nl1.setLocation(n[1], GPoint.ZERO_POINT);
        nl1.translate(-1, 1);
        assertEquals(new GPoint(-1, 1), nl1.getLocation(n[1]));
        
        CompositeLocator nl2 = new CompositeLocator();
        nl2.setLocation(n[2], GPoint.ZERO_POINT);
        nl2.translate(1, 1);
        assertEquals(new GPoint(1, 1), nl2.getLocation(n[2]));
        
        nl0.addLocator(nl1);
        nl0.addLocator(nl2);
        assertEquals(new GPoint(-1, 1), nl0.getLocation(n[1]));
        assertEquals(new GPoint(1, 1), nl0.getLocation(n[2]));
        
        Locator nl3 = new BasicLocator();
        nl3.setLocation(n[3], new GPoint(0, 0));
        nl3.translate(-1, 1);
        
        Locator nl4 = new BasicLocator();
        nl4.setLocation(n[4], new GPoint(0, 0));
        nl4.translate(1, 1);
        
        nl2.addLocator(nl3);
        nl2.addLocator(nl4);
        
        assertEquals(new GPoint(0, 0), nl0.getLocation(n[0]));
        assertEquals(new GPoint(-1, 1), nl0.getLocation(n[1]));
        assertEquals(new GPoint(1, 1), nl0.getLocation(n[2]));
        assertEquals(new GPoint(0, 2), nl0.getLocation(n[3]));
        assertEquals(new GPoint(2, 2), nl0.getLocation(n[4]));
    }
    
    public void testNoStaleNodeLocations() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode();
        Locator locator = new BasicLocator();
        Locator comp = new CompositeLocator(locator);
        GPoint loc1 = new GPoint(10, 10);
        locator.setLocation(n, loc1);
        
        assertEquals(loc1, comp.getLocation(n));
        GPoint loc2 = new GPoint(5, 5);
        locator.setLocation(n, loc2);
        assertEquals(loc2, comp.getLocation(n));
    }
    
    public void testNoStaleEdgeLocations() {
        Graph g = new PrimaryGraph();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        Locator locator = new BasicLocator();
        Locator comp = new CompositeLocator(locator);
        GPoint loc1 = new GPoint(10, 10);
        locator.setBend(e, loc1);
        
        assertEquals(Arrays.asList(loc1), comp.getBends(e));
        GPoint loc2 = new GPoint(5, 5);
        locator.setBend(e, loc2);
        assertEquals(Arrays.asList(loc2), comp.getBends(e));
    }
    
    public void testNoStaleNodeLocationsAfterTransform() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode();
        Locator locator = new BasicLocator();
        Locator comp = new CompositeLocator(locator);
        GPoint loc1 = new GPoint(10, 10);
        locator.setLocation(n, loc1);
        
        assertEquals(loc1, comp.getLocation(n));
        locator.translate(10, 10);
        assertEquals(new GPoint(20, 20), comp.getLocation(n));
    }
    
    public void testNoStaleBendsAfterTransform() {
        Graph g = new PrimaryGraph();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        Locator locator = new BasicLocator();
        Locator comp = new CompositeLocator(locator);
        GPoint loc1 = new GPoint(10, 10);
        locator.setBend(e, loc1);
        
        assertEquals(Arrays.asList(loc1), comp.getBends(e));
        locator.translate(10, 10);
        assertEquals(Arrays.asList(new GPoint(20, 20)), comp.getBends(e));
    }
}