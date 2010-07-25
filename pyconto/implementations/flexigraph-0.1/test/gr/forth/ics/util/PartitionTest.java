package gr.forth.ics.util;

import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class PartitionTest extends TestCase {
    
    public PartitionTest(String testName) {
        super(testName);
    }

    public void testValue() {
        Partition<String> p = Partition.singleton("test");
        assertEquals("test", p.value());
    }
    
    public void testFind() {
        Partition<String> p1 = Partition.singleton("a");
        assertSame(p1, p1.find());
    }

    public void testUnionWithEqualRank() {
        Partition<String> p1 = Partition.singleton("a");
        Partition<String> p2 = Partition.singleton("b");
        assertNotSame(p1.find(), p2.find());

        p1.merge(p2);
        assertSame(p1.find(), p2.find());
    }

    public void testUnionWithLessRank() {
        Partition<?> pSmall = Partition.singleton("a");
        Partition<?> pBig = Partition.singleton("b").merge(Partition.singleton("c"));
        
        Partition<?> pAll = pSmall.merge(pBig);

        //the small is attached to the big
        assertSame(pSmall.find(), pBig);
        assertSame(pBig.find(), pBig);
        assertSame(pAll, pBig);
    }

    public void testUnionWithMoreRank() {
        Partition<?> pSmall = Partition.singleton("a");
        Partition<?> pBig = Partition.singleton("b").merge(Partition.singleton("c"));

        Partition<?> pAll = pBig.merge(pSmall);

        //the small is attached to the big
        assertSame(pSmall.find(), pBig);
        assertSame(pBig.find(), pBig);
        assertSame(pAll, pBig);
    }
}
