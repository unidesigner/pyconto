package gr.forth.ics.util;

import java.util.*;

public class CompoundIterator<E> extends AbstractCompoundIterator<E> {
    private final Iterator<Iterator<E>> iteratorsIterator;
    
    public CompoundIterator(Iterator<E> i1, Iterator<E> i2) {
        this(i1, i2, null);
    }
    
    /**
     * Will call underlying iterator's remove() method, then will call IteratorRemoveStrategy callback
     */
    public CompoundIterator(Iterator<E> i1, Iterator<E> i2,
            IteratorRemoveStrategy<E> removeStrategy) {
        super(removeStrategy);
        List<Iterator<E>> iterators = new ArrayList<Iterator<E>>(2);
        if (i1 != null) {
            iterators.add(i1);
        }
        if (i2 != null) {
            iterators.add(i2);
        }
        iteratorsIterator = iterators.iterator();
    }
    
    public CompoundIterator(List<Iterator<E>> iterators) {
        this(iterators, null);
    }
    
    public CompoundIterator(List<Iterator<E>> iterators,
            IteratorRemoveStrategy<E> removeStrategy) {
        super(removeStrategy);
        iteratorsIterator = iterators.iterator();
    }
    
    protected Iterator<E> nextIterator() {
        return iteratorsIterator.next();
    }
    
    protected boolean hasNextIterator() {
        return iteratorsIterator.hasNext();
    }
}
