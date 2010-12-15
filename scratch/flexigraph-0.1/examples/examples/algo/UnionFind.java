package examples.algo;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.event.EmptyGraphListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.util.Partition;

/**
 * Example of using "UnionFind" algorithm. We will use it to dynamically calculate
 * whether a pair of graph nodes are connected to each other (ignoring edges direction).
 * We will also assume that no edge is removed (or else the algorithm doesn't apply), so
 * if two nodes become connected, they remain connected forever.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Disjoint-set_data_structure">
 * Disjoint-set data structure article on Wikipedia</a>
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class UnionFind {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Connectivity con = new Connectivity(g);

        Node[] nodes = g.newNodes(1, 2, 3, 4);
        System.out.println(con.areConnected(nodes[0], nodes[1])); //print false
        System.out.println(con.areConnected(nodes[2], nodes[3])); //print false

        //Graph: 0    1-->2    3
        g.newEdge(nodes[1], nodes[2]);
        System.out.println(con.areConnected(nodes[1], nodes[2])); //prints true

        //Graph: 0    1-->2-->3
        g.newEdge(nodes[2], nodes[3]);
        System.out.println(con.areConnected(nodes[1], nodes[3])); //prints true!

        //Graph: 0-->1-->2-->3
        g.newEdge(nodes[0], nodes[1]);
        System.out.println(con.areConnected(nodes[0], nodes[3])); //prints true!
    }

    //this class maintains at each node an identifier representing which
    //partition (connected component) each graph node belongs into.
    //All nodes are initialized with singleton partitions (representing only themselves),
    //and for each edge, two partitions are merged
    static class Connectivity {
        final Object componentKey = new Object();

        Connectivity(Graph g) {
            g.addEdgeListener(new EmptyGraphListener() {
                @Override public void edgeAdded(GraphEvent e) {
                    Edge edge = e.getEdge();
                    Partition<?> set1 = getOrCreatePartitionOf(edge.n1());
                    Partition<?> set2 = getOrCreatePartitionOf(edge.n2());
                    //combine sets
                    set1.merge(set2);
                }
            });
        }

        boolean areConnected(Node n1, Node n2) {
            return Partition.areMerged(
                    getOrCreatePartitionOf(n1),
                    getOrCreatePartitionOf(n2));
        }

        Partition<?> getOrCreatePartitionOf(Node n) {
            Partition<?> partition = (Partition<?>)n.get(componentKey);
            if (partition == null) {
                partition = Partition.singleton(n);
                n.putWeakly(componentKey, partition);
            }
            return partition;
        }
    }
}
