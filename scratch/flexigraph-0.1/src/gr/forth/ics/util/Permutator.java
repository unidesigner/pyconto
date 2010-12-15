package gr.forth.ics.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Permutator {
    private Permutator() { }
    
    public static <T> Iterable<List<T>> permutations(final List<T> list) {
        return new Iterable<List<T>>() {
            public Iterator<List<T>> iterator() {
                return new Iterator<List<T>>() {
                    private int current = 0;
                    private final long length = factorial(list.size());
                    
                    public List<T> next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        List<T> permutation = new ArrayList<T>(list);
                        int k = current;
                        for (int j = 2; j <= list.size(); j++) {
                            k /= j - 1;
                            Collections.swap(permutation, (k % j), j - 1);
                        }
                        current++;
                        return permutation;
                    }
                    
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    public boolean hasNext() {
                        return current < length;
                    }
                };
            }
        };
    }
    
    private static long factorial(int k) {
        long factorial = 1L;
        for (int i = 2; i <= k; i++) {
            factorial *= i;
        }
        return factorial;
    }
}
