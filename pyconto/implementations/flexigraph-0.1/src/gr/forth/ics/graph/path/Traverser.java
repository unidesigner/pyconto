package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.Tuple;
import gr.forth.ics.graph.Filters;
import gr.forth.ics.util.Factory;
import gr.forth.ics.util.Filter;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A utility that simplifies executing graph traversals. A traversal may be executed in a depth-first
 * or breadth-first fashion, while it can also be configured <em>not</em> to visit twice the same
 * node or the same edge. A traversal is modelled as an iterator of {@link Path} instances, representing
 * the various discovered paths of the graph. The direction of the traversal is also configured
 * (see {{@link #traverse(InspectableGraph, Node, Direction)}), so the traversal may flow
 * towards the direction of the discovered edges ({@code Direction.OUT}), oppositely
 * ({@code Direction.IN}) or either way ({@code Direction.EITHER}).
 *
 * <p>This example prints all paths that discover a new node in a graph:
 *{@code
 *InspectableGraph graph = ...;
 *Traverser traverser = Traverser.newDfs().withoutRepeatingNodes().build();
 *for (Path path : traverser.traverse(graph, graph.aNode(), Direction.OUT)) {
 *    System.out.println(path);
 *}
 *}
 *
 * <p>This (slow by nature) example collects <em>all</em> directed paths of a graph:
 *{@code
 *InspectableGraph graph = ...; //should be a dag, or else the paths are infinite
 *Traverser traverser = Traverser.newDfs().build();
 *Set<Path> paths = new HashSet<Path>();
 *for (Node n : graph.nodes()) {
 *    PathIterator iterator = traverser.traverse(graph, n, Direction.OUT).iterator();
 *    while (iterator.hasNext()) {
 *        Path path = iterator.next();
 *        if (paths.contains(path)) {
 *            iterator.skipExplorationOfLastPath(); //discontinue the exploration of already visited path
 *        } else {
 *            paths.add(path);
 *        }
 *    }
 *}
 *}
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class Traverser {
    private final Factory<PathQueue> pathQueueFactory;
    private final Filter<Path> filter;
    
    private Traverser(Factory<PathQueue> pathQueueFactory, Filter<Path> filter) {
        this.pathQueueFactory = pathQueueFactory;
        this.filter = filter;
    }

    /**
     * Initiates a traversal of the specified graph, starting with the given node (which must be contained
     * in the graph) and returns a {@link PathIterable} that returns the explored paths in a step-by-step fashion.
     * A traversal starts at a node and tries to expand, via incident edges, as much as possible. The traversal
     * consider only edges that are incident to already discovered nodes, and have compatible direction
     * to the {@code direction} parameter given. For example, if node {@code A} has been discovered
     * via some path, it may lead to the discovery of node {@code B} if
     *
     * <ul>
     * <li>there is an edge {@code {A-->B}} and direction was set to {@code Direction.OUT} or {@code Direction.EITHER}, or
     * <li>there is an edge {@code {B-->A}} and direction was set to {@code Direction.IN} or {@code Direction.EITHER}
     * </ul>
     *
     * <p>The return iterators do not support {@link Iterator#remove()} method.
     *
     * @param graph the graph on which to perform the traversal
     * @param startNode the node to start the traversal
     * @param direction defines the direction that edges must have in order
     * to be considered when expanding already explored paths
     * @return a PathIterable that gives access to the paths discovered by the traversal
     */
    public PathIterable traverse(
            final InspectableGraph graph,
            final Node startNode,
            final Direction direction) {
        return new PathIterable() {
            public PathIterator iterator() {
                return new PathIterator() {
                    final PathQueue queue = pathQueueFactory.create(null); {
                        addPathIfValid(startNode.asPath());
                    }

                    Path toExpand = null;

                    private void expand() {
                        if (toExpand != null) {
                            for (Edge e : graph.edges(toExpand.tailNode(), direction)) {
                                addPathIfValid(toExpand.append(e.asPath(toExpand.tailNode())));
                            }
                        }
                        toExpand = null;
                    }

                    public void skipExplorationOfLastPath() {
                        if (toExpand == null) {
                            throw new IllegalStateException("No path was returned, or this method has been called " +
                                    "twice before accessing another path via next()");
                        }
                        toExpand = null;
                    }

                    public boolean hasNext() {
                        expand();
                        return queue.hasNext();
                    }

                    public Path next() {
                        expand();
                        if (!queue.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return toExpand = queue.poll();
                    }

                    private void addPathIfValid(Path path) {
                        if (filter.accept(path)) {
                            queue.addPath(path);
                        }
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Specifies that depth-first-search will be used for the traversal of a graph, and returns a builder to
     * further configure that traversal.
     *
     * @return a TraverserBuilder, to further configure the traversal
     */
    public static TraverserBuilder newDfs() {
        return new TraverserBuilder(new Factory<PathQueue>() {
            public PathQueue create(Object o) {
                 return new LIFO();
            }
        });
    }

    /**
     * Specifies that breadth-first-search will be used for the traversal of a graph, and returns a builder to
     * further configure that traversal.
     *
     * @return a TraverserBuilder, to further configure the traversal
     */
    public static TraverserBuilder newBfs() {
        return new TraverserBuilder(new Factory<PathQueue>() {
            public PathQueue create(Object o) {
                 return new FIFO();
            }
        });
    }

    /**
     * Specifies that the traversal will visit the discovered paths using a priority queue with the given
     * path comparator, and returns a builder to further configure that traversal. At every step,
     * when the traversal has many unexplored paths from which to choose the next one to explore,
     * the minimum (as defined by the comparator) will be used.
     *
     * @return a TraverserBuilder, to further configure the traversal
     */
    public static TraverserBuilder newCustom(final Comparator<? super Path> pathComparator) {
        if (pathComparator == null) {
            throw new NullPointerException("pathComparator");
        }
        return new TraverserBuilder(new Factory<PathQueue>() {
            public PathQueue create(Object o) {
                 return new PriorityQueue(pathComparator);
            }
        });
    }

    /**
     * A builder for a {@link Traverser}.
     */
    public static class TraverserBuilder {
        private final Factory<PathQueue> pathQueueFactory;
        private boolean uniqueNodes;
        private boolean excludingStart;
        
        private boolean uniqueEdges;

        private TraverserBuilder(Factory<PathQueue> pathQueueFactory) {
            this.pathQueueFactory = pathQueueFactory;
        }

        /**
         * Specifies that during all traversals performed by the {@link Traverser}, no path that
         * leads to an already-visited node will be reported. This is similar to performing a Hamilton tour.
         *
         * @return this
         */
        public TraverserBuilder notRepeatingNodes() {
            if (uniqueNodes) {
                throw new IllegalStateException("Cannot set withoutRepeatingNodes twice");
            }
            uniqueNodes = true;
            return this;
        }

        /**
         * Specifies that during all traversals performed by the {@link Traverser}, no path that
         * leads to an already-visited node will be reported, but the node from which the traversal
         * starts is not considered to be already "visited" due to that fact; it will only be regarded
         * as visited if there is a cycle (perhaps of length 1) that leads back to it
         *
         * @return this
         */
        public TraverserBuilder notRepeatingNodesExcludingStart() {
            notRepeatingNodes();
            excludingStart = true;
            return this;
        }

        /**
         * Specifies that during all traversals performed by the {@link Traverser}, no path that
         * leads to an already-visited edge will be reported. This is similar to performing an Euler tour.
         *
         * @return this
         */
        public TraverserBuilder notRepeatingEdges() {
            if (uniqueEdges) {
                throw new IllegalStateException("Cannot set withoutRepeatingEdges twice");
            }
            uniqueEdges = true;
            return this;
        }

        /**
         * Builds a Traverser as specified by this builder.
         *
         * @return a new Traverser
         */
        public Traverser build() {
            Filter<Path> filter = null;
            if (uniqueNodes) {
                if (excludingStart) {
                    filter = new NoDuplicateNodeExcludingStartFilter();
                } else {
                    filter = new NoDuplicateNodeFilter();
                }
            }
            if (uniqueEdges) {
                Filter<Path> edgeFilter = new NoDuplicateEdgeFilter();
                if (filter == null) {
                    filter = edgeFilter;
                } else {
                    filter = Filters.and(filter, edgeFilter);
                }
            }
            if (filter == null) {
                filter = Filters.alwaysTrue();
            }
            return new Traverser(pathQueueFactory, filter);
        }
    }

    private interface PathQueue {
        void addPath(Path path);
        boolean hasNext();
        Path poll();
    }

    private static abstract class AbstractQueue implements PathQueue {
        protected final Deque<Path> deque = new ArrayDeque<Path>();

        public Path poll() {
            return deque.pollFirst();
        }

        public boolean hasNext() {
            return !deque.isEmpty();
        }
    }

    private static class FIFO extends AbstractQueue {
        public void addPath(Path path) {
            deque.addLast(path);
        }
    }

    private static class LIFO extends AbstractQueue {
        public void addPath(Path path) {
            deque.addFirst(path);
        }
    }

    private static class PriorityQueue implements PathQueue {
        private final Comparator<? super Path> comparator;
        private final java.util.PriorityQueue<Path> queue;

        PriorityQueue(Comparator<? super Path> comparator) {
            this.comparator = comparator;
            this.queue = new java.util.PriorityQueue<Path>(11, comparator);
        }

        public void addPath(Path path) {
            queue.add(path);
        }

        public Path poll() {
            return queue.poll();
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }
    }

    private static abstract class NoDuplicateTupleFilter implements Filter<Path> {
        private final Object marked = new Object();

        protected boolean acceptAndMark(Tuple tuple) {
            if (tuple.has(marked)) {
                return false;
            }
            tuple.putWeakly(marked, null);
            return true;
        }
    }

    private static class NoDuplicateNodeFilter extends NoDuplicateTupleFilter {
        public boolean accept(Path path) {
            return acceptAndMark(path.tailNode());
        }
    }

    private static class NoDuplicateNodeExcludingStartFilter extends NoDuplicateNodeFilter {
        public boolean accept(Path path) {
            if (path.size() == 0) {
                return true;
            }
            return super.accept(path);
        }
    }

    private static class NoDuplicateEdgeFilter extends NoDuplicateTupleFilter {
        public boolean accept(Path path) {
            if (path.edgeCount() == 0) {
                return true;
            }
            return acceptAndMark(path.tailEdge());
        }
    }

    /**
     * An iterable of {@code Path}s, which return iterators of type {@link PathIterator}.
     */
    public interface PathIterable extends Iterable<Path> {
        PathIterator iterator();
    }

    /**
     * A iterator of {@code Path}s, used by {@link Traverser}. Apart from simply iterating over paths, this iterator
     * supports <em>skipping</em> the exploration of the current path, in a traversal. To understand this,
     * note that the {@code Traverser} offers access to a discovered {@code Path} by returning it by
     * this iterator. If the application does not call {@link #skipExplorationOfLastPath()}, then that path
     * will also be used to further discover other paths (by appending some elligible incident edge). But if
     * the application code that reads the path verifies that it is not interesting in exploring that further,
     * it should call {@code skipExplorationOfLastPath()} so that path will not be considered when
     * the traverser will try to explore and discover new paths.
     */
    public interface PathIterator extends Iterator<Path> {

        /**
         * Signals that the path that was just returned via a call to {@link #next()} must not
         * be considered when the {@link Traverser} will attempt to further explore and discover new paths.
         * So, when application code reads a {@link Path} object returned via {@code #next()} and verifies
         * that the path is uninteresting and/or its further exploration cannot lead to discovering other
         * interesting paths, the application should call this method to stop that path's further exploration.
         *
         * <p>This method call is allowable after invoking {@link #next()} but before subsequent invocations
         * of {@link #hasNext()} (since this operation may affect the result of {@code hasNext()}).
         */
        void skipExplorationOfLastPath();
    }
}
//