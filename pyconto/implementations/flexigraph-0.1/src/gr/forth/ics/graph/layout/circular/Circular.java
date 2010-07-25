package gr.forth.ics.graph.layout.circular;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Dfs;
import gr.forth.ics.graph.SecondaryGraph;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.algo.DegreeSorter;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.path.Traverser;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 * @author Alkis Symeonidis (older version)
 */
public class Circular {
    
    private Node startDFS = null;
    
    public void setStartDFS(Node node){
        this.startDFS = node;
    }
    
    public Circular() {
    }
    
    public CircularOrder execute(InspectableGraph graph) {
        Graph copy = new SecondaryGraph(graph);
        DegreeSorter bucketSort = new DegreeSorter(copy);
//        Node lowestNode = bucketSort.getNodes(bucketSort.getMinDegree()).iterator().next();
        Node lowestNode = null;
        if(startDFS == null){
            lowestNode = bucketSort.getNodes(bucketSort.getMinDegree()).iterator().next();
//            lowestNode = copy.aNode();
        }
        else{
            lowestNode = startDFS;
        }
        
        Collection<Edge> edgesToBeRemoved = new LinkedList<Edge>();
        while (copy.nodeCount() >= 3) {
            //choose lowest degree node
            Node selected = bucketSort.getNodes(bucketSort.getMinDegree()).iterator().next();
            Iterator<Node> neighbors = copy.adjacentNodes(selected).iterator();
            if (neighbors.hasNext()) {
                Node n1 = neighbors.next();
                while (neighbors.hasNext()) {
                    Node n2 = neighbors.next();
                    Edge e;
                    if (!copy.areAdjacent(n1, n2)) {
                        e = copy.newEdge(n1, n2);
                    } else {
                        e = copy.anEdge(n1, n2);
                    }
                    edgesToBeRemoved.add(e);
                    n1 = n2;
                }
            }
            copy.removeNode(selected);
        }
        Graph tree = new SecondaryGraph(graph);
        
        // Perform dfs find long path and then put all neighbors
        // Get a dfs tree from the graph
        Dfs dfs = new Dfs(tree, lowestNode, Direction.EITHER);
        dfs.execute();
        //now actually produce a tree through a dfs
        for(Edge e : graph.edges()){
            if(!dfs.isTreeEdge(e))
                tree.removeEdge(e);
        }
        // Compute the longest path
        Path longestPath = computeLongestPath(tree, lowestNode);
        //Get all unplaced vertices
        LinkedList<Node> unplaced = new LinkedList<Node>();
        final Object PARENT = recordPrevInPath(longestPath);
        for (Node n : graph.nodes()) {
            if (!n.has(PARENT)) {
                unplaced.add(n);
            }
        }
        //Place them next to 2-1-0 neighbors
        while (!unplaced.isEmpty()) {
            boolean placed = false;
            Node toBePlaced = unplaced.removeFirst();
            Iterator<Node> neighbors = graph.adjacentNodes(toBePlaced).iterator();
            Node neighbor = null;
            Node nextToNeighbor = null;
            while (neighbors.hasNext()) {
                neighbor = neighbors.next();
                nextToNeighbor = neighbor.getNode(PARENT);
                if (nextToNeighbor != null) {
                    // Place between neighbors if possible
                    if (graph.areAdjacent(toBePlaced, nextToNeighbor)) {
                        neighbor.putWeakly(PARENT, toBePlaced);
                        toBePlaced.putWeakly(PARENT, nextToNeighbor);
                        placed = true;
                        break;
                    }
                }
            }
            if (!placed) {
//                Node n = graph.isIsolated(toBePlaced) ? longestPath.root() : graph.aNode(toBePlaced);
                Node n = (nextToNeighbor == null) ? longestPath.headNode() : neighbor;
                // Insert the unplaced vertex in the list
                neighbor = n.getNode(PARENT);
                n.putWeakly(PARENT, toBePlaced);
                toBePlaced.putWeakly(PARENT, neighbor);
            }
        }
        // Now the order is computed in the hashtable, I just copy it on a list for efficiency
        LinkedList<Node> order = new LinkedList<Node>();
        
        Node farthest = longestPath.headNode();
        order.add(farthest);
        Node tmp = farthest.getNode(PARENT);
        int y = 0;
        while (tmp != farthest) {
            order.add(tmp);
//            y++;
//            if(!graph.areAdjacent(farthest,tmp) && y == 1 && graph.nodeCount() == 3){
//                //TODO: not tested
//                tmp = tmp.getNode(PARENT);
//                order.add(y++,tmp);
//            }
            tmp = tmp.getNode(PARENT);
        }
        return new CircularOrder(order);
    }
    
    private Object recordPrevInPath(Path longestPath) {
        Object KEY = new Object();
        Node last = null;
        for (Node node : longestPath.nodes()) {
            node.putWeakly(KEY, last);
            last = node;
        }
        longestPath.headNode().putWeakly(KEY, longestPath.tailNode());
        return KEY;
    }
    
    private Path computeLongestPath(InspectableGraph graph, Node start) {
        Traverser traverser = Traverser.newDfs().notRepeatingEdges().build();
        Path longestPath = start.asPath();
        for (Path path : traverser.traverse(graph, start, Direction.EITHER)) {
            if (longestPath.size() < path.size()) {
                longestPath = path;
            }
        }
        for (Path path : traverser.traverse(graph, longestPath.tailNode(), Direction.EITHER)) {
            if (longestPath.size() < path.size()) {
                longestPath = path;
            }
        }
        return longestPath;
    }
}