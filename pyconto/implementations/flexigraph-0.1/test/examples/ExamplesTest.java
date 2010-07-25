package examples;

import examples.algo.NodeReachability;
import examples.algo.TransitiveClosure;
import examples.algo.TransitiveReduction;
import examples.algo.UnionFind;
import examples.basic.AdjacentNodes;
import examples.basic.FindAllPaths;
import examples.basic.GraphSharing;
import examples.basic.LoopOverNodesAndEdges;
import examples.basic.Paths;
import examples.basic.Simplest;
import examples.basic.Tuples;
import examples.basic.WeakTuples;
import examples.io.GmlOutput;
import examples.io.GmlOutputWithLayout;
import examples.layout.CombinedLayout;
import examples.layout.RandomizedLayout;
import examples.layout.SimpleLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ExamplesTest extends TestCase {

    public ExamplesTest(String testName) {
        super(testName);
    }

    private static final PrintStream realOut = System.out;
    private static final PrintStream nullOut = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
    });

    @Override
    protected void setUp() {
        System.setOut(nullOut);
    }

    @Override
    protected void tearDown() {
        System.setOut(realOut);
    }

    public void testUnionFind() {
        UnionFind.main(null);
    }

    public void testAdjacentNodes() {
        AdjacentNodes.main(null);
    }

    public void testGraphSharing() {
        GraphSharing.main(null);
    }

    public void testLoopOverNodesAndEdges() {
        LoopOverNodesAndEdges.main(null);
    }

    public void testSimplest() {
        Simplest.main(null);
    }

    public void testTuples() {
        Tuples.main(null);
    }

    public void testWeakTuples() {
        WeakTuples.main(null);
    }

    public void testGmlOutput() throws Exception {
        GmlOutput.main(null);
    }

    public void testGmlOutputWithLayout() throws Exception {
        GmlOutputWithLayout.main(null);
    }

    public void testCombinedLayout() {
        CombinedLayout.main(null);
    }

    public void testRandomizedLayout() {
        RandomizedLayout.main(null);
    }

    public void testSimpleLayout() {
        SimpleLayout.main(null);
    }

    public void testPaths() {
        Paths.main(null);
    }

    public void testFindAllPaths() {
        FindAllPaths.main(null);
    }

    public void testNodeReachability() {
        NodeReachability.main(null);
    }

    public void testTransitiveClosure() {
        TransitiveClosure.main(null);
    }

    public void testTransitiveReduction() {
        TransitiveReduction.main(null);
    }
}
