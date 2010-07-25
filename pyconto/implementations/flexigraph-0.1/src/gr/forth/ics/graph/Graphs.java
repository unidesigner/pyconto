package gr.forth.ics.graph;

import gr.forth.ics.graph.event.EmptyGraphListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.event.NodeListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import gr.forth.ics.util.Args;
import java.util.LinkedList;

public class Graphs {
    public static Set<Node> collectNodes(InspectableGraph graph, Node start, Direction direction) {
        return collectNodes(graph, start, direction, new HashSet<Node>());
    }
    
    public static Set<Node> collectNodes(InspectableGraph graph, Node start, Direction direction, Set<Node> set) {
        Args.notNull(graph, "graph");
        Args.notNull(start, "node");
        Args.notNull(direction, "direction");
        Args.notNull(set, "set");
        
        LinkedList<Node> stack = new LinkedList<Node>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Node current = stack.removeLast();
            for (Node next : graph.adjacentNodes(current, direction)) {
                boolean changed = set.add(next);
                if (changed) {
                    stack.addLast(next);
                }
            }
        }
        return set;
    }
    
    public static Appendable printPretty(InspectableGraph g) {
        return printPretty(g, new StringBuilder());
    }
    
    public static Appendable printPretty(InspectableGraph g, Appendable appendable) {
        Args.notNull("Appendable", appendable);
        try {
            if (g == null) {
                appendable.append("null");
                return appendable;
            }
            appendable.append("Nodes (count = ").append("" + g.nodeCount()).append("):\n");
            for (Node n : g.nodes()) {
                appendable.append(n.toString()).append('\n');
            }
            appendable.append("\nEdges (count = ").append("" + g.edgeCount()).append("):\n");
            for (Edge e : g.edges()) {
                appendable.append(e.toString()).append('\n');
            }
            return appendable;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Appendable printCompact(InspectableGraph g) {
        return printCompact(g, new StringBuilder());
    }
    
    public static Appendable printCompact(InspectableGraph g, Appendable appendable) {
        Args.notNull("Appendable", appendable);
        try {
            appendable.append("[N={");
            int pos = g.nodeCount();
            for (Node n : g.nodes()) {
                appendable.append(n.toString());
                if (pos > 1) {
                    appendable.append(", ");
                }
                pos--;
            }
            appendable.append("}, E={");
            pos = g.edgeCount();
            for (Edge e : g.edges()) {
                appendable.append(e.toString());
                if (pos > 1) {
                    appendable.append(", ");
                }
                pos--;
            }
            appendable.append("}]");
            return appendable;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static InspectableGraph undirected(InspectableGraph graph) {
        Args.notNull(graph);
        if (graph instanceof UndirectedInspectableGraph) {
            return graph;
        }
        return new UndirectedInspectableGraph(graph);
    }
    
    public static InspectableGraph inverted(InspectableGraph graph) {
        Args.notNull(graph);
        if (graph instanceof InvertedInspectableGraph) {
            ((InvertedInspectableGraph)graph).getDelegateGraph();
        }
        return new InvertedInspectableGraph(graph);
    }
    
    /**
     * Returns the max node degree found in the specified graph, with the
     * given direction.
     * @return the max degree, or zero if the graph is empty
     */
    public static int maxDegree(InspectableGraph g, Direction direction) {
        Args.notNull(g, direction);
        int max = 0;
        for (Node n : g.nodes()) {
            int d = g.degree(n, direction);
            if (max < d) {
                max = d;
            }
        }
        return max;
    }
    
    /**
     * Returns the max node degree found in the specified graph. Equivalent
     * to {@link #maxDegree(InspectableGraph, Direction)
     * maxDegree(g, Direction.EITHER)}.
     * @return the max degree, or zero if the graph is empty
     */
    public static int maxDegree(InspectableGraph g) {
        return maxDegree(g, Direction.EITHER);
    }
    
    /**
     * Returns the min node degree found in the specified graph, with the
     * given direction.
     * @return the min degree, or Integer.MAX_VALUE if the graph is empty
     */
    public static int minDegree(InspectableGraph g, Direction direction) {
        Args.notNull(g, direction);
        int min = Integer.MAX_VALUE;
        for (Node n : g.nodes()) {
            int d = g.degree(n, direction);
            if (min > d) {
                min = d;
            }
        }
        return min;
    }
    
    /**
     * Returns the min node degree found in the specified graph. Equivalent
     * to {@link #minDegree(InspectableGraph, Direction)
     * maxDegree(g, Direction.EITHER)}.
     * @return the max degree, or Integer.MAX_VALUE if the graph is empty
     */
    public static int minDegree(InspectableGraph g) {
        return minDegree(g, Direction.EITHER);
    }
    
    public static InspectableGraph union(InspectableGraph g1, InspectableGraph g2) {
        Args.notNull(g1, g2);
        SecondaryGraph sg = new SecondaryGraph(g1);
        sg.adoptGraph(g2);
        DualListener l = new UnionListener(sg);
        l.setGraphs(g1, g2);
        g1.addGraphListener(l);
        g2.addGraphListener(l);
        return sg;
    }
    
    public static InspectableGraph intersection(InspectableGraph g1, InspectableGraph g2) {
        Args.notNull(g1, g2);
        Args.notNull(g1, g2);
        SecondaryGraph sg = new SecondaryGraph(g1);
        sg.retainGraph(g2);
        DualListener l = new IntersectionListener(sg);
        l.setGraphs(g1, g2);
        g1.addGraphListener(l);
        g2.addGraphListener(l);
        return sg;
    }
    
    public static InspectableGraph subtraction(InspectableGraph g1, InspectableGraph g2) {
        Args.notNull(g1, g2);
        SecondaryGraph sg = new SecondaryGraph(g1);
        sg.removeGraph(g2);
        DualListener l = new SubtractionListener(sg);
        l.setGraphs(g1, g2);
        g1.addGraphListener(l);
        g2.addGraphListener(l);
        return sg;
    }
    
    public static InspectableGraph xor(InspectableGraph g1, InspectableGraph g2) {
        Args.notNull(g1, g2);
        int size1 = g1.nodeCount() + g1.edgeCount();
        int size2 = g2.nodeCount() + g2.edgeCount();
        InspectableGraph bigGraph = size1 > size2 ? g1 : g2;
        InspectableGraph smallGraph = bigGraph == g1 ? g2 : g1;
        SecondaryGraph sg = new SecondaryGraph(bigGraph);
        for (Node n : smallGraph.nodes()) {
            if (bigGraph.containsNode(n)) {
                sg.removeNode(n);
            }
        }
        for (Edge e : smallGraph.edges()) {
            if (bigGraph.containsEdge(e)) {
                sg.removeEdge(e);
            }
        }
        DualListener l = new XorListener(sg);
        l.setGraphs(g1, g2);
        g1.addGraphListener(l);
        g2.addGraphListener(l);
        return sg;
    }
    
    /**
     * Returns true for graphs g1 and g2 have identical elements. More specifically, returns true
     * if and only if:<BR>
     * <ul>
     * <li> g1 and g2 have the same node/edge count</li>
     * <li> each node of g1 is contained in g2</li>
     * <li> each edge of g1 is contained in g2</li>
     * </ul>
     *<P>
     * Since a node or an edge cannot be contained multiple times in a graph, this sufficies.
     */
    public static boolean equalGraphs(InspectableGraph g1, InspectableGraph g2) {
        Args.notNull(g1, g2);
        if (g1 == g2) {
            return true;
        }
        if (g1.nodeCount() != g2.nodeCount()) {
            return false;
        }
        if (g1.edgeCount() != g2.edgeCount()) {
            return false;
        }
        for (Node n : g1.nodes()) {
            if (!g2.containsNode(n)) {
                return false;
            }
        }
        for (Edge e : g1.edges()) {
            if (!g2.containsEdge(e)) {
                return false;
            }
        }
        return true;
    }
    
    private static abstract class DualListener extends EmptyGraphListener {
        final SecondaryGraph g;
        InspectableGraph g1, g2;
        DualListener(SecondaryGraph g) {
            this.g = g;
        }
        void setGraphs(InspectableGraph g1, InspectableGraph g2) {
            this.g1 = g1;
            this.g2 = g2;
        }
    }
    
    private static class IntersectionListener extends DualListener {
        IntersectionListener(SecondaryGraph g) { super(g); }
        
        @Override public void nodeAdded(GraphEvent e) {
            Node n = e.getNode();
            if (!g1.containsNode(n) || !g2.containsNode(n)) {
                return;
            }
            g.adoptNode(n);
        }
        
        @Override public void nodeRemoved(GraphEvent e) {
            g.removeNode(e.getNode());
        }
        
        @Override public void edgeAdded(GraphEvent ev) {
            Edge e = ev.getEdge();
            if (!g1.containsEdge(e) || !g2.containsEdge(e)) {
                return;
            }
            g.adoptEdge(e);
        }
        
        @Override public void edgeRemoved(GraphEvent ev) {
            g.removeEdge(ev.getEdge());
        }
    }
    
    private static class UnionListener extends DualListener {
        UnionListener(SecondaryGraph g) { super(g); }
        
        @Override public void nodeAdded(GraphEvent e) {
            g.adoptNode(e.getNode());
        }
        
        @Override public void nodeRemoved(GraphEvent e) {
            Node n = e.getNode();
            int count = 0;
            if (g1.containsNode(n)) {
                count++;
            }
            if (g2.containsNode(n)) {
                count++;
            }
            if (count == 1) {
                return;
            }
            g.removeNode(n);
            return;
        }
        
        @Override public void edgeAdded(GraphEvent e) {
            g.adoptEdge(e.getEdge());
        }
        
        @Override public void edgeRemoved(GraphEvent ev) {
            Edge e = ev.getEdge();
            int count = 0;
            if (g1.containsEdge(e)) {
                count++;
            }
            if (g2.containsEdge(e)) {
                count++;
            }
            if (count == 1) {
                return;
            }
            g.removeEdge(e);
        }
    }
    
    private static class SubtractionListener extends DualListener {
        SubtractionListener(SecondaryGraph g) { super(g); }
        
        @Override public void nodeAdded(GraphEvent e) {
            Node n = e.getNode();
            InspectableGraph source = e.getSource();
            if (source == g2) {
                g.removeNode(n);
            } else if (!g2.containsNode(n)) {
                g.adoptNode(n);
            }
        }
        
        @Override public void nodeRemoved(GraphEvent e) {
            Node n = e.getNode();
            InspectableGraph source = e.getSource();
            if (source == g1) {
                g.removeNode(n);
            } else if (g1.containsNode(n)) {
                g.adoptNode(n);
                g.adoptEdges(g1.edges(n));
            }
        }
        
        @Override public void edgeAdded(GraphEvent ev) {
            Edge e = ev.getEdge();
            InspectableGraph source = ev.getSource();
            if (source == g2) {
                g.removeEdge(e);
            } else if (!g2.containsEdge(e)) {
                g.adoptEdge(e);
            }
        }
        
        @Override public void edgeRemoved(GraphEvent ev) {
            Edge e = ev.getEdge();
            InspectableGraph source = ev.getSource();
            if (source == g1) {
                g.removeEdge(e);
            } else if (g1.containsEdge(e)) {
                g.adoptEdge(e);
            }
        }
    }
    
    private static class XorListener extends DualListener {
        XorListener(SecondaryGraph g) { super(g); }
        
        @Override public void nodeAdded(GraphEvent e) {
            Node n = e.getNode();
            int count = 0;
            if (g1.containsNode(n)) {
                count++;
            }
            if (g2.containsNode(n)) {
                count++;
            }
            if (count == 2) {
                g.removeNode(n);
                return;
            }
            g.adoptNode(n);
        }
        
        @Override public void nodeRemoved(GraphEvent e) {
            Node n = e.getNode();
            if (g.containsNode(n)) {
                g.removeNode(n);
            } else {
                g.adoptNode(n);
                InspectableGraph other = e.getSource() == g1 ? g2 : g1;
                for (Edge edge : other.edges(n)) {
                    if (g.containsNode(edge.opposite(n))) {
                        g.adoptEdge(edge);
                    }
                }
            }
        }
        
        @Override public void edgeAdded(GraphEvent ev) {
            Edge e = ev.getEdge();
            int count = 0;
            if (g1.containsEdge(e)) {
                count++;
            }
            if (g2.containsEdge(e)) {
                count++;
            }
            if (count == 2) {
                g.removeEdge(e);
                return;
            }
            if (g.containsNode(e.n1()) && (g.containsNode(e.n2()))) {
                g.adoptEdge(e);
            }
        }
        
        @Override public void edgeRemoved(GraphEvent ev) {
            Edge e = ev.getEdge();
            if (g.containsEdge(e)) {
                g.removeEdge(e);
            } else {
                if (g.containsNode(e.n1()) && (g.containsNode(e.n2()))) {
                    g.adoptEdge(e);
                }
            }
        }
    }
    
    public static NodeListener attachNodeNamer(InspectableGraph g) {
        NodeListener listener = new EmptyGraphListener() {
            int id = 0;
            
            @Override
            public void nodeAdded(GraphEvent e) {
                if (e.getEventType() == GraphEvent.Type.NODE_REINSERTED) {
                    return;
                }
                e.getNode().setValue(id++);
            }
        };
        g.addNodeListener(listener);
        return listener;
    }
}
