package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import gr.forth.ics.util.ExtendedIterable;
import java.util.NoSuchElementException;

/**
 * An immutable path, representing a continuous trail from a node to another node, moving through
 * edges and nodes alternatively. Edges may be used opposite to their direction in a path. For example,
 * assume nodes {@code N1, N2, N3} and edges {@code [N1-->N2], [N3-->N2]}. These edges
 * may form a path with this structure: {@code [N1-->N2<--N3]}.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Path {
    /**
     * Returns the first node of this path.
     *
     * @return the first node of this path
     */
    Node headNode();
    
    /**
     * Returns the first edge of this path.
     *
     * @return the first edge of this path
     * @throws NoSuchElementException if this path has no edges
     */
    Edge headEdge();

    /**
     * Returns the number of the node-to-node steps that are included in this path.
     *
     * @return the number of the node-to-node steps that are included in this path
     */
    int size();
    
    /**
     * Returns the number of edges included in this path. This is equivalent to {@code size()}, since each
     * edge defines a node-to-node step.
     *
     * @see #size()
     * @return the number of edges included in this path
     */
    int edgeCount();
    
    /**
     * Returns the number of nodes included in this path. This is equivalent to {@code size() + 1}.
     *
     * @see #size()
     * @return the number of nodes included in this path
     */
    int nodeCount();
    
    /**
     * Returns the last node of this path.
     *
     * @return the last node of this path
     */
    Node tailNode();
    
    /**
     * Returns the last edge of this path. 
     *
     * @throws NoSuchElementException if this path has no edges
     * @return the last edge of this path
     */
    Edge tailEdge();

    /**
     * Returns true if no node in this path occurs more than once, false otherwise.
     *
     * @return true if no node in this path occurs more than once, false otherwise
     */
    boolean isHamilton();
    
    /**
     * Returns true if no edge in this path occurs more than once, false otherwise.
     *
     * @return true if no edge in this path occurs more than once, false otherwise
     */
    boolean isEuler();
    
    /**
     * Returns an iterable of all subpaths of with {@code size() == 1} of this path.
     *
     * @return an iterable of all the single-step subpaths of this path
     */
    ExtendedIterable<Path> steps();
    
    /**
     * Returns an iterable that iterates this path's nodes. The first node is the
     * {@code #headNode()} and the last is the {@code #tailNode()}.
     *
     * @return an iterable that iterates this path's nodes
     */
    ExtendedIterable<Node> nodes();
    
    /**
     * Returns an iterable that iterates this path's edges. The first edge (if exists) is
     * the {@code #headEdge()} and the last (if exists) is the {@code #tailEdge()}
     *
     * @return an iterable that iterates this path's edges
     */
    ExtendedIterable<Edge> edges();
    
    /**
     * Returns the specified node of this path.
     * Valid node indexes are {@code [-(nodeCount)..nodeCount - 1]}.
     * Zero index means the first node (the {@linkplain #headNode()}). Positive values
     * denote the path steps from the head needed to get to the requested node, while negatives
     * are translated to positives by adding nodeCount, i.e. -1 means the tail node, -2 the node before that, etc.
     * 
     * @param index the index of the path node to return
     * @return the node corresponding to the specified index
     */
    Node getNode(int index);
    
    /**
     * Returns the specified edge of this path.
     * Valid edge indexes are {@code [-(edgeCount)..edgeCount - 1]}.
     * Zero index means the first edge({@linkplain #headEdge()}). Positive values
     * denote the path steps from the head needed to get to the requested node, while negatives
     * are translated to positives by adding nodeCount, i.e. -1 means the last edge
     * ({@linkplain #headEdge()}), -2 the edge before that, etc.
     *
     * @param index the index of the path edge to return
     * @return the edge corresponding to the specified index
     */
    Edge getEdge(int index);
    
    /**
     * Returns a path that starts at {@code getNode(start)} and ends
     * at {@code getNode(end)}. So, the resulting path has exactly
     * {@code end - start} size. If {@code start == end}, then the resulting path consists of a single node.
     * Valid node indexes are {@code [0..nodeCount() - 1]}.
     *
     * @param start the node index from which the returned path starts
     * @param end the node index to which the returned path ends
     * @return a path that starts at {@code getNode(start)} and ends
     * at {@code getNode(end)}
     * @throws IllegalArgumentException when {@code start > end} or {@code start < 0}
     *  or {@code end >= nodeCount()}
     */
    Path slice(int start, int end);
    
    /**
     * Returns a path that starts at the start of this path, and continues for the specified number of steps along this path.
     * Valid step indexes are {@code [0... edgeCount() - 1]}.
     * 
     * @param steps the number of steps of this path that the returned path should have
     * @return a path with the specified number of steps from the head of this path
     * @throws IllegalArgumentException when specified {@code steps > size()}
     */
    Path headPath(int steps);

    /**
     * Returns a path that ends to the end of this path, and continues backwards for the specified number of steps along this path.
     * Valid step indexes are {@code [0... edgeCount() - 1]}.
     *
     * @param steps the number of steps of this path that the returned path should have
     * @return a path with the specified number of steps from the tail of this path
     * @throws IllegalArgumentException when specified {@code steps > size()}
     */
    Path tailPath(int steps);

    /**
     * Splits this path at a certain point and returns the two parts. This position denotes the index of the
     * {@code tailNode()} of the first path, and the {@code headNode()} of the second path.
     * Valid position indexes are {@code [0... nodeCount() - 1]}.
     *
     * @param position the position of the node ({@code getNode(position)}) that the first path
     *      will end to and the second path will begin from
     * @return an array with the two splitted paths
     * @throws IllegalArgumentException if {@code position} is not a valid node index
     */
    Path[] split(int position);
    
    /**
     * Returns a path that is produced by appending the given path at the tail of this path.
     * The two paths must be consecutive, that is,
     * {@code other.headNode() == this.tailNode()}.
     *
     * @return a path that is produced by appending the given path at the tail of this path
     * @throws IllegalArgumentException if {@code other.headNode() != this.tailNode()}
     */
    Path append(Path other);

    /**
     * Returns {@code true} if {@code headNode() == tailNode()}, that is, if the path starts and ends at the same node,
     * unless the path is a single node, when conventionally it is not considered a cycle.
     *
     * @return whether this path is a cycle
     */
    boolean isCycle();
    
    /**
     * Returns a path that is produced by exchanging the first occurence of the given subpath, in this path, with the given
     * replacement. The subpath and the replacement must start and end on the same nodes respectively. There is
     * an exception though: if the subpath lies at the start or at the end of this path, then the replacement needs
     * not start or end and the same node respectively. Thus, in this cases the replacement can be used to create a
     * path that either starts at a different node than this path, or ends in a different node.
     *
     * <p>If the subpath is not contained in this path, nothing happens.
     *
     * <p>Examples, assuming this path is {@code [1-->2-->3-->4]}:
     * <ul>
     * <li>if subpath is {@code [2-->3]}, then the replacement <strong>must</strong> start on {@code [2]}
     * and end on {@code [3]} (with whatever length)
     * <li>if subpath is {@code [1-->2]}, then the replacement path only needs to <strong>end</strong>
     * at {@code [2]}, for example this replacement is fine: {@code [A-->B-->C-->2]}, which would yield:
     * {@code [A-->B-->C-->2-->3-->4]}
     * <li>if subpath is {@code [3-->4]}, then the replacement path only needs to <strong>start</strong>
     * at {@code [3]}, for example this replacement if fine: {@code [3-->X-->Y--Z]}, which would yield:
     * {@code [1-->2-->3-->X-->Y-->Z]}
     * </ul>
     *
     * @return a path resulted from replacing the specified subpath with the replacement
     * @throws IllegalArgumentException if the replacement is illegal, in the sense that would produce
     * a non-continuous path
     */
    Path replaceFirst(Path subpath, Path replacement);
    
    /**
     * Returns a path by replacing the specified subpath (described as a slice) with a replacement.
     * Parameters {@code start} and {@code end} describe the head and tail node of the subpath to
     * be replaced. Valid node indexes are {@code [0... nodeCount() - 1]}. Applying the replacement
     * must produce a continuous path, i.e. its head and tail node must match the head and tail node
     * of the subpath, unless either is the head or tail of this path. See {@link  #replaceFirst(Path, Path)}
     * for more details.
     *
     * @param start the index of the head node of the subpath
     * @param end the index of the end node of the subpath
     * @return a path by replacing the specified subpath with a replacement
     * @throws IllegalArgumentException if the indexes are invalid
     * @see #replaceFirst(Path, Path)
     */
    Path replace(int start, int end, Path replacement);
    
    /**
     * Returns a path that is produces by replacing every occurrence of the specified subpath in this path,
     * by the specified replacement. The replacement's head and tail nodes must match the head and tail
     * nodes of the subpath, respectively.
     *
     * @param subpath the subpath occurrences of which to replace with another path
     * @param replacement the path with which to replace all occurrences of {@code subpath}
     * @return the resulting path
     * @see #replaceFirst(Path, Path)
     */
    Path replaceAll(Path subpath, Path replacement);
    
    /**
     * Returns whether the specified path occurs in this path. This is equivalent to {@code find(path) != -1}.
     *
     * @param subpath the subpath to search in this path
     * @return whether the specified path occurs in this path
     */
    boolean contains(Path subpath);
    
    /**
     * Searches for the given subpath in this path. If a match is found, the index of its first
     * node in this path is returned. If no match is found, {@code -1} is returned.
     *
     * @param subpath the subpath to search in this path
     * @return the index of the node where a match starts, or {@code -1} is no match was found
     */
    int find(Path subpath);
    
    /**
     * Searches for the given subpath in this path, starting at the specified node index and continuing
     * towards the tail. If a match is found, the index of its first
     * node in this path is returned. If no match is found, {@code -1} is returned.
     *
     * @param path the subpath to search in this path
     * @param from the index of the node to start the search, and continue towards the tail
     * @return the index of the node where a match starts, or {@code -1} is no match was found
     */
    int find(Path path, int from);
    
    /**
     * Returns a path that is the reverse of this path.
     *
     * @return a path that is the reverse of this path
     */
    Path reverse();
}
