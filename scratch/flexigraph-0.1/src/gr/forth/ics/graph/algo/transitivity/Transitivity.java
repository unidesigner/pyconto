package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Orders;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.LinkedList;

/**
 * Transitive closure and reduction algorithms for directed graphs.
 * 
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Transitivity {
    private Transitivity() { }

    /**
     * Creates the transitive {@link Closure} of a directed acyclic graph, using the specified factory
     * of successor sets. The time complexity is {@code O(|E|U)} where {@code |E|} is the number
     * of edges of the graph and {@code U} is the average time to {@link MutableSuccessorSet#addAll(SuccessorSet) merge}
     * two successor sets, so the efficiency of the given {@link SuccessorSetFactory} is crucially affecting this
     * algorithm. The space complexity also depends directly on the {@code SuccessorSetFactory}, and
     * particularly in the space that the generated successor sets use.
     *
     * <p>To actually create an edge in the graph for every (directly or indirectly) connected pair
     * of nodes, materialize the closure in the graph by
     * {@code Transitivity.materialize(g, closure)}.
     *
     * @param g the directed acyclic graph to create the transitive closure of
     * @param successorSetFactory the factory of successor sets
     * @return the closure of the graph
     * @see SuccessorSetFactory
     * @see #materialize(Graph, Closure) 
     */
    public static Closure acyclicClosure(InspectableGraph g, SuccessorSetFactory successorSetFactory) {
        ClosureImpl closure = new ClosureImpl();
        for (Node n : Orders.reverseTopological(g)) {
            MutableSuccessorSet successors = successorSetFactory.create();
            closure.setSuccessors(n, successors);
            for (Node successor : g.adjacentNodes(n, Direction.OUT)) {
                if (successor == n) continue;
                successors.addAll(closure.successorsOf(successor));
                successors.add(successor);
            }
        }
        return closure;
    }

//    private static FoldingGraph makeAcyclic(Graph g) {
//        FoldingGraph foldingGraph = new FoldingGraph(g, false);
//        Clusterer scc = Clusterers.stronglyConnectedComponents(g);
//        //for each strongly connected component, remove edges and make a simple cycle instead
//        for (Object cluster : scc.getClusters()) {
//            Node folder = foldingGraph.fold(scc.getCluster(cluster));
//            Graph folded = foldingGraph.viewFolder(folder);
//
//            int removed = folded.removeAllEdges();
//
//            Node first = null;
//            Node last = null;
//            for (Node n : folded.nodes()) {
//                if (first == null) first = n;
//                if (last != null) {
//                    folded.newEdge(last, n);
//                }
//                last = n;
//            }
//            if (last != null) {
//                if (last != first || removed > 0) {
//                    //avoid creating self-loop, except if the original graph did have one
//                    folded.newEdge(last, first);
//                }
//            }
//        }
//        return foldingGraph;
//    }
    
//    public static Closure generalClosure(Graph g, SuccessorSetFactory successorSetFactory) {
//        FoldingGraph dag = makeAcyclic(g);
////        final List<Edge> deletedEdges = new LinkedList<Edge>();
////        dag.addEdgeListener(new EmptyGraphListener() {
////            @Override
////            public void edgeRemoved(GraphEvent e) {
////                deletedEdges.add(e.getEdge());
////            }
////        });
//        ClosureImpl closure = (ClosureImpl)acyclicClosure(dag, successorSetFactory);
//
//        //unfold folders
//        for (Node n : dag.nodes()) {
//            if (!dag.isFolder(n)) continue;
//            for (Node kid : dag.viewFolder(n).nodes()) {
//                closure.setSuccessors(kid, closure.successorsOf(n));
//            }
//            dag.unfold(n);
//        }
//        return closure;
//    }

    /**
     * Creates the transitive reduction of a directed acyclic graph, using a {@code pathFinder} object to
     * check whether two arbitrary nodes of the specified graph are connected via some path.
     * The given graph is directly modified (by deleting all edges but those belonging to the
     * transitive reduction).
     *
     * <p>The time complexity of this algorithm is {@code O(|E|P)}, where {@code |E|}
     * is the number of edges and {@code P} is the time complexity of a single {@code pathFinder}
     * invocation.
     *
     * @param g the directed acyclic graph to create the transitive reduction of
     * @param pathFinder an object that can answer whether two nodes are connected via
     * some path
     */
    public static void acyclicReduction(Graph g, PathFinder pathFinder) {
        for (Node n : Orders.topological(g)) {
            for (Edge e1 : g.edges(n, Direction.OUT)) {
                for (Edge e2 : g.edges(n, Direction.OUT)) {
                    if (e1 == e2) continue;
                    if (pathFinder.pathExists(e1.opposite(n), e2.opposite(n))) {
                        g.removeEdge(e2);
                    }
                }
            }
        }
    }

//    public static void generalReduction(Graph g, final PathFinder pathFinder) {
//        final FoldingGraph dag = makeAcyclic(new SecondaryGraph(g));
//        final List<Edge> deletedEdges = new LinkedList<Edge>();
//        dag.addEdgeListener(new EmptyGraphListener() {
//            @Override
//            public void edgeRemoved(GraphEvent e) {
//                deletedEdges.add(e.getEdge());
//            }
//        });
//        acyclicReduction(dag, new PathFinder() {
//            public boolean pathExists(Node n1, Node n2) {
//                return pathFinder.pathExists(dag.viewFolder(n1).aNode(), dag.viewFolder(n2).aNode());
//            }
//        });
//
//        for (Edge e : deletedEdges) {
//            g.removeEdge(dag.getRealEdge(e));
//        }
//    }

    /**
     * Translates a specified transitive closure to edges in a graph, that is, after this method
     * every node of the graph has at least one edge to each node belonging to each
     * successor set, as defined by the given {@code closure}.
     *
     * @param g the graph in which to materialize the closure
     * @param closure the closure to materialize
     */
    public static void materialize(Graph g, Closure closure) {
        for (Node n : g.nodes()) {
            for (Node s : closure.successorsOf(n)) {
                if (!g.areAdjacent(n, s, Direction.OUT)) {
                    g.newEdge(n, s);
                }
            }
        }
    }

    /**
     * Sweeps the graph in reverse-topological order ({@literal i.e.} starting with nodes with
     * zero out-degree, and ending at nodes with zero in-degree), creates on-the-fly
     * the successor set for each node, notifies the provided {@link SuccessorsListener} to do
     * arbitrary processing when the complete successor set is available for each node, and then discards the successor sets
     * when they are no longer needed. This method is more memory-efficient than
     * {@link #acyclicClosure(InspectableGraph, SuccessorSetFactory)} since it only stores successor
     * sets for the duration they are needed, and can be used by providing a {@code SuccessorsListener}
     * that processes the successor sets.
     *
     * <p>Consider for example the graph {@code [A-->B-->C]}. The node {@code C} will be processed
     * first (through the {@code SuccesorsListener}), with an empty successor set, then the node {@code B} with a successor set of {@code
     * {C}}, and finally the node {@code A} with a successor set of {@code {B, C}}.
     *
     * <p>See {@link #acyclicClosure(InspectableGraph, SuccessorSetFactory)} to understand the
     * time complexity of the algorithm as well as the function of the {@code SuccessorSetFactory}.
     *
     * @param g the graph whose nodes to create the successor sets for
     * @param successorSetFactory the factory of successor sets
     * @param listener the listener that processes nodes and their respective successor sets
     */
    public static void acyclicClosureSweep(final InspectableGraph g,
            SuccessorSetFactory successorSetFactory, SuccessorsListener listener) {
        final Object kidsKey = new Object();
        //this counts the remaining unvisited kids of a node
        class Kids {
            int kids;
            Kids(Node n) {
                kids = g.inDegree(n);
            }
        }

        ClosureImpl closure = new ClosureImpl();
        for (Node n : Orders.reverseTopological(g)) {
            Kids kids = new Kids(n);
            n.putWeakly(kidsKey, kids);
            
            MutableSuccessorSet successors = successorSetFactory.create();
            if (kids.kids > 0) {
                closure.setSuccessors(n, successors);
            }
            for (Node successor : g.adjacentNodes(n, Direction.OUT)) {
                if (successor == n) continue;
                successors.addAll(closure.successorsOf(successor));
                successors.add(successor);
            }
            listener.process(n, successors);
            
            for (Node successor : g.adjacentNodes(n, Direction.OUT)) {
                Kids successorKids = (Kids)successor.get(kidsKey);
                successorKids.kids--;
                if (successorKids.kids == 0) {
                    //destroy successor set of parent, not needed any more!
                    closure.removeSuccessors(successor);
                    successor.remove(kidsKey);
                }
            }
        }
    }

    /**
     * Callback that can process a graph's node and its set of successors. Used by
     * {@link #acyclicClosureSweep(InspectableGraph, SuccessorSetFactory, Transitivity.SuccessorsListener)}.
     */
    public interface SuccessorsListener {
        /**
         * Called once for each graph's node and provides its successor set.
         *
         * @param node a node
         * @param successorSet the successor set of the node (representing the reachable nodes
         * from the provided node)
         */
        void process(Node node, SuccessorSet successorSet);
    }

    public static Closure generalClosure(InspectableGraph g, SuccessorSetFactory successorSetFactory) {
        new StackTc(g);
        return null;
    }

    private static class StackTc {
        final InspectableGraph g;
        final Object infoKey = new Object();
        int indexCounter = 0;
        final LinkedList<Node> uStack = new LinkedList<Node>();
        final LinkedList<Object> cStack = new LinkedList<Object>();

        StackTc(InspectableGraph g) {
            this.g = g;
            for (Node n : g.nodes()) {
                if (!n.has(infoKey)) {
                    visit(n);
                }
            }
        }

        void visit(Node u) {
            NodeInfo uInfo = new NodeInfo();
            u.putWeakly(infoKey, uInfo);
            uInfo.root = uInfo.index = indexCounter++;
            uStack.push(u);
            uInfo.savedHeight = uStack.size();
            for (Node w : g.adjacentNodes(u, Direction.OUT)) {
                if (w == u) {
                    uInfo.selfLoop = true;
                } else {
                    if (!w.has(infoKey)) {
                        visit(w);
                    }
                    NodeInfo wInfo = (NodeInfo)w.get(infoKey);
                    if (wInfo.comp == null) {
                        uInfo.root = Math.min(uInfo.root, wInfo.root);
                    } else if (wInfo.comp != uInfo.comp) { //(u, w) not a forward edge
                        cStack.push(wInfo.comp);
                    }
                }
            }
            if (uInfo.root == uInfo.root) {
                Object C = new Object();
                if (uStack.peekFirst() != u || uInfo.selfLoop) {
                    
                } else {
                    
                }
            }
        }

        private static class NodeInfo {
            int index;
            int root;
            Object comp;
            int savedHeight;
            boolean selfLoop;
        }
    }
}
