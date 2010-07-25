package gr.forth.ics.util;

import java.util.Iterator;

public class PushBackIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;
    
    public PushBackIterator(Iterator<E> iterator) {
        Args.notNull(iterator);
        this.iterator = iterator;
    }
    
    private E saved = null;
    private E last;
    
    public boolean hasNext() {
        return saved != null || iterator.hasNext();
    }
    
    public E next() {
        if (saved != null) {
            try {
                return last = saved;
            } finally {
                saved = null;
            }
        }
        return last = iterator.next();
    }
    
    public void pushBack() {
        saved = last;
    }
    
    //can only remove in fresh next(), not pushBack()
    public void remove() {
        if (saved != null) {
            throw new IllegalStateException("Cannot remove when next element has been pushed back");
        }
        iterator.remove();
    }
}
