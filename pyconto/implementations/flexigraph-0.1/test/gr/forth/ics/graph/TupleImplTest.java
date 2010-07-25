package gr.forth.ics.graph;

import junit.framework.*;
import gr.forth.ics.graph.Tuple;
import java.util.*;
import java.util.Map.Entry;

public class TupleImplTest extends TestCase {
    
    public TupleImplTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TupleImplTest.class);
        
        return suite;
    }
    
    private Tuple t;
    private Tuple parent;
    private Object key;
    private Object value;
    
    protected void setUp() {
        t = new TupleImpl();
        parent = new TupleImpl();
        t.setParentTuple(parent);
        key = new Object();
        value = new Object();
    }
    
    public void testNonExistentBoolean() {
        assertNull(t.getBoolean(new Object()));
    }
    
    public void testNonExistendInt() {
        try {
            t.getInt(new Object());
        } catch (NullPointerException ok) { }
    }
    
    public void testRemove() {
        assertFalse(t.has(key));
        assertNull(t.remove(key));
        t.put(key, value);
        assertTrue(t.has(key));
        assertEquals(value, t.remove(key));
        assertFalse(t.has(key));
        
        t.putWeakly(key, value);
        assertTrue(t.has(key));
        assertEquals(value, t.remove(key));
        assertFalse(t.has(key));
    }
    
    public void testPut() {
        internalTestPut(key);
    }
    
    public void testPutNull() {
        internalTestPut(null);
    }
    
    private void internalTestPut(Object key) {
        t.put(key, value);
        assertEquals(value, t.get(key));
        Object newValue = new Object();
        t.put(key, newValue);
        assertEquals(newValue, t.get(key));
    }
    
    public void testPutWeakly() {
        t.put(key, value);
        Object key2 = new Object();
        Object key3 = new Object();
        t.putWeakly(key2, value);
        t.putWeakly(key3, value);
        key2 = null;
        for (int i = 0; i < 10; i++) {
            System.gc();
        }
        assertEquals(value, t.get(key));
        assertNull(t.get(key2));
        assertFalse(t.has(key2));
        assertEquals(value, t.get(key3));
        assertTrue(t.has(key3));
    }
    
    public void testHas() {
        t.put("1", "2");
        assertTrue(t.has("1"));
        t.remove("1");
        assertFalse(t.has("1"));
    }
    
    public void testHasThroughParent() {
        parent.put("1", "3");
        assertTrue(t.has("1"));
        parent.remove("1");
        assertFalse(t.has("1"));
    }
    
    public void testKeySet() {
        Set<Object> keys = t.keySet();
        assertTrue(keys.isEmpty());
        t.put("1", "2");
        keys = t.keySet();
        assertEquals(1, keys.size());
        assertEquals("1", keys.iterator().next());
        keys.clear();
        assertTrue(t.has("1"));
        assertEquals("2", t.get("1"));
    }
    
    public void testAsMap() {
        Map<Object, Object> map = t.asMap();
        int size0 = map.size();
        map.put("1", "2");
        assertTrue(t.has("1"));
        assertEquals("2", t.get("1"));
        assertEquals(1 + size0, map.size());
        
        t.putWeakly("1", "3");
        assertTrue(map.containsKey("1"));
        assertEquals("3", map.get("1"));
        assertEquals(1 + size0, map.size());
        assertEquals(t.asMap(), map);
        
        t.put("4", "5");
        assertEquals(2 + size0, t.asMap().size());
        assertEquals(2 + size0, map.size());
    }
    
    public void testMapIterator() {
        t.put(key, value);
        Iterator<Entry<Object, Object>> i = t.asMap().entrySet().iterator();
        Entry<Object, Object> next = i.next();
        assertTrue(t.has(next.getKey()));
        assertEquals(next.getValue(), t.get(next.getKey()));
        i.remove();
        assertFalse(t.has(next.getKey()));
        
    }
    
    public void testMapEntrySet() {
        Entry<Object, Object> entry = getAnEntry("1", "2");
        
        t.put(key, value);
        Set<Entry<Object, Object>> set = t.asMap().entrySet();
        
        set.add(entry);
        assertTrue(t.has("1"));
        assertEquals("2", t.get("1"));
    }
    
    public void testChangeMapEntryValue() {
        Entry<Object, Object> entry = getAnEntry("1", "2");
        t.asMap().entrySet(); //should not matter
        t.asMap().entrySet().add(entry);
        
        entry = null;
        for (Entry<Object, Object> e : t.asMap().entrySet()) {
            if (e.getKey() == "1") {
                entry = e;
                break;
            }
        }
        entry.setValue("3");
        assertEquals("3", t.get("1"));
    }
    
    private Entry<Object, Object> getAnEntry(Object key, Object value) {
        Tuple t = new TupleImpl();
        t.put(key, value);
        return t.asMap().entrySet().iterator().next();
    }
}
