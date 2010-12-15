package examples.basic;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;

/**
 * Nodes, edges, and even graphs are containers of arbitrary key/value entries.
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Tuples {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode();
        
        n1.put("key1", new Integer(10));
        int x = n1.getInt("key1"); //x == 10
        
        Edge e = g.newEdge(n1, n1);
        e.put("key2", "value2");
        
        String value = e.getString("key2");
        String alias = (String)e.get("key2"); //value == alias == "value2"
        
        g.tuple().put("key", "value");
        
        Object o = g.tuple().remove("key"); //o == "value"
    }
}
