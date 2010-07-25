package gr.forth.ics.graph.path;

import java.util.*;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.FastLinkedList;

/**
 *
 * @deprecated Use {@link Traverser}
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public abstract class ExpansionFront {
    protected ExpansionFront() { }
    
    public abstract void addPath(Path path);
    public abstract boolean hasNext();
    public abstract Path next();
    public abstract void reset();
    public void nextRound() { }
    
    public static ExpansionFront newDFS() {
        return new ExpansionFront() {
            FastLinkedList<Path> paths = new FastLinkedList<Path>();
            FastLinkedList<Path> currentRun;
            public void addPath(Path path) {
                currentRun.addLast(path);
            }

            public Path next() {
                return paths.removeFirst();
            }

            public boolean hasNext() {
                return !paths.isEmpty();
            }

            public void reset() {
                paths = new FastLinkedList<Path>();
                currentRun = new FastLinkedList<Path>();
            }
            
            public void nextRound() {
                if (currentRun != null) {
                    currentRun.append(paths);
                    paths = currentRun;
                }
                currentRun = new FastLinkedList<Path>();
            }
        };
    }
    
    public static ExpansionFront newBFS() {
        return new ExpansionFront() {
            final LinkedList<Path> paths = new LinkedList<Path>();
            
            public void addPath(Path path) {
                paths.addLast(path);
            }
            
            public Path next() {
                return paths.removeFirst();
            }
            
            public boolean hasNext() {
                return !paths.isEmpty();
            }
            
            public void reset() {
                paths.clear();
            }
        };
    }
    
    public static ExpansionFront newSortedSearch(final Comparator<? super Path> comparator) {
        Args.notNull(comparator);
        return new ExpansionFront() {
            final SortedSet<Path> paths = new TreeSet<Path>(comparator);
            
            public void addPath(Path path) {
                paths.add(path);
            }

            public void reset() {
                paths.clear();
            }

            public Path next() {
                Iterator<Path> iterator = paths.iterator();
                Path next = iterator.next();
                iterator.remove();
                return next;
            }

            public boolean hasNext() {
                return !paths.isEmpty();
            }
        };
    }
}
