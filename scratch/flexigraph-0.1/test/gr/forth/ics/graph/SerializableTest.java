package gr.forth.ics.graph;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.*;

public abstract class SerializableTest extends TestCase {
    
    public SerializableTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        return suite;
    }
    
    protected abstract Graph createGraph();
    
    public void testSerialization() throws Exception {
        Graph g = createGraph();
        Node n1 = g.newNode("1");
        final String s = "2";
        n1.putWeakly(s, "3");
        Node n2 = g.newNode("2");
        n2.put("4", "5");
        Edge e = g.newEdge(n1, n2);
        Graph g2 = read(write(g));
        
        assertEquals(g.nodeCount(), g2.nodeCount());
        assertEquals(g.edgeCount(), g2.edgeCount());
        Edge e2 = g2.anEdge();
        assertEquals(e2.n1().getValue(), n1.getValue());
        assertEquals(e2.n2().getValue(), n2.getValue());
        
        assertEquals(g.getNodeListeners().size(), g2.getNodeListeners().size());
        assertEquals(g.getEdgeListeners().size(), g2.getEdgeListeners().size());
        
        assertEquals("3", e2.n1().get("2"));
        assertEquals("5", e2.n2().get("4"));
    }
    
    private byte[] write(Graph g) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(g);
        out.flush();
        return bout.toByteArray();
    }
    
    private Graph read(byte[] bytes) throws Exception {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bin);
        return (Graph)in.readObject();
    }
}
