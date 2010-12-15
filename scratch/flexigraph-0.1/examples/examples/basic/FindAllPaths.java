package examples.basic;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.path.Traverser;
import gr.forth.ics.graph.path.Traverser.PathIterator;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple example that finds *all* paths of a dag.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class FindAllPaths {
    public static void main(String[] args) {
        Graph graph = new PrimaryGraph();
        Node n1 = graph.newNode("1");
        Node n2 = graph.newNode("2");
        Node n3 = graph.newNode("3");
        Node n4 = graph.newNode("4");

        graph.newEdge(n1, n2);
        graph.newEdge(n1, n3);
        graph.newEdge(n1, n4);
        graph.newEdge(n2, n3);
        graph.newEdge(n2, n4);
        graph.newEdge(n3, n4);

        Traverser traverser = Traverser.newDfs().build();
        Set<Path> paths = new HashSet<Path>();
        for (Node n : graph.nodes()) {
            PathIterator iterator = traverser.traverse(graph, n, Direction.OUT).iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (paths.contains(path)) {
                    iterator.skipExplorationOfLastPath(); //discontinue the exploration of already visited path
                } else {
                    paths.add(path);
                }
            }
        }
    }
}
