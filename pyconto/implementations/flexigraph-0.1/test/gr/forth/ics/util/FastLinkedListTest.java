package gr.forth.ics.util;

import junit.framework.*;
import java.util.*;
import java.io.*;

public class FastLinkedListTest extends TestCase {
    
    public FastLinkedListTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FastLinkedListTest.class);
        return suite;
    }
    
    public void testSize() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        List<Accessor<Integer>> refs = new ArrayList<Accessor<Integer>>();
        for (int i = 0; i < 5; ++i) {
            assertEquals(i, seq.size());
            refs.add(seq.addFirst(i));
        }
        for (int i = 4; i >= 0; --i) {
            refs.get(i).remove();
            assertEquals(i, seq.size());
        }
    }
    
    public void testAddFirst() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        for (int i = 0; i < 10; ++i) {
            seq.addFirst(i);
        }
        int current = Integer.MAX_VALUE;
        for (int next : seq) {
            assertTrue(current > next);
            current = next;
        }
    }
    
    public void testAddLast() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        for (int i = 0; i < 10; ++i) {
            seq.addLast(i);
        }
        int current = Integer.MIN_VALUE;
        for (int next : seq) {
            assertTrue(current < next);
            current = next;
        }
    }
    
    public void testAddAfterBefore() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        boolean before = false;
        LinkedList<Accessor<Integer>> refs = new LinkedList<Accessor<Integer>>();
        
        Accessor<Integer> ref = seq.addFirst(1);
        refs.add(ref);
        for (int i = 2; i < 11; ++i) {
            if (before) {
                ref = ref.addBefore(i);
            } else {
                ref = ref.addAfter(i);
            }
            refs.add(ref);
            before ^= true;
        }
        int[] nums = {1,3,5,7,9,10,8,6,4,2};
        int i = 0;
        for (int next : seq) {
            assertEquals(nums[i++], next);
        }
    }
    
    public void testIterator() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        final int size = 5;
        for (int i = 0; i < size; ++i) {
            seq.addLast(i);
        }
        int count = 0;
        int expectedSize = size;
        for (ListIterator<Integer> i = seq.listIterator(); i.hasNext(); ) {
            int next = i.next();
            assertEquals(0, i.previousIndex());
            assertEquals(count++, next);
            assertEquals(1, i.nextIndex());
            i.remove();
            assertEquals(--expectedSize, seq.size());
        }
        assertEquals(size, count);
    }
    
    public void testRemove() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Map<Integer, Accessor<Integer>> refs = new HashMap<Integer, Accessor<Integer>>();
        Set<Integer> ints = new HashSet<Integer>();
        for (int i = 0; i < 10; ++i) {
            ints.add(i);
        }
        for (int i = 0; i < 10; ++i) {
            int next = ints.iterator().next();
            ints.remove(next);
            refs.put(next, seq.addLast(next));
        }
        for (int i = 0; i < 10; ++i) {
            Accessor<Integer> ref = refs.remove(i);
            ref.remove();
            assertEquals(10 - (i + 1), seq.size());
            for (int next : seq) {
                assertTrue(next != i);
            }
        }
    }
    
    public void testNonExistentRemove() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor[] refs = { seq.addLast(1), seq.addLast(2), seq.addLast(3) };
        int count = 0;
        int expectedSize = refs.length;
        for (Iterator<Integer> i = seq.iterator(); i.hasNext(); ) {
            int value = i.next();
            assertEquals(count + 1, value);
            assertEquals(expectedSize--, seq.size());
            
            i.remove();
            assertEquals(expectedSize, seq.size());
            refs[count].remove(); //must be noop
            assertEquals(expectedSize, seq.size());
            
            int innerCount = count;
            for (Iterator<Integer> j = seq.iterator(); j.hasNext(); ) {
                innerCount++;
                assertEquals(innerCount + 1, j.next().intValue());
            }
            assertEquals(seq.size(), innerCount - count);
            count++;
        }
    }
    
    public void testConcurrentModification() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor[] refs = { seq.addFirst(2), seq.addFirst(1), seq.addFirst(0) };
        int removed = 0;
        try {
            for (int value : seq) {
                refs[value].remove();
                removed++;
            }
        } catch (ConcurrentModificationException e) {
            fail();
        }
    }
    
    public void testRemovals() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor[] refs = { seq.addLast(0), seq.addLast(1), seq.addLast(2), seq.addLast(3)  };
        Iterator<Integer> i1 = seq.iterator();
        assertEquals(true, i1.hasNext());
        assertEquals(0, i1.next().intValue());
        refs[1].remove();
        assertEquals(2, i1.next().intValue());
        refs[2].remove();
        refs[3].remove();
        assertFalse(i1.hasNext());
        try {
            i1.next();
            fail();
        } catch (NoSuchElementException ok) {
        }
    }
    
    public void testDoubleRemoval() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor[] refs = { seq.addLast(0), seq.addLast(1), seq.addLast(2), seq.addLast(3)  };
        Iterator<Integer> i1 = seq.iterator();
        try {
            i1.remove();
            fail();
        } catch (IllegalStateException ok) {
        }
        i1.next();
        i1.remove();
        try {
            i1.remove();
            fail();
        } catch (IllegalStateException ok) {
        }
    }
    
    public void testIndex() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.addLast(1);
        seq.addLast(2);
        ListIterator<Integer> iter = seq.listIterator(1);
        assertEquals(1, iter.nextIndex());
        assertEquals(0, iter.previousIndex());
        iter = seq.listIterator();
        iter.next();
        iter.remove();
        assertEquals(0, iter.nextIndex());
        assertEquals(-1, iter.previousIndex());
    }
    
    public void testAddByIterator() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.addLast(1);
        seq.addLast(3);
        ListIterator<Integer> iter = seq.listIterator(1);
        iter.add(2);
        assertEquals(3, seq.size());
        Iterator<Integer> i = Arrays.asList(1, 2, 3).iterator();
        iter = seq.listIterator();
        while (i.hasNext()) {
            assertEquals(i.next(), iter.next());
        }
    }
    
    public void testSet() {
        FastLinkedList<Integer> q = new FastLinkedList<Integer>(Arrays.asList(0, 2, 4, 6));
        ListIterator<Integer> it = q.listIterator();
        while (it.hasNext()) {
            int value = it.next();
            it.add(value + 1);
        }
        int[] expected = { 0, 1, 2, 3, 4, 5, 6, 7 };
        for (int i = 0; i < expected.length; ++i) {
            assertEquals(new Integer(expected[i]), q.get(i));
        }
        it = q.listIterator();
        while (it.hasNext()) {
            it.set(8 - it.next());
        }
        int last = Integer.MAX_VALUE;
        for (int value : q) {
            assertTrue(value < last);
            last = value;
        }
    }
    
    public void testAsList() {
        FastLinkedList<Integer> q = new FastLinkedList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
        for (int i = 0; i < q.size(); ++i) {
            assertEquals(new Integer(i), q.get(i));
        }
        ListIterator<Integer> i = q.listIterator(2);
        assertEquals(new Integer(2), i.next());
        assertEquals(new Integer(3), i.next());
        assertEquals(new Integer(4), i.next());
        assertEquals(new Integer(4), i.previous());
        assertEquals(new Integer(3), i.previous());
        assertEquals(new Integer(2), i.previous());
        assertEquals(new Integer(1), i.previous());
        assertEquals(new Integer(0), i.previous());
        assertEquals(false, i.hasPrevious());
        try {
            i.previous();
        } catch (NoSuchElementException ok) { }
    }
    
    public void testAppend() {
        FastLinkedList<Integer> q1 = new FastLinkedList<Integer>(Arrays.asList(1, 2, 3, 4));
        FastLinkedList<Integer> q2 = new FastLinkedList<Integer>(Arrays.asList(5, 6, 7, 8));
        
        q1.removeLast();
        q1.removeFirst();
        q2.removeLast();
        
        q1.append(q2); // 2, 3, 5, 6, 7
        assertEquals(Arrays.asList(2, 3, 5, 6, 7), q1);
        assertTrue(q2.isConsumed());
        assertEquals(5, q1.size());
        int last = -1;
        for (int next : q1) {
            assertTrue(next > last);
            last = next;
        }
    }
    
    public void testConsumed() {
        FastLinkedList<Integer> q = new FastLinkedList<Integer>();
        q.add(5);
        q.add(10);
        FastLinkedList<Integer> q2 = new FastLinkedList<Integer>();
        q2.append(q);
        assertEquals(0, q.size());
        assertTrue(q.isEmpty());
        try {
            q.add(5);
            fail();
        } catch (IllegalStateException ok) {
        }
        q.remove(new Object());
        q.removeAll(null);
        try {
            q.append(null);
            fail();
        } catch (IllegalStateException ok) {
        }
        try {
            q.add(0, new Integer(1000));
            fail();
        } catch (IllegalStateException ok) {
        }
        try {
            q.addFirst(1);
            fail();
        } catch (IllegalStateException ok) {
        }
        try {
            q.addLast(1);
            fail();
        } catch (IllegalStateException ok) {
        }
        try {
            q.add(1);
            fail();
        } catch (IllegalStateException ok) {
        }
        assertTrue(q.subList(0, 0).isEmpty());
        
        try {
            q.subList(0, 0).add(5);
            fail();
        } catch (IllegalStateException ok) {
        }
        q.equals(null);
        q.hashCode();
        try {
            q.getFirst();
            fail();
        } catch (NoSuchElementException ok) {
        }
        try {
            q.getLast();
            fail();
        } catch (NoSuchElementException ok) {
        }
    }
    
    public void testSimple() {
        FastLinkedList<Integer> q = new FastLinkedList<Integer>();
        q.add(1);
        q.add(2);
        q.add(3);
        assertEquals(3, q.size());
        assertEquals(new Integer(1), q.getFirst());
        assertEquals(new Integer(3), q.getLast());
        assertEquals(new Integer(3), q.removeLast());
        assertEquals(new Integer(2), q.removeLast());
        assertEquals(new Integer(1), q.removeLast());
        
        try {
            q.getFirst();
            fail();
        } catch (NoSuchElementException ok) {
        }
        
        assertTrue(q.isEmpty());
    }
    
    @SuppressWarnings("unchecked")
    public void testSerializability() throws Exception {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.add(5);
        seq.add(10);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(seq);
        out.flush();
        out.close();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        FastLinkedList<Integer> seq2 = (FastLinkedList<Integer>)in.readObject();
        in.close();
        assertTrue(seq.equals(seq2));
    }
    
    public void testImmediateRemoveFirst() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.add(5);
        seq.removeFirst();
    }
    
    public void testImmediateRemoveLast() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.add(5);
        seq.removeLast();
    }
    
    public void testAppendToEmpty() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        FastLinkedList<Integer> seq2 = new FastLinkedList<Integer>();
        FastLinkedList<Integer> seq3 = new FastLinkedList<Integer>();
        seq3.add(5);
        
        seq.append(seq2);
        seq.append(seq3);
        assertEquals("[5]", seq.toString());
    }
    
    public void testListIterator() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        for (int i = 1; i <= 3; i++) {
            seq.addLast(i);
        }
        ListIterator<Integer> iterator = seq.listIterator();
        String commands = "NPNNPNNPNPPNPPNP";
        //1112223333222111
        int prevIndex = -1;
        int currIndex = 0;
        List<Integer> values = new LinkedList<Integer>();
        for (int i = 0; i < commands.length(); i++) {
            Integer value;
            if (commands.charAt(i) == 'N') {
                prevIndex++;
                currIndex++;
                value = iterator.next();
            } else {
                prevIndex--;
                currIndex--;
                value = iterator.previous();
            }
            values.add(value);
        }
        assertEquals("[1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1]", values.toString());
    }
    
    public void testListIteratorBounds() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.add(1);
        seq.add(2);
        ListIterator<Integer> iter = seq.listIterator(2);
        assertFalse(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(2, iter.nextIndex());
        assertEquals(1, iter.previousIndex());
        assertEquals(new Integer(2), iter.previous());
        
        ListIterator<Integer> iter2 = seq.listIterator(1);
        assertEquals(1, iter.nextIndex());
        assertEquals(0, iter.previousIndex());
        assertEquals(new Integer(2), iter2.next());
        assertEquals(new Integer(2), iter2.previous());
        
        ListIterator<Integer> iter3 = seq.listIterator(0);
        assertEquals(new Integer(1), iter3.next());
        assertEquals(new Integer(1), iter3.previous());
        
        ListIterator<Integer> iter4 = seq.listIterator(1);
        assertEquals(new Integer(1), iter4.previous());
        assertEquals(new Integer(1), iter4.next());
    }
    
    public void testMoveBackAndFront() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor<Integer> r1 = seq.addLast(1);
        Accessor<Integer> r2 = seq.addLast(2);
        Accessor<Integer> r3 = seq.addLast(3);
        
        assertEquals("[1, 2, 3]", seq.toString());
        r1.moveToBack();
        assertEquals(seq.getFirst(), new Integer(2));
        assertEquals(seq.getLast(), new Integer(1));
        assertEquals("[2, 3, 1]", seq.toString());
        r1.moveToBack();
        assertEquals("[2, 3, 1]", seq.toString());
        
        r1.moveToFront();
        assertEquals(seq.getFirst(), new Integer(1));
        assertEquals(seq.getLast(), new Integer(3));
        assertEquals("[1, 2, 3]", seq.toString());
        r1.moveToFront();
        assertEquals("[1, 2, 3]", seq.toString());
        
        r3.moveToFront();
        assertEquals(seq.getFirst(), new Integer(3));
        assertEquals(seq.getLast(), new Integer(2));
        assertEquals("[3, 1, 2]", seq.toString());
    }
    
    public void testMoveRemoved() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor<Integer> r = seq.addFirst(5);
        r.remove();
        try {
            r.moveToFront();
            fail();
        } catch (RuntimeException e) {
            //ok
        }
    }
    
    public void testIterableConstructor() {
        FastLinkedList<Number> seq = new FastLinkedList<Number>(Arrays.asList(1, 2, 3));
        assertEquals("[1, 2, 3]", seq.toString());
    }
    
    public void testRemoveThroughListInterface() {
        FastLinkedList<Number> seq = new FastLinkedList<Number>(Arrays.asList(1, 2, 3));
        seq.remove(new Integer(2));
        assertEquals("[1, 3]", seq.toString());
        
        seq.remove(new Integer(3));
        assertEquals("[1]", seq.toString());
        
        seq.remove(new Integer(1));
        assertEquals("[]", seq.toString());
    }
    
    public void testCreateRef() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>(Arrays.asList(1, 2, 3));
        Accessor<Integer> r1 = seq.accessorFor(1);
        assertNotNull(r1);
        assertTrue(seq.ownsAccessor(r1));
        r1.remove();
        assertEquals("[2, 3]", seq.toString());
        assertNull(seq.accessorFor(5));
    }
    
    public void testMoveAfterAndBefore() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor<Integer> r1 = seq.addLast(1);
        Accessor<Integer> r2 = seq.addLast(2);
        Accessor<Integer> r3 = seq.addLast(3);
        
        r1.moveAfter(r2);
        assertEquals(seq.getFirst(), new Integer(2));
        assertEquals("[2, 1, 3]", seq.toString());
        assertTrue(seq.ownsAccessor(r1));
        
        r3.moveBefore(r2);
        assertEquals(seq.getFirst(), new Integer(3));
        assertEquals("[3, 2, 1]", seq.toString());
        assertTrue(seq.ownsAccessor(r3));
        
        r2.remove();
        try {
            r3.moveAfter(r2);
            fail("Allowed operation while second ref is removed");
        } catch (RuntimeException ok) { }
        
        try {
            r2.moveAfter(r1);
            fail("Allowed operation while first ref is removed");
        } catch (RuntimeException ok) { }
        
        
        Accessor<Integer> rr3 = seq.accessorFor(3);
        String seqString = seq.toString();
        
        r3.moveAfter(rr3);
        assertEquals(seqString, seq.toString());
        rr3.moveAfter(r3);
        assertEquals(seqString, seq.toString());
        r3.moveBefore(rr3);
        assertEquals(seqString, seq.toString());
        rr3.moveBefore(r3);
        assertEquals(seqString, seq.toString());
        
        r3.moveAfter(r3);
        assertEquals(seqString, seq.toString());
        r3.moveBefore(r3);
        assertEquals(seqString, seq.toString());
    }
    
    public void testFindAfterAndBefore() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        Accessor<Integer> r1 = seq.addLast(1);
        Accessor<Integer> r2 = seq.addLast(2);
        Accessor<Integer> r3 = seq.addLast(3);
        
        assertEquals(new Integer(2), r1.next().get());
        assertEquals(new Integer(1), r2.previous().get());
        assertEquals(new Integer(3), r2.next().get());
        assertEquals(new Integer(2), r3.previous().get());
        
        try {
            r1.previous();
            fail();
        } catch (RuntimeException ok) { }
        
        try {
            r3.next();
            fail();
        } catch (RuntimeException ok) { }
    }
    
    public void testRemoveAndAddThroughIterator() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        seq.addLast(1);
        seq.addLast(2);
        ListIterator<Integer> it = seq.listIterator();
        Integer value1 = it.next();
        assertEquals(Integer.valueOf(1), value1);
        it.remove(); 
        it.add(11);
        Integer value2 = it.next();
        assertEquals(Integer.valueOf(2), value2);
        assertEquals(Integer.valueOf(2), it.previous());
        assertEquals(Integer.valueOf(2), it.next());
        it.remove();
        it.add(22);
        assertEquals(2, seq.size());
        
        assertEquals(Integer.valueOf(11), seq.get(0));
        assertEquals(Integer.valueOf(22), seq.get(1));
    }
    
    public void testListIteratorWithReference() {
        FastLinkedList<Integer> seq = new FastLinkedList<Integer>();
        List<Accessor<Integer>> refs = new ArrayList<Accessor<Integer>>();
        for (int i = 0; i < 10; i++) {
            refs.add(seq.addLast(i));
        }
        for (int i = 0; i < refs.size(); i++) {
            assertEqualIterators(seq.listIterator(i + 1), refs.get(i).listIterator());
        }
        for (int i = 0; i < refs.size(); i += 2) {
            refs.remove(i).remove();
        }
        for (int i = 0; i < refs.size(); i++) {
            assertEqualIterators(seq.listIterator(i + 1), refs.get(i).listIterator());
        }
    }
    
    private static <E> void assertEqualIterators(ListIterator<E> expected, ListIterator<E> actual) {
        while (expected.hasNext()) {
            E expectedElement = expected.next();
            if (!actual.hasNext()) {
                fail("Expected next element: " + expectedElement + ", but no next found");
            }
            E actualElement = actual.next();
            assertEquals(expectedElement, actualElement);
        }
        assertFalse(actual.hasNext());
        while (expected.hasPrevious()) {
            E expectedElement = expected.previous();
            if (!actual.hasPrevious()) {
                fail("Expected previous element: " + expectedElement + ", but no previous found");
            }
            E actualElement = actual.previous();
            assertEquals(expectedElement, actualElement);
        }
    }
    
    public void testAccessorGet() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> pos = list.addLast(5);
         assertEquals(Integer.valueOf(5), pos.get());
    }
    
    public void testAccessorSet() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> pos = list.addLast(5);
         pos.set(10);
         assertEqualLists(list, 10);
    }
    
    public void testAccessorDelete() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> pos1 = list.addLast(5);
         Accessor<Integer> pos2 = list.addLast(15);
         Accessor<Integer> pos3 = list.addLast(25);
         pos1.remove();
         assertEqualLists(list, 15, 25);
         pos3.remove();
         assertEqualLists(list, 15);
         
         assertTrue(pos1.isRemoved());
         assertFalse(pos2.isRemoved());
         assertTrue(pos1.isRemoved());
    }
    
    public void testAccessorOfDeadListStillWorks() {
         FastLinkedList<Integer> list1 = new FastLinkedList<Integer>();
         FastLinkedList<Integer> list2 = new FastLinkedList<Integer>();
         
         Accessor<Integer> accessor = list2.addLast(5);
         list1.append(list2);
         assertEqualLists(list1, 5);
         accessor.set(10);
         assertEqualLists(list1, 10);
    }
    
    public void testWhatHappensToAccessorWhenRemoveElement() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> acc = list.addLast(5);
         list.remove(Integer.valueOf(5));
         assertTrue(acc.isRemoved());
    }
    
    public void testRemovedAccessor() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> acc = list.addLast(5);
         acc.remove();
         try {
            acc.addAfter(10);
         } catch (NoSuchElementException ok) {
         }
         try {
            acc.addBefore(10);
         } catch (NoSuchElementException ok) {
         }
         try {
            acc.get();
         } catch (NoSuchElementException ok) {
         }
         try {
            acc.set(10);
         } catch (NoSuchElementException ok) {
         }
         try {
             acc.moveToBack();
         } catch (NoSuchElementException ok) {
         }
         try {
             acc.moveToFront();
         } catch (NoSuchElementException ok) {
         }
    }
    
    public void testGetToARemovedAccessorThrowsException() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> acc = list.addLast(5);
         acc.remove();
    }

    public void testOwner() {
         FastLinkedList<Integer> list = new FastLinkedList<Integer>();
         Accessor<Integer> acc = list.addLast(5);
         assertSame(list, acc.owner());

         acc.remove();
         assertNull(acc.owner());
    }

    private static void assertEqualLists(FastLinkedList list, Integer... contents) {
        assertEquals(Arrays.asList(contents), list);
    }

}