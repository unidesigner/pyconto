package gr.forth.ics.util;

import junit.framework.*;
import java.util.*;

public class ExtendedIterableTest extends TestCase {
    
    public ExtendedIterableTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ExtendedIterableTest.class);
        
        return suite;
    }
    
    public void testDrainTo() {
        List<String> list = Arrays.asList("1", "2", "3");
        List<String> list2 = ExtendedIterable.drainToList(list.iterator());
        assertEquals(list, list2);
    }
    
    public void testFilter() {
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4);
        Filter<Object> filter = new Filter<Object>() {
            public boolean accept(Object element) {
                int num = (Integer)element;
                return num % 2 == 0;
            }
        };
        ExtendedIterable<Integer> i = new ExtendedIterable<Integer>(list).filter(filter);
        assertEquals("[0, 2, 4]", i.drainToList().toString());
    }
    
    public void testToString() {
        assertEquals("[1, 2]", new ExtendedIterable<Integer>(Arrays.asList(1, 2)).toString());
    }
    
    public void testSize() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
        ExtendedIterable<Integer> i = new ExtendedIterable<Integer>(list);
        assertEquals(5, i.size());
        assertEquals(5, i.size());
        list.remove(0);
        assertEquals(4, i.size());
        list.clear();
        assertEquals(0, i.size());
    }
}
