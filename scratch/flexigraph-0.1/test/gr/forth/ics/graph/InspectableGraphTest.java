package gr.forth.ics.graph;

import gr.forth.ics.graph.event.EdgeListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.event.NodeListener;
import junit.framework.*;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;

public abstract class InspectableGraphTest extends MockObjectTestCase {
    
    public InspectableGraphTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(BasicGraphTest.class);
        suite.addTestSuite(InspectableDecoratorTest.class);
        suite.addTestSuite(UnionTest.class);
        suite.addTestSuite(IntersectionTest.class);
        suite.addTestSuite(SubtractionTest.class);
        suite.addTestSuite(XorTest.class);
        return suite;
    }
    
    protected abstract InspectableGraph create();
    
    protected abstract Node achieveNodeInsertion(InspectableGraph g);
    
    protected abstract Node achieveNodeRemoval(InspectableGraph g);
    
    protected abstract Edge achieveEdgeInsertion(InspectableGraph g);
    
    protected abstract Edge achieveEdgeRemoval(InspectableGraph g);
    
    public void testNodeInsertion() {
        InspectableGraph g = create();
        Constraint eq = new EventSourceConstraint(g);
        Mock mock = mock(NodeListener.class);
        mock.expects(once()).method("preEvent").id("preEventID");
        mock.expects(once()).method("nodeToBeAdded").with(eq).after("preEventID").id("nodeToBeAddedID");
        mock.expects(once()).method("nodeAdded").with(eq).after("nodeToBeAddedID").id("nodeAddedID");
        mock.expects(once()).method("postEvent").after("nodeAddedID");
        NodeListener nl = (NodeListener)mock.proxy();
        g.addNodeListener(nl);
        Node n = achieveNodeInsertion(g);
        assertTrue(g.containsNode(n));
    }
    
    public void testNodeRemoval() {
        InspectableGraph g = create();
        achieveNodeInsertion(g);
        Constraint eq = new EventSourceConstraint(g);
        Mock mock = mock(NodeListener.class);
        mock.expects(once()).method("preEvent").id("preEventID");
        mock.expects(once()).method("nodeToBeRemoved").with(eq).after("preEventID").id("nodeToBeRemovedID");
        mock.expects(once()).method("nodeRemoved").with(eq).after("nodeToBeRemovedID").id("nodeRemovedID");
        mock.expects(once()).method("postEvent").after("nodeRemovedID");
        NodeListener nl = (NodeListener)mock.proxy();
        g.addNodeListener(nl);
        Node n = achieveNodeRemoval(g);
        assertFalse(g.containsNode(n));
    }
    
    public void testEdgeInsertion() {
        InspectableGraph g = create();
        achieveNodeInsertion(g);
        Constraint eq = new EventSourceConstraint(g);
        Mock mock = mock(EdgeListener.class);
        mock.expects(once()).method("preEvent").id("preEventID");
        mock.expects(once()).method("edgeToBeAdded").with(eq).after("preEventID").id("edgeToBeAddedID");
        mock.expects(once()).method("edgeAdded").with(eq).after("edgeToBeAddedID").id("edgeAddedID");
        mock.expects(once()).method("postEvent").after("edgeAddedID");
        EdgeListener el = (EdgeListener)mock.proxy();
        g.addEdgeListener(el);
        Edge e = achieveEdgeInsertion(g);
        assertTrue(g.containsEdge(e));
    }
    
    public void testEdgeRemoval() {
        InspectableGraph g = create();
        achieveNodeInsertion(g);
        achieveEdgeInsertion(g);
        Constraint eq = new EventSourceConstraint(g);
        Mock mock = mock(EdgeListener.class);
        mock.expects(once()).method("preEvent").id("preEventID");
        mock.expects(once()).method("edgeToBeRemoved").with(eq).after("preEventID").id("edgeToBeRemovedID");
        mock.expects(once()).method("edgeRemoved").with(eq).after("edgeToBeRemovedID").id("edgeRemovedID");
        mock.expects(once()).method("postEvent").after("edgeRemovedID");
        EdgeListener el = (EdgeListener)mock.proxy();
        g.addEdgeListener(el);
        Edge e = achieveEdgeRemoval(g);
        assertFalse(g.containsEdge(e));
    }
    
    private static class EventSourceConstraint implements Constraint {
        private final InspectableGraph g;
        EventSourceConstraint(InspectableGraph g) {
            this.g = g;
        }
        
        public boolean eval(Object o) {
            GraphEvent ge = (GraphEvent)o;
            return ge.getSource() == g;
        }
        
        public StringBuffer describeTo(StringBuffer sb) {
            sb.append("EventSource equals correct graph");
            return sb;
        }
    }
    
    public static class BasicGraphTest extends InspectableGraphTest {
        public BasicGraphTest(String testName) {
            super(testName);
        }
        
        protected InspectableGraph create() {
            return new PrimaryGraph();
        }
        
        protected Node achieveNodeRemoval(InspectableGraph g) {
            Node node = g.aNode();
            ((Graph)g).removeNode(node);
            return node;
        }
        
        protected Node achieveNodeInsertion(InspectableGraph g) {
            return ((Graph)g).newNode();
        }
        
        protected Edge achieveEdgeRemoval(InspectableGraph g) {
            Edge edge = g.anEdge();
            ((Graph)g).removeEdge(edge);
            return edge;
        }
        
        protected Edge achieveEdgeInsertion(InspectableGraph g) {
            return ((Graph)g).newEdge(g.aNode(), g.aNode());
        }
    }
    
    public static class InspectableDecoratorTest extends InspectableGraphTest {
        public InspectableDecoratorTest(String testName) {
            super(testName);
        }
        
        protected InspectableGraph create() {
            return new InspectableGraphForwarder(new PrimaryGraph());
        }
        
        protected Node achieveNodeRemoval(InspectableGraph g) {
            Graph graph = getGraph(g);
            Node node = graph.aNode();
            graph.removeNode(node);
            return node;
        }
        
        protected Node achieveNodeInsertion(InspectableGraph g) {
            return getGraph(g).newNode();
        }
        
        protected Edge achieveEdgeRemoval(InspectableGraph g) {
            Graph graph = getGraph(g);
            Edge edge = graph.anEdge();
            graph.removeEdge(edge);
            return edge;
        }
        
        protected Edge achieveEdgeInsertion(InspectableGraph g) {
            Graph graph = getGraph(g);
            return graph.newEdge(graph.aNode(), graph.aNode());
        }
        
        private Graph getGraph(InspectableGraph g) {
            InspectableGraphForwarder igd = (InspectableGraphForwarder)g;
            return (Graph)igd.getDelegateGraph();
        }
    }
    
    
    private static abstract class BooleanGraphTest extends InspectableGraphTest {
        public BooleanGraphTest(String testName) {
            super(testName);
        }
        
        protected final Object G1 = new Object();
        protected final Object G2 = new Object();
        
        protected final void register(InspectableGraph g, Graph g1, Graph g2) {
            g.tuple().put(G1, g1);
            g.tuple().put(G2, g2);
        }
        
        protected Graph getG1(InspectableGraph g) {
            return g.tuple().getGraph(G1);
        }
        
        protected Graph getG2(InspectableGraph g) {
            return g.tuple().getGraph(G2);
        }
    }
    
    public static class UnionTest extends BooleanGraphTest {
        public UnionTest(String testName) {
            super(testName);
        }
        
        protected InspectableGraph create() {
            Graph g1 = new PrimaryGraph();
            Graph g2 = new PrimaryGraph();
            InspectableGraph g = Graphs.union(g1, g2);
            register(g, g1, g2);
            return g;
        }
        
        protected Node achieveNodeRemoval(InspectableGraph g) {
            Node n = g.aNode();
            getG1(g).removeNode(n);
            getG2(g).removeNode(n);
            return n;
        }
        
        protected Node achieveNodeInsertion(InspectableGraph g) {
            Node n = getG1(g).newNode();
            return n;
        }
        
        protected Edge achieveEdgeRemoval(InspectableGraph g) {
            Edge e = g.anEdge();
            getG1(g).removeEdge(e);
            getG2(g).removeEdge(e);
            return e;
        }
        
        protected Edge achieveEdgeInsertion(InspectableGraph g) {
            Node n = g.aNode();
            Graph graph = getG1(g).containsNode(n) ? getG1(g) : getG2(g);
            return graph.newEdge(n, n);
        }
    }
    
    public static class IntersectionTest extends BooleanGraphTest {
        public IntersectionTest(String testName) {
            super(testName);
        }
        
        protected InspectableGraph create() {
            Graph g1 = new SecondaryGraph();
            Graph g2 = new SecondaryGraph();
            InspectableGraph g = Graphs.intersection(g1, g2);
            register(g, g1, g2);
            return g;
        }
        
        protected Node achieveNodeRemoval(InspectableGraph g) {
            Node n = g.aNode();
            getG1(g).removeNode(n);
            getG2(g).removeNode(n);
            return n;
        }
        
        protected Node achieveNodeInsertion(InspectableGraph g) {
            Node n = getG2(g).newNode();
            ((SecondaryGraph)getG1(g)).adoptNode(n);
            return n;
        }
        
        protected Edge achieveEdgeRemoval(InspectableGraph g) {
            Edge e = g.anEdge();
            getG1(g).removeEdge(e);
            return e;
        }
        
        protected Edge achieveEdgeInsertion(InspectableGraph g) {
            Node n = g.aNode();
            SecondaryGraph g1 = (SecondaryGraph)getG1(g);
            SecondaryGraph g2 = (SecondaryGraph)getG2(g);
            Graph graph = g1.containsNode(n) ? g1 : g2;
            Edge e = graph.newEdge(n, n);
            g1.adoptEdge(e);
            g2.adoptEdge(e);
            return e;
        }
    }
    
    public static class SubtractionTest extends BooleanGraphTest {
        public SubtractionTest(String testName) {
            super(testName);
        }
        
        protected InspectableGraph create() {
            Graph g1 = new SecondaryGraph();
            Graph g2 = new SecondaryGraph();
            InspectableGraph g = Graphs.subtraction(g1, g2);
            register(g, g1, g2);
            return g;
        }
        
        protected Node achieveNodeRemoval(InspectableGraph g) {
            Node n = g.aNode();
            getG1(g).removeNode(n);
            return n;
        }
        
        protected Node achieveNodeInsertion(InspectableGraph g) {
            Node n = getG1(g).newNode();
            return n;
        }
        
        protected Edge achieveEdgeRemoval(InspectableGraph g) {
            Edge e = g.anEdge();
            getG1(g).removeEdge(e);
            return e;
        }
        
        protected Edge achieveEdgeInsertion(InspectableGraph g) {
            Node n = g.aNode();
            return getG1(g).newEdge(n, n);
        }
    }
    
    public static class XorTest extends BooleanGraphTest {
        public XorTest(String testName) {
            super(testName);
        }
        
        protected InspectableGraph create() {
            Graph g1 = new SecondaryGraph();
            Graph g2 = new SecondaryGraph();
            InspectableGraph g = Graphs.xor(g1, g2);
            register(g, g1, g2);
            return g;
        }
        
        protected Node achieveNodeRemoval(InspectableGraph g) {
            Node n = g.aNode();
            getG1(g).removeNode(n);
            return n;
        }
        
        protected Node achieveNodeInsertion(InspectableGraph g) {
            Node n = getG1(g).newNode();
            return n;
        }
        
        protected Edge achieveEdgeRemoval(InspectableGraph g) {
            Edge e = g.anEdge();
            getG1(g).removeEdge(e);
            return e;
        }
        
        protected Edge achieveEdgeInsertion(InspectableGraph g) {
            Node n = g.aNode();
            return getG1(g).newEdge(n, n);
        }
    }
}
