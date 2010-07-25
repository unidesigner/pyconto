package gr.forth.ics.util;

import java.util.Iterator;

public abstract class FilteringIterator<E> implements Iterator<E> {
    private final PushBackIterator<E> iterator;
    
    public FilteringIterator(Iterator<E> iterator) {
        Args.notNull(iterator);
        this.iterator = new PushBackIterator<E>(iterator);
    }
    
    protected abstract boolean accept(E element);
    
    private void checkNext() {
        while (iterator.hasNext()) {
            E next = iterator.next();
            if (accept(next)) {
                iterator.pushBack();
                break;
            }
        }
    }
    
    public boolean hasNext() {
        checkNext();
        return iterator.hasNext();
    }
    
    public E next() {
        checkNext();
        return iterator.next();
    }
    
    public void remove() {
        iterator.remove();
    }
}
