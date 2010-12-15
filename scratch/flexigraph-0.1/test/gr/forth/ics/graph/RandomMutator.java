package gr.forth.ics.graph;

import java.util.List;
import java.util.Random;

public class RandomMutator {
    private static final Random random = new Random(0);
    private static int counter = 0;
    
    public static void mutate(Graph g) {
        switch (random.nextInt(8)) {
            case 0: case 1: newNode(g); break;
            case 2: case 3: case 4: case 5: newEdge(g); break;
            case 6: removeNode(g); break;
            case 7: removeEdge(g); break;
        }
    }
    
    private static void newNode(Graph g) {
        g.newNode(counter++);
    }
    
    private static void newEdge(Graph g) {
        if (g.nodeCount() == 0) {
            return;
        }
        List<Node> nodes = g.nodes().drainToList();
        g.newEdge(nodes.get(random.nextInt(nodes.size())), nodes.get(random.nextInt(nodes.size())));
    }
    
    private static void removeNode(Graph g) {
        if (g.nodeCount() == 0) {
            return;
        }
        List<Node> nodes = g.nodes().drainToList();
        g.removeNode(nodes.get(random.nextInt(nodes.size())));
    }
    
    private static void removeEdge(Graph g) {
        if (g.edgeCount() == 0) {
            return;
        }
        List<Edge> edges = g.edges().drainToList();
        g.removeEdge(edges.get(random.nextInt(edges.size())));
    }
}
