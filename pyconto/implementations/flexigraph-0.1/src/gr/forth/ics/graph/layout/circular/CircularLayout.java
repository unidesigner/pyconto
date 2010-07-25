package gr.forth.ics.graph.layout.circular;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Biconnectivity;
import gr.forth.ics.graph.SecondaryGraph;
import gr.forth.ics.graph.algo.Clusterer;
import gr.forth.ics.graph.algo.Clusterers;
import gr.forth.ics.graph.layout.Layout;
import gr.forth.ics.graph.layout.LayoutProcess;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.graph.layout.SingleStepLayoutProcess;
import gr.forth.ics.graph.FoldingGraph;
import gr.forth.ics.graph.Graphs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.DVMap;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class CircularLayout implements Layout {
    private final GPoint center;
    private final double r;
    private final double startArc;
    private final double totalArc;
    private boolean isBiconnected;
    private Hashtable<String, Node> cutNodes = null;
    private Locator circularLocator = null;
    private CircularOrder circOrder = null;
    
    public CircularLayout() {
        this(GPoint.ZERO_POINT, 50, 0);
    }
    
    public CircularLayout(GPoint center, double r, double startArc) {
        this(center, r, startArc, 2 * Math.PI);
    }
    
    public CircularLayout(GPoint center, double r, double startArc, double totalArc) {
        Args.notNull(center);
        this.center = center;
        this.r = r;
        this.startArc = startArc;
        this.totalArc = totalArc;
    }
    
    private Graph createBCGraph(FoldingGraph sg, Node f){
        Graph g = sg.viewFolder(f);
        Hashtable<String, Node> list = new Hashtable<String, Node>();
        for(Edge e : sg.edges(f)){
            if(sg.isSyntheticEdge(e)){
                Edge re = sg.getRealEdge(e);
                Node n1 = re.n1();
                Node n2 = re.n2();
                Node m1 = null;
                Node m2 = null;
                if(cutNodes.containsKey(n1.getValue().toString()) && !list.containsKey(n1.getValue().toString())){
                    m1 = g.newNode(n1.getValue());
                    list.put(n1.getValue().toString(), m1);
                }
                else if(cutNodes.containsKey(n1.getValue().toString()) && list.containsKey(n1.getValue().toString())){
                    m1 = list.get(n1.getValue().toString());
                }
                if(cutNodes.containsKey(n2.getValue().toString()) && !list.containsKey(n2.getValue().toString())){
                    m2 = g.newNode(n2.getValue());
                    list.put(n2.getValue().toString(), m2);
                }
                else if(cutNodes.containsKey(n2.getValue().toString()) && list.containsKey(n2.getValue().toString())){
                    m2 = list.get(n2.getValue().toString());
                }
                
                if(m1 != null){
                    if(m2 != null){
                        g.newEdge(m1, m2);
                    }
                    else{
                        g.newEdge(m1, n2);
                    }
                }
                if(m2 != null){
                    g.newEdge(n1, m2);
                }
                
            }
        }
        return g;
    }
    
    public LayoutProcess layout(final InspectableGraph graph) {
        return new SingleStepLayoutProcess() {
            protected void stepImpl(Locator locator) {
                isBiconnected = false;
                if (graph.isEmpty()) {
                    return;
                }
                Collection<Collection<Node>> clusters = getClusters(graph);
                if (!isBiconnected) {
                    FoldingGraph superGraph = new FoldingGraph(new SecondaryGraph(graph), false);
                    for (Collection<Node> cluster : clusters) {
                        superGraph.fold(cluster);
                    }
                    Clusterer cc = Clusterers.connectedComponents(Graphs.undirected(superGraph));
                    LinkedList<Node> order = new LinkedList<Node>();
                    for (Collection<Node> comp : cc) {
                        order.addAll(comp);
                    }
                    Circular circular = new Circular();
                    CircularOrder circularOrder = new CircularOrder(new LinkedList<Node>());
                    Collection placed = new LinkedList<Node>();
                    for (Node n : order) {
                        if (superGraph.isFolder(n)) {
                            Graph inner = superGraph.viewFolder(n);
                            if(inner.nodeCount() != 1) {
                                inner = createBCGraph(superGraph, n);
                                Node startDFS = findWhereToStartDFS(inner, graph);
                                circular.setStartDFS(startDFS);
                                CircularOrder semiorder = circular.execute(inner);
                                List<Node> list = new ArrayList<Node>();
                                List<Node> lis = semiorder.getOrder();
                                int start = lis.indexOf(startDFS);
                                List<Node> lis1 = lis.subList(0, start);
                                List<Node> lis2 = lis.subList(start, lis.size());
                                List<Node> lis3 = new ArrayList<Node>();
                                lis3.addAll(lis2);
                                for (int i = (lis1.size() - 1); i >= 0; i--) {
                                    Node d = lis1.get(i);
                                    lis3.add(d);
                                }
                                for (Node h : lis3){
                                    if(!cutNodes.containsKey(h.getValue().toString())) {
                                        list.add(h);
                                    }
                                }
                                CircularOrder semiord = new CircularOrder(list);
                                circularOrder.append(semiord);
                            } else {
                                if (!placed.contains(inner.aNode())) {
                                    List<Node> list = new ArrayList<Node>();
                                    list.add(inner.aNode());
                                    circularOrder.append(new CircularOrder(list));
                                }
                            }
                        }
                    }
                    circOrder = circularOrder;
                    circularLocator = circOrder.getCircleLayout(center, r, startArc, totalArc, locator);
                } else {
                    Circular circular = new Circular();
                    circOrder = circular.execute(graph);
                    circularLocator = circOrder.getCircleLayout(center, r, startArc, totalArc, locator);
                }
            }
        };
    }
    
    private Node findWhereToStartDFS(Graph graf, InspectableGraph graph){
        Node init = null;
        LinkedList<Node> list = new LinkedList<Node>();
        Hashtable<String, Node> hash = new Hashtable<String, Node>();
        for(Node n : graf.nodes()){
            String name = n.getValue().toString();
            for(String s : cutNodes.keySet()){
                Node c = cutNodes.get(s);
                if(name.equals(s)){
                    list.add(n);
                    hash.put(s, c);
                }
            }
        }
        int max = 0;
        for(Node m : list){
            Node b = hash.get(m.getValue().toString());
            max = (graph.degree(b) > max) ? graph.degree(b) : max;
            init = m;
        }
        if(init == null){
            init = graf.aNode();
        }
        return init;
    }
    
    private Collection<Collection<Node>> getClusters(InspectableGraph g) {
        Biconnectivity bic = Biconnectivity.execute(g);
        Map<Object, Collection<Edge>> components = DVMap.newHashMapWithArrayLists();
        for(Edge e : g.edges()){
            components.get(bic.componentOf(e)).add(e);
        }
        
        Map<Integer, Collection<Node>> bicon = DVMap.newHashMapWithHashSets();
        cutNodes = new Hashtable<String, Node>();
        int q = 0;
        
        Node[] narray = new Node[2];
        for (Collection<Edge> comp : components.values()) {
            Collection<Node> clusterNodes = bicon.get(q++);
            for (Edge ed : comp) {
                narray[0] = ed.n1();
                narray[1] = ed.n2();
                for (Node node : narray) {
                    if (!bic.isCutNode(node)) {
                        clusterNodes.add(node);
                    }
                    else{
                        if(!cutNodes.containsKey(node.getValue().toString())){
                            cutNodes.put(node.getValue().toString(), node);
                        }
                    }
                }
            }
        }
        Collection<Collection<Node>> clusters = new LinkedList<Collection<Node>>();
        for (Collection<Node> biconCluster : bicon.values()) {
            if (!biconCluster.isEmpty()) {
                clusters.add(biconCluster);
            }
        }
        for(Node n : g.nodes()){
            if (g.degree(n) == 0 || bic.isCutNode(n)) {
                clusters.add(Arrays.asList(n));
            }
        }
        isBiconnected = clusters.size() == 1;
        return clusters;
    }
}
