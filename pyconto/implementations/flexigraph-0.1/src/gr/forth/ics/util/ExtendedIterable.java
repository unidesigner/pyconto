package gr.forth.ics.util;

import java.util.*;

public class ExtendedIterable<E> implements Iterable<E> {
    private final Iterable<E> iter;
    private final int expectedSize;
    protected List<Filter<? super E>> filters;
    
    protected ExtendedIterable() {
        this(-1);
    }
    
    //only for subclasses. Must implement iterator()
    protected ExtendedIterable(int expectedSize) {
        iter = new Iterable<E>() {
            public Iterator<E> iterator() {
                return ExtendedIterable.this.iterator();
            }
        };
        this.expectedSize = expectedSize;
    }
    
    public ExtendedIterable(Iterable<E> iter) {
        this(iter, -1);
    }
    
    public ExtendedIterable(Iterable<E> iter, int expectedSize) {
        Args.notNull(iter);
        this.iter = iter;
        this.expectedSize = expectedSize;
    }
    
    /**
     * Returns the number of elements of this ExtendedIterable. Traverses all the elements
     * to count them; it is a slow operation.
     */ 
    public int size() {
        int size = 0;
        for (E e : this) { size++; }
        return size;
    }
    
    public Iterator<E> iterator() {
        if (filters == null || filters.isEmpty()) {
            return iter.iterator();
        }
        return new FilteringIterator<E>(iter.iterator()) {
            protected boolean accept(E element) {
                for (Filter<? super E> filter : filters) {
                    if (!filter.accept(element)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
    
    public Collection<E> drainTo(Collection<E> col) {
        Args.notNull(col);
        for (E e : this) {
            col.add(e);
        }
        return col;
    }
    
    public Set<E> drainTo(Set<E> set) {
        drainTo((Collection<E>)set);
        return set;
    }
    
    public List<E> drainTo(List<E> list) {
        drainTo((Collection<E>)list);
        return list;
    }
    
    public Set<E> drainToSet() {
        return drainTo(new LinkedHashSet<E>());
    }
    
    public List<E> drainToList() {
        ArrayList<E> list = expectedSize > 0 ? new ArrayList<E>(expectedSize) : new ArrayList<E>();
        return drainTo(list);
    }
    
    public ExtendedIterable<E> filter(Filter<? super E> filter) {
        Args.notNull(filter);
        if (filters == null) {
            filters = new ArrayList<Filter<? super E>>();
        }
        filters.add(filter);
        return this;
    }
    
    public ExtendedIterable<E> filter(List<Filter<? super E>> filters) {
        Args.notNull(filters);
        for (Filter<? super E> f : filters) {
            filter(f);
        }
        return this;
    }
    
    @Override
    public String toString() {
        return drainToList().toString();
    }
    
    public static <K> ExtendedIterable<K> wrap(Iterable<K> iter) {
        if (iter instanceof ExtendedIterable) {
            return (ExtendedIterable<K>)iter;
        }
        Args.notNull(iter);
        return new ExtendedIterable<K>(iter);
    }
    
    public static <E> Collection<E> drainTo(Iterator<? extends E> iter, Collection<E> col) {
        Args.notNull(col);
        if (iter != null) {
            while (iter.hasNext()) {
                col.add(iter.next());
            }
        }
        return col;
    }
    
    public static <E> Set<E> drainTo(Iterator<? extends E> iter, Set<E> set) {
        drainTo(iter, (Collection<E>)set);
        return set;
    }
    
    public static <E> List<E> drainTo(Iterator<? extends E> iter, List<E> list) {
        drainTo(iter, (Collection<E>)list);
        return list;
    }
    
    public static <E> Set<E> drainToSet(Iterator<? extends E> iter) {
        return drainTo(iter, new LinkedHashSet<E>());
    }
    
    public static <E> List<E> drainToList(Iterator<? extends E> iter) {
        return drainTo(iter, new ArrayList<E>());
    }

    @SuppressWarnings("unchecked") //read only, thus safe
    private static final ExtendedIterable EMPTY = new ExtendedIterable(Collections.EMPTY_LIST);

@SuppressWarnings("unchecked") //read only, thus safe
    public static <E> ExtendedIterable<E> empty() {
        return EMPTY;
    }
}
