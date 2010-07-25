package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import gr.forth.ics.util.CompoundIterator;
import gr.forth.ics.util.ExtendedIterable;
import java.util.*;
import gr.forth.ics.util.Args;

/**
 * A default Path implementation.
 *
 * @see gr.forth.ics.graph.path.Path
 */
class DefaultPath extends AbstractPath {
    private final Node head;
    private final Node tail;
    private final Path left;
    private final Path right;
    private final int size;

    private DefaultPath(Path left, Path right) {
        if (left.tailNode() != right.headNode()) {
            throw new IllegalArgumentException("Paths: " + left + ", " + right + " are not consequtive.");
        }
        this.left = left;
        this.right = right;
        this.head = left.headNode();
        this.tail = right.tailNode();
        this.size = left.size() + right.size();
    }

    public Node headNode() {
        return head;
    }

    public Node tailNode() {
        return tail;
    }

    public Edge headEdge() {
        if (left.edgeCount() > 0) {
            return left.headEdge();
        }
        return right.headEdge();
    }

    public Edge tailEdge() {
        if (right.edgeCount() == 0) {
            return left.tailEdge();
        }
        return right.tailEdge();
    }

    public ExtendedIterable<Path> steps() {
        return new ExtendedIterable<Path>(new Iterable<Path>() {
            public Iterator<Path> iterator() {
                return new CompoundIterator<Path>(
                        left.steps().iterator(),
                        right.steps().iterator());
            }
        });
    }

    public Path append(Path other) {
        return newPath(this, other);
    }

    public int size() {
        return size;
    }

    private int maskNegative(int index, int size) {
        if (index < 0) {
            index += size;
            if (index < 0) throw new IndexOutOfBoundsException("Illegal index: " + index
                    + ", valid range: [" + -size + "..." + size + "]");
        }
        return index;
    }

    public Node getNode(int index) {
        index = maskNegative(index, size + 1);
        if (index < left.nodeCount()) {
            return left.getNode(index);
        }
        return right.getNode(index - left.edgeCount());
    }

    public Edge getEdge(int index) {
        index = maskNegative(index, size);
        if (index < 0) {
            index = index % edgeCount();
            if (index < 0) index += edgeCount();
        }
        if (index < left.edgeCount()) {
            return left.getEdge(index);
        }
        return right.getEdge(index - left.edgeCount());
    }

    public Path headPath(int steps) {
        if (steps == edgeCount()) {
            return this;
        } else if (steps <= left.edgeCount()) {
            return left.headPath(steps);
        }
        return newPath(left, right.headPath(steps - left.edgeCount()));
    }

    public Path tailPath(int steps) {
        if (steps == edgeCount()) {
            return this;
        } else if (steps <= right.edgeCount()) {
            return right.tailPath(steps);
        }
        return newPath(left.tailPath(steps - right.edgeCount()), right);
    }

    public Path slice(int start, int end) {
        if (end <= left.edgeCount()) {
            return left.slice(start, end);
        } else if (start >= left.edgeCount()) {
            return right.slice(start - left.edgeCount(), end - left.edgeCount());
        } else {
            if (start == 0 && end == nodeCount()) {
                return this;
            }
            return newPath(
                    left.tailPath(left.edgeCount() - start),
                    right.headPath(end - left.edgeCount()));
        }
    }

    public Path reverse() {
        List<Path> list = steps().drainToList();
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i).reverse());
        }
        return Paths.newPath(list);
    }

    static Path newPath(Path left, Path right) {
        if (right.headNode() != left.tailNode()) {
            throw new IllegalArgumentException("Paths: " + left + " and " + right + " are not consequtive");
        }
        if (left.edgeCount() == 0) {
            return right;
        } else if (right.edgeCount() == 0) {
            return left;
        }
        return new DefaultPath(left, right);
    }
}
