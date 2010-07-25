package examples.layout;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.graph.layout.random.RandomLayout;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class RandomizedLayout {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Generators.createRandom(g, 100, 0.1); //a random graph with 100 nodes an 0.1 edges for every pair
        
        Locator locator = new BasicLocator();
        
        new RandomLayout(new GRect(0, 0, 100, 100)) //randomly layout inside (0, 0)-(100, 100)
                .layout(g).run(locator);
        
        for (Node n : g.nodes()) {
            GPoint nodeLocation = locator.getLocation(n);
            System.out.println(nodeLocation);
        }
    }
}
