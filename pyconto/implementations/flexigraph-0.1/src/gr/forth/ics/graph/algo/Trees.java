package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.SecondaryGraph;
import gr.forth.ics.graph.Filters;
import gr.forth.ics.graph.GraphException;
import gr.forth.ics.graph.path.Path;
import java.util.Collection;
import java.util.HashSet;

/**
 * Algorithms for trees.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 * @author Vouzoukidou Nelly, email: vuzukid (at) csd.uoc.com
 */
public class Trees {
    private Trees() { }

    /**
     * Returns the center of the tree. The center of the tree is the node that has the greatest minimum distance
     * from each of the root and the leaves.
     *
     * <p> If the supplied graph is not a single tree, null may be returned.
     *
     * @param tree the graph that represents a tree
     * @return the center of the tree
     */
    public static Node findCenter(InspectableGraph tree) {
        if (tree.nodeCount() == 0) {
            return null;
        }
        Graph copy = new SecondaryGraph(tree);

        Iterable<Node> leaves = copy.nodes().filter(Filters.degreeEqual(tree, Direction.EITHER, 1));
        while (copy.nodeCount() > 2) {
            Collection<Node> leafParents = new HashSet<Node>();
            for (Node leaf : leaves) {
                Node parent = copy.aNode(leaf);
                copy.removeNode(leaf);
                if (copy.degree(parent) == 1) {
                    leafParents.add(parent);
                }
            }
            leaves = leafParents;
        }
        if (copy.nodeCount() == 0) {
            return null;
        }
        return copy.aNode();
    }

    /**
     * Executes the node-level finding algorithm on a tree. The tree is contained in the given
     * graph and its root is specified. Also, the direction of edges to be traversed from the root
     * to explore the whole tree is specified.
     *
     * @param graph the graph that contains the tree
     * @param root the root of the tree
     * @param directionToKids the direction of edges from the root (or any node) to its kids
     * @return a NodeLevelFinder instance corresponding to the analysis of the tree
     */
    public static NodeLevelFinder findNodeLevels(final InspectableGraph graph, final Node root, final Direction directionToKids) {
        final Object layersKey = new Object();

        Dfs dfs = new Dfs(graph, root, directionToKids) {

            @Override
            protected boolean visitPost(Path path) {
                Node node = path.tailNode();
                int above = path.size();

                int maxLayerBelowOfKid = -1;
                Node parent = directionToKids == Direction.EITHER && above > 0 ? path.getNode(path.nodeCount() - 2) : null;
                for (Node kid : graph.adjacentNodes(node, directionToKids)) {
                    if (kid == parent) continue; //Direction.EITHER may cause this to visit the parent instead of kid!
                    maxLayerBelowOfKid = Math.max(maxLayerBelowOfKid, ((Layers) kid.get(layersKey)).below);
                }
                int below = maxLayerBelowOfKid + 1;
                node.putWeakly(layersKey, new Layers(below, above));
                return false;
            }

            @Override
            protected boolean visitBackEdge(Path path) {
                int rootNodeIndex = path.find(path.tailNode().asPath());
                throw new GraphException("Graph is not a tree. Found cycle: " +
                        path.tailPath(path.edgeCount() - rootNodeIndex));
            }

            @Override
            protected boolean visitNewTree(Node node) {
                return node != root;
            }
        };
        dfs.execute();
        return new NodeLevelFinder(graph, layersKey);
    }

    private static class Layers {
        int below;
        int above;

        private Layers(int below, int above) {
            this.below = below;
            this.above = above;
        }
    }

    /**
     * Represents the number of layers that exist underneath and above each node of a tree.
     *
     * @see Trees#findNodeLevels(InspectableGraph, Node, Direction)
     */
    public static class NodeLevelFinder {
        private final InspectableGraph graph;
        private final Object layersKey;

        private NodeLevelFinder(InspectableGraph graph,
                Object layersKey) {
            this.graph = graph;
            this.layersKey = layersKey;
        }

        /**
         * Returns the graph that contained the tree that was inspected.
         *
         * @return the graph that contained the tree that was inspected
         */
        public InspectableGraph getGraph() {
            return graph;
        }

        /**
         * Returns the (maximum) number of layers below the given node (zero if the node is a leaf).
         *
         * @param node a node of the tree
         * @return the (maximum) number of layers below the given node (zero if the node is a leaf)
         * @throws IllegalArgumentException if the node was not contained in the tree
         */
        public int getLayersBelow(Node node) {
            checkKeyContainment(node);
            return ((Layers) node.get(layersKey)).below;
        }

        /**
         * Returns the number of layers above the given node (zero if the node is the root).
         *
         * @param node a node of the tree
         * @return the number of layers below above given node (zero if the node is a root)
         * @throws IllegalArgumentException if the node was not contained in the tree
         */
        public int getLayersAbove(Node node) {
            checkKeyContainment(node);
            return ((Layers) node.get(layersKey)).above;
        }

        private void checkKeyContainment(Node node) {
            if (!node.has(layersKey)) {
                throw new IllegalArgumentException("Node " + node + " was not in the tree");
            }
        }
    }

    /**
     * Finds the diameter (number of edges in the longest path) of a tree. The tree is contained in the given
     * graph. The diameter of the empty tree, or a tree with only a single node, is zero.
     *
     * @param graph the graph that contains the tree
     * @return the diameter of the tree
     */
    public static int findDiameter(InspectableGraph graph) {
        if (graph.nodeCount() == 0) {
            return 0;
        }
        Node root = graph.aNode();
        return findDiameter(graph, root, Direction.EITHER).diameterOf(root);
    }

    /**
     * Finds the diameter (number of edges in the longest path) of each subtree of a tree. The tree is contained in the given
     * graph and its root is specified.
     *
     * @param graph the graph that contains the tree
     * @param root the root of the tree
     * @param directionToKids the direction of edges from the root (or any node) to its kids
     * @return a DiameterFinder instance correspoding to the analysis of the tree
     */
    public static DiameterFinder findDiameter(final InspectableGraph graph,
            final Node root, final Direction directionToKids) {
        final Object diameterKey = new Object();
        final NodeLevelFinder levelFinder = findNodeLevels(graph, root, directionToKids);

        new Dfs(graph, root, directionToKids) {
            @Override
            protected boolean visitPost(Path path) {
                Node node = path.tailNode();
                int maxHeight1 = -1;
                int maxHeight2 = -1;
                int maxDiameter = 0;

                Node parent = directionToKids == Direction.EITHER && path.nodeCount() > 1 ? path.getNode(path.nodeCount() - 2) : null;
                for (Node child : graph.adjacentNodes(node, directionToKids)) {
                    if (child == parent) continue; //Direction.EITHER may cause this to visit the parent instead of kid!
                    int childHeight = levelFinder.getLayersBelow(child);
                    maxDiameter = Math.max(maxDiameter, child.getInt(diameterKey));

                    if (childHeight > maxHeight1) {
                        maxHeight2 = maxHeight1;
                        maxHeight1 = childHeight;
                    } else if (childHeight > maxHeight2) {
                        maxHeight2 = childHeight;
                    }
                }
                int diameter = Math.max(maxDiameter, maxHeight1 + maxHeight2 + 3);
                node.putWeakly(diameterKey, diameter);

                return false;
            }
        }.execute();

        return new DiameterFinder(graph, levelFinder, diameterKey);
    }

    /**
     * Represents the diameter of each subtreee of the tree.
     *
     * @see Trees#findNodeLevels(InspectableGraph, Node, Direction)
     */
    public static class DiameterFinder {
        private final InspectableGraph graph;
        private final NodeLevelFinder nodeLevelFinder;
        private final Object diameterKey;

        private DiameterFinder(InspectableGraph graph,
                NodeLevelFinder nodeLevels, Object diameterKey) {
            this.graph = graph;
            this.nodeLevelFinder = nodeLevels;
            this.diameterKey = diameterKey;
        }

        public int diameterOf(Node subtree) {
            try {
                return subtree.getInt(diameterKey);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("This node: " + subtree + " was not part of the graph " +
                        "when this " + DiameterFinder.class + " was created");
            }

        }

        public NodeLevelFinder getNodeLevelFinder() {
            return nodeLevelFinder;
        }

        /**
         * Returns the graph that contained the tree that was inspected.
         *
         * @return the graph that contained the tree that was inspected
         */
        public InspectableGraph getGraph() {
            return graph;
        }
    }

    private static class SubtreesCounts {
        int nodeCount;
        int leafCount;

        private SubtreesCounts(int nodeCountKey, int leafCountKey) {
            this.nodeCount = nodeCountKey;
            this.leafCount = leafCountKey;
        }
    }

    /**
     * Finds the number of nodes and leaves for every subtree of the specified tree. A subtree is defined
     * by a node of the tree, and everything below it (itself included) is in the subtree, while
     * its parent and everything else connected to it is not in the subtree (i.e. when a subtree
     * is defined by any other node than the original root, its node count is strictly less than
     * the node count of the complete tree).
     *
     * @param graph the graph that contains the tree
     * @param root the root of the tree
     * @param directionToKids the direction of edges from the root (or any node) to its kids
     * @return a SubtreeNodeCounter instance corresponding to the analysis of the tree
     */
    public static SubtreeAnalyzer analyzeSubtrees(InspectableGraph graph,
            Node root, Direction directionFromRootToKids) {
        final Object subtreesKey = new Object();

        SubtreesCounts counts = new SubtreesCounts(0, 0);
        root.putWeakly(subtreesKey, counts);
        new Dfs(graph, root, directionFromRootToKids) {
            @Override
            protected boolean visitPre(Path path) {
                path.tailNode().putWeakly(subtreesKey, new SubtreesCounts(1, 0));
                return false;
            }

            @Override
            protected boolean visitPost(Path path) {
                Node current = path.tailNode();
                SubtreesCounts currentCounts = (SubtreesCounts) current.get(subtreesKey);

                currentCounts.leafCount = Math.max(1, currentCounts.leafCount);
                if (path.size() > 0) {
                    Node parent = path.getNode(-2);
                    SubtreesCounts parentCounts = (SubtreesCounts) parent.get(subtreesKey);
                    parentCounts.nodeCount += currentCounts.nodeCount;
                    parentCounts.leafCount += currentCounts.leafCount;
                    parent.put(subtreesKey, parentCounts);
                }
                if (currentCounts.leafCount == 0) {
                    currentCounts.leafCount = 1;
                }
                current.put(subtreesKey, currentCounts);
                return false;
            }
        }.execute();
        return new SubtreeAnalyzer(graph, subtreesKey);
    }

    /**
     * Represents the number of nodes that exist in every subtree of a tree.
     *
     * @see Trees#findNodesPerSubtree(InspectableGraph, Node, Direction)
     */
    public static class SubtreeAnalyzer {
        private final InspectableGraph graph;
        private final Object subtreesKey;

        private SubtreeAnalyzer(InspectableGraph graph, Object subtreesKey) {
            this.graph = graph;
            this.subtreesKey = subtreesKey;
        }

        /**
         * Returns the number of nodes in the subtree defined by the provided node (including
         * the root of the subtree).
         *
         * @param subtree the root of the subtree of which to return the node count
         * @return the node count of the subtree rooted in the specified node
         * @throws IllegalArgumentException if {@code getGraph().contains(subtree) == false}
         */
        public int nodeCountOf(Node subtree) {
            try {
                return ((SubtreesCounts) subtree.get(subtreesKey)).nodeCount;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("This node: " + subtree + " was not part of the graph " +
                        "when this " + SubtreeAnalyzer.class + " was created");
            }
        }

        /**
         * Returns the number of nodes in the subtree defined by the provided node (including
         * the root of the subtree).
         *
         * @param subtree the root of the subtree of which to return the node count
         * @return the node count of the subtree rooted in the specified node
         * @throws IllegalArgumentException if {@code getGraph().contains(subtree) == false}
         */
        public int leafCountOf(Node subtree) {
            try {
                return ((SubtreesCounts) subtree.get(subtreesKey)).leafCount;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("This node: " + subtree + " was not part of the graph " +
                        "when this " + SubtreeAnalyzer.class + " was created");
            }
        }

        /**
         * Returns the graph that contained the tree that was inspected.
         *
         * @return the graph that contained the tree that was inspected
         */
        public InspectableGraph getGraph() {
            return graph;
        }
    }
}
