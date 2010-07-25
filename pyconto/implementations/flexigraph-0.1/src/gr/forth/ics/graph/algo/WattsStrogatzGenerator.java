package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import java.util.List;
import java.util.Random;
import gr.forth.ics.util.Args;

/**
 * WattsStrogatzGenerator is a graph generator that produces a small
 * world network using the beta-model as proposed by Duncan Watts. The basic ideas is
 * to start with a one-dimensional ring lattice in which each Node has k-neighbors and then randomly
 * rewire the edges, with probability beta, in such a way that a small world networks can be created for
 * certain values of beta and k that exhibit low charachteristic path lengths and high clustering coefficient.
 * @see "Small Worlds:The Dynamics of Networks between Order and Randomness by D.J. Watts"
 *
 * @author Theofanis Oikonomou
 */
class WattsStrogatzGenerator {
    private int numNodes = 0;
    private double rewiringProbability = 0;
    private int neighbors = 0;
    private final Random random;
    
    /**
     * Creates a new instance of WattsStrogatzGenerator
     */
    WattsStrogatzGenerator(Random random, int numNodes, double probability, int neighbors) {
        Args.notNull(random);
        Args.gte(numNodes, 2.0);
        Args.inRangeII(probability, 0.0, 1.0);
        Args.inRangeII(neighbors, 2, numNodes / 2);
        this.random = random;
        this.numNodes = numNodes;
        this.rewiringProbability = probability;
        this.neighbors = neighbors;
    }
    
    void generate(Graph graph) {
        Generators.createRingLattice(graph, numNodes, neighbors);
        List<Node> nodes = graph.nodes().drainToList();
        //rewire edges
        for (int i = 0; i < numNodes; i++) {
            for (int s = 1; s <= neighbors; s++) {
                
                while (true) {
                    // randomly rewire a proportion, beta, of the edges in the graph.
                    double r = random.nextDouble();
                    if (r < rewiringProbability) {
                        int v = random.nextInt(numNodes);
                        
                        Node vthNode = nodes.get(v);
                        Node ithNode = nodes.get(i);
                        Node kthNode = nodes.get((i + s) % numNodes);//upIndex(i, s));
                        Edge e = graph.anEdge(ithNode, kthNode);
                        
                        if (!graph.areAdjacent(kthNode, vthNode) && kthNode != vthNode) {
                            graph.removeEdge(e);
                            graph.newEdge(kthNode, vthNode);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Determines the index of the neighbor ksteps above
     * @param numSteps is the number of steps away from the current index that is being considered.
     * @param currentIndex the index of the selected vertex.
     */
    private int upIndex(int currentIndex, int numSteps) {
        if (currentIndex + numSteps > numNodes - 1) {
            return numSteps - (numNodes - currentIndex);
        }
        return currentIndex + numSteps;
    }
    
}
