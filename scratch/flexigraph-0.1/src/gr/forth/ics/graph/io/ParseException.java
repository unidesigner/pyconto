package gr.forth.ics.graph.io;

import java.io.IOException;

/**
 * Thrown when a parser cannot interpret its input.
 * 
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ParseException extends IOException {
    /**
     * Creates a ParseException with a cause.
     *
     * @param cause the cause of this exception
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a ParseException with a message and a cause.
     *
     * @param message the message of this exception
     * @param cause the cause of this exception
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a ParseException with a message.
     *
     * @param message the message of this exception
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a ParseException.
     */
    public ParseException() {
    }
}
