package gr.forth.ics.graph.layout.random;

import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Layout;
import gr.forth.ics.graph.layout.Locator;
import junit.framework.*;

public class RandomLayoutTest extends TestCase {
    
    public RandomLayoutTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RandomLayoutTest.class);
        
        return suite;
    }
    
    private Graph g;
    
    protected void setUp() {
        g = new PrimaryGraph();
        Generators.createRandom(g, 100, 20 / 100 * 100);
    }
    
    public void test() {
        GRect rect = new GRect(50, 50, 125, 37);
        Layout layout = new RandomLayout(rect);
        Locator locator = new BasicLocator();
        layout.layout(g).run(locator);
        for (Node n : g.nodes()) {
            assertTrue(rect.contains(locator.getLocation(n)));
        }
        for (Edge e : g.edges()) {
            assertNull(locator.getBends(e));
        }
    }
}
