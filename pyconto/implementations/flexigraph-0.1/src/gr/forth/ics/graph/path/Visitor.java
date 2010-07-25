package gr.forth.ics.graph.path;

import gr.forth.ics.graph.path.Path;

/**
 * A visitor that visits path explored in a graph traversal. Return
 * <ul>
 * <li><tt>Traversal.CONTINUE</tt>
 * (or null) to indicate that the exploration should proceed normally.
 * </li>
 * <li><tt>Traversal.STOP</tt> to indicate that the current path should be ignored,
 * and no exploration should commence from it. (But exploration can proceed from
 * other paths).
 * </li>
 * <li><tt>Traversal.EXIT</tt> to indicate that the traversal should exit abruptly,
 * without visiting any other path.
 * </li>
 * @deprecated Use {@link Traverser}
 */ 
public interface Visitor {
    Traversal visit(Path path); 
}
