package examples.layout;

import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.SecondaryGraph;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.graph.layout.Locators;
import gr.forth.ics.graph.layout.random.RandomLayout;

/**
 * Creates two graphs, randomly layouts them, then moves the second layout to the right side
 * of the first and combines them into a single, unified layout.
 * 
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class CombinedLayout {
    public static void main(String[] args) {
        SecondaryGraph g1 = new SecondaryGraph();
        Generators.createRandom(g1, 10, 0.2); //a random graph with 10 nodes an 0.2 edges for every pair
        
        Locator locator1 = new BasicLocator();
        new RandomLayout(new GRect(0, 0, 100, 100)) //randomly layout inside (0, 0)-(100, 100)
                .layout(g1).run(locator1);
        
        SecondaryGraph g2 = new SecondaryGraph();
        Generators.createRandom(g2, 10, 0.2); //another random graph
        
        Locator locator2 = new BasicLocator();
        new RandomLayout(new GRect(0, 0, 100, 100)) //randomly layout inside (0, 0)-(100, 100)
                .layout(g2).run(locator2);
        
        GRect boundingBox1 = Locators.getBoundingBox(locator1, g1); //bounding box for first graph
        
        GRect boundingBox2 = Locators.getBoundingBox(locator2, g2); //bounding box for second graph
        
        locator2.translate( //move the second layout just right of the first
                boundingBox1.maxX() - boundingBox2.minX(),
                boundingBox1.minY() - boundingBox2.minY());
        
        Locator combinedLayout = Locators.compose(locator1, locator2);
        //now combinedLayout can answer the location of every node
        
        for (Node n : g1.nodes()) {
            System.out.println(combinedLayout.getLocation(n));
        }
        for (Node n : g2.nodes()) {
            System.out.println(combinedLayout.getLocation(n));
        }
    }
}
