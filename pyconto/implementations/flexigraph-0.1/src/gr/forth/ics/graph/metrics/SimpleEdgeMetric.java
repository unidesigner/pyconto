package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Edge;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
class SimpleEdgeMetric implements EdgeMetric {
    private final Object key;
    
    SimpleEdgeMetric(Object key) {
        this.key = key;
    }
    
    public double getValue(Edge element) {
        return element.getDouble(key);
    }
    
    void setValue(Edge element, double value) {
        element.putWeakly(key, value);
    }
    
    Object getKey() {
        return key;
    }
}
