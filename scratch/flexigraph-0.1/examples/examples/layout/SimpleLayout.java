package examples.layout;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Locator;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class SimpleLayout {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Node[] nodes = g.newNodes("1", "2", "3");
        
        Locator locator = new BasicLocator();
        locator.setLocation(nodes[0], new GPoint(0, 00));
        locator.setLocation(nodes[1], new GPoint(50, 50));
        locator.setLocation(nodes[2], new GPoint(100, 0));
        
        locator.rotate(Math.toRadians(180), 50, 0); //rotate 180 degrees at point (50, 0)
        
        System.out.println(locator.getLocation(nodes[0])); //prints (100.0, 0.0)
        System.out.println(locator.getLocation(nodes[1])); //prints (50.0, -50.0)
        System.out.println(locator.getLocation(nodes[2])); //prints (0.0, 0.0)
    }
}
