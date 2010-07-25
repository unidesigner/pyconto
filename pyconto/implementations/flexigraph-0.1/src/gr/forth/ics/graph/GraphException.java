package gr.forth.ics.graph;

public class GraphException extends RuntimeException {
    public GraphException(String message) {
        super(message);
    }
    
    public GraphException(Throwable cause) {
        super(cause);
    }
    
    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
