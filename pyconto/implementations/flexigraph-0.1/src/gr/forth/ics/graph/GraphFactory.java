package gr.forth.ics.graph;

@Deprecated
public class GraphFactory {
    /**
     * Use {@link PrimaryGraph#PrimaryGraph()} instead.
     */
    public static Graph newGraph() {
        return new PrimaryGraph();
    }

    /**
     * Use {@link PrimaryGraph#PrimaryGraph()} and {@link Graph#hint(Hint)} instead.
     */
    public static Graph newGraph(Hint... hints) {
        Graph g = new PrimaryGraph();
        for (Hint hint : hints) {
            g.hint(hint);
        }
        return g;
    }
    
    /**
     * Use {@link SecondaryGraph#SecondaryGraph()} instead.
     */
    public static SecondaryGraph newSecondary() {
        return new SecondaryGraph();
    }
    
    /**
     * Use {@link SecondaryGraph#SecondaryGraph()} and {@link Graph#hint(Hint)} instead.
     */
    public static SecondaryGraph newSecondary(Hint... hints) {
        SecondaryGraph g = new SecondaryGraph();
        for (Hint hint : hints) {
            g.hint(hint);
        }
        return g;
    }
}
