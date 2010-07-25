package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.LayoutProcess;
import gr.forth.ics.graph.layout.Locator;

public class ForcesTest extends TestCase {
    
    public ForcesTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ForcesTest.class);
        
        return suite;
    }
    
    private Locator locator;
    private Graph g;
    private Node n1;
    private Node n2;
    
    protected void setUp() {
        g = new PrimaryGraph();
        n1 = g.newNode("1");
        n2 = g.newNode("2");
        locator = new BasicLocator();
    }
    
    public void testGravity() {
        Force force = new Forces.Gravity(new GPoint(50, 50), 0.001);
        LayoutProcess layout = new MockForceLayout(g, force);
        locator.setLocation(n1, GPoint.ZERO_POINT);
        locator.setLocation(n2, new GPoint(100, 100));
        layout.run(locator);
        assertEquals(new GPoint(2.5, 2.5), locator.getLocation(n1));
        assertEquals(new GPoint(97.5, 97.5), locator.getLocation(n2));
    }
    
    public void testRepulsion() {
        Force force = new Forces.NodeRepulsion(0.001);
        LayoutProcess layout = new MockForceLayout(g, force);
        locator.setLocation(n1, GPoint.ZERO_POINT);
        locator.setLocation(n2, new GPoint(1, 1));
        layout.run(locator);
        assertEquals(new GPoint(-5.0E-7, -5.0E-7), locator.getLocation(n1));
        assertEquals(new GPoint(1.0000005, 1.0000005), locator.getLocation(n2));
    }
    
    public void testEadesSpring() {
        Force force = new Forces.EadesSpring(1, 4);
        LayoutProcess layout = new MockForceLayout(g, force);
        locator.setLocation(n1, GPoint.ZERO_POINT);
        locator.setLocation(n2, new GPoint(1, 1));
        g.newEdge(n1, n2);
        layout.run(locator);
        assertEquals(new GPoint(0.9802581434685472, 0.9802581434685472), locator.getLocation(n1));
        assertEquals(new GPoint(0.019741856531452773, 0.019741856531452773), locator.getLocation(n2));
    }
    
    public void testFRSpring() {
        Force force = new Forces.FRSpring(100);
        LayoutProcess layout = new MockForceLayout(g, force);
        locator.setLocation(n1, GPoint.ZERO_POINT);
        locator.setLocation(n2, new GPoint(100, 100));
        g.newEdge(n1, n2);
        layout.run(locator);
        assertEquals(new GPoint(141.4213562373095, 141.4213562373095), locator.getLocation(n1));
        assertEquals(new GPoint(-41.42135623730951, -41.42135623730951), locator.getLocation(n2));
    }
    
    private static class MockForceLayout extends ForceLayoutProcess {
        private int steps = 0;
        public MockForceLayout(InspectableGraph g, Force force) {
            super(g, force);
        }
        
        public boolean isDone() {
            return steps >= 1;
        }
        
        protected void step(InspectableGraph graph, Locator locator, ForceAggregator forceMap) {
            super.step(graph, locator, forceMap);
            steps++;
        }
    }
}
