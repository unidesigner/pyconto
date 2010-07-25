package gr.forth.ics.graph;

import gr.forth.ics.graph.path.AbstractPath;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Accessor;
import gr.forth.ics.util.ExtendedIterable;
import gr.forth.ics.util.FastLinkedList;
import java.util.NoSuchElementException;

final class NodeImpl extends TupleImpl implements Node {
    FastLinkedList<EdgeImpl> outEdges = new FastLinkedList<EdgeImpl>();
    FastLinkedList<EdgeImpl> inEdges = new FastLinkedList<EdgeImpl>();
    Accessor<NodeImpl> reference;
    
    NodeImpl(Object value) {
        super(value);
    }
    
    public String toString() {
        return getValue() != null ? getValue().toString() : "null";
    }

    public Path asPath() {
        return new AbstractPath() {
            public Node headNode() {
                return NodeImpl.this;
            }

            public Edge headEdge() {
                throw new NoSuchElementException();
            }

            public int size() {
                return 0;
            }

            public Node tailNode() {
                return NodeImpl.this;
            }

            public Edge tailEdge() {
                throw new NoSuchElementException();
            }

            public ExtendedIterable<Path> steps() {
                return ExtendedIterable.<Path>empty();
            }

            public Node getNode(int index) {
                if (index == 0) {
                    return NodeImpl.this;
                }
                throw new IllegalArgumentException("Illegal index specified");
            }

            public Edge getEdge(int index) {
                throw new NoSuchElementException();
            }

            public Path slice(int start, int end) {
                if (start == 0 && end == 0) {
                    return this;
                }
                throw new IllegalArgumentException("Illegal indexes specified");
            }

            public Path headPath(int steps) {
                if (steps == 0) {
                    return this;
                }
                throw new IllegalArgumentException("Illegal index specified");
            }

            public Path tailPath(int steps) {
                if (steps == 0) {
                    return this;
                }
                throw new IllegalArgumentException("Illegal index specified");
            }

            public Path reverse() {
                return this;
            }
        };
    }
}
