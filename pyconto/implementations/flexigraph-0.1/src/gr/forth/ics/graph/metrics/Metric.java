package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Tuple;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Metric<E extends Tuple> {
    double getValue(E element);
}
