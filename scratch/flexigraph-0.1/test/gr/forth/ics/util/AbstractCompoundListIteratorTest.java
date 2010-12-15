package gr.forth.ics.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import junit.framework.*;
import java.util.List;
import java.util.ListIterator;

public class AbstractCompoundListIteratorTest extends TestCase {
    
    public AbstractCompoundListIteratorTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(AbstractCompoundListIteratorTest.class);
        
        return suite;
    }
    
    public void test() {
        final List<Integer> list1 = Arrays.asList(1, 2, 3);
        final List<Integer> list2 = Arrays.asList(4, 5, 6);
        ListIterator<Integer> iterator = new AbstractCompoundListIterator<Integer>() {
            final ListIterator<ListIterator<Integer>> iteratorIterator;
            {
                final List<ListIterator<Integer>> iterators = new ArrayList<ListIterator<Integer>>();
                iterators.add(list1.listIterator());
                iterators.add(list2.listIterator());
                iteratorIterator = iterators.listIterator();
            }
            
            protected ListIterator<Integer> previousIterator() {
                return iteratorIterator.previous();
            }
            
            protected ListIterator<Integer> nextIterator() {
                return iteratorIterator.next();
            }
            
            protected boolean hasPreviousIterator() {
                return iteratorIterator.hasPrevious();
            }
            
            protected boolean hasNextIterator() {
                return iteratorIterator.hasNext();
            }
            
        };
        List<Integer> values = new LinkedList<Integer>();
        List<Integer> nextIndexes = new LinkedList<Integer>();
        List<Integer> prevIndexes = new LinkedList<Integer>();
        while (iterator.hasNext()) {
            nextIndexes.add(iterator.nextIndex());
            prevIndexes.add(iterator.previousIndex());
            values.add(iterator.next());
        }
        while (iterator.hasPrevious()) {
            nextIndexes.add(iterator.nextIndex());
            prevIndexes.add(iterator.previousIndex());
            values.add(iterator.previous());
        }
        assertEquals("[1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1]", values.toString());
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1]", nextIndexes.toString());
        assertEquals("[-1, 0, 1, 2, 3, 4, 5, 4, 3, 2, 1, 0]", prevIndexes.toString());
    }
}
