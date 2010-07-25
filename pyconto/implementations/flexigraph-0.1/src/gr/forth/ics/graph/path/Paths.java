package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Utilities for paths.
 */
public class Paths {
    private  Paths() { }

    /**
     * Returns a path that represents the concatenation of the specified paths.
     *
     * @throws IllegalArgumentException if any two subsequent paths are not consequtive (i.e. the tail
     * of the first does not match the head of the second), or if no path at all is given
     * @param paths the paths to concatenate into a single path
     * @return a path that represents the concatenation of the specified paths
     */
    public static Path newPath(Iterable<Path> paths) {
        LinkedList<Path> list = new LinkedList<Path>();
        for (Path p : paths) {
            list.add(p);
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Empty paths given");
        }
        while (list.size() > 1) {
            for (ListIterator<Path> i = list.listIterator(); i.hasNext(); ) {
                Path p1 = i.next();
                if (!i.hasNext()) {
                    break;
                }
                i.remove();
                Path p2 = i.next();
                i.set(p1.append(p2));
            }
        }
        return list.get(0);
    }


    /**
     * Finds a directed path from a node to another, in a given graph.
     * 
     * @param graph the graph into which the path is to be searched
     * @param start the node to start from
     * @param target the node to find
     * @param the allowable direction of edges to follow while searching for a path
     * @return a path from {@literal start} node to {@literal target} node, or {@literal null}
     *              if no such path exists. If multiple paths exist, an arbitrary path of them is returned
     */
    public static Path findPath(InspectableGraph graph, Node start, Node target, Direction direction) {
        for (Path path : Traverser.newDfs().notRepeatingEdges().build().traverse(graph, start, direction)) {
            if (path.tailNode() == target) {
                return path;
            }
        }
        return null;
    }
}
