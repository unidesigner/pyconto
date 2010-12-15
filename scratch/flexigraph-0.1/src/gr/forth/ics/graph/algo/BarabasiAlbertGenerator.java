package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import gr.forth.ics.util.Args;

/**
 * <p>Simple evolving scale-free random graph generator. At each time
 * step, a new Node is created and is connected to existing vertices
 * according to the principle of "preferential attachment", whereby
 * vertices with higher degree have a higher probability of being
 * selected for attachment.</p>
 *
 * <p>At a given timestep, the probability <code>p</code> of creating an edge
 * between an existing Node <code>v</code> and the newly added Node is
 * <pre>
 * p = (degree(v) + 1) / (|E| + |V|);
 * </pre>
 *
 * <p>where <code>|E|</code> and <code>|V|</code> are, respectively, the number
 * of edges and vertices currently in the network (counting neither the new
 * Node nor the other edges that are being attached to it).</p>
 *
 * <p>Note that the formula specified in the original paper
 * (cited below) was
 * <pre>
 * p = degree(v) / |E|
 * </pre>
 * </p>
 *
 * <p>However, this would have meant that the probability of attachment for any existing
 * isolated Node would be 0.  This version uses Lagrangian smoothing to give
 * each existing Node a positive attachment probability.</p>
 *
 * <p>The graph created may be either directed or undirected (controlled by a constructor
 * parameter); the default is undirected.
 * If the graph is specified to be directed, then the edges added will be directed
 * from the newly added Node u to the existing Node v, with probability proportional to the
 * indegree of v (number of edges directed towards v).  If the graph is specified to be undirected,
 * then the (undirected) edges added will connect u to v, with probability proportional to the
 * degree of v.</p>
 *
 * <p>The <code>parallel</code> constructor parameter specifies whether parallel edges
 * may be created.</p>
 *
 * @see "A.-L. Barabasi and R. Albert, Emergence of scaling in random networks, Science 286, 1999."
 * @author Theofanis Oikonomou
 */
public class BarabasiAlbertGenerator {
    private Graph graph;
    private Set<Node> nodeSet;
    private List<Node> nodeList;
    
    private final int attachedEdgesPerStep;
    private final int steps;
    private final int startNodes;
    private final boolean allowParallel;
    
    private final Random random = new Random();
    
    /**
     * Constructs a new instance of the generator.
     *
     * @param steps the number of times that the algorithm's main loop will
     * run in {@link #generate(Graph)} method
     * @param startNodes number of unconnected 'seed' vertices that the graph should start with
     * @param attachedEdgesPerStep the number of edges that should be attached from the
     * new Node to pre-existing vertices at each time step
     * @param allowParallel specifies whether the algorithm permits parallel edges
     */
    public BarabasiAlbertGenerator(int steps, int startNodes,
            int attachedEdgesPerStep, boolean allowParallel) {
        Args.gte(startNodes, 0);
        Args.gte(steps, 1);
        Args.inRangeII(attachedEdgesPerStep, 0, startNodes);
        this.steps = steps;
        this.startNodes = startNodes;
        this.attachedEdgesPerStep = attachedEdgesPerStep;
        this.allowParallel = allowParallel;
    }
    
    private List<Node> createRandomEdge(Node newNode,
            Set<List<Node>> addedPairs) {
        Node attachNode;
        boolean created_edge = false;
        List<Node> endpoints = new ArrayList<Node>(2);
        do {
            attachNode = (Node)nodeList.get(random.nextInt(nodeList.size()));
            
            endpoints.add(newNode);
            endpoints.add(attachNode);
            // if parallel edges are not allowed, skip attachNode if <newNode, attachNode>
            // already exists; note that because of the way edges are added, we only need to check
            // the list of candidate edges for duplicates.
            if (!allowParallel && addedPairs.contains(endpoints)) {
                continue;
            }
            
            double degree = graph.degree(attachNode, Direction.IN);
            
            // subtract 1 from numVertices because we don't want to count newNode
            // (which has already been added to the graph, but not to nodeIndex)
            double attach_prob = (degree + 1) / (graph.edgeCount() + graph.nodeCount() - 1);
            if (attach_prob >= random.nextDouble()) {
                created_edge = true;
            }
        } while (!created_edge);
        addedPairs.add(endpoints);
        return endpoints;
    }
    
    public Graph generate(Graph g) {
        this.graph = g;
        for (int i = 0; i < startNodes; i++) {
            graph.newNode();
        }
        nodeSet = graph.nodes().drainToSet();
        nodeList = graph.nodes().drainToList();
        
        for (int i = 0; i < steps; i++) {
            Object IS_NEW = new Object();
            Node newNode = graph.newNode();
            newNode.putWeakly(IS_NEW, true);
            // generate and store the new edges; don't add them to the graph
            // yet because we don't want to bias the degree calculations
            // (all new edges in a timestep should be added in parallel)
            Collection<List<Node>> edgesToBeAdded = new LinkedList<List<Node>>();
            
            HashSet<List<Node>> added_pairs = new HashSet<List<Node>>(attachedEdgesPerStep);
            for (int j = 0; j < attachedEdgesPerStep; j++) {
                edgesToBeAdded.add(createRandomEdge(newNode, added_pairs));
            }
            
            // add edges to graph, now that we have them all
            for(List<Node> col : edgesToBeAdded){
                graph.newEdge(col.get(0), col.get(1));
            }
            // now that we're done attaching edges to this new Node,
            // add it to the index
            nodeList.add(newNode);
        }
        graph = null;
        nodeList = null;
        nodeSet = null;
        return g;
    }
}