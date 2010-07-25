package gr.forth.ics.util;

/**
 * An integer interval that is inclusive on both its ends. 
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Interval {
    /**
     * Returns the start of this interval. If {@code start() > end()}, then this interval is empty;
     *
     * @return the start of this interval
     */
    int start();

    /**
     * Returns the end of this interval. If {@code start() > end()}, then this interval is empty;
     *
     * @return the end of this interval
     */
    int end();

    /**
     * Returns whether this interval is empty. This is equivalent to {@code start() > end()}.
     *
     * @return whether this interval is empty
     */
    boolean isEmpty();

    /**
     * Returns whether the specified integer is included in this interval. This is defined as:
     * {@code start() <= integer && integer <= end() }.
     * 
     * @param integer an integer
     * @return whether the specified integer is included in this interval
     */
    boolean contains(int integer);
}
