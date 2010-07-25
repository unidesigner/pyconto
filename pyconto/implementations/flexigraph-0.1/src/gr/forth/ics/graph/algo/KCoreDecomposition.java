package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.SecondaryGraph;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * K-core node partitioning of a graph. 
 * 
 * @see <a href="http://en.wikipedia.org/wiki/K-core">K-Core article on Wikipedia</a>
 * @see S. B. Seidman, 1983, Network structure and minimum degree, Social Networks 5:269-287
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class KCoreDecomposition {
    private final SortedMap<Integer, Set<Node>> cores;

    private KCoreDecomposition(SortedMap<Integer, Set<Node>> cores) {
        this.cores = cores;
    }

    /**
     * Returns all non-empty cores indexed by their "k" index.
     *
     * @return all non-empty cores indexed by their "k" index
     */
    public SortedMap<Integer, Set<Node>> getCores() {
        return cores;
    }

    /**
     * Returns the set of nodes of the specified k-core, or an empty set (but not null) if there
     * is no such core.
     *
     * @param kcore the core to return
     * @return the nodes of core specified, or an empty set if there is no such core
     * @throws IllegalArgumentException if {@code kcore < 1}
     */
    public Set<Node> getCore(int kcore) {
        if (kcore < 1) {
            throw new IllegalArgumentException("kcore cannot be < 1: " + kcore);
        }
        Set<Node> core = cores.get(kcore);
        if (core == null) {
            return Collections.emptySet();
        }
        return core;
    }

    /**
     * Creates a K-core decomposition for the specified graph. The graph remains unmodified.
     *
     * @param g the graph for which to create the K-core decomposition
     * @return the K-core decomposition
     */
    public static KCoreDecomposition execute(InspectableGraph g) {
        SecondaryGraph sg = new SecondaryGraph(g);
        DegreeSorter buckets = new DegreeSorter(sg);
        int core = 1;
        SortedMap<Integer, Set<Node>> cores = new TreeMap<Integer, Set<Node>>();
        while (!buckets.isEmpty()) {
            Set<Node> coreSet = new HashSet<Node>();
            while (!buckets.isEmpty() && buckets.getMinDegree() <= core) {
                Collection<Node> next = buckets.getNodes(buckets.getMinDegree());
                coreSet.addAll(next);
                sg.removeNodes(next);
            }
            if (!coreSet.isEmpty()) {
                cores.put(core, Collections.unmodifiableSet(coreSet));
            }
            core++;
        }
        return new KCoreDecomposition(Collections.unmodifiableSortedMap(cores));
    }
}
