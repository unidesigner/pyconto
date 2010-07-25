package gr.forth.ics.util;

import java.util.Iterator;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class TransformingIterable<S, T> implements Iterable<T> {
    private final Iterable<S> source;

    public TransformingIterable(Iterable<S> source) {
        Args.notNull(source);
        this.source = source;
    }

    protected abstract T transform(S source);

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            final Iterator<S> sourceIterator = source.iterator();

            public boolean hasNext() {
                return sourceIterator.hasNext();
            }

            public T next() {
                return transform(sourceIterator.next());
            }

            public void remove() {
                sourceIterator.remove();
            }
        };
    }
}
