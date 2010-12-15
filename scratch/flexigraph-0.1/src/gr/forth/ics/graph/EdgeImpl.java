package gr.forth.ics.graph;

import gr.forth.ics.graph.path.AbstractPath;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.Accessor;
import gr.forth.ics.util.ExtendedIterable;
import java.util.Collections;
import static gr.forth.ics.graph.Edge.Orientation.*;

final class EdgeImpl extends TupleImpl implements Edge {
    final NodeImpl n1, n2;
    Accessor<EdgeImpl> outReference, inReference;
    
    EdgeImpl(NodeImpl n1, NodeImpl n2, Object value) {
        super(value);
        this.n1 = n1;
        this.n2 = n2;
    }
    
    public NodeImpl n1() {
        return n1;
    }
    
    public NodeImpl n2() {
        return n2;
    }
    
    public boolean isIncident(Node node) {
        return node == n1 || node == n2;
    }
    
    public NodeImpl opposite(Node node) {
        if (isIncident(node)) {
            if (node == n1) {
                return n2;
            } else {
                return n1;
            }
        } else {
            throw new RuntimeException("Edge: " + this + " does not contain node: " + node);
        }
    }
    
    public boolean isSelfLoop() {
        return n1 == n2;
    }
    
    public boolean isIncident(Edge other) {
        Args.notNull(other);
        return n1 == other.n1() || n1 == other.n2() || n2 == other.n1() || n2 == other.n2();
    }
    
    //returns first intersection with other edge found
    public NodeImpl getIntersection(Edge other) {
        Args.notNull(other);
        if (n1 == other.n1() || n1 == other.n2()) {
            return n1;
        }
        if (n2 == other.n1() || n2 == other.n2()) {
            return n2;
        }
        return null;
    }
    
    public NodeImpl getIntersection(boolean startFromN1, Edge other) {
        if (startFromN1) {
            return getIntersection(other);
        }
        Args.notNull(other);
        if (n2 == other.n1() || n2 == other.n2()) {
            return n2;
        }
        if (n1 == other.n1() || n1 == other.n2()) {
            return n1;
        }
        return null;
    }
    
    public Path asPath() {
        return asPath(n1());
    }
    
    public Path asPath(final Node head) {
        Args.isTrue("Node not contained in edge", isIncident(head));
        return new AbstractPath() {
            final Node tail = EdgeImpl.this.opposite(head);

            public Node headNode() {
                return head;
            }

            public Edge headEdge() {
                return EdgeImpl.this;
            }

            public int size() {
                return 1;
            }

            public Node tailNode() {
                return tail;
            }

            public Edge tailEdge() {
                return EdgeImpl.this;
            }

            public ExtendedIterable<Path> steps() {
                return ExtendedIterable.wrap(Collections.<Path>singleton(this));
            }

            public Node getNode(int index) {
                switch (index) {
                    case -2: return head;
                    case -1: return tail;
                    case 0: return head;
                    case 1: return tail;
                }
                throw new IllegalArgumentException("Illegal index specified: " + index);
            }

            public Edge getEdge(int index) {
                if (index != 0) {
                    throw new IllegalArgumentException("Illegal index specified" + index);
                }
                return EdgeImpl.this;
            }

            public Path slice(int start, int end) {
                switch (start) {
                    case 0:
                        if (end == 0) {
                            return head.asPath();
                        } else if (end == 1) {
                            return this;
                        }
                        break;

                    case 1:
                        if (end == 1) {
                            return tail.asPath();
                        }
                }
                throw new IllegalArgumentException("Illegal indexes specified");
            }

            public Path headPath(int steps) {
                if (steps == 1) {
                    return this;
                } else if (steps == 0) {
                    return head.asPath();
                } else {
                    throw new IllegalArgumentException("Illegal index specified");
                }
            }

            public Path tailPath(int steps) {
                if (steps == 1) {
                    return this;
                } else if (steps == 0) {
                    return tail.asPath();
                } else {
                    throw new IllegalArgumentException("Illegal index specified");
                }
            }

            public Path reverse() {
                return EdgeImpl.this.asPath(tail);
            }
        };
    }
    
    @Override
    public String toString() {
        return "{" + n1 + "->" + n2 + (getValue() == null ? "" : ", (" + getValue().toString() + ")") + "}";
    }
    
    public Edge.Orientation testOrientation(Edge other) {
        Args.notNull(other);
        Node n = getIntersection(other);
        if (n == null || isSelfLoop() || other.isSelfLoop()) {
            return UNDEFINED;
        }
        int x = 0;
        if (n == n2) {
            x++;
        }
        if (n == other.n2()) {
            x++;
        }
        switch (x % 2) {
            case 1: return SAME;
            default: return OPPOSITE;
        }
    }
    
    public boolean areParallel(Edge other) {
        Args.notNull(other);
        if (n1 == other.n1()) {
            return n2 == other.n2();
        } else if (n1 == other.n2()) {
            return n2 == other.n1();
        }
        return false;
    }
}
