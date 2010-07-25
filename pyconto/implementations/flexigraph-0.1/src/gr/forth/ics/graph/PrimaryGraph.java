package gr.forth.ics.graph;

import gr.forth.ics.util.Accessor;
import gr.forth.ics.graph.event.GraphEvent;

import gr.forth.ics.util.FastLinkedList;

/**
 * Fastest {@link Graph} implementation, which has the restriction that its nodes and edges
 * cannot be contained in any other {@code PrimaryGraph} (but they can be contained in as many
 * {@link SecondaryGraph}s as desirable).
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public final class PrimaryGraph extends AbstractListGraph {
    private static final long serialVersionUID = -4941194412829796815L;

    @Override final FastLinkedList<EdgeImpl> getOutEdges(NodeImpl node) {
        return node.outEdges;
    }

    @Override final FastLinkedList<EdgeImpl> getInEdges(NodeImpl node) {
        return node.inEdges;
    }

    @Override final Accessor<NodeImpl> getNodeRef(NodeImpl node) {
        return node.reference;
    }

    @Override final Accessor<EdgeImpl> getEdgeOutRef(EdgeImpl edge) {
        return edge.outReference;
    }

    @Override final Accessor<EdgeImpl> getEdgeInRef(EdgeImpl edge) {
        return edge.inReference;
    }

    @Override final void removeEdgeRefs(EdgeImpl edge) {
        edge.inReference = null;
        edge.outReference = null;
    }

    @Override final void setNodeRef(NodeImpl node, Accessor<NodeImpl> ref) {
        node.reference = ref;
    }

    @Override final void setEdgeOutRef(EdgeImpl edge, Accessor<EdgeImpl> ref) {
        edge.outReference = ref;
    }

    @Override final void setEdgeInRef(EdgeImpl edge, Accessor<EdgeImpl> ref) {
        edge.inReference = ref;
    }

    @Override final void removeNodeRef(NodeImpl node) {
        node.reference = null;
    }

    @Override final void initNode(NodeImpl node) { }

    @Override public final boolean isPrimary() {
        return true;
    }

    public boolean reinsertEdge(Edge e) {
        final EdgeImpl edge = (EdgeImpl)e;
        if (getEdgeInRef(edge) != null) {
            if (containsEdge(e)) {
                return false;
            }
            throw new IllegalArgumentException("Edge must not belong to any graph, to be reinserted to one");
        }
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.EDGE_REINSERTED, edge), new Runnable() {
            public void run() {
                if (!containsNode(edge.n1)) {
                    reinsertNode(edge.n1);
                }
                if (!containsNode(edge.n2)) {
                    reinsertNode(edge.n2);
                }
                setEdgeOutRef(edge, edge.n1.outEdges.addLast(edge));
                setEdgeInRef(edge, edge.n2.inEdges.addLast(edge));
                edgeCount++;
            }
        });
        return true;
    }

    public boolean reinsertNode(Node n) {
        final NodeImpl node = (NodeImpl)n;
        if (getNodeRef(node) != null) {
            if (containsNode(n)) {
                return false;
            }
            throw new IllegalArgumentException("Node must not belong to any graph, to be reinserted to one");
        }
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.NODE_REINSERTED, node), new Runnable() {
            public void run() {
                setNodeRef(node, nodes.addLast(node));
            }
        });
        return true;
    }
}