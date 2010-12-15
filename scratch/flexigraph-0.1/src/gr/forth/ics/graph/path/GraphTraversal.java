package gr.forth.ics.graph.path;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.util.*;
import gr.forth.ics.util.Args;

/**
 *
 * @deprecated Use {@link Traverser}
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class GraphTraversal {
    private final InspectableGraph graph;
    private final ExpansionFront expansionFront;
    private final List<PathFilter> filters;
    private final PathAccumulator pathAccumulator;
    
    private Visitor visitor;
    
    private Explorer explorer;
    
    public GraphTraversal(InspectableGraph graph, ExpansionFront expansionFront) {
        this(graph, expansionFront, new PathFilter[0]);
    }
    
    public GraphTraversal(InspectableGraph graph, ExpansionFront expansionFront, PathFilter... filters) {
        this(graph, expansionFront, Arrays.asList(filters));
    }
    
    public GraphTraversal(InspectableGraph graph, ExpansionFront expansionFront, Collection<PathFilter> filters) {
        Args.notNull(graph, expansionFront);
        Args.doesNotContainNull(filters);
        this.graph = graph;
        this.expansionFront = expansionFront;
        this.filters = new ArrayList<PathFilter>(filters);
        this.pathAccumulator = new PathAccumulator() {
            public void addPath(Path path) {
                if (accepted(path)) {
                    GraphTraversal.this.expansionFront.addPath(path);
                }
            }
        };
        this.explorer = Explorer.newDefaultExplorer();
    }
    
    public void setExplorer(Explorer explorer) {
        Args.notNull(explorer);
        this.explorer = explorer;
    }
    
    public Explorer getExplorer() {
        return explorer;
    }
    
    private void initFilters(Path rootPath) {
        for (PathFilter f : filters) {
            f.init(rootPath);
        }
    }
    
    private void endFilters() {
        for (PathFilter f : filters) {
            f.end();
        }
    }
    
    private boolean accepted(Path path) {
        for (PathFilter f : filters) {
            if (!f.accept(path)) {
                return false;
            }
        }
        return true;
    }
    
    public void traverse(Node root, Visitor visitor) {
        Args.notNull("Null visitor", visitor);
        this.visitor = visitor;
        Args.isTrue("Starting node not contained in graph", graph.containsNode(root));
        Path rootPath = root.asPath();
        initFilters(rootPath);
        expansionFront.reset();
        
        expansionFront.addPath(rootPath);      
        expansionFront.nextRound();
        loop:
        while (expansionFront.hasNext()) {
            Path next = expansionFront.next();
            Traversal command = visitor.visit(next);
            if (command == null) command = Traversal.CONTINUE;
            switch (command) {
                case IGNORE:
                    expansionFront.nextRound();
                    continue;
                case EXIT:
                    break loop;
            }
            explorer.explore(graph, next, pathAccumulator);
            expansionFront.nextRound();
        }
        endFilters();
        this.visitor = null;
    }
}
