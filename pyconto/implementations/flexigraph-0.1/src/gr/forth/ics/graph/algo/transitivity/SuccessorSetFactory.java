package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.util.IntervalSet;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A factory of {@link SuccessorSet}. The representation of successor sets is crucial in
 * time and space complexity of transitive closure/reduction algorithms. The created
 * {@code SuccessorSet} instances must be able to create the union of each other (i.e.,
 * when calling {@link MutableSuccessorSet#addAll(SuccessorSet)} using two successor sets created
 * by the same factory, the call should always succeed), but they are not necessary to handle
 * successor sets created by different factories.
 *
 * @see Transitivity
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class SuccessorSetFactory {
    public abstract MutableSuccessorSet create();

    /**
     * Returns a {@code SuccessorSetFactory} that creates successor sets represented
     * as {@code HashSet}s.
     *
     * @return a {@code SuccessorSetFactory} that creates successor sets represented
     * as {@code HashSet}s
     */
    public static SuccessorSetFactory hashSetBased() {
        return HashSetBasedSuccessorSetFactory.instance;
    }

    private static class HashSetBasedSuccessorSetFactory extends SuccessorSetFactory {
        static final SuccessorSetFactory instance = new HashSetBasedSuccessorSetFactory();

        public MutableSuccessorSet create() {
            return new HashSetBasedSuccessorSet();
        }

        private static class HashSetBasedSuccessorSet implements MutableSuccessorSet {
            private final Set<Node> successors = new HashSet<Node>();

            public boolean contains(Node node) {
                return successors.contains(node);
            }

            public Set<Node> toSet() {
                return Collections.unmodifiableSet(successors);
            }

            public Iterator<Node> iterator() {
                return successors.iterator();
            }

            public void add(Node node) {
                successors.add(node);
            }

            public void addAll(SuccessorSet successorSet) {
                for (Node n : successorSet) {
                    successors.add(n);
                }
            }

            @Override
            public String toString() {
                return successors.toString();
            }

            @Override
            public boolean equals(Object that) {
                if (!(that instanceof HashSetBasedSuccessorSet)) {
                    return false;
                }
                return successors.equals(((HashSetBasedSuccessorSet)that).successors);
            }

            @Override
            public int hashCode() {
                return successors.hashCode();
            }
        }
    }

    /**
     * Returns a {@code SuccessorSetFactory} that creates successor sets represented
     * as integer intervals, which is much more compact than {@code HashSet}s.
     *
     * @param graph the graph for which the returned {@code SuccessorSetFactory} will work.
     * If nodes are added or removed from the supplied graph, the behavior of the {@code SuccessorSetFactory}
     * becomes unspecified
     * @return a {@code SuccessorSetFactory} that creates successor sets represented
     * as integer intervals
     */
    public static SuccessorSetFactory intervalBased(InspectableGraph graph) {
        return new IntervalBasedSuccessorSetFactory(graph);
    }

    static class IntervalBasedSuccessorSetFactory extends SuccessorSetFactory {
        private final InspectableGraph graph;
        private final Node[] nodes;
        private final Object indexKey = new Object();

        IntervalBasedSuccessorSetFactory(InspectableGraph graph) {
            this.graph = graph;
            nodes = new Node[graph.nodeCount()];
            int pos = 0;
            for (Node n : graph.nodes()) {
                n.putWeakly(indexKey, pos);
                nodes[pos++] = n;
            }
        }
        
        @Override
        public MutableSuccessorSet create() {
            return new IntervalMutableSuccessorSet();
        }

        private class IntervalMutableSuccessorSet implements MutableSuccessorSet {
            private final IntervalSet intervalSet = new IntervalSet();

            public void add(Node node) {
                intervalSet.add(node.getInt(indexKey));
            }

            public void addAll(SuccessorSet successorSet) {
                intervalSet.add(((IntervalMutableSuccessorSet)successorSet).intervalSet);
            }

            public boolean contains(Node node) {
                return intervalSet.contains(node.getInt(indexKey));
            }

            public Set<Node> toSet() {
                Set<Node> nodes = new HashSet<Node>(intervalSet.size());
                for (Node n : this) {
                    nodes.add(n);
                }
                return nodes;
            }

            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    final Iterator<Integer> iterator = intervalSet.elements().iterator();

                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    public Node next() {
                        return nodes[iterator.next()];
                    }

                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            @Override
            public String toString() {
                return intervalSet.toString();
            }
        }
    }
}
