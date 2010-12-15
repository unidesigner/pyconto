package gr.forth.ics.util;

public interface Filter<E> {
    boolean accept(E element);
}
