package gr.forth.ics.util;

import gr.forth.ics.graph.event.EdgeListener;
import gr.forth.ics.graph.event.EmptyGraphListener;
import java.util.Arrays;
import junit.framework.*;

public class EventSupportTest extends TestCase {
    
    public EventSupportTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(EventSupportTest.class);
        
        return suite;
    }
    
    public void testRemoveListener() {
        EventSupport<EdgeListener> support = new EventSupport<EdgeListener>();
        EdgeListener listener = new EmptyGraphListener() { };
        support.removeListener(listener); //should be a no-op
        support.addListener(listener);
        
        assertEquals(Arrays.asList(listener), support.getListeners());
        support.removeListener(listener);
        assertTrue(support.getListeners().isEmpty());
        
        support.removeListener(listener); //no-op, as previously
    }
    
}
