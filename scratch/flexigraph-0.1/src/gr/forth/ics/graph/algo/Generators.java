package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.GraphChecker;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.path.Paths;
import gr.forth.ics.graph.event.EmptyGraphListener;
import gr.forth.ics.graph.event.NodeListener;
import gr.forth.ics.graph.path.Cycles;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.TransformingIterable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Graph generator utilities.
 * 
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Generators {
    private Generators() { }

    /**
     * Creates a path with the specified number of nodes.
     * 
     * @param g the graph object in which to create the path
     * @param nodes the number of nodes that the created path will have
     * @return a {@link Path} object denoting the created path
     */
    public static Path createPath(Graph g, int nodes) {
        Node[] n = g.newNodes(nodes);
        LinkedList<Edge> edges = new LinkedList<Edge>();
        for (int i = 1; i < n.length; i++) {
            edges.add(g.newEdge(n[i - 1], n[i]));
        }
        return createPathFromEdges(edges);
    }
    
    /**
     * Creates a cycle with the specified number of nodes.
     *
     * @param g the graph object in which to create the cycle
     * @param nodes the number of nodes that the created cycle will have
     * @return a {@link Path} object denoting the created cycle. Its first node will also be its last node
     */
    public static Path createCycle(Graph g, int nodes) {
        Node[] n = g.newNodes(nodes);
        LinkedList<Edge> edges = new LinkedList<Edge>();
        for (int i = 1; i < n.length; i++) {
            edges.add(g.newEdge(n[i - 1], n[i]));
        }
        edges.addLast(g.newEdge(n[n.length - 1], n[0]));
        return createPathFromEdges(edges);
    }

    private static Path createPathFromEdges(List<Edge> edges) {
        return Paths.newPath(new TransformingIterable<Edge, Path>(edges) {
            @Override
            protected Path transform(Edge source) {
                return source.asPath();
            }
        });
    }

    /**
     * Creates a grid with the specified number of rows and columns.
     *
     * @param g the graph object in which to create the cycle
     * @param nodesPerDimension how many nodes to create for each dimension
     * (the number of dimensions is the number of the int arguments)
     */
    public static void createGrid(Graph g, int... nodesPerDimension) {
        createGrid(g, false, nodesPerDimension);
    }
    
    /**
     * Creates a grid with the specified number of rows and columns, which folds around and connects back to itself.
     *
     * @param g the graph object in which to create the cycle
     * @param nodesPerDimension how many nodes to create for each dimension
     * (the number of dimensions is the number of the int arguments)
     */
    public static void createFoldedGrid(Graph g, int... nodesPerDimension) {
        createGrid(g, true, nodesPerDimension);
    }
    
    private static void createGrid(Graph g, boolean folded, int... nodesPerDimension) {
        Args.notNull(g);
        if (nodesPerDimension.length == 0) {
            return;
        }
        int totalNodes = 1;
        for (int dimension : nodesPerDimension) {
            totalNodes *= dimension;
        }
        Mapper mapper = new Mapper(nodesPerDimension);
        Node[] nodes = g.newNodes(totalNodes);
        int[] point = new int[nodesPerDimension.length];
        int[] nextPoint = new int[nodesPerDimension.length];
        for (int i = 0; i < totalNodes; i++) {
            Node n1 = nodes[i];
            mapper.indexToPoint(i, point);
            for (int k = 0; k < nodesPerDimension.length; k++) nextPoint[k] = point[k];
            for (int j = 0; j < nodesPerDimension.length; j++) {
                nextPoint[j]++;
                if (nextPoint[j] == nodesPerDimension[j]) {
                    nextPoint[j] = 0;
                    if (!folded) {
                        nextPoint[j] = point[j]; //revert back
                        continue;
                    }
                }
                Node n2 = nodes[mapper.pointToIndex(nextPoint)];
                g.newEdge(n1, n2);
                nextPoint[j] = point[j]; //revert back
            }
        }
    }

    private static class Mapper {
        private final int[] maxPerDimension;
        
        Mapper(int... maxPerDimension) {
            this.maxPerDimension = maxPerDimension;
        }

        int pointToIndex(int... point) {
            int index = 0;
            int unit = 1;
            int dimensionIndex = 0;
            for (int dimension : maxPerDimension) {
                index += (point[dimensionIndex++] * unit);
                unit *= dimension;
            }
            return index;
        }

        void indexToPoint(int index, int[] point) {
            int pos = 0;
            for (int dimension : maxPerDimension) {
                point[pos++] = index % dimension;
                index /= dimension;
            }
        }
    }
    
    /**
     * Constructs a random graph with small world properties.
     * Graph generator that produces a random graph with small world properties. The underlying model is
     * an <tt>[gridSize]x[gridSize]</tt> toroidal grid. Each node <tt>u</tt> has four local connections, one to each of its neighbors, and
     * in addition one long range connection to some randomly chosen <tt>v</tt> according to
     * probability proportional to <tt>d^(-clusteringExponent)</tt> where <tt>d</tt> is the lattice distance between
     * <tt>u</tt> and <tt>v</tt>.
     * 
     * @param g the graph object in which to create the graph
     * @param gridSize the grid size (length of row or column dimension)
     * @param clusteringExponent the clustering exponent parameter (somewhere around 2 is a good choice)
     * @see "Navigation in a small world J. Kleinberg, Nature 406(2000), 845."
     */
    public static void createSmallWorld_Kleinberg(Graph g, int gridSize, double clusteringExponent) {
        createSmallWorld_Kleinberg(g, new Random(), gridSize, 2);
    }
    
    /**
     * Constructs a random graph with small world properties, based on Kleinberg's algorithm.
     * Graph generator that produces a random graph with small world properties. The underlying model is
     * an <tt>[gridSize]x[gridSize]</tt> toroidal grid. Each node <tt>u</tt> has four local connections, one to each of its neighbors, and
     * in addition one long range connection to some randomly chosen <tt>v</tt> according to
     * probability proportional to <tt>d^(-clusteringExponent)</tt> where <tt>d</tt> is the lattice distance between
     * <tt>u</tt> and <tt>v</tt>.
     * 
     * @param g the graph object in which to create the graph
     * @param random the random number generator to use
     * @param gridSize the grid size (length of row or column dimension)
     * @param clusteringExponent the clustering exponent parameter (somewhere around 2 is a good choice)
     * @see "Navigation in a small world J. Kleinberg, Nature 406(2000), 845."
     */
    public static void createSmallWorld_Kleinberg(Graph g, Random random, int gridSize, double clusteringExponent) {
        Args.notNull(g);
        Args.notNull(random);
        new KleinbergSmallWorldGenerator(random, gridSize, clusteringExponent).generate(g);
    }
    
    /**
     * Constructs a random graph with small world properties, based on Watts/Strogatz algorithm.
     * A beta-model is used as proposed by Duncan Watts. The basic ideas is
     * to start with a one-dimensional ring lattice in which each node has {@code k}-neighbors and then randomly
     * rewire the edges, with probability {@code p}, in such a way that a small world networks can be created for
     * certain values of {@code p} and {@code k} that exhibit low characteristic path lengths and high clustering coefficient.
     *
     * @param g the graph object in which to create the graph
     * @param nodes the number of nodes to create
     * @param k the number of neighbors for each node, in the generated ring lattice
     * @param p the probability of an edge rewiring
     * @see "Small Worlds:The Dynamics of Networks between Order and Randomness by D.J. Watts"
     */
    public static void createSmallWorld_Watts(Graph g, int nodes, int k, double p) {
        createSmallWorld_Watts(g, new Random(), nodes, k, p);
    }
    
    /**
     * Constructs a random graph with small world properties, based on Watts/Strogatz algorithm.
     * A beta-model is used as proposed by Duncan Watts. The basic ideas is
     * to start with a one-dimensional ring lattice in which each node has {@code k}-neighbors and then randomly
     * rewire the edges, with probability {@code p}, in such a way that a small world networks can be created for
     * certain values of {@code p} and {@code k} that exhibit low characteristic path lengths and high clustering coefficient.
     * 
     * @param g the graph object in which to create the graph
     * @param random the random number generator to use
     * @param nodes the number of nodes to create
     * @param k the number of neighbors for each node, in the generated ring lattice
     * @param p the probability of an edge rewiring
     * @see "Small Worlds:The Dynamics of Networks between Order and Randomness by D.J. Watts"
     */
    public static void createSmallWorld_Watts(Graph g, Random random, int nodes, int k, double p) {
        new WattsStrogatzGenerator(random, nodes, p, k);
    }
    
    /**
     * Creates a ring lattice.
     * 
     * @param g the graph object in which to create the graph
     * @param nodes the number of nodes to create
     * @param neighbors the number of (outgoing) neighbors of each node
     */
    public static void createRingLattice(Graph g, int nodes, int neighbors) {
        Args.gte(nodes, 2.0);
        Args.inRangeII(neighbors, 2, (nodes - 2) / 2);
        Node[] n = g.newNodes(nodes);
        for (int i = 0; i < nodes; i++) {
            for(int j = 0; j < neighbors; j++){
                g.newEdge(n[i], n[(i + (neighbors - j)) % nodes]);
            }
        }
    }
    
    /**
     * Creates a random graph, using Erdos-Renyi algorithm.
     * 
     * @param g the graph object in which to create the graph
     * @param nodes the number of nodes to generate
     * @param p the probability {@code (0 >= p >= 1)} that, for any pair of nodes,
     * an edge between them should be created
     */
    public static void createRandom(Graph g, int nodes, double p) {
        createRandom(g, new Random(), nodes, p);
    }
    
    /**
     * Creates a random graph, using Erdos-Renyi algorithm.
     * 
     * @param g the graph object in which to create the graph
     * @param random the random number generator to use
     * @param nodes the number of nodes to generate
     * @param p the probability {@code (0 >= p >= 1)} that, for any pair of nodes,
     * an edge between them should be created
     */
    public static void createRandom(Graph g, Random random, int nodes, double p) {
        Args.gt(nodes, 0);
        Args.notNull(random);
        Args.inRangeII(p, 0.0, 1.0);
        Node[] n = g.newNodes(nodes);
        for (Node n1 : n) {
            for (Node n2 : n) {
                if (random.nextDouble() < p){
                    g.newEdge(n1, n2);
                }
            }
        }
    }


    /**
     * Creates a random tree with specified number of nodes.
     *
     * @param g the graph object in which to create the tree
     * @param nodes the number of nodes the produced tree will have
     * @param directionFromRoot {@code Direction.OUT} creates edges from the root towards
     * to the leaves; {@code Direction.IN} creates edges from the leaves to the root; {@code Direction.EITHER}
     * creates both pairs of edges.
     * @return the root of the generated tree, or {@code null} if {@code nodes == 0}
     */
    public static Node createRandomTree(Graph g, int nodes, Direction directionFromRoot) {
        return createRandomTree(g, new Random(), nodes, directionFromRoot);
    }
    
    /**
     * Creates a random tree with specified number of nodes.
     *
     * @param g the graph object in which to create the tree
     * @param random the random number generator to use
     * @param nodes the number of nodes the produced tree will have
     * @param directionFromRoot {@code Direction.OUT} creates edges from the root towards
     * to the leaves; {@code Direction.IN} creates edges from the leaves to the root; {@code Direction.EITHER}
     * creates both pairs of edges.
     * @return the root of the generated tree, or {@code null} if {@code nodes == 0}
     */
    public static Node createRandomTree(Graph g, Random random, int nodes, Direction directionFromRoot) {
        Args.notNull(g);
        Args.notNull(random);
        Args.notNull(directionFromRoot);
        List<Node> parents = new ArrayList<Node>();
        //first create parents, then kids. This implies root is created first. Method createRandomBiconnectedGraph depends on this fact.
        while (nodes-- > 0) {
            Node newNode = g.newNode();
            if (!parents.isEmpty()) {
                Node parent = parents.get(random.nextInt(parents.size()));
                switch (directionFromRoot) {
                    case OUT:
                        g.newEdge(parent, newNode); break;
                    case IN:
                        g.newEdge(newNode, parent); break;
                    case EITHER:
                        g.newEdge(parent, newNode);
                        g.newEdge(newNode, parent); break;
                    default: throw new AssertionError();
                }
            }
            parents.add(newNode);
        }
        return parents.isEmpty() ? null : parents.get(0);
    }

    /**
     * Creates a random biconnected graph. It starts by with creating a random tree, and connects
     * all leaf nodes to the root.
     *
     * @param g the graph object in which to create the biconnected graph
     * @param nodes the number of nodes the produced biconnected graph will have
     */
    public static void createRandomBiconnectedGraph(Graph g, int nodes) {
        createRandomBiconnectedGraph(g, new Random(), nodes);
    }

    /**
     * Creates a random biconnected graph. It starts by with creating a random tree, and further
     * connects all leaf nodes plus the root with a single simple path, thus there are always exactly two node-disjoint
     * paths connecting any pair of nodes: a path following tree edges, and a path through the
     * leaf connections.
     * 
     * @param g the graph object in which to create the biconnected graph
     * @param random the random number generator to use
     * @param nodes the number of nodes the produced biconnected graph will have
     */
    public static void createRandomBiconnectedGraph(Graph g, Random random, int nodes) {
        //we also want to handle graphs with existing elements, so keeping track which nodes created here
        if (nodes < 0) throw new IllegalArgumentException("Negative number of nodes: " + nodes);
        if (nodes == 0) return;
        final List<Node> createdNodes = new ArrayList<Node>(nodes);
        NodeListener nodeListener = new EmptyGraphListener() {
            @Override
            public void nodeAdded(GraphEvent e) {
                createdNodes.add(e.getNode());
            }
        };
        g.addNodeListener(nodeListener);
        createRandomTree(g, random, nodes, Direction.OUT);
        g.removeNodeListener(nodeListener);

        Node last = createdNodes.get(0);
        for (Node node : createdNodes) {
            if (g.outDegree(node) > 0) continue;
            g.newEdge(last, node);
            last = node;
        }
    }
    
    /**
     * Creates a random dag, using Erdos-Renyi algorithm, while making sure that no cycle exists.
     * The edges can be less than a call to {@link #createRandom(Graph, Random, int, double)}
     * would generate.
     * 
     * @param g the graph object in which to create the graph
     * @param nodes the number of nodes to generate
     * @param p the probability {@code (0 >= p >= 1)} that, for any pair of nodes,
     * an edge between them should be created.
     */
    public static void createRandomDag(Graph g, int nodes, double p) {
        createRandomDag(g, new Random(), nodes, p);
    }
    
    /**
     * Creates a random dag, using Erdos-Renyi algorithm, while making sure that no cycle exists.
     * The edges can be less than a call to {@link #createRandom(Graph, Random, int, double)}
     * would generate.
     * 
     * @param g the graph object in which to create the graph
     * @param random the random number generator to use
     * @param nodes the number of nodes to generate
     * @param p the probability {@code (0 >= p >= 1)} that, for any pair of nodes,
     * an edge between them should be created.
     */
    public static void createRandomDag(Graph g, Random random, int nodes, double p) {
        Generators.createRandom(g, random, nodes, p);
        Path cycle;
        while ((cycle = Cycles.findCycle(g)) != null) {
            g.removeEdge(cycle.tailEdge());
        }
    }
    
    /**
     * Creates a graph that tries to honor the specified degree distribution. The degree distribution
     * must be graphical, as defined in {@link Graphs#isSequenceGraphical(int[])}.
     * @param g the graph object in which to create the graph
     * @param nodeDegreeDistribution the distribution of node degrees, which must be graphical
     * @see Graphs#isSequenceGraphical(int[]) 
     */
    public static void createGeneral(Graph g, int ... nodeDegreeDistribution) {
        createGeneral(g, new Random(), nodeDegreeDistribution);
    }
    
    /**
     * Creates a graph that tries to honor the specified degree distribution. The degree distribution
     * must be graphical, as defined in {@link Graphs#isSequenceGraphical(int[])}.
     * @param g the graph object in which to create the graph
     * @param random the random number generator to use
     * @param nodeDegreeDistribution the distribution of node degrees, which must be graphical
     * @see Graphs#isSequenceGraphical(int[]) 
     */
    public static void createGeneral(Graph g, Random random, int ... nodeDegreeDistribution) {
        Args.notNull(nodeDegreeDistribution);
        Args.isTrue("Sequence is not graphical", GraphChecker.isSequenceGraphical(nodeDegreeDistribution));
        Node[] nodes = g.newNodes(nodeDegreeDistribution.length);
        final Object STUBS = new Object();
        for(int i = 0; i < nodes.length; i++){
            Node n = nodes[i];
            n.putWeakly(STUBS, nodeDegreeDistribution[i]);
        }
        for (Node n : nodes) {
            while(n.getInt(STUBS) > g.degree(n)) {
                int index = random.nextInt(nodes.length);
                Node tmp = nodes[index];
                if (tmp.getInt(STUBS) > g.degree(tmp) && !g.areAdjacent(n, tmp)){
                    g.newEdge(n, tmp);
                }
            }
        }
    }
}
