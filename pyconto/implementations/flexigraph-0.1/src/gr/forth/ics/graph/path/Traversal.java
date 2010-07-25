package gr.forth.ics.graph.path;

/**
 *
 * @deprecated Use {@link Traverser}
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public enum Traversal {
    CONTINUE(0), IGNORE(1), EXIT(2);
    
    private final int strictness;
    
    private Traversal(int strictness) {
        this.strictness = strictness;
    }
    
    public static Traversal keepStricter(Traversal t1, Traversal t2) {
        t1 = maskNull(t1);
        t2 = maskNull(t2);
        return t1.strictness > t2.strictness ? t1 : t2;
    }
    
    public static Traversal maskNull(Traversal t) {
        if (t == null) {
            return CONTINUE;
        }
        return t;
    }
}
