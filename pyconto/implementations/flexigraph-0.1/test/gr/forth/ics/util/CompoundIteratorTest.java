package gr.forth.ics.util;

import junit.framework.*;
import java.util.*;

public class CompoundIteratorTest extends TestCase {
    
    public CompoundIteratorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CompoundIteratorTest.class);
        return suite;
    }
    
    @SuppressWarnings("unchecked")
    public void test() {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList();
        List<Integer> list3 = Arrays.asList(4, 5, 6);
        List<Integer> list4 = Arrays.asList(7);
        List<Integer> list5 = Arrays.asList();
        List<Integer> list6 = Arrays.asList(8, 9);
        
        CompoundIterator<Integer> iterator = new CompoundIterator<Integer>(
                Arrays.asList(list1.iterator(), list2.iterator(), list3.iterator(), list4.iterator(), list5.iterator(), list6.iterator()));
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            assertEquals(i, iterator.next().intValue());
        }
    }
}
