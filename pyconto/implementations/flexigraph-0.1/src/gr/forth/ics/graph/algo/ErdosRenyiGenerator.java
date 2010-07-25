package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import java.util.Random;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
class ErdosRenyiGenerator {
    private final int numNodes;
    private final double edgeConnectionProbability;
    private final Random random;
    
    /** Creates a new instance of ErdosRenyiGenerator */
    ErdosRenyiGenerator(int nodes, double p) {
        this(nodes, p, new Random());
    }
    
    /** Creates a new instance of ErdosRenyiGenerator */
    ErdosRenyiGenerator(int nodes, double p, Random random) {
        Args.gt(nodes, 0);
        Args.inRangeII(p, 0.0, 1.0);
        Args.notNull(random);
        this.numNodes = nodes;
        this.edgeConnectionProbability = p;
        this.random = random;
    }
    
    Graph generate(Graph graph){
        Node[] nodes = graph.newNodes(numNodes);
        for (Node n1 : nodes) {
            for (Node n2 : nodes) {
                if (random.nextDouble() < edgeConnectionProbability){
                    graph.newEdge(n1, n2);
                }
            }
        }
        return graph;
    }
}
