package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.util.Args;

/**
 *
 * @deprecated Use {@link Traverser}
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public abstract class Explorer {
    public abstract void explore(InspectableGraph g, Path currentPath, PathAccumulator accumulator);
    
    public static Explorer newDefaultExplorer() {
        return new DefaultExplorer(Direction.OUT);
    }
    
    public static Explorer newDefaultExplorer(Direction direction) {
        return new DefaultExplorer(direction);
    }
    
    public static Explorer newTruncatingExplorer() {
        return newTruncatingExplorer(newDefaultExplorer());
    }
    
    public static Explorer newTruncatingExplorer(Explorer explorer) {
        return new TruncatingExplorer(explorer);
    }
    
    private static final class DefaultExplorer extends Explorer {
        final Direction direction;
        
        DefaultExplorer(Direction direction) {
            Args.notNull(direction);
            this.direction = direction;
        }
        
        public void explore(InspectableGraph g, Path currentPath, PathAccumulator accumulator) {
            for (Edge e : g.edges(currentPath.tailNode(), direction)) {
                Path p = e.asPath();
                if (currentPath.tailNode() != e.n1()) {
                    p = p.reverse();
                }
                accumulator.addPath(currentPath.append(p));
            }
        }
    }
    
    private static class TruncatingExplorer extends Explorer {
        private final Explorer delegate;
        private final FilteringAccumulator acc = new FilteringAccumulator();
        
        TruncatingExplorer(Explorer delegate) {
            Args.notNull(delegate);
            this.delegate = delegate;
        }

        public void explore(InspectableGraph g, Path currentPath, PathAccumulator accumulator) {
            acc.delegateAccumulator = accumulator;
            delegate.explore(g, currentPath, acc);
        }
        
        private static class FilteringAccumulator implements PathAccumulator {
            PathAccumulator delegateAccumulator;

            public void addPath(Path path) {
                if (path.size() == 0) {
                    delegateAccumulator.addPath(path);
                } else {
                    delegateAccumulator.addPath(path.tailPath(1));
                }
            }
        }
    }
}
