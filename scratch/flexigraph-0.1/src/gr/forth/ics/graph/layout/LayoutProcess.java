package gr.forth.ics.graph.layout;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface LayoutProcess {
    /**
     * Returns true if this layout process has been completed.
     */
    boolean isDone();
    
    /**
     * Executes a single pass of this layout process.
     *
     * @throws IllegalStateException if {@link #isDone()} == true.
     * @throws NullPointerException if locator == null
     */
    void step(Locator locator);
    
    /**
     * Runs this layout process until completion.
     *
     * @throws IllegalStateException if {@link #isDone()} == true.
     * @throws NullPointerException if locator == null
     */
    void run(Locator locator);
}
