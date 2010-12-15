package gr.forth.ics.graph;

import java.util.Set;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.SerializableObject;

abstract class AbstractTuple implements Tuple {
    private static final Object PARENT = new SerializableObject();
    
    private Object value;
    
    public AbstractTuple() { }
    
    public AbstractTuple(Object value) {
        setValue(value);
    }
    
    public Object setValue(Object value) {
        Object old = this.value;
        this.value = value;
        return old;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void copyInto(Tuple copy) {
        Args.notNull(copy);
        copy.setValue(getValue());
        for (Object key : keySet()) {
            Object value = get(key);
            copy.put(key, value);
        }
    }
    
    public boolean equalValues(Tuple that) {
        if (that == null) {
            return false;
        }
        Object thisValue = getValue();
        Object thatValue = that.getValue();
        if ((thisValue == null && thatValue != null) || (thisValue != null && !thisValue.equals(thatValue))) {
            return false;
        }
        Set<Object> thisKeys = keySet();
        Set<Object> thatKeys = that.keySet();
        if (thisKeys.size() != thatKeys.size()) {
            return false;
        }
        
        for (Object key : thisKeys) {
            Object value = get(key);
            Object otherValue = that.get(key);
            if (value == null && otherValue != null) {
                return false;
            }
            if (value != null && !value.equals(otherValue)) {
                return false;
            }
        }
        thatKeys.removeAll(thisKeys);
        for (Object key : thatKeys) {
            Object value = get(key);
            Object otherValue = that.get(key);
            if (value == null && otherValue != null) {
                return false;
            }
            if (!value.equals(otherValue)) {
                return false;
            }
        }
        return true;
    }
    
    public Node getNode(Object key) {
        return (Node)get(key);
    }
    
    public Edge getEdge(Object key) {
        return (Edge)get(key);
    }
    
    public Boolean getBoolean(Object key) {
        return (Boolean)get(key);
    }
    
    public int getInt(Object key) {
        Number number = ((Number)get(key));
        return number.intValue();
    }
    
    public short getShort(Object key) {
        Number number = ((Number)get(key));
        return number.shortValue();
    }
    
    
    public long getLong(Object key) {
        Number number = ((Number)get(key));
        return number.longValue();
    }
    
    public double getDouble(Object key) {
        Number number = ((Number)get(key));
        return number.doubleValue();
    }
    
    public float getFloat(Object key) {
        Number number = ((Number)get(key));
        return number.floatValue();
    }
    
    public String getString(Object key) {
        return (String)get(key);
    }
    
    public Character getChar(Object key) {
        return ((Character)get(key)).charValue();
    }
    
    public InspectableGraph getInspectableGraph(Object key) {
        return (InspectableGraph)get(key);
    }
    
    public Graph getGraph(Object key) {
        return (Graph)get(key);
    }
    
    public Number getNumber(Object key) {
        return (Number)get(key);
    }
    
    public Tuple getTuple(Object key) {
        return (Tuple)getLocally(key);
    }
    
    //search only locally - do NOT search within parent from this method (will cause infinite loop)
    abstract Object getLocally(Object key);
    abstract boolean hasLocally(Object key);
    
    public final Object get(Object key) {
        Object value = getLocally(key);
        if (value == null && !hasLocally(key)) {
            Tuple parent = getParentTuple();
            if (parent == null) {
                return null;
            }
            return parent.get(key);
        }
        return value;
    }
    
    public final boolean has(Object key) {
        boolean exists = hasLocally(key);
        if (!exists) {
            Tuple parent = getParentTuple();
            if (parent == null) {
                return false;
            }
            return parent.has(key);
        }
        return exists;
    }
    
    public void setParentTuple(Tuple parent) {
        putWeakly(PARENT, parent);
    }
    
    public Tuple getParentTuple() {
        return getTuple(PARENT);
    }
}
