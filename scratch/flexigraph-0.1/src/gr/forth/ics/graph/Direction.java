package gr.forth.ics.graph;

/**
 * The direction that an eligible {@link Edge} sbetween two {@link Node Nodes} should have. Given an
 * edge {@literal e} that belongs to a {@link Graph} {@literal g}, it is guaranteed that the edge
 * will be contained in {@code g.edges(e.n1(), Direction.OUT))}, and similarly, the edge will be
 * contained in {@code g.edges(e.n2(), Direction.IN)}. Direction.EITHER denotes the union of both
 * directions.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public enum Direction {
    /**
     * Outward direction from somewhere.
     */
    OUT(1),
    /**
     * Inward direction to somewhere.
     */
    IN(2),
    /**
     * Either direction.
     */
    EITHER(1 | 2);
    
    private final int direction;
    private static final int out = 1;
    private static final int in = 2;
    
    private Direction(int i) {
        direction = i;
    }
    
    /**
     * Returns true if this == Direction.OUT || this == Direction.EITHER.
     */
    public boolean isOut() {
        return (direction & out) != 0;
    }
    
    /**
     * Returns true if this == Direction.IN || this == Direction.EITHER.
     */
    public boolean isIn() {
        return (direction & in) != 0;
    }
    
    /**
     * Returns the inverse of this Direction. That is: <br>
     * <li>if this direction is OUT, IN is returned,
     * <li>if this direction is IN, OUT is returned,
     * <li>if this direction is EITHER, EITHER is returned.
     */
    public Direction flip() {
        switch (this) {
            case OUT:
                return IN;
            case IN:
                return OUT;
            default:
                return this;
        }
    }
}
