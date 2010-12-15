package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.util.List;
import gr.forth.ics.util.Args;

/**
 * Clustering coefficient of a graph, either directed or undirected.
 *
 * @author Theofanis Oikonomou, email: thoikon (at) csd (dot) uoc (dot) gr
 */
public class ClusteringMetrics {
    private final SimpleNodeMetric nodeClustering;
    private final double graphClustering;

    /**
     * Creates a new instance of ClusteringMetrics.
     */
    private ClusteringMetrics(InspectableGraph graph, boolean isDirected) {
        Args.notNull(graph);

        final Object cc = new Object();
        double temporaryGraphClustering = 0.0;
        final Direction dir = isDirected ? Direction.OUT : Direction.EITHER;
        for (Node n : graph.nodes()) {
            List<Node> neighbors = graph.adjacentNodes(n, dir).drainToList();
            final int size = neighbors.size();
            final double value;
            if (size > 1) {
                double a = 0.0;
                for(int x = 0 ; x < (size - 1) ; x++){
                    final Node n1 = neighbors.get(x);
                    for(int y = (x + 1) ; y < size ; y++){
                        final Node n2 = neighbors.get(y);
                        if(graph.areAdjacent(n1, n2)){
                            a += 1.0;
                        }
                    }
                }
                a *= (isDirected) ? 1 : 2d;
                a /= (double)(size * (size - 1));
                value = a;
            } else {
				value = 0.0;
			}
            n.putWeakly(cc, value);
        }
        nodeClustering = new SimpleNodeMetric(cc);
        for (Node n : graph.nodes()) {
            temporaryGraphClustering += n.getDouble(cc);
        }
        temporaryGraphClustering /= graph.nodeCount();

        graphClustering = temporaryGraphClustering;
    }

    public static ClusteringMetrics execute(InspectableGraph graph, boolean isDirected) {
        return new ClusteringMetrics(graph, isDirected);
    }

    public double getGraphClustering() {
        return graphClustering;
    }

    public NodeMetric getNodeClustering() {
        return nodeClustering;
    }
}
