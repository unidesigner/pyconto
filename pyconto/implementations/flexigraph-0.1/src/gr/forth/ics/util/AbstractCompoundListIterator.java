package gr.forth.ics.util;

import java.util.Collections;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractCompoundListIterator<E> implements ListIterator<E> {
    private ListIterator<E> currentIterator;
    private int index = 0;
    
    public AbstractCompoundListIterator() {
        currentIterator = Collections.<E>emptyList().listIterator();
    }
    
    protected E last;
    
    protected abstract ListIterator<E> nextIterator();
    protected abstract boolean hasNextIterator();
    protected abstract ListIterator<E> previousIterator();
    protected abstract boolean hasPreviousIterator();
    
    private void proceedForward() {
        while (!currentIterator.hasNext()) {
            if (hasNextIterator()) {
                currentIterator = nextIterator();
            } else {
                return;
            }
        }
    }
    
    private void proceedBackward() {
        while (!currentIterator.hasPrevious()) {
            if (hasPreviousIterator()) {
                currentIterator = previousIterator();
            } else {
                return;
            }
        }
    }
    
    public boolean hasNext() {
        proceedForward();
        return currentIterator.hasNext();
    }
    
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        proceedForward();
        E next = currentIterator.next();
        last = next;
        index++;
        return next;
        
    }
    
    public boolean hasPrevious() {
        proceedBackward();
        return currentIterator.hasPrevious();
    }
    
    public E previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        E prev = currentIterator.previous();
        last = prev;
        index--;
        return prev;
    }
    
    public int previousIndex() {
        return index - 1;
    }
    
    public int nextIndex() {
        return index;
    }
    
    public void set(E o) {
        currentIterator.set(o);
        last = null;
    }
    
    public void remove() {
        currentIterator.remove();
        last = null;
    }
    
    public void add(E o) {
        currentIterator.add(o);
        last = null;
    }
}
