package gr.forth.ics.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A filtering list iterator.
 * 
 * @author andreou
 */
public class FilteringListIterator<E> implements ListIterator<E> {
    private final Filter<? super E> filter;
    private final ListIterator<E> listIterator;

    private int index;
    private int offset;
    private E elementToReturn;
    private Accessed accessed = Accessed.NONE;

    private enum Accessed {
        NONE(false) {
            void moveForward(ListIterator<?> listIterator) {
                throw new IllegalStateException();
            }
            void moveBackward(ListIterator<?> listIterator) {
                throw new IllegalStateException();
            }
        }, NEXT(true) {
            void moveForward(ListIterator<?> listIterator) {
                listIterator.next();
            }
            void moveBackward(ListIterator<?> listIterator) {
                listIterator.previous();
            }
        }, PREVIOUS(true) {
            void moveForward(ListIterator<?> listIterator) {
                listIterator.previous();
            }
            void moveBackward(ListIterator<?> listIterator) {
                listIterator.next();
            }
        };

        final boolean exists;

        Accessed(boolean accessed) {
            this.exists = accessed;
        }

        abstract void moveForward(ListIterator<?> listIterator);
        abstract void moveBackward(ListIterator<?> listIterator);

        void moveBackAndForth(ListIterator<?> listIterator) {
            moveForward(listIterator);
            moveBackward(listIterator);
        }
    }

    public FilteringListIterator(ListIterator<E> listIterator, Filter<? super E> filter) {
        this(listIterator, filter, 0);
    }

    public FilteringListIterator(ListIterator<E> listIterator, Filter<? super E> filter, int index) {
        Args.notNull(listIterator);
        Args.notNull(filter);
        this.listIterator = listIterator;
        this.filter = filter;
        this.index = index;
    }

    public void add(E e) {
        moveBack();
        listIterator.add(e);
        index++;
        accessed = Accessed.NONE;
    }

    public boolean hasNext() {
        if (elementToReturn != null && offset > 0) {
            return true;
        }
        elementToReturn = null;
        while (offset < 0) {
            listIterator.next();
            offset++;
        }
        do {
            if (!listIterator.hasNext()) {
                return false;
            }
            E element = listIterator.next();
            offset++;
            if (filter.accept(element)) {
                elementToReturn = element;
                return true;
            }
        } while (true);
    }

    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return elementToReturn;
        } finally {
            offset = 0;
            accessed = Accessed.PREVIOUS;
            elementToReturn = null;
            index++;
        }
    }

    public boolean hasPrevious() {
        if (elementToReturn != null && offset < 0) {
            return true;
        }
        elementToReturn = null;
        while (offset > 0) {
            listIterator.previous();
            offset--;
        }
        do {
            if (!listIterator.hasPrevious()) {
                return false;
            }
            E element = listIterator.previous();
            offset--;
            if (filter.accept(element)) {
                elementToReturn = element;
                return true;
            }
        } while (true);
    }

    public E previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        try {
            return elementToReturn;
        } finally {
            offset = 0;
            accessed = Accessed.NEXT;
            elementToReturn = null;
            index--;
        }
    }

    public int nextIndex() {
        return index;
    }

    public int previousIndex() {
        return index - 1;
    }

    public void remove() {
        moveBack();
        accessed.moveForward(listIterator);
        if (accessed == Accessed.PREVIOUS) {
            index--;
        }
        listIterator.remove();
        accessed = Accessed.NONE;
        elementToReturn = null;
    }

    public void set(E e) {
        moveBack();
        accessed.moveBackAndForth(listIterator);
        listIterator.set(e);
    }

    private void moveBack() {
        while (offset > 0) {
            listIterator.previous();
            offset--;
        }
        while (offset < 0) {
            listIterator.next();
            offset++;
        }
    }
}
