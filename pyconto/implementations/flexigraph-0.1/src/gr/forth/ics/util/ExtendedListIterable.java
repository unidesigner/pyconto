package gr.forth.ics.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class ExtendedListIterable<E> extends ExtendedIterable<E> {
    public ExtendedListIterable() {
        super();
    }
    
    public ExtendedListIterable(int expectedSize) {
        super(expectedSize);
    }
    
    public final ListIterator<E> listIterator() {
        if (filters == null || filters.isEmpty()) {
            return listIteratorImpl();
        }
        return new FilteringListIterator<E>(listIteratorImpl(), new Filter<E>() {
            public boolean accept(E element) {
                for (Filter<? super E> filter : filters) {
                    if (!filter.accept(element)) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
    
    @Override
    public ExtendedListIterable<E> filter(List<Filter<? super E>> filters) {
        super.filter(filters);
        return this;
    }
    
    protected abstract ListIterator<E> listIteratorImpl();
    
    @Override public Iterator<E> iterator() {
        return listIterator();
    }
}
