package gr.forth.ics.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import junit.framework.*;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class FilteringListIteratorTest extends TestCase {
    
    public FilteringListIteratorTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FilteringListIteratorTest.class);
        
        return suite;
    }
    
    public void testIterator() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        ListIterator<Integer> i = new FilteringListIterator<Integer>(list.listIterator(), new Filter<Integer>() {
            public boolean accept(Integer i) {
                return i % 2 == 1;
            }
        });
        assertEquals(0, i.nextIndex());
        assertEquals(-1, i.previousIndex());
        assertTrue(i.hasNext());
        
        assertEquals(new Integer(1), i.next());
        checkNextPrevious(i, true, true);
        assertEquals(1, i.nextIndex());
        assertEquals(0, i.previousIndex());
        
        assertEquals(new Integer(3), i.next());
        checkNextPrevious(i, false, true);
        assertEquals(2, i.nextIndex());
        assertEquals(1, i.previousIndex());
        
        assertEquals(new Integer(3), i.previous());
        checkNextPrevious(i, true, true);
        assertEquals(1, i.nextIndex());
        assertEquals(0, i.previousIndex());
        
        assertEquals(new Integer(3), i.next());
        checkNextPrevious(i, false, true);
        assertEquals(2, i.nextIndex());
        assertEquals(1, i.previousIndex());
        
        assertEquals(new Integer(3), i.previous());
        checkNextPrevious(i, true, true);
        assertEquals(1, i.nextIndex());
        assertEquals(0, i.previousIndex());
        
        assertEquals(new Integer(1), i.previous());
        checkNextPrevious(i, true, false);
        assertEquals(0, i.nextIndex());
        assertEquals(-1, i.previousIndex());
        
        try {
            i.previous();
            fail();
        } catch (NoSuchElementException e) {
            //ok
        }
    }
    
    private void checkNextPrevious(ListIterator<?> i, boolean hasNext, boolean hasPrevious) {
        assertEquals(hasPrevious, i.hasPrevious());
        assertEquals(hasNext, i.hasNext());
        assertEquals(hasNext, i.hasNext());
        assertEquals(hasPrevious, i.hasPrevious());
    }

    public void testRandomized() {
        List<Action> actions = Arrays.asList(
                new NextAction()
                ,new HasNextAction()
                ,new PreviousAction()
                ,new HasPreviousAction()
                ,new NextIndexAction()
                ,new PreviousIndexAction()
                ,new RemoveAction()
                ,new AddAction()
                ,new SetAction()
                );

        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        Filter<Integer> filter = new Filter<Integer>() {
            public boolean accept(Integer element) {
                return element % 2 == 0;
            }
        };
        List<Integer> filteredList = copyFiltered(list, filter);
        ListIterator<Integer> filteringIterator = new FilteringListIterator<Integer>(list.listIterator(), filter);
        ListIterator<Integer> referenceIterator = filteredList.listIterator();

        Random random = new Random();
        for (int i = 0; i < 20000; i++) {
            Action next = actions.get(random.nextInt(actions.size()));

            Object o1 = next.execute(filteringIterator);
            Object o2 = next.execute(referenceIterator);

            if (!Action.equals(o1, o2) || !copyFiltered(list, filter).equals(filteredList)) {
                fail(copyFiltered(list, filter) + "\n" + filteredList + "\n" + next.getClass().getSimpleName() + "\n" + o1 + "\n" + o2 + "\n");
            }
        }
    }

    private static List<Integer> copyFiltered(List<Integer> list, Filter<Integer> filter) {
        List<Integer> result = new ArrayList<Integer>();
        for (Integer i : list) {
            if (filter.accept(i)) {
                result.add(i);
            }
        }
        return result;
    }
    
    abstract static class Action {
        abstract Object action(ListIterator<?> iterator);

        Object execute(ListIterator<?> iterator) {
            try {
                return action(iterator);
            } catch (Throwable t) {
                return t;
            }
        }

        static boolean equals(Object o1, Object o2) {
            if (o1 == null) {
                return o2 == null;
            }
            if (o1 instanceof Throwable) {
                if (o2 == null) {
                    ((Throwable)o1).printStackTrace(System.out);
                    return false;
                }
                return o1.getClass() == o2.getClass();
            }
            return o1.equals(o2);
        }
    }

    static class NextAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            return iterator.next();
        }
    }

    static class PreviousAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            return iterator.previous();
        }
    }

    static class HasNextAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            return iterator.hasNext();
        }
    }

    static class NextIndexAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            return iterator.nextIndex();
        }
    }

    static class HasPreviousAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            return iterator.hasPrevious();
        }
    }

    static class PreviousIndexAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            return iterator.previousIndex();
        }
    }

    static class RemoveAction extends Action {
        @Override
        Object action(ListIterator<?> iterator) {
            iterator.remove();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static class AddAction extends Action {
        final Map<ListIterator<?>, AtomicInteger> counters = new HashMap<ListIterator<?>, AtomicInteger>();
        @Override
        Object action(ListIterator<?> iterator) {
            AtomicInteger counter = counters.get(iterator);
            if (counter == null) {
                counters.put(iterator, counter = new AtomicInteger(0));
            }
            ((ListIterator)iterator).add(counter.addAndGet(-2));
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static class SetAction extends Action {
        final Map<ListIterator<?>, AtomicInteger> counters = new HashMap<ListIterator<?>, AtomicInteger>();
        @Override
        Object action(ListIterator<?> iterator) {
            AtomicInteger counter = counters.get(iterator);
            if (counter == null) {
                counters.put(iterator, counter = new AtomicInteger(0));
            }
            ((ListIterator)iterator).set(counter.addAndGet(-2));
            return null;
        }
    }
}
