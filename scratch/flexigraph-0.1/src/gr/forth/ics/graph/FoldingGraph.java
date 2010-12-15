package gr.forth.ics.graph;

import java.util.*;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.ExtendedIterable;
import gr.forth.ics.util.Factory;
import gr.forth.ics.util.SerializableObject;

//TODO: De mporeis na svineis komvous (folder komvous) apo to grafo pou pairneis apo viewFolder.
//den ananewnontai swsta oi domes dedomenwn. Kai de douleyei to na vgaleis ena folder kai na ton 3anavaleis
//argotera, ston idio grafo i se allo (tis idias ierarxias pantws).
//TODO: isws anadromiko FoldingGraph stous ypografous??? 'H einai too-much?
public class FoldingGraph extends GraphForwarder {
    private final Object SUBGRAPH = new SerializableObject();
    private final Object REAL_EDGE = new SerializableObject();
    private final Object PARENT = new SerializableObject();
    private final Object KIDS = new SerializableObject();
    private final Factory<Graph> graphFactory;
    
    private final Graph graph;
    
    public FoldingGraph(Graph graph) {
        this(graph, true);
    }
    
    public FoldingGraph(Graph graph, boolean usePrimaries) {
        super(graph);
        this.graph = graph;
        if (usePrimaries) {
            graphFactory = new Factory<Graph>() {
                public Graph create(Object ignored) {
                    return new PrimaryGraph();
                }
            };
        } else {
            graphFactory = new Factory<Graph>() {
                public Graph create(Object ignored) {
                    return new SecondaryGraph();
                }
            };
        }
    }
    
    public FoldingGraph(Graph graph, Factory<Graph> graphFactory) {
        super(graph);
        Args.notNull(graphFactory);
        this.graph = graph;
        this.graphFactory = graphFactory;
    }
    
    public Node fold() {
        return fold(Collections.<Node>emptyList());
    }
    
    public Node fold(Node ... nodes) {
        Args.notNull((Object[])nodes);
        return fold(Arrays.asList(nodes));
    }
    
    public Node fold(Iterable<Node> nodes) {
        Args.notNull(nodes);
        Collection<Node> importedNodes = ExtendedIterable.wrap(nodes).drainToSet();
        Node folder = graph.newNode();
        Graph subgraph = graphFactory.create(null);
        folder.putWeakly(PARENT, null);
        
        Collection<Node> kids = getKids(folder);
        for (Node n : importedNodes) {
            n.putWeakly(PARENT, folder);
            if (isFolder(n)) {
                kids.add(n);
            }
        }
        folder.putWeakly(SUBGRAPH, subgraph);
        Collection<Edge> interedges = subgraph.importGraph(graph, importedNodes);
        for (Edge interedge : interedges) {
            Edge syntheticEdge = graph.newEdge(
                    subgraph.containsNode(interedge.n1()) ? folder : interedge.n1(),
                    subgraph.containsNode(interedge.n2()) ? folder : interedge.n2()
                    );
            syntheticEdge.putWeakly(REAL_EDGE, getRealEdge(interedge));
        }
        return folder;
    }
    
    public boolean isSyntheticEdge(Edge e) {
        if (e == null) {
            return false;
        }
        return e.has(REAL_EDGE);
    }
    
    public Edge getRealEdge(Edge e) {
        Args.notNull(e);
        if (!isSyntheticEdge(e)) {
            return e;
        }
        return (Edge)e.get(REAL_EDGE);
    }
    
    public boolean isFolder(Node node) {
        return node.has(SUBGRAPH);
    }
    
    public Graph viewFolder(Node folder) {
        if (folder == null) {
            return graph;
        }
        checkFolder(folder);
        return (Graph)folder.get(SUBGRAPH);
    }
    
    public void unfold(Node folder) {
        checkFolder(folder);
        Graph subgraph = folder.getGraph(SUBGRAPH);
        Node parent = folder.getNode(PARENT);
        Graph parentGraph = parent != null ? parent.getGraph(SUBGRAPH) : graph;
        
        Collection<Edge> syntheticEdges = parentGraph.edges(folder).drainToSet();
        Collection<Node> importedNodes = subgraph.nodes().drainToList();
        parentGraph.removeEdges(syntheticEdges);
        parentGraph.importGraph(subgraph);
        folder.remove(SUBGRAPH);
        parentGraph.removeNode(folder);
        for (Edge syntheticEdge : syntheticEdges) {
            if (isSyntheticEdge(syntheticEdge)) { //filter out edges that directly point to groups
                Direction direction = syntheticEdge.n1() == folder ? Direction.OUT : Direction.IN;
                Edge realEdge = getRealEdge(syntheticEdge);
                //which node should we ascent to find the correct unfolded node? (The opposite node will remain unchanged)
                Node searchNode = direction == Direction.OUT ? realEdge.n1() : realEdge.n2();
                Node constantNode = direction == Direction.OUT ? syntheticEdge.n2() : syntheticEdge.n1();
                Node unfoldedParent = searchNode.getNode(PARENT);
                while (unfoldedParent != folder) {
                    searchNode = unfoldedParent;
                    unfoldedParent = searchNode.getNode(PARENT);
                }
                if (!parentGraph.containsNode(searchNode)) {
                    continue;
                }
                //now searchNode and constantNode are the nodes of the new edge to be inserted
                if (realEdge.isIncident(searchNode) && realEdge.isIncident(constantNode)) {
                    //no synthetic edge needed, the real one is revealed
                    parentGraph.reinsertEdge(realEdge);
                } else {
                    Edge newEdge = direction == Direction.OUT ?
                        parentGraph.newEdge(searchNode, constantNode) :
                        parentGraph.newEdge(constantNode, searchNode);
                    newEdge.putWeakly(REAL_EDGE, realEdge);
                }
            }
        }
        //move subfolders of unfolded folder to its parent (or null)
        Collection<Node> kids = getKids(folder);
        if (parent == null) {
            for (Node kid : kids) {
                kid.remove(PARENT);
            }
        } else {
            for (Node kid : kids) {
                if (isFolder(kid)) {
                    kid.putWeakly(PARENT, parent);
                }
            }
        }
        //also removing this folder from its parent kid list
        if (parent != null) {
            getKids(parent).remove(folder);
        }
        //setting for all imported nodes, the correct parent
        for (Node importedNode : importedNodes) {
            importedNode.putWeakly(PARENT, parent);
        }
    }
    
    public Node getParent(Node node) {
        Args.notNull(node);
        return node.getNode(PARENT);
    }
    
    private void checkFolder(Node folder) {
        if (!isFolder(folder)) {
            throw new IllegalArgumentException("Not a folder node");
        }
    }
    
    @SuppressWarnings("unchecked")
    private Collection<Node> getKids(Node parent) {
        Collection<Node> kids = (Collection<Node>)parent.get(KIDS);
        if (kids == null) {
            kids = new HashSet<Node>();
            parent.putWeakly(KIDS, kids);
        }
        return kids;
    }
}
