package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Node;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
class ClosureImpl implements Closure {
    private final Object successorSetKey = new Object();

    ClosureImpl() { }

    void setSuccessors(Node node, SuccessorSet successorSet) {
        node.putWeakly(successorSetKey, successorSet);
    }

    void removeSuccessors(Node node) {
        node.remove(successorSetKey);
    }

    public SuccessorSet successorsOf(Node node) {
        SuccessorSet set = (SuccessorSet)node.get(successorSetKey);
        if (set == null) {
            return EMPTY_SET;
        }
        return set;
    }

    public boolean pathExists(Node n1, Node n2) {
        return successorsOf(n1).contains(n2);
    }

    private static final SuccessorSet EMPTY_SET = new SuccessorSet() {
        public boolean contains(Node node) {
            return false;
        }

        public Set<Node> toSet() {
            return Collections.<Node>emptySet();
        }

        public Iterator<Node> iterator() {
            return toSet().iterator();
        }
    };
}
