package gr.forth.ics.graph.layout;

import junit.framework.*;

public class ArithmeticTestCase extends TestCase {
    
    public ArithmeticTestCase(String testName) {
        super(testName);
    }
    
    public void assertNear(double exp, double real, double e) {
        if (Math.abs(exp - real) >= e) {
            throw new AssertionFailedError("Expected: " + exp + ", was: " + real
                    + ", allowable distance E: " + e);
        }
    }
    
    public void assertNear(GPoint exp, GPoint real, double e) {
        assertNear(exp.x, real.x, e);
        assertNear(exp.y, real.y, e);
    }    
}
