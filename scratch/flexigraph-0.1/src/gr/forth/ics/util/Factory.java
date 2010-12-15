package gr.forth.ics.util;

public interface Factory<V> {
    V create(Object o);
}
