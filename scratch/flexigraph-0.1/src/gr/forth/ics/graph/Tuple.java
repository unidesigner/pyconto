package gr.forth.ics.graph;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

//TODO: document CLEARLY specification that weak values are not copied in copyInto, nor appear in asMap
/**
 * Entity that can be decorated with custom entries. It works similarly to a standard <code>java.util.Map</code>.
 * It features:
 * <li>Type-safe methods for a variety of types</li>
 * <li>Internal support for weak entries (see {@link #putWeakly(Object, Object)}) </li>
 * <li>A single property ({@link #getValue()}, {@link #setValue(Object)}) that can be accessed without key (so,
 * much faster than a normal key-value entry).
 * <li>Inheritance of entries (see {@link #setParentTuple(Tuple)}</li>
 * 
 * A Tuple supports for weak entries through {@link #putWeakly(Object key, Object value)} method. A weak
 * entry is an entry that is eventually (automatically) removed when its key is no longer reachable (that
 * is, eligible for garbage collection). This removes the need for "cleanup" style methods in algorithms
 * that decorate entities with data.
 *
 * Implementation note: in the standard implementation of Tuple ({@linkplain gr.forth.ics.graph.concrete.TupleImpl}), weak
 * entries are actually faster than normal, strong entries. Also, a <i>single</i> internal map is used
 * to store both types of entries, to save memory (both in terms of memory used by the maps themselves <i>and</i>
 * for storing the references to the maps).
 */
public interface Tuple extends Serializable {
    //does affect equalValues
    /**
     * Puts a regular key-value entry to this tuple. Null keys and values are allowed.
     * @return the old value of this key, if any (or null).
     */
    Object put(Object key, Object value);
    
    /**
     * Returns the value associated with the specified key. If no value is found, and this tuple
     * has a parent (see {@link #setParentTuple(Tuple)}), the parent is queried with the same key. Null
     * is returned if no value is ultimately found.
     */
    Object get(Object key);
    
    //affects equalsValues only if the removed key exists and was not put weakly
    /**
     * Removes a key-value entry, either regular or weak. Returns the value of the entry, if found (or null).
     */
    Object remove(Object key);
    
    /**
     * Returns true if an entry is found (in this tuple, or in its parent) with the specified key,
     * either regular or weak. 
     */
    boolean has(Object key);
    
    //does NOT affects equalValues
    /**
     * Puts a weak key-value entry to this tuple. This entry will be automatically removed when
     * the key becomes unreachable (and eligible for garbage collection). Returns the old value of the
     * specified key, if there was one (or null). 
     */
    Object putWeakly(Object key, Object value);
    
    /**
     * Gets the value associated with this tuple.
     */
    Object getValue();
    
    /**
     * Associates a value with this tuple. Returns the old value that is replaced.
     */
    Object setValue(Object value);
    
    /**
     * Returns the value of the given key as Boolean.
     */
    Boolean getBoolean(Object key);
    
    /**
     * Returns the value of the given key as int. (The real value needs only to be a Number, not
     * necessarily an Integer).
     * @throws NullPointerException for a non-existent key
     */
    int getInt(Object key);
    
    /**
     * Returns the value of the given key as long. (The real value needs only to be a Number, not
     * necessarily a Long).
     * @throws NullPointerException for a non-existent key
     */
    long getLong(Object key);
    
    /**
     * Returns the value of the given key as double. (The real value needs only to be a Number, not
     * necessarily a Double).
     * @throws NullPointerException for a non-existent key
     */
    double getDouble(Object key);
    
    /**
     * Returns the value of the given key as float. (The real value needs only to be a Number, not
     * necessarily a Float).
     * @throws NullPointerException for a non-existent key
     */
    float getFloat(Object key);
    
    /**
     * Returns the value of the given key as Character.
     */
    Character getChar(Object key);
    
    /**
     * Returns the value of the given key as short. (The real value needs only to be a Number, not
     * necessarily a Short).
     * @throws NullPointerException for a non-existent key
     */
    short getShort(Object key);
    
    /**
     * Returns the value of the given key as Number.
     */
    Number getNumber(Object key);
    
    /**
     * Returns the value of the given key as String.
     */
    String getString(Object key);
    
    /**
     * Returns the value of the given key as InspectableGraph.
     */
    InspectableGraph getInspectableGraph(Object key);
    
    /**
     * Returns the value of the given key as Node.
     */
    Node getNode(Object key);
    
    /**
     * Returns the value of the given key as Edge.
     */
    Edge getEdge(Object key);
    
    /**
     * Returns the value of the given key as Graph.
     */
    Graph getGraph(Object key);
    
    /**
     * Returns the value of the given key as Tuple.
     */
    Tuple getTuple(Object key);
    
    //copies only strong values
    /**
     * Copies every regular (non-weak) entry of this tuple, to the specified tuple.
     * @throws NullArgumentException when tuple is null
     */
    void copyInto(Tuple tuple);
    
    /**
     * Returns true if, and only if, other is a Tuple that has exactly the same regular entries. That
     * is, weak entries are not taken into consideration. Entries are tested by the equals() method of
     * keys and values.
     */
    boolean equalValues(Tuple other);
    
    /**
     * Returns a modifiable set that contains all keys of regular (non-weak) entries. Adding or
     * removing elements to/from this set has no effect on the tuple. Does not contain keys
     * from parent tuple.
     */
    Set<Object> keySet();
    
    /**
     * Returns the parent tuple of this tuple, or null if no parent has been set.
     */
    Tuple getParentTuple();
    
    //beware cycles. Can easily lead to infinite loops
    /**
     * Sets the parent tuple of this tuple. This tuple will inherit all entries (regular and weak) of
     * its parent, and every entry defined in this tuple, which has a key that is contained in the
     * parent tuple, effectively overrides the parent entry. This facility can be used as a mechanism
     * to share common attributes between tuples.
     *
     * Note: Pay attention not to create cyclic inheritance between tuples, or else {@linkplain #get(Object) get()}
     * operations may end up in an infinite loop.
     */
    void setParentTuple(Tuple parent);
    
    /**
     * Returns a live Map view of this tuple. The Map will contain all the entries
     * of this tuple, including weak entries. Any modifications to the returned map will be delegated to
     * this tuple. The returned map has value semantics, i.e. its equals/hashCode methods are based on
     * its entries.
     *
     * Entries are also supported in this map. If an entry is added, a regular entry is added in
     * this tuple. If an entry is removed, the respective entry is removed from this tuple. If an
     * entry's <code>setValue(Object)</code> method is called, the respective entry in this tuple
     * also changes value.
     */
    Map<Object, Object> asMap();
}
