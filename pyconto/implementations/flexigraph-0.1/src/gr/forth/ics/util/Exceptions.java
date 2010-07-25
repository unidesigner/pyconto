package gr.forth.ics.util;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Exceptions {
    private Exceptions() { }
    
    public static RuntimeException asUnchecked(Throwable exception) {
        if (exception instanceof RuntimeException) {
            return (RuntimeException)exception;
        }
        return new RuntimeException(exception);
    }
}
