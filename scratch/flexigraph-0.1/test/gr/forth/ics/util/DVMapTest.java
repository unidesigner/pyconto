package gr.forth.ics.util;

import junit.framework.*;
import java.util.*;

public class DVMapTest extends TestCase {
    
    public DVMapTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DVMapTest.class);
        
        return suite;
    }
    
    
    public void testSimple() {
        DVMap<String, Collection<Integer>> map = DVMap.newHashMapWithLinkedLists();
        assertFalse(map.containsKey("hello"));
        Collection<Integer> collection = map.get("hello");
        assertTrue(map.containsKey("hello"));
        assertNotNull(collection);
        collection.add(5);
        map.remove("hello");
        assertFalse(map.containsKey("hello"));
        collection = map.get("hello");
        assertNotNull(collection);
        assertTrue(collection.isEmpty());
    }
}
