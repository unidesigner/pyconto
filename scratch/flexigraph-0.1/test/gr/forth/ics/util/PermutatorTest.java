package gr.forth.ics.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class PermutatorTest extends TestCase {
    
    public PermutatorTest(String testName) {
        super(testName);
    }

    public void testPermutations() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        Set<List<Integer>> allLists = new HashSet<List<Integer>>();
        for (List<Integer> permutation : Permutator.permutations(list)) {
            allLists.add(permutation);
        }
        assertEquals(allLists.size(), 1 * 2 * 3 * 4 * 5 * 6);
    }

}
