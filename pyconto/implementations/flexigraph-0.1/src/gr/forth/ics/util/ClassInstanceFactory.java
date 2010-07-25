package gr.forth.ics.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ClassInstanceFactory<T> implements Factory<T> {
    private final Class<T> clazz;
    
    public static <T> ClassInstanceFactory<T> from(Class<T> clazz) {
        return new ClassInstanceFactory<T>(clazz);
    }
    
    public ClassInstanceFactory(Class<T> clazz) {
        Args.notNull(clazz);
        this.clazz = clazz;
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException("Class: " + clazz + " is abstract");
        }
        Constructor c = null;
        try {
            c = clazz.getConstructor();
        } catch (SecurityException ex) {
            throw Exceptions.asUnchecked(ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Class: " + clazz + "" +
                    " does not define an empty accessible constructor");
        }
        if (!Modifier.isPublic(c.getModifiers())) {
            throw new IllegalArgumentException("Class: " + clazz + "" +
                    " does not define an empty public constructor");
        }
    }
    
    public T create(Object o) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw Exceptions.asUnchecked(e.getCause());
        } catch (Exception e) {
            throw (AssertionError)new AssertionError().initCause(e);
        }
    }
}
