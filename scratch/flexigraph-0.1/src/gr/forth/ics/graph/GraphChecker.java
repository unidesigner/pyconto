package gr.forth.ics.graph;

import gr.forth.ics.graph.algo.Biconnectivity;
import gr.forth.ics.graph.algo.Clusterers;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.algo.Dfs;
import gr.forth.ics.util.FastLinkedList;
import java.util.HashMap;
import java.util.Map;

/**
 * Various graph property checking static methods.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class GraphChecker {
    private GraphChecker() { }
    
    public static boolean isTree(InspectableGraph graph) {
        if (graph.nodeCount() == 0) {
            return true;
        }
        CycleChecker dfs = CycleChecker.checkCycle(graph);
        return !dfs.hasCycle && dfs.getComponentCount() == 1;
    }
    
    public static boolean isForest(InspectableGraph graph) {
        if (graph.nodeCount() == 0) {
            return true;
        }
        CycleChecker dfs = CycleChecker.checkCycle(graph);
        return !dfs.hasCycle;
    }

    public static boolean isBiconnected(InspectableGraph graph) {
        Biconnectivity bicon = Biconnectivity.execute(graph);
        if (bicon.componentsCount() > 1) {
            return false;
        }
        if (graph.edgeCount() == 0) {
            return true;
        }
        Object component = bicon.componentOf(graph.anEdge());
        for (Edge e : graph.edges()) {
            if (bicon.componentOf(e) != component) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns whether the specified graph is acyclic (in other words, a dag)
     * 
     * @param g the graph to be checked whether it is acyclic
     * @return whether the specified graph is acyclic
     */
    public static boolean isAcyclic(InspectableGraph g) {
        return CycleChecker.checkCycle(g).hasCycle == false;
    }
    
    private static class CycleChecker extends Dfs {
        private boolean hasCycle;

        private CycleChecker(InspectableGraph g) {
            super(g, Direction.OUT);
        }

        static CycleChecker checkCycle(InspectableGraph g) {
            CycleChecker checker = new CycleChecker(g);
            checker.execute();
            return checker;
        }

        @Override protected boolean visitBackEdge(Path path) {
            hasCycle = true;
            return true;
        }
    }

    /**
     * Tests whether the specified graph is connected, using undirected semantics (without needing
     * to wrap the graph in {@link Graphs#undirected(InspectableGraph)}. An
     * empty graph, or a graph with a single node, is considered connected.
     *
     * @param g a graph
     * @return whether the graph is connected, regardless of edge direction
     * @see Clusterers#connectedComponents(InspectableGraph)
     */
    public static boolean isConnected(InspectableGraph g) {
        return ComponentChecker.checkConnected(g).connected;
    }

    private static class ComponentChecker extends Dfs {
        private boolean onFirstComponent = false;
        boolean connected = true;

        private ComponentChecker(InspectableGraph g) {
            super(g, Direction.EITHER);
        }

        static ComponentChecker checkConnected(InspectableGraph g) {
            ComponentChecker checker = new ComponentChecker(g);
            checker.execute();
            return checker;
        }

        @Override
        protected boolean visitNewTree(Node node) {
            if (onFirstComponent) {
                connected = false;
                return true;
            }
            onFirstComponent = true;
            return false;
        }
    }

    /**
     * Tests whether the specified graph is strongly connected.
     *
     * @param g a graph
     * @return whether the graph is strongly connected
     * @see Clusterers#stronglyConnectedComponents(InspectableGraph)
     */
    public static boolean isStronglyConnected(InspectableGraph g) {
        return Clusterers.stronglyConnectedComponents(g).getClusters().size() <= 1;
    }

    /**
     * @see "A. Tripathi and S. Vijay1, A note on a theorem of Erdos & Gallai, Discrete Mathematics, 265, p. 417-420, 2003."
     */
    //TODO: slow? undocumented preconditions?
    public static boolean isSequenceGraphical(int ... degreeSequence){
        int sum = 0;
        FastLinkedList<Integer> a = new FastLinkedList<Integer>();
        FastLinkedList<Integer> m = new FastLinkedList<Integer>();
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i : degreeSequence) {
            sum += i;
            if (map.containsKey(i)) {
                if (!a.contains(i)) {
                    a.addLast(i);
                }
                int times = map.get(i);
                map.put(i, ++times);
            } else {
                if (!a.contains(i)) {
                    a.addLast(i);
                }
                map.put(i, 1);
            }
        }

        for (int o = 0; o < a.size(); o++) {
            m.addLast(map.get(a.get(o)));
        }

        if (sum % 2 == 0) {
            for (int k = 1; k <= m.size(); k++) {
                int n = -1;
                for (int i = 1; i <= k; i++) {
                    n += m.get(i - 1);
                }
                int sum1 = 0;
                for (int b = 0; b < n; b++) {
                    sum1 += degreeSequence[b];
                }
                int sum2 = 0;
                for (int bb = n; bb < degreeSequence.length; bb++) {
                    sum2 += Math.min(n, degreeSequence[bb]);
                }
                sum2 += n * (n - 1);
                if (sum1 > sum2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /*
     *
static boolean 	isBipartite(Graph graph)
          Checks whether or not the given graph his bipartite.
static boolean 	isCyclic(Graph graph)
          Checks whether or not the given graph contains a directed cycle.
static boolean 	isMultipleEdgeFree(Graph graph)
          Checks whether or not the given graph contains multiple edges, i.e.
static boolean 	isNaryTree(Graph graph, int n)
          Checks whether or not the given graph is a rooted tree where each node has a maximum of n children.
static boolean 	isPlanar(Graph graph)
          Checks whether or not the given graph is planar.
static boolean 	isRootedTree(Graph graph)
          Checks whether or not the given graph is a rooted tree.
static boolean 	isSelfLoopFree(Graph graph)
          Checks whether or not the given graph contains selfloops.
static boolean 	isSimple(Graph graph)
          Checks whether or not the given graph is simple, i.e.
     */
}

