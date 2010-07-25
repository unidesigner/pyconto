package examples.basic;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;

/**
 * Demonstrates weak keys, which help protect against memory leaks.
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class WeakTuples {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        
        Node n = g.newNode();
        
        Object key = new Object(); //key
        n.putWeakly(key, new ExpensiveObject());
        
        //the above entry remains as long as the key is reachable
        
        key = null; //now the key object becomes unreachable
        //thus the garbage collector will eventually clean up the above entry
        
        //as another example, consider the following
        Node[] nodes = g.newNodes(100); //creates a hundred nodes
        key = new Object();
        for (Node node : nodes) {
            node.putWeakly(key, new Object()); //create arbitrary entries
        }
        
        //now, if we would like to clean up all the created entries, the following suffices:
        key = null;
        //i.e. it is not necessary to loop over each node and manually call node.remove(key);
    }
    
    private static class ExpensiveObject {
    }
}
