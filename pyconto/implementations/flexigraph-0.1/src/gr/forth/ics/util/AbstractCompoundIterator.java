package gr.forth.ics.util;

import java.util.*;

public abstract class AbstractCompoundIterator<E> implements Iterator<E> {
    private Iterator<E> currentIterator;
    private IteratorRemoveStrategy<E> removeStrategy;
    protected E last;
    
    protected abstract Iterator<E> nextIterator();
    protected abstract boolean hasNextIterator();
    
    public AbstractCompoundIterator() {
          this(null);
    }
    
    public AbstractCompoundIterator(IteratorRemoveStrategy<E> removeStrategy) {
          this.removeStrategy = removeStrategy;
    }
    
    private void proceed() {
        if (currentIterator == null) {
            if (hasNextIterator()) {
                currentIterator = nextIterator();
            } else {
                return;
            }
        }
        if (!currentIterator.hasNext()) {
            currentIterator = null;
            while (hasNextIterator()) {
                currentIterator = nextIterator();
                if (currentIterator.hasNext()) {
                    return;
                } else {
                    currentIterator = null;
                }
            }
        }
    }
    
    public boolean hasNext() {
        proceed();
        return currentIterator != null;
    }

    public E next() {
        proceed();
        return last = currentIterator.next();
    }

    public void remove() {
        currentIterator.remove();
        if (removeStrategy != null) {
              removeStrategy.removed(last);
        }
    }
}
