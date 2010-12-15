package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.algo.Dfs;

/**
 * Utility that finds graph cycles.
 * 
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Cycles {
    private Cycles() { }

    /**
     * Finds and returns a cycle of the specified graph, or {@code null} if no cycle was found.
     *
     * <p>An easy way to find an <em>undirected</em> cycle using this method, is
     * to invoke: {@code Cycles.findCycle(Graphs.undirected(graph));}
     *
     * @param graph the graph of which to find a cycle
     * @return a {@code Path} representing a cycle (its first and last nodes are the same),
     * if found, otherwise {@code null}.
     * @see Graphs
     */
    public static Path findCycle(InspectableGraph graph) {
        CycleFinderDfs dfs = new CycleFinderDfs(graph);
        dfs.execute();
        return dfs.getCycle();
    }
    
    private static class CycleFinderDfs extends Dfs {
        private Path cycle;

        CycleFinderDfs(InspectableGraph g) {
            super(g, Direction.OUT);
        }

        Path getCycle() {
            return cycle;
        }

        boolean hasCycle() {
            return cycle != null;
        }

        @Override public boolean visitBackEdge(Path path) {
            cycle = path.tailPath(path.size() - path.find(path.tailNode().asPath()));
            return true;
        }
    }
}
