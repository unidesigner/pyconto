package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.event.EmptyGraphListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.event.WeakListener;
import gr.forth.ics.util.Accessor;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.DVMap;
import gr.forth.ics.util.Factory;
import gr.forth.ics.util.FastLinkedList;
import java.util.TreeMap;

/**
 * A dynamic node partition of a graph based on node degrees. A BucketSort is attached to
 * a particular graph and can be used to query nodes based on their degree. The type
 * of the degree taken into consideration is customizable via a specified {@link Direction},
 * i.e. {@code Direction.OUT} means to use the out-degree of the nodes, {@code Direction.IN}
 * the in-degree, and {@code Direction.EITHER} the total (in and out) degree
 * (see {@link #BucketSort(InspectableGraph, Direction)}.
 *
 * <p>The BucketSort automatically updates itself when the graph is modified, so it always
 * represents the most up-to-date degree information of the graph.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public final class DegreeSorter {
    private final MyListener listener;
    private final Object accessorKey = new Object();

    private final DVMap<Integer, FastLinkedList<Node>> nodes = new DVMap<Integer, FastLinkedList<Node>>(
                new TreeMap<Integer, FastLinkedList<Node>>(),
                new Factory<FastLinkedList<Node>>() {
            public FastLinkedList<Node> create(Object o) {
                return new FastLinkedList<Node>();
            }
        });

    private final Direction direction;

    /**
     * Creates a BucketSort attached on the specified graph, where the degree of each node
     * is taken via {@link InspectableGraph#degree(Node)}, {@literal i.e.} undirected.
     * 
     * <p>The BucketSort will update itself in response to graph modifications,
     * so it always represents the most up-to-date state of the graph.
     *
     * @param g the graph of which to create a node partition based on the node degrees
     */
    public DegreeSorter(InspectableGraph g) {
        this(g, Direction.EITHER);
    }
    
    /**
     * Creates a BucketSort attached on the specified graph, where the degree of each node
     * is taken via {@link InspectableGraph#degree(Node, Direction)}.
     *
     * <p>The BucketSort will update itself in response to graph modifications,
     * so it always represents the most up-to-date state of the graph.
     *
     * @param g the graph of which to create a node partition based on the node degrees
     * @param degreeDirection defines the direction on which "degree" is defined.
     * <ul>
     * <li>{@code Direction.OUT} means out-degree of nodes,
     * <li>{@code Direction.IN} means in-degree of nodes, and
     * <li>{@code Direction.EITHER} means undirected degree of nodes
     * </ul>
     */
    public DegreeSorter(InspectableGraph g, Direction degreeDirection) {
        Args.notNull(g, degreeDirection);
        this.direction = degreeDirection;
        for (Node n : g.nodes()) {
            ensureIsAdded(nodes.get(g.degree(n, degreeDirection)), n);
        }
        listener = new MyListener();
        WeakListener.createAndAttach(g, listener);
    }

    /**
     * Returns an unmodifiable collection containing all nodes of the graph with the specified degree.
     * This operation is executed in O(1) time.
     *
     * <p>The graph and the meaning of the degree is specified in BucketSort constructors.
     * 
     * @param degree the degree of the returned nodes
     * @return all nodes of the graph with degree equal to the specified one
     */
    public Collection<Node> getNodes(int degree) {
        Integer key = degree;
        if (nodes.containsKey(key)) {
            return Collections.unmodifiableCollection(nodes.get(key));
        }
        return Collections.<Node>emptySet();
    }

    /**
     * Returns whether there are any nodes in the graph with the specified degree. Equivalent to
     * {@code !getNodes(degree).isEmpty()}.
     *
     * @param degree the required degree of nodes to check the existence of
     * @return whether there are nodes in the graph with the specified degree
     */
    public boolean hasNodes(int degree) {
        return nodes.containsKey(degree);
    }

    /**
     * Returns an unmodifiable set (in ascending order) with all the degrees that
     * have at least one node.
     *
     * @return an unmodifiable sorted set (in ascending order) with all the degrees that
     * have at least one node
     */
    public Set<Integer> keySet() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    /**
     * Returns the min degree for which there is at least one node.
     *
     * @return the min degree for which there is at least one node
     * @throws NoSuchElementException if there isn't any node in the graph
     */
    public int getMinDegree() {
        if (nodes.isEmpty()) {
            throw new NoSuchElementException("Graph is empty");
        }
        return ((SortedMap<Integer, FastLinkedList<Node>>)nodes.getDelegate()).firstKey();
    }

    /**
     * Returns the max degree for which there is at least one node.
     *
     * @return the max degree for which there is at least one node
     * @throws NoSuchElementException if there isn't any node in the graph
     */
    public int getMaxDegree() {
        if (nodes.isEmpty()) {
            throw new NoSuchElementException();
        }
        return ((SortedMap<Integer, FastLinkedList<Node>>)nodes.getDelegate()).lastKey();
    }

    /**
     * Returns whether there are no entries in this BucketSort (which implies that the graph is
     * also empty, since it has no nodes).
     *
     * @return whether there are no entries in this BucketSort
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
    
    @SuppressWarnings("unchecked")
    private void ensureIsAdded(FastLinkedList<Node> container, Node node) {
        Accessor<Node> accessor = (Accessor<Node>)node.get(accessorKey);
        if (accessor == null) {
            accessor = container.addLast(node);
            node.putWeakly(accessorKey, accessor);
        }
    }

    private class MyListener extends EmptyGraphListener {
        GraphEvent pendingEdgeRemoval;

        @SuppressWarnings("unchecked")
        private void removeNode(Node n, int previousDegree) {
            Accessor<Node> accessor = (Accessor<Node>)n.get(accessorKey);
            if (accessor == null) {
                return;
            }
            FastLinkedList<Node> owner = accessor.owner();
            accessor.remove();
            n.remove(accessorKey);

            if (owner.isEmpty()) {
                nodes.remove(previousDegree);
            }
        }

        @Override public void nodeRemoved(GraphEvent e) {
            removeNode(e.getNode(), 0);
        }
        
        @Override public void nodeAdded(GraphEvent e) {
            ensureIsAdded(nodes.get(0), e.getNode());
        }
        
        @Override public void edgeRemoved(GraphEvent e) {
            reindex(e.getSource(), e.getEdge(), false);
        }
        
        @Override public void edgeAdded(GraphEvent e) {
            reindex(e.getSource(), e.getEdge(), true);
        }

        private void reindex(InspectableGraph g, Edge e, boolean added) {
            Node n1 = e.n1();
            Node n2 = e.n2();
            int d1 = direction.isOut() ? 1 : 0;
            int d2 = direction.isIn() ? 1 : 0;

            if (n1 == n2) {
                switch (direction) {
                    case EITHER:
                        d1 = d2 = 2;
                        break;
                    case OUT:
                        d2 = d1;
                        break;
                    case IN:
                        d1 = d2;
                        break;
                }
            }
            //removed
            if (added) {
                d1 = -d1;
                d2 = -d2;
            }

            int previousDegree1 = g.degree(n1, direction) + d1;
            int previousDegree2 = g.degree(n2, direction) + d2;

            removeNode(n1, previousDegree1);
            removeNode(n2, previousDegree2);

            ensureIsAdded(nodes.get(g.degree(n1, direction)), n1);
            ensureIsAdded(nodes.get(g.degree(n2, direction)), n2);
        }
    }

    public String toString() {
        return "[" + DegreeSorter.class.getName() + ": " + nodes + "]";
    }
}
