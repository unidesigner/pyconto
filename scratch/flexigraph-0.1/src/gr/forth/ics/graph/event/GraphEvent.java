package gr.forth.ics.graph.event;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.util.EventObject;

public class GraphEvent extends EventObject {
    public static enum Type {
        NODE_ADDED(Node.class),
        NODE_REMOVED(Node.class),
        EDGE_ADDED(Edge.class),
        EDGE_REMOVED(Edge.class),
        
        NODE_REINSERTED(Node.class),
        EDGE_REINSERTED(Edge.class),
        
        NODE_REORDERED(Node.class),
        EDGE_REORDERED(Edge.class)
        ;
        
        private final Class clazz;
        
        private Type(Class expectedDataClass) {
            this.clazz = expectedDataClass;
        }
        
        private void checkAssignable(Object data) {
            clazz.cast(data);
        }
    };
    
    private final InspectableGraph source;
    private final Type eventType;
    private final Object data;
        
    public GraphEvent(InspectableGraph source, Type type, Object data) {
        super(source);
        this.source = source;
        this.eventType = type;
        this.data = data;
        type.checkAssignable(data);
    }
    
    @Override public InspectableGraph getSource() {
        return source;
    }
    
    public Type getEventType() {
        return eventType;
    }
    
    public Object getData() {
        return data;
    }
    
    public Node getNode() {
        return (Node)data;
    }
    
    public Edge getEdge() {
        return (Edge)data;
    }
    
    public int hashCode() {
        return 17 + source.hashCode() + eventType.hashCode() * 11 + data.hashCode() * 37;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof GraphEvent)) {
            return false;
        }
        GraphEvent other = (GraphEvent)o;
        return source == other.source &&
                eventType == other.eventType &&
                data == other.data;
    }
    
   public String toString() {
       return "[Event: source=" + source + ", type=" + eventType + ", data=" + data + "]";
   }
}
