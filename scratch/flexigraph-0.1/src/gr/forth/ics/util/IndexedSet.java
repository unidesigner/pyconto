package gr.forth.ics.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class IndexedSet<T> extends AbstractCollection<T> implements Set<T> {
    private final List<T> list = new ArrayList<T>();
    private final Set<T> set = new HashSet<T>();
    
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Iterator<T> realIterator = list.iterator();
            T current = null;

            public boolean hasNext() {
                return realIterator.hasNext();
            }

            public T next() {
                T next = realIterator.next();
                current = next;
                return next;
            }

            public void remove() {
                realIterator.remove();
                set.remove(current);
                current = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T e) {
        boolean added = set.add(e);
        if (added) {
            list.add(e);
        }
        return added;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = set.remove(o);
        if (removed) {
            list.remove(o);
        }
        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean removed = false;
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T next = it.next();
            if (!c.contains(next)) {
                it.remove();
                set.remove(it);
                removed = true;
            }
        }
        return removed;
    }

    public void clear() {
        set.clear();
        list.clear();
    }

    public T get(int index) {
        return list.get(index);
    }

    public T remove(int index) {
        T t = list.get(index);
        remove(t);
        return t;
    }
}
