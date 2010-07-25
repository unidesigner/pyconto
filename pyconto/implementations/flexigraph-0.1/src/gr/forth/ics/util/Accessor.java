package gr.forth.ics.util;

import java.util.ListIterator;

/**
 * An Accessor is a handle to an element contained in a {@link FastLinkedList},
 * direct access to it, its previous and next elements in the list, etc.
 * <p>
 * Accessor objects are created by FastLinkedList, and all operations of these objects
 * take constant time.
 * 
 * @param <E> the type of the element being accessed
 * @see FastLinkedList
 * @see FastLinkedList#addFirst(Object)
 * @see FastLinkedList#addLast(Object)
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public interface Accessor<E> {
    /**
     * Removes the element of this accessor from its owner {@link FastLinkedList}.
     * @return true if the element was indeed removed, or false if it was not there
     */
    boolean remove();
    
    /**
     * Returns whether the element of this accessor has already been removed
     * from its owner {@link FastLinkedList} (not
     * necessarily through the {@link #remove} method).
     */
    boolean isRemoved();
    
    /**
     * Replaces the element of this accessor with a new, and returns the old one.
     * @param newElement the element with which to replace the element represented by this accessor
     * @return the old value of this accessor
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    E set(E newElement);
    
    /**
     * Returns the element represented by this accessor.
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    E get();
    
    /**
     * Adds a new element immediately after the element of this accessor,
     * in the owner from its owner {@link FastLinkedList}, and returns
     * a new Accessor to the newly inserted element.
     * @param element the new element to insert to the owner list
     * @return an accessor to operate on the newly inserted element
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    Accessor<E> addAfter(E element);
    
    /**
     * Adds a new element immediately before the element of this accessor,
     * in the owner from its owner {@link FastLinkedList}, and returns
     * a new Accessor to the newly inserted element.
     * @param element the new element to insert to the owner list
     * @return an accessor to operate on the newly inserted element
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    Accessor<E> addBefore(E element);
    
    /**
     * Returns an accessor that represents the element immediately after the
     * element represented by this accessor, in the owner {@link FastLinkedList}. 
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    Accessor<E> next();
    
    /**
     * Returns an accessor that represents the element immediately before the
     * element represented by this accessor, in the owner {@link FastLinkedList}. 
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    Accessor<E> previous();
    
    /**
     * Returns a list iterator to traverse the owner {@link FastLinkedList} right from the position of the element
     * of this accessor. Specifically, the returned iterator will be positioned right after
     * the element of this accessor, i.e. an immediate <tt>listIterator.previous()</tt>
     * would yield this accessor's element, while <tt>listIterator.next()</tt> would
     * yield its next. To put it in other words, the returned iterator would be at the same
     * starting position as one created by <tt>myList.listIterator(indexOfElement + 1)</tt>
     * The returned iterator 
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    ListIterator<E> listIterator();
    
    /**
     * Moves the element of this accessor to the front (start) of the owner {@link FastLinkedList}.
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    void moveToFront();
    
    /**
     * Moves the element of this accessor to the back (end) of the owner {@link FastLinkedList}.
     * @throws NoSuchElementException if there element of this accessor has been removed
     */
    void moveToBack();
    
    /**
     * Moves the element of this accessor immediately after the element of the given accessor.
     * If the specified accessor is this accessor itself, nothing happens.
     * @throws NoSuchElementException if there element of this accessor has been removed
     * @throws IllegalArgumentException if the specified accessor is <tt>null</tt>,
     * or if it does not have the same owner {@link FastLinkedList} as this accessor.
     */
    void moveAfter(Accessor<E> accessor);
    
    /**
     * Moves the element of this accessor immediately before the element of the given accessor.
     * If the specified accessor is this accessor itself, nothing happens.
     * @throws NoSuchElementException if there element of this accessor has been removed
     * @throws IllegalArgumentException if the specified accessor is <tt>null</tt>,
     * or if it does not have the same owner {@link FastLinkedList} as this accessor.
     */
    void moveBefore(Accessor<E> accessor);

    /**
     * Returns the {@link FastLinkedList} instance that contains this
     * accessor, or {@code null} if this accessor has been removed.
     *
     * @return the owner of this accessor, or {@code null} if this accessor has been removed
     */
    FastLinkedList<E> owner();
}
