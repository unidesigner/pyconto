package gr.forth.ics.graph;

import gr.forth.ics.util.Args;
import gr.forth.ics.util.Filter;

public class Filters {
    private Filters() { }
    
    public static Filter<Node> degreeEqual(final InspectableGraph graph, final Direction direction, final int degree) {
        Args.notNull(graph, direction);
        return new Filter<Node>() {
            public boolean accept(Node n) {
                return graph.degree(n, direction) == degree;
            }
        };
    }
    
    public static Filter<Node> inDegreeEqual(InspectableGraph graph, int degree) {
        return degreeEqual(graph, Direction.IN, degree);
    }
    
    public static Filter<Node> outDegreeEqual(InspectableGraph graph, int degree) {
        return degreeEqual(graph, Direction.OUT, degree);
    }
    
    public static Filter<Node> degreeEqual(InspectableGraph graph, int degree) {
        return degreeEqual(graph, Direction.EITHER, degree);
    }
    
    public static Filter<Node> degreeAtLeast(final InspectableGraph graph, final Direction direction, final int degree) {
        Args.notNull(graph, direction);
        return new Filter<Node>() {
            public boolean accept(Node n) {
                return graph.degree(n, direction) >= degree;
            }
        };
    }
    
    public static Filter<Node> inDegreeAtLeast(InspectableGraph graph, int degree) {
        return degreeAtLeast(graph, Direction.IN, degree);
    }
    
    public static Filter<Node> outDegreeAtLeast(InspectableGraph graph, int degree) {
        return degreeAtLeast(graph, Direction.OUT, degree);
    }
    
    public static Filter<Node> degreeAtLeast(InspectableGraph graph, int degree) {
        return degreeAtLeast(graph, Direction.EITHER, degree);
    }
    
    public static Filter<Node> degreeAtMost(final InspectableGraph graph, final Direction direction, final int degree) {
        Args.notNull(graph, direction);
        return new Filter<Node>() {
            public boolean accept(Node n) {
                return graph.degree(n, direction) <= degree;
            }
        };
    }
    
    public static Filter<Node> inDegreeAtMost(InspectableGraph graph, int degree) {
        return degreeAtMost(graph, Direction.IN, degree);
    }
    
    public static Filter<Node> outDegreeAtMost(InspectableGraph graph, int degree) {
        return degreeAtMost(graph, Direction.OUT, degree);
    }
    
    public static Filter<Node> degreeAtMost(InspectableGraph graph, int degree) {
        return degreeAtMost(graph, Direction.EITHER, degree);
    }
    
    public static <T> Filter<T> not(final Filter<? super T> f) {
        Args.notNull(f);
        return new Filter<T>() {
            public boolean accept(T element) {
                return !f.accept(element);
            }
        };
    }
    
    public static <T> Filter<T> or(final Filter<? super T> f1, final Filter<? super T> f2) {
        Args.notNull(f1, f2);
        return new Filter<T>() {
            public boolean accept(T element) {
                return f1.accept(element) || f2.accept(element);
            }
        };
    }
    
    public static <T> Filter<T> xor(final Filter<? super T> f1, final Filter<? super T> f2) {
        Args.notNull(f1, f2);
        return new Filter<T>() {
            public boolean accept(T element) {
                return f1.accept(element) ^ f2.accept(element);
            }
        };
    }
    
    public static <T> Filter<T> and(final Filter<? super T> f1, final Filter<? super T> f2) {
        Args.notNull(f1, f2);
        return new Filter<T>() {
            public boolean accept(T element) {
                return f1.accept(element) && f2.accept(element);
            }
        };
    }
    
    public static <T extends Tuple> Filter<T> equalProperty(final Object key, final Object value) {
        return new Filter<T>() {
            public boolean accept(T element) {
                if (value == null) {
                    return element.has(key) && element.get(key) == null;
                } else {
                    return value.equals(element.get(key));
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> Filter<T> alwaysTrue() {
        return (Filter<T>)alwaysTrue;
    }

    @SuppressWarnings("unchecked")
    public static <T> Filter<T> alwaysFalse() {
        return (Filter<T>)alwaysFalse;
    }

    private static final Filter<Object> alwaysTrue = new Filter<Object>() {
        public boolean accept(Object o) {
            return true;
        }
    };

    private static final Filter<Object> alwaysFalse = new Filter<Object>() {
        public boolean accept(Object o) {
            return false;
        }
    };
}
