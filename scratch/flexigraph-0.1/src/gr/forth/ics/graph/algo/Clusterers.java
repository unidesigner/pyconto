package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.FoldingGraph;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.util.Args;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Clusterers {
    private Clusterers() { }

    /**
     * Returns the connected components of a graph, using undirected semantics (without needing
     * to wrap the graph in {@link Graphs#undirected(InspectableGraph)}.
     *
     * @param g a graph
     * @return a Clusterer representing the connected components of the graph
     */
    public static Clusterer connectedComponents(InspectableGraph g) {
        return ConnectedComponents.execute(g);
    }
    
    /**
     * Finds the strongly connected components of a graph, using Tarjan's algorithm. A
     * strongly connected component is the maximal set of nodes where there is a directed path
     * connecting every pair of them.
     * 
     * @param g the graph of which to find the strongly connected components
     * @return a clusterer representing the strongly connected components
     */
    public static Clusterer stronglyConnectedComponents(InspectableGraph g) {
        return StronglyConnectedComponents.execute(g);
    }

    /**
     * Create a folding graph by folding all clusters of a graph. That is, each
     * cluster of the graph is collapsed into a single node.
     *
     * @param clusterer the clusterer which defines the cluster to be folded
     * @param graph the graph of which the strongly connected components are to be folded
     * @return a folding graph which will contain one node per strongly connected component
     * of the original graph
     */
    public static FoldingGraph fold(Clusterer clusterer, Graph graph) {
        Args.notNull(graph);
        FoldingGraph foldingGraph = new FoldingGraph(graph);
        for (Object cluster : clusterer.getClusters()) {
            foldingGraph.fold(clusterer.getCluster(cluster));
        }

        return foldingGraph;
    }
}
