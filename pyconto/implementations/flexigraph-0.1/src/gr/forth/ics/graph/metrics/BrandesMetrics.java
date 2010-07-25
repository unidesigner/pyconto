package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.Filters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.DVMap;

/**
 *
 * @author Theofanis Oikonomou, email: thoikon (at) csd (dot) uoc (dot) gr
 */
public class BrandesMetrics {
    private final InspectableGraph graph;
    private SimpleNodeMetric closenessOut;
    private SimpleNodeMetric closenessIn;
    private SimpleNodeMetric normalizedOutCloseness;
    private SimpleNodeMetric normalizedInCloseness;
    private SimpleNodeMetric nodeBetweeness;
    private SimpleNodeMetric normalizedNodeBetweeness;
    private SimpleEdgeMetric edgeBetweeness;
    private SimpleEdgeMetric normalizedEdgeBetweeness;
    private SimpleNodeMetric bridging;
    private SimpleNodeMetric eccentricity;
    private double characteristicPathLength;
    private double networkDegree;
    private double networkCloseness;
    private double checkNode;
    private double networkEdgeBetweenness;
    private double networkDiameter;
    private double networkRadius;
    private boolean isDirected = false;
    private BrandesMetrics(InspectableGraph graph, boolean directed) {
        Args.notNull(graph);
        this.graph = graph;
        isDirected = directed;
        execute();
    }
    
    private void execute() {
        final Object cC = new Object();
        final Object cCIn = new Object();
        final Object cB = new Object();
        final Object cE = new Object();
        final Object cR = new Object();
        final Object ecc = new Object();
        
        final Direction dir = isDirected ? Direction.OUT : Direction.EITHER;
        for (Node n : graph.nodes()) {
            n.putWeakly(cC, 0.0);
            n.putWeakly(cCIn, 0.0);
            n.putWeakly(cB, 0.0);
            n.putWeakly(cR, 0.0);
            n.putWeakly(ecc, 0.0);
        }
        for(Edge e : graph.edges()){
            e.putWeakly(cE, 0.0);
        }
        for (Node n : graph.nodes()) {
            LinkedList<Node> S = new LinkedList<Node>();
            final Object P = new Object();
            Map<Node, Collection<Node>> pMap = DVMap.newHashMapWithLinkedLists();
            final Object sigma = new Object();
            final Object d = new Object();
            for (Node t : graph.nodes()) {
                t.putWeakly(d, -1);
                t.putWeakly(sigma, 0);
            }
            n.putWeakly(d, 0);
            n.putWeakly(sigma, 1);
            final LinkedList<Node> Q = new LinkedList<Node>();
            Q.addLast(n);
            while (!Q.isEmpty()) {
                Node u = Q.removeFirst();
                S.addFirst(u);
                for (Node w : graph.adjacentNodes(u, dir)) {
                    //w found for the first time?
                    int dw = w.getInt(d);
                    int du = u.getInt(d);
                    if (dw < 0) {
                        Q.addLast(w);
                        dw = du + 1;
                        w.putWeakly(d, dw);
                        w.putWeakly(cCIn, w.getInt(cCIn) + dw);
                        characteristicPathLength += dw;
                        n.putWeakly(cC, n.getInt(cC) + dw);
                        n.putWeakly(ecc, (n.getInt(ecc) < dw) ? dw : n.getInt(ecc));
                    }
                    //shortest path to w via u?
                    if (dw == du + 1) {
                        w.putWeakly(sigma, w.getInt(sigma) + u.getInt(sigma));
                        pMap.get(w).add(u);
                    }
                }
            }
            n.putWeakly(cC, 1 / n.getDouble(cC));
            final Object delta = new Object();
            for (Node u : graph.nodes()) {
                u.putWeakly(delta, 0.0);
            }
            //S returns vertices in order of non-increasing distance from n
            while (!S.isEmpty()) {
                Node w = S.removeFirst();
                for (Node u : pMap.get(w)) {
                    double oldDeltaU = u.getDouble(delta);
                    double fraction = (double)u.getInt(sigma) / w.getInt(sigma);
                    u.putWeakly(delta, oldDeltaU + fraction * (1 + w.getDouble(delta)));
                }
                if (w != n) {
                    w.putWeakly(cB, w.getDouble(cB) + w.getDouble(delta));
                }
            }
            for(Edge e : graph.edges()){
                Node n1 = e.n1();
                Node n2 = e.n2();
                double fraction = (double)n1.getInt(sigma) / n2.getInt(sigma);
                if(n1 != n && n2 != n){
                    e.putWeakly(cE, e.getDouble(cE) + fraction * (1 + n2.getDouble(cB)));
                }
            }
        }
        
        int maxx = 0;
        int min = graph.nodeCount();
        for (Node f : graph.nodes()) {
            f.putWeakly(cCIn, (f.getDouble(cCIn) == 0) ? 0.0 : 1 / f.getDouble(cCIn));
            min = (f.getInt(ecc) < min) ? f.getInt(ecc) : min;
            maxx = (f.getInt(ecc) > maxx) ? f.getInt(ecc) : maxx;
        }
        networkDiameter = maxx;
        networkRadius = min;
        eccentricity = new SimpleNodeMetric(ecc);
        
        if (isDirected) {
            characteristicPathLength /= graph.nodeCount() * (graph.nodeCount() - 1);
        } else {
            characteristicPathLength /= (graph.nodeCount() * (graph.nodeCount() - 1)) / 2.0;
        }
        
        double sum = 0.0;
        double max = 0.0;
        NodeMetric normalizedDegreeMetric = Metrics.normalizedDegreeMetric(graph);
        for (Node u : graph.nodes()) {
            double metric = normalizedDegreeMetric.getValue(u);
            max = (max >= metric) ? max : metric;
            sum += metric;
        }
        networkDegree = (graph.nodeCount() * max - sum) / (graph.nodeCount() - 2);
        
        closenessOut = new SimpleNodeMetric(cC);
        closenessIn = new SimpleNodeMetric(cCIn);
        final Object normalizedC = new Object();
        final Object normalizedCIn = new Object();
        normalizedOutCloseness = new SimpleNodeMetric(normalizedC);
        normalizedInCloseness = new SimpleNodeMetric(normalizedCIn);
        sum = 0.0;
        max = 0.0;
        for (Node u : graph.nodes()) {
            u.putWeakly(normalizedC, (graph.nodeCount() - 1) * closenessOut.getValue(u));
            u.putWeakly(normalizedCIn, (graph.nodeCount() - 1) * closenessIn.getValue(u));
            max = (max >= u.getDouble(normalizedC)) ? max : u.getDouble(normalizedC);
            sum += u.getDouble(normalizedC);
        }
        networkCloseness = (graph.nodeCount() * max - sum) /
                ((double)((graph.nodeCount() - 1) * (graph.nodeCount() - 2)) /
                (double)(2 * graph.nodeCount() - 3));
        
        nodeBetweeness = new SimpleNodeMetric(cB);
        final Object normalizedB = new Object();
        normalizedNodeBetweeness = new SimpleNodeMetric(normalizedB);
        double denominator = (graph.nodeCount() - 1) * (graph.nodeCount() - 2);
        sum = 0.0;
        max = 0.0;
        for (Node u : graph.nodes()) {
            u.putWeakly(normalizedB, nodeBetweeness.getValue(u) / denominator);
            max = (max >= u.getDouble(normalizedB)) ? max : u.getDouble(normalizedB);
            sum += u.getDouble(normalizedB);
        }
        checkNode = (graph.nodeCount() * max - sum) / (graph.nodeCount() - 1);
        
        edgeBetweeness = new SimpleEdgeMetric(cE);
        final Object normalizedE = new Object();
        normalizedEdgeBetweeness = new SimpleEdgeMetric(normalizedE);
        double denomin = (graph.nodeCount() - 1) * (graph.nodeCount() - 2);
        sum = 0.0;
        max = 0.0;
        for (Edge e : graph.edges()) {
            e.putWeakly(normalizedE, edgeBetweeness.getValue(e) / denomin);
            max = (max >= e.getDouble(normalizedE)) ? max : e.getDouble(normalizedE);
            sum += e.getDouble(normalizedE);
        }
        networkEdgeBetweenness = (graph.nodeCount() * max - sum) / (graph.nodeCount() - 1);
        
        for (Node u : graph.nodes()) {
            double denom = 0.0;
            for(Node n : graph.adjacentNodes(u)){
                denom += 1.0 / graph.degree(n);
            }
            double BC = (1.0 / graph.degree(u)) / denom;
            u.putWeakly(cR, normalizedNodeBetweeness.getValue(u) * BC);
        }
        bridging = new SimpleNodeMetric(cR);
    }
    
    public Collection<Node> getCenterNodes() {
        Collection<Node> col = new ArrayList<Node>();
        for (Node n : graph.nodes().filter(
                Filters.equalProperty(eccentricity.getKey(), getRadius()))) {
            col.add(n);
        }
        return col;
    }
    
    public double getDiameter() {
        return networkDiameter;
    }
    
    public double getRadius() {
        return networkRadius;
    }
    
    public double getCharacteristicPathLength() {
        return characteristicPathLength;
    }
    
    public double getNetworkDegreeCentralization() {
        return networkDegree;
    }
    
    public double getNetworkClosenessCentralization() {
        return networkCloseness;
    }
    
    public double getNetworkNodeBetweennessCentralization() {
        return checkNode;
    }

    public double getNetworkEdgeBetweenness() {
        return networkEdgeBetweenness;
    }
    
    public NodeMetric getEccentricity() {
        return checkNode(eccentricity);
    }
    
    public NodeMetric getCloseness() {
        return checkNode(closenessOut);
    }
    
    public NodeMetric getNormalizedCloseness() {
        return getNormalizedOutCloseness();
    }
    
    public NodeMetric getNormalizedInCloseness() {
        return checkNode(normalizedInCloseness);
    }
    
    public NodeMetric getNormalizedOutCloseness() {
        return checkNode(normalizedOutCloseness);
    }
    
    public NodeMetric getNodeBetweeness() {
        return checkNode(nodeBetweeness);
    }
    
    public NodeMetric getNormalizedNodeBetweeness() {
        return checkNode(normalizedNodeBetweeness);
    }
    
    public EdgeMetric getEdgeBetweeness() {
        return checkEdge(edgeBetweeness);
    }
    
    public EdgeMetric getNormalizedEdgeBetweeness() {
        return checkEdge(normalizedEdgeBetweeness);
    }
    
    public NodeMetric getBridging() {
        return checkNode(bridging);
    }
    
    private NodeMetric checkNode(NodeMetric metric) {
        if (metric == null) {
            throw new IllegalStateException("Algorithm has not been executed");
        }
        return metric;
    }
    
    private EdgeMetric checkEdge(EdgeMetric metric) {
        if (metric == null) {
            throw new IllegalStateException("Algorithm has not been executed");
        }
        return metric;
    }
    
    public static BrandesMetrics execute(InspectableGraph g, boolean directed) {
        return new BrandesMetrics(g, directed);
    }
}
