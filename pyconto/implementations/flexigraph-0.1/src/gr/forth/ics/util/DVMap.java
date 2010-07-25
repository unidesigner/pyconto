package gr.forth.ics.util;

import java.util.*;

public class DVMap<K, V> implements Map<K, V> {
    private final Map<K, V> delegate;
    private final Factory<V> factory;
    
    public DVMap(Map<K, V> delegate, Factory<V> factory) {
        if (delegate == null) {
            throw new IllegalArgumentException("argument is null");
        }
        this.delegate = delegate;
        this.factory = factory;
    }
    
    public DVMap(Factory<V> factory) {
        this(new HashMap<K, V>(), factory);
    }
    
    public DVMap(final Copyable<? extends V> defaultValue) {
        this(new Factory<V>() {
            public V create(Object o) {
                return defaultValue.copy();
            }
        });
    }
    
    public DVMap(final V defaultValue) {
        this(new Factory<V>() {
            public V create(Object o) {
                return defaultValue;
            }
        });
    }
    
    public void clear() {
        delegate.clear();
    }
    
    /**
     * Returns the underlying map implementation used by this DVMap. Modifications
     * to the returned map may break the usual expectations of the DVMap, and
     * result in unspecified behaviour.
     */
    public Map<K, V> getDelegate() {
        return delegate;
    }
    
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }
    
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }
    
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }
    
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (!delegate.containsKey(key)) {
            V value = factory.create(key);
            delegate.put((K)key, value);
            return value;
        }
        return delegate.get(key);
    }
    
    public V getIfExists(Object key) {
        return delegate.get(key);
    }
    
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
    
    public Set<K> keySet() {
        return delegate.keySet();
    }
    
    public V put(K key, V value) {
        return delegate.put(key, value);
    }
    
    public void putAll(Map<? extends K, ? extends V> map) {
        delegate.putAll(map);
    }
    
    public V remove(Object key) {
        V v = delegate.remove(key);
        if (v == null) {
            return factory.create(key);
        }
        return v;
    }
    
    public int size() {
        return delegate.size();
    }
    
    public Collection<V> values() {
        return delegate.values();
    }
    
    @Override
    public String toString() {
        return delegate.toString();
    }
    
    public static <K, V> DVMap<K, Collection<V>> newHashMapWithLinkedLists() {
        return new DVMap<K, Collection<V>>(
                new HashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new LinkedList<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newHashMapWithArrayLists() {
        return new DVMap<K, Collection<V>>(
                new HashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new ArrayList<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newHashMapWithHashSets() {
        return new DVMap<K, Collection<V>>(
                new HashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new HashSet<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newHashMapWithTreeSets(final Comparator<V> c) {
        return new DVMap<K, Collection<V>>(
                new HashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new TreeSet<V>(c);
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newHashMapWithTreeSets() {
        return new DVMap<K, Collection<V>>(
                new HashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new TreeSet<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newTreeMapWithLinkedLists() {
        return new DVMap<K, Collection<V>>(
                new TreeMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new LinkedList<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newTreeMapWithArrayLists() {
        return new DVMap<K, Collection<V>>(
                new TreeMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new ArrayList<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newTreeMapWithHashSets() {
        return new DVMap<K, Collection<V>>(
                new TreeMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new HashSet<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newTreeMapWithTreeSets(final Comparator<V> c) {
        return new DVMap<K, Collection<V>>(
                new TreeMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new TreeSet<V>(c);
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newTreeMapWithTreeSets() {
        return new DVMap<K, Collection<V>>(
                new TreeMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new TreeSet<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newLinkedHashMapWithLinkedLists() {
        return new DVMap<K, Collection<V>>(
                new LinkedHashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new LinkedList<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newLinkedHashMapWithArrayLists() {
        return new DVMap<K, Collection<V>>(
                new LinkedHashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new ArrayList<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newLinkedHashMapWithHashSets() {
        return new DVMap<K, Collection<V>>(
                new LinkedHashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new HashSet<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newLinkedHashMapWithLinkedHashSets() {
        return new DVMap<K, Collection<V>>(
                new HashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new LinkedHashSet<V>();
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newLinkedHashMapWithTreeSets(final Comparator<V> c) {
        return new DVMap<K, Collection<V>>(
                new LinkedHashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new TreeSet<V>(c);
            }
        });
    }
    
    public static <K, V> DVMap<K, Collection<V>> newLinkedHashMapWithTreeSets() {
        return new DVMap<K, Collection<V>>(
                new LinkedHashMap<K, Collection<V>>(),
                new Factory<Collection<V>>() {
            public Collection<V> create(Object o) {
                return new TreeSet<V>();
            }
        });
    }
}
