package gr.forth.ics.util;

import java.io.Serializable;
import java.util.*;


/**
 * A linked list that also provides pointers to list nodes, making it possible for
 * several operations to be of constant time instead of linear. 
 * <p>
 * For example, consider a list containing arbitrary <tt>Node</tt>s:
 * <pre>
 * class Node {
 *      Accessor&lt;Node&gt; accessor;
 *      //other members here
 * }
 * </pre>
 * <pre>
 * FastLinkedList&lt;Node&gt; list = new FastLinkedList&lt;Node&gt;();
 * Node node = ...;
 * Accessor&lt;Node&gt; accessor = list.addFirst(node);
 * node.accessor = accessor;
 * </pre>
 * If there is a need to later remove this node from the list, instead of the usual operation:
 * <pre>
 * list.remove(node);
 * </pre>
 * which would take linear time as it involves searching through the list for the element, this can
 * be used, since the accessor to the element is available:
 * <pre>
 * node.accessor.remove();
 * </pre>
 * which takes constant time.
 * <p>
 * Also, linked lists can be appended together to produce a single list, in O(1) time. The list
 * that is appended to the tail of another becomes permanently empty, and all operations
 * attempting to insert an element will throw an <tt>IllegalStatementException</tt>.
 * <p>
 * This implementation allows structural modifications while iterations are in progress, without
 * throwing <tt>ConcurrentModificationException</tt>.
 * <p>
 * 
 * @param <E> the type of the elements contained in the list
 * @see Accessor
 * @author Dimitris Andreou
 */
public class FastLinkedList<E> extends AbstractSequentialList<E> implements Serializable {
    private static final long serialVersionUID = -5460960741591122089L;
    
    /**
     * The number of this list's elements.
     */
    private int size;
    
    /**
     * The head of this list, or null if empty.
     */
    private ListCell<E> head;
    
    /**
     * The tail of this list, or null if empty.
     */
    private ListCell<E> tail;
    
    /**
     * Represents whether this list is appended to another one
     */
    private boolean consumed;
    
    /**
     * Creates a new FastLinkedList.
     */
    public FastLinkedList() { }
    
    /**
     * Creates a new FastLinkedList by copying all provided elements to it.
     */
    public FastLinkedList(Iterable<? extends E> elements) {
        Args.notNull(elements);
        for (E e : elements) {
            addLast(e);
        }
    }
    
    /**
     * Returns whether this list has been {@link #append(FastLinkedList) appended}
     * to another one. If this is the case, then this list will always be empty, and throw
     * IllegalStateException for all operations trying to modify it; essentially after being
     * appended, the list is a throw-away object.
     */
    public boolean isConsumed() {
        return consumed;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }
    
    private void markConsumed() {
        consumed = true;
        size = 0;
        head = tail = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns the first element in this list.
     * @throws NoSuchElementException if this list is empty
     */
    public E getFirst() {
        checkExisting(head);
        return head.value;
    }
    
    /**
     * Returns the last element in this list.
     * @throws NoSuchElementException if this list is empty
     */
    public E getLast() {
        checkExisting(tail);
        return tail.value;
    }
    
    private void checkExisting(ListCell<E> cell) {
        if (cell == null) {
            throw new NoSuchElementException();
        }
    }
    
    private AccessorImpl<E> checkOwnedAccessor(Accessor<E> accessor) {
        if (accessor == null) {
            throw new IllegalArgumentException("Null accessor");
        }
        AccessorImpl<E> accessorImpl = (AccessorImpl<E>)accessor;
        if (!accessorImpl.belongsTo(this)) {
            throw new IllegalArgumentException("This accessor does not belong to this list");
        }
        return accessorImpl;
    }
    
    /**
     * Inserts the given element at the beginning of this list.
     * @param value the element to be inserted at the beginning of this list
     * @return an {@link Accessor} that provide fast operations regarding the newly inserted element
     */
    public Accessor<E> addFirst(E value) {
        checkNotConsumed();
        size++;
        ListCell<E> cell = new ListCell<E>(value);
        cell.next = head;
        if (head != null) {
            head.prev = cell;
        }
        head = cell;
        if (tail == null) {
            tail = cell;
        }
        return new AccessorImpl<E>(cell, this);
    }
    
    /**
     * Appends the given element to the end of this list.
     * @param value the element to be inserted at the beginning of this list
     * @return an {@link Accessor} that provide fast operations regarding the newly inserted element
     */
    public Accessor<E> addLast(E value) {
        checkNotConsumed();
        size++;
        ListCell<E> newCell = new ListCell<E>(value);
        newCell.prev = tail;
        if (tail != null) {
            tail.next = newCell;
        }
        tail = newCell;
        if (head == null) {
            head = newCell;
        }
        return new AccessorImpl<E>(newCell, this);
    }
    
    private Accessor<E> addAfter(AccessorImpl<E> previous, E value) {
        checkNotConsumed();
        size++;
        ListCell<E> previousCell = previous.cell;
        ListCell<E> nextCell = previousCell.next; //may be null
        ListCell<E> newCell = new ListCell<E>(value);
        
        previousCell.next = newCell;
        if (nextCell != null) {
            nextCell.prev = newCell;
        }
        newCell.next = nextCell;
        newCell.prev = previousCell;
        if (tail == previousCell) {
            tail = newCell;
        }
        return new AccessorImpl<E>(newCell, this);
    }
    
    private Accessor<E> addBefore(AccessorImpl<E> next, E value) {
        checkNotConsumed();
        size++;
        ListCell<E> nextCell = next.cell;
        ListCell<E> previousCell = nextCell.prev; //may be null
        ListCell<E> newCell = new ListCell<E>(value);
        
        nextCell.prev = newCell;
        if (previousCell != null) {
            previousCell.next = newCell;
        }
        newCell.prev = previousCell;
        newCell.next = nextCell;
        if (head == nextCell) {
            head = newCell;
        }
        return new AccessorImpl<E>(newCell, this);
    }

    private void checkNotConsumed() {
        if (consumed) {
            throwIllegalStateException();
        }
    }
    
    private static void throwIllegalStateException() {
        throw new IllegalStateException("This list has been appended to " +
                "another one, and thus has been marked as empty and unmodifiable, and should be thrown away");
    }
    
    /**
     * Removes and returns the first element from this list.
     * @throws NoSuchElementException if this list is empty
     */
    public E removeFirst() {
        checkExisting(head);
        size--;
        if (head == tail) {
            tail = null;
            try {
                return head.value;
            } finally {
                head = null;
            }
        }
        try {
            return head.value;
        } finally {
            head.next.prev = null;
            head = head.next;
        }
    }
    
    /**
     * Removes and returns the last element from this list.
     * @throws NoSuchElementException if this list is empty
     */
    public E removeLast() {
        checkExisting(tail);
        size--;
        if (head == tail) {
            head = null;
            try {
                return tail.value;
            } finally {
                tail = null;
            }
        }
        try {
            return tail.value;
        } finally {
            tail.prev.next = null;
            tail = tail.prev;
        }
    }
    
    /**
     * Appends the elements of a list to this one (in constant time). To preserve
     * consistency, the other list is turned to an empty immutable list, and its {@link #isConsumed()}
     * method will return true. The other list should be thrown away after this operation.
     * @param list the list to be appended to this one
     */
    public void append(FastLinkedList<E> list) {
        checkNotConsumed();
        if (list.isEmpty()) {
            return;
        }
        if (isEmpty()) { //just point to the other list's head and tail
            head = list.head;
            tail = list.tail;
        } else { //transform tail
            tail.next = list.head;
            list.head.prev = tail;
            tail = list.tail;
        }
        size += list.size();
        list.markConsumed();
    }
    
    /**
     * Returns whether the given accessor is owned by this list. For this to be true, the
     * accessor must have been created by this list, and its respective element must not
     * have been removed by any means.
     */
    public boolean ownsAccessor(Accessor<E> accessor) {
        if ((accessor == null) || (!(accessor instanceof AccessorImpl))) {
            return false;
        }
        AccessorImpl<E> ref = (AccessorImpl<E>)accessor;
        return ref.belongsTo(this);
    }
    
    private void removeCell(ListCell<E> cell) {
        if (cell.prev != null) {
            cell.prev.next = cell.next;
        }
        if (cell.next != null) {
            cell.next.prev = cell.prev;
        }
        if (head == cell) {
            head = cell.next;
        }
        if (tail == cell) {
            tail = cell.prev;
        }
        cell.isDeleted = true;
        cell.value = null;
        size--;
    }
    
    /**
     * Creates a new {@link Accessor} object that will operate on an element which exists in this list,
     * or null if the given element is not contained in it. 
     * @return an Accessor object which will operate on the given element if it is contained
     * in this list, or null otherwise
     */
    public Accessor<E> accessorFor(E element) {
        ListCell<E> curr = head;
        while (curr != null) {
            if (element == null) {
                if (curr.value == null) {
                    return new AccessorImpl<E>(curr, this);
                }
            } else {
                if (element.equals(curr.value)) {
                    return new AccessorImpl<E>(curr, this);
                }
            }
            curr = curr.next;
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index) {
        if (isConsumed()) {
            return new ConsumedIterator<E>();
        }
        return new ListIteratorImpl(index);
    }
    
    private static class ConsumedIterator<E> implements ListIterator<E> {
        public void add(E e) {
            throwIllegalStateException();
        }

        public boolean hasNext() {
            return false;
        }

        public boolean hasPrevious() {
            return false;
        }

        public E next() {
            throw new NoSuchElementException();
        }

        public int nextIndex() {
            return 0;
        }

        public E previous() {
            throw new NoSuchElementException();
        }

        public int previousIndex() {
            return -1;
        }

        public void remove() {
            throw new NoSuchElementException();
        }

        public void set(E e) {
            throw new NoSuchElementException();
        }
    }
    
    private class ListIteratorImpl implements ListIterator<E> {
        private ListCell<E> next;
        private ListCell<E> prev;
        private ListCell<E> last;
        private final boolean isIndexValid;
        private int index;
        
        ListIteratorImpl(int offset) {
            if (offset < 0 || offset > size) {
                throw new NoSuchElementException();
            }
            this.isIndexValid = true;
            index = offset;
            if (offset <= size / 2) {
                next = head;
                while (offset > 0) {
                    prev = next;
                    next = next.next;
                    offset--;
                }
            } else {
                prev = tail;
                offset = size - offset;
                while (offset > 0) {
                    next = prev;
                    prev = prev.prev;
                    offset--;
                }
            }
        }
        
        ListIteratorImpl(Accessor<E> ref) {
            AccessorImpl<E> reference = checkOwnedAccessor(ref);
            this.last = null;
            this.prev = reference.cell;
            this.next = prev.next;
            isIndexValid = false;
        }
        
        public void add(E obj) {
            //add between prev and next, next is unaffected, next.previous is the newly added
            last = null;
            index++;
            size++;
            ListCell<E> newCell = new ListCell<E>(obj);
            newCell.next = next;
            newCell.prev = prev;
            if (prev == null) {
                head = newCell;
            } else {
                prev.next = newCell;
            }
            prev = newCell;
            if (next == null) {
                tail = newCell;
            } else {
                next.prev = newCell;
            }
        }
        
        private void proceedForward() {
            while (next != null && next.isDeleted) {
                prev = next;
                next = next.next;
            }
        }
        
        private void proceedBackward() {
            while (prev != null && prev.isDeleted) {
                next = prev;
                prev = prev.prev;
            }
        }
        
        public boolean hasNext() {
            proceedForward();
            return next != null;
        }
        
        public boolean hasPrevious() {
            proceedBackward();
            return prev != null;
        }
        
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return next.value;
            } finally {
                index++;
                last = next;
                prev = next;
                next = next.next;
            }
        }
        
        public int nextIndex() {
            return index;
        }
        
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            try {
                return prev.value;
            } finally {
                index--;
                last = prev;
                next = prev;
                prev = prev.prev;
            }
        }
        
        public int previousIndex() {
            return index - 1;
        }
        
        public void remove() {
            if (last == null || last.isDeleted) {
                throw new IllegalStateException();
            }
            index--;
            size--;
            last.isDeleted = true;
            prev = last.prev;
            next = last.next;
            if (prev != null) {
                prev.next = last.next;
            } else {
                head = next;
            }
            if (next != null) {
                next.prev = last.prev;
            } else {
                tail = prev;
            }
            last = null;
        }
        
        public void set(E element) {
            if (last == null) {
                throw new IllegalStateException();
            }
            last.value = element;
        }
    }

    private static class AccessorImpl<E> implements Accessor<E>, Serializable {
        ListCell<E> cell; //not final, for facilitating reordering operations
        FastLinkedList<E> owner;

        AccessorImpl(ListCell<E> cell, FastLinkedList<E> owner) {
            this.cell = cell;
            this.owner = owner;
        }

        boolean belongsTo(FastLinkedList seq) {
            return owner == seq;
        }

        private void checkOwner() {
            if (owner == null) {
                throw new NoSuchElementException("this element has been removed");
            }
        }

        void invalidate() {
            owner = null;
        }

        @Override
        public String toString() {
            return cell.toString() + "," + (owner != null ? "valid" : "invalid");
        }

        public boolean remove() {
            if (cell.isDeleted) {
                return false;
            }
            owner.removeCell(cell);
            invalidate();
            return true;
        }

        public E get() {
            checkOwner();
            return cell.value;
        }

        public FastLinkedList<E> owner() {
            return owner;
        }

        public boolean isRemoved() {
            return cell.isDeleted;
        }

        public E set(E newValue) {
            try {
                return cell.value;
            } finally {
                cell.value = newValue;
            }
        }

        public Accessor<E> addAfter(E value) {
            checkOwner();
            return owner.addAfter(this, value);
        }

        public Accessor<E> addBefore(E value) {
            checkOwner();
            return owner.addBefore(this, value);
        }

        public void moveAfter(Accessor<E> afterWhat) {
            checkOwner();
            AccessorImpl<E> r = owner.checkOwnedAccessor(afterWhat);
            if (r.cell == cell) {
                return;
            }
            E value = cell.value;
            FastLinkedList<E> owner = this.owner;
            this.remove();
            update(r.addAfter(value), owner);
        }

        public void moveBefore(Accessor<E> beforeWhat) {
            checkOwner();
            AccessorImpl<E> r = owner.checkOwnedAccessor(beforeWhat);
            if (r.cell == cell) {
                return;
            }
            E value = cell.value;
            FastLinkedList<E> owner = this.owner;
            this.remove();
            update(r.addBefore(value), owner);
        }

        public void moveToBack() {
            checkOwner();
            E value = cell.value;
            FastLinkedList<E> owner = this.owner;
            this.remove();
            update(owner.addLast(value), owner);
        }

        public void moveToFront() {
            checkOwner();
            E value = cell.value;
            FastLinkedList<E> owner = this.owner;
            this.remove();
            update(owner.addFirst(value), owner);
        }
        
        public Accessor<E> next() {
            checkOwner();
            if (cell.next == null) {
                throw new NoSuchElementException();
            }
            return new AccessorImpl<E>(cell.next, owner);
        }

        public Accessor<E> previous() {
            checkOwner();
            if (cell.prev == null) {
                throw new NoSuchElementException();
            }
            return new AccessorImpl<E>(cell.prev, owner);
        }

        public ListIterator<E> listIterator() {
            checkOwner();
            return owner.new ListIteratorImpl(this);
        }
        
        private void update(Accessor<E> valid, FastLinkedList<E> owner) {
            update((AccessorImpl<E>)valid, owner);
        }
        
        private void update(AccessorImpl<E> copyFrom, FastLinkedList<E> owner) {
            this.owner = owner;
            this.cell = copyFrom.cell;
        }
    }

    private static class ListCell<E> implements Serializable {
        boolean isDeleted;
        ListCell<E> prev;
        ListCell<E> next;
        E value;

        ListCell(E value) {
            this.value = value;
        }

        public String toString() {
            return (prev == null ? "null" : prev.value.toString())
            + "<--"
                    + (value == null ? "null" : value.toString())
                    + "-->"
                    + (next == null ? "null" : next.value.toString());
        }
    }
}

