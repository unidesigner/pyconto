package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.Tuple;
import java.util.Iterator;
import java.util.List;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.ExtendedIterable;
import java.util.NoSuchElementException;

public abstract class AbstractPath implements Path {
    public boolean contains(Path path) {
        return find(path) != -1;
    }

    public ExtendedIterable<Node> nodes() {
        return new ExtendedIterable<Node>(new Iterable<Node>() {
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    boolean first = true;
                    final Iterator<Path> stepIterator = steps().iterator();

                    public boolean hasNext() {
                        return (first || stepIterator.hasNext());
                    }

                    public Node next() {
                        if (first) {
                            first = false;
                            return headNode();
                        }
                        return stepIterator.next().tailNode();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        });
    }

    public ExtendedIterable<Edge> edges() {
        return new ExtendedIterable<Edge>(new Iterable<Edge>() {
            public Iterator<Edge> iterator() {
                return new Iterator<Edge>() {
                    final Iterator<Path> stepIterator = steps().iterator();

                    public boolean hasNext() {
                        return stepIterator.hasNext();
                    }

                    public Edge next() {
                        return stepIterator.next().tailEdge();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        });
    }

    public boolean isCycle() {
        return headNode() == tailNode() && size() > 0;
    }

    public boolean isEuler() {
        return hasDuplicates(edges());
    }

    public boolean isHamilton() {
        return hasDuplicates(nodes());
    }

    private boolean hasDuplicates(Iterable<? extends Tuple> iterable) {
        final Object tempKey = new Object();
        for (Tuple tuple : iterable) {
            if (tuple.has(tempKey)) {
                return false;
            }
            tuple.putWeakly(tempKey, null);
        }
        return true;
    }

    public int edgeCount() {
        return size();
    }

    public int nodeCount() {
        return size() + 1;
    }

    public int find(Path path) {
        return find(path, 0);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Path)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Path other = (Path) o;
        if (size() != other.size()) {
            return false;
        }
        if (headNode() != other.headNode()) {
            return false;
        }
        Iterator<Path> myIter = steps().iterator();
        Iterator<Path> otherIter = other.steps().iterator();
        while (myIter.hasNext()) {
            if (myIter.next().tailEdge() != otherIter.next().tailEdge()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash += 37 * headNode().hashCode();
        for (Path p : steps()) {
            hash += 31 * p.tailEdge().hashCode();
        }
        return hash;
    }

    public int find(Path path, int from) {
        Args.notNull(path);
        List<Path> steps = steps().drainToList();
        Node head = headNode();
        out:
        for (int i = from,  len = size() - path.size() + 1; i < len; i++) {
            if (i > 0) {
                head = steps.get(i - 1).tailNode(); //update head
            }
            if (head == path.headNode()) {
                List<Path> followingSteps = steps.subList(i, i + path.size());
                int j = 0;
                for (Path step : path.steps()) {
                    if (!followingSteps.get(j).equals(step)) { //no match, step forward
                        continue out;
                    }
                    j++;
                }
                return i; //match found!
            }
        }
        return -1;
    }

    public Path replaceFirst(Path subpath, Path replacement) {
        int index = find(subpath);
        if (index != -1) {
            return replace(index, index + subpath.size(), replacement);
        }
        return this;
    }

    public Path replaceAll(Path subpath, Path replacement) {
        Args.notNull(subpath, replacement);
        Path path = this;
        int index = 0;
        while (true) {
            index = path.find(subpath, index);
            if (index == -1) {
                break;
            }
            path = path.replace(index, index + subpath.size(), replacement);
            index += replacement.size() + 1;
        }
        return path;
    }

    public Path[] split(int position) {
        Path[] paths = new Path[2];
        paths[0] = headPath(position);
        paths[1] = tailPath(size() - position);
        return paths;
    }

    /** Assumes a non-null replacement. For information about which conditions are checked here, see Path.replaceFirst(Path, Path)
     *
     *  @see gr.forth.ics.graph.path.Path#replaceFirst(Path, Path)
     */
    protected final void chechReplacementPreconditions(int start, int end, Path replacement) {
        if (start != 0) {
            Args.isTrue("Replacement does not start at the same node as the replaced part",
                    getNode(start) == replacement.headNode());
        }
        if (end != nodeCount()) {
            Args.isTrue("Replacement does not end at the same node as the replaced part",
                    getNode(end) == replacement.tailNode());
        }
    }

    public Path append(Path other) {
        return DefaultPath.newPath(this, other);
    }

    public Path replace(int start, int end, Path replacement) {
        Path headPath = headPath(start);
        Path tailPath = tailPath(edgeCount() - end);
        if (start == 0) {
            return replacement.append(tailPath);
        } else if (end == size()) {
            return headPath.append(replacement);
        } else {
            return headPath.append(replacement).append(tailPath);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(headNode().toString());
        for (Path s : steps()) {
            boolean inverted = s.headEdge().n2() != s.tailNode();
            sb.append(inverted ? "<--" : "-->");
            sb.append(s.tailNode().toString());
        }
        sb.append(']');
        return sb.toString();
    }
}
