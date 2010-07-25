package gr.forth.ics.graph;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import gr.forth.ics.util.Args;

//TODO: don't serialize weak entries. Test to serialize a graph with a BfsLayerer (which creates weak entries)
class TupleImpl extends AbstractTuple {
    private transient Map<Object, Object> values;
    
    public TupleImpl() { }
    
    public TupleImpl(Object value) {
        super(value);
    }
    
    public TupleImpl(Map<?, ?> values) {
        Args.notNull(values);
        if (values.isEmpty()) {
            return;
        }
        lazyInit();
        for (Entry<?, ?> entry : values.entrySet()) {
            this.values.put(entry.getKey(), entry.getValue());
        }
    }
    
    public TupleImpl(Tuple copy) {
        this(copy.asMap());
        setValue(copy.getValue());
    }
    
    private void lazyInit() {
        if (values == null) {
            values = new WeakHashMap<Object, Object>(1);
        }
    }
    
    protected Object getLocally(Object key) {
        if (values == null) {
            return null;
        }
        return unmask(values.get(key));
    }
    
    public Object remove(Object key) {
        if (values == null) {
            return null;
        }
        Object old = values.remove(key);
        if (values.size() == 0) {
            values = null;
        }
        return unmask(old);
    }
    
    public Object put(Object key, Object value) {
        lazyInit();
        return unmask(values.put(key, mask(key, value)));
    }
    
    public Object putWeakly(Object key, Object value) {
        lazyInit();
        Args.notNull("Null cannot be put weakly - use normal put instead", key);
        return values.put(key, value);
    }
    
    private StrongValue mask(Object key, Object value) {
        return new StrongValue(key, value);
    }
    
    private Object unmask(Object value) {
        if (value instanceof StrongValue) {
            return ((StrongValue)value).value;
        }
        return value;
    }
    
    protected boolean hasLocally(Object key) {
        if (values == null) {
            return false;
        }
        return values.containsKey(key);
    }
    
    public Set<Object> keySet() {
        if (values == null) {
            return Collections.emptySet();
        }
        Set<Object> keys = new HashSet<Object>();
        for (Entry<Object, Object> entry : values.entrySet()) {
            if (entry.getValue() instanceof StrongValue) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
    
    public Map<Object, Object> asMap() {
        return new AbstractMap<Object, Object>() {
            public Object remove(Object key) {
                return TupleImpl.this.remove(key);
            }
            
            public Object get(Object key) {
                return TupleImpl.this.get(key);
            }
            
            public boolean containsKey(Object key) {
                return TupleImpl.this.has(key);
            }
            
            public Object put(Object key, Object value) {
                return TupleImpl.this.put(key, value);
            }
            
            public Set<Map.Entry<Object, Object>> entrySet() {
                if (values == null) {
                    return Collections.emptySet();
                }
                final Set<Map.Entry<Object, Object>> delegate = values.entrySet();
                return new Set<Map.Entry<Object, Object>>() {
                    public boolean add(Map.Entry<Object, Object> o) {
                        Object oldValue = TupleImpl.this.get(o.getKey());
                        boolean existed;
                        if (oldValue != null) {
                            existed = true;
                        } else {
                            existed = TupleImpl.this.has(o.getKey());
                        }
                        TupleImpl.this.put(o.getKey(), o.getValue());
                        if (oldValue != o.getValue()) {
                            return true;
                        }
                        return !existed;
                    }
                    
                    public boolean addAll(Collection<? extends Entry<Object, Object>> c) {
                        boolean changed = false;
                        for (Entry<Object, Object> entry : c) {
                            changed |= add(entry);
                        }
                        return changed;
                    }
                    
                    public boolean contains(Object o) {
                        return delegate.contains(o);
                    }
                    
                    public boolean remove(Object o) {
                        return delegate.remove(o);
                    }
                    
                    public <T> T[] toArray(T[] a) {
                        return delegate.toArray(a);
                    }
                    
                    public boolean containsAll(Collection<?> c) {
                        return delegate.containsAll(c);
                    }
                    
                    public boolean removeAll(Collection<?> c) {
                        return delegate.removeAll(c);
                    }
                    
                    public boolean retainAll(Collection<?> c) {
                        return delegate.retainAll(c);
                    }
                    
                    public void clear() {
                        delegate.clear();
                    }
                    
                    public boolean isEmpty() {
                        return delegate.isEmpty();
                    }
                    
                    public Iterator<Entry<Object, Object>> iterator() {
                        final Iterator<Entry<Object, Object>> iDelegate = delegate.iterator();
                        return new Iterator<Entry<Object, Object>>() {
                            public boolean hasNext() {
                                return iDelegate.hasNext();
                            }
                            
                            public Entry<Object, Object> next() {
                                Entry<Object, Object> e = iDelegate.next();
                                e.setValue(unmask(e.getValue()));
                                return e;
                            }
                            
                            public void remove() {
                                iDelegate.remove();
                            }
                        };
                    }
                    
                    public int size() {
                        return delegate.size();
                    }
                    
                    public Object[] toArray() {
                        return delegate.toArray();
                    }
                };
            }
            
            public int size() {
                if (values == null) {
                    return 0;
                }
                return values.size();
            }
            
            public String toString() {
                if (values == null || values.isEmpty()) {
                    return "{}";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                for (Object key : values.keySet()) {
                    sb.append(key);
                    sb.append("=");
                    sb.append(get(key));
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append("}");
                return sb.toString();
            }
        };
    }
    
    private final class StrongValue implements Serializable {
        final Object key;
        final Object value;
        
        StrongValue(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof StrongValue)) {
                return false;
            }
            
            StrongValue sv = (StrongValue)o;
            return value.equals(sv.value) && key.equals(sv.key);
        }
        
        public int hashCode() {
            int hash = 0;
            if (key != null) { hash += key.hashCode(); }
            if (value != null) { hash += value.hashCode(); }
            return hash;
        }
        
        public String toString() {
            
            return "[StrongValue: " + value + "]";
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws Exception {
        out.defaultWriteObject();
        Map<Object, Object> map = new HashMap<Object, Object>();
        if (values != null) {
            map.putAll(values);
        }
        out.writeObject(map);
    }
    
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        Map<Object, Object> map = (Map<Object, Object>)in.readObject();
        if (!map.isEmpty()) {
            values = new WeakHashMap<Object, Object>(map);
        }
    }
    
    public String toString() {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object key : values.keySet()) {
            sb.append(key);
            sb.append("->");
            sb.append(get(key));
            sb.append(", ");
        }
        sb.append("value=");
        sb.append(getValue());
        sb.append("]");
        return sb.toString();
    }
    
}
