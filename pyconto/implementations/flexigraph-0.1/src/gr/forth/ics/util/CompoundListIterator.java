package gr.forth.ics.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CompoundListIterator<E> extends AbstractCompoundListIterator<E> {
    private final ListIterator<ListIterator<E>> iteratorIterator;
    
    public CompoundListIterator(ListIterator<E> iterator1, ListIterator<E> iterator2) {
        List<ListIterator<E>> list = new ArrayList<ListIterator<E>>(2);
        if (iterator1 != null) list.add(iterator1);
        if (iterator2 != null) list.add(iterator2);
        iteratorIterator = list.listIterator();
    }
    
    public CompoundListIterator(List<ListIterator<E>> listOfIterators) {
        Args.notNull(listOfIterators);
        iteratorIterator = listOfIterators.listIterator();
    }

    protected ListIterator<E> nextIterator() {
        return iteratorIterator.next();
    }

    protected boolean hasNextIterator() {
        return iteratorIterator.hasNext();
    }

    protected ListIterator<E> previousIterator() {
        return iteratorIterator.previous();
    }

    protected boolean hasPreviousIterator() {
        return iteratorIterator.hasPrevious();
    }
}
