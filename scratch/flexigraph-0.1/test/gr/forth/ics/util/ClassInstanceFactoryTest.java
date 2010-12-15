package gr.forth.ics.util;

import junit.framework.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ClassInstanceFactoryTest extends TestCase {
    
    public ClassInstanceFactoryTest(String testName) {
        super(testName);
    }

    public void testOk() {
        Factory<OK> factory = ClassInstanceFactory.from(OK.class);
        OK ok = factory.create(null);
        assertThat(ok, instanceOf(OK.class));
    }
    
    public void testAbstract() {
        try {
            ClassInstanceFactory.from(ABSTRACT.class);
            fail();
        } catch (Exception ok) { }
    }
    
    public void testNonPublicConstructor() {
        try {
            ClassInstanceFactory.from(NON_PUBLIC_CONSTRUCTOR.class);
            fail();
        } catch (Exception ok) { }
    }
    
    public void testNonPublicClass() {
        try {
            ClassInstanceFactory.from(NON_PUBLIC_CLASS.class);
            fail();
        } catch (Exception ok) { }
    }
    
    public void testNoConstructor() {
        try {
            ClassInstanceFactory.from(NO_CONSTRUCTOR.class);
            fail();
        } catch (Exception ok) { }
    }
    
    public static class OK { }
    
    public static abstract class ABSTRACT { }
    
    public static class NON_PUBLIC_CONSTRUCTOR {
        NON_PUBLIC_CONSTRUCTOR() { }
    }
    
    static class NON_PUBLIC_CLASS { }
    
    public static class NO_CONSTRUCTOR {
        NO_CONSTRUCTOR(Object arg) { }
    }
}
