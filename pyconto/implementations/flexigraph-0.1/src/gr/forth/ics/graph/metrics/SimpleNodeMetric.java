package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Node;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
class SimpleNodeMetric implements NodeMetric {
    private final Object key;
    
    SimpleNodeMetric(Object key) {
        this.key = key;
    }
    
    public double getValue(Node element) {
        return element.getDouble(key);
    }
    
    void setValue(Node element, double value) {
        element.putWeakly(key, value);
    }
    
    Object getKey() {
        return key;
    }
}
