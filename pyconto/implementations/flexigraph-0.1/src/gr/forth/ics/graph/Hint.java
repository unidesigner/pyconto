package gr.forth.ics.graph;

/**
 * Runtime performance hints that can be used by graph implementations.
 * 
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public enum Hint {
    /**
     * Hint that fast node iteration is desired.
     */
    FAST_NODE_ITERATION,
    
    /**
     * Hint that fast edge iteration is desired.
     */
    FAST_EDGE_ITERATION
}
