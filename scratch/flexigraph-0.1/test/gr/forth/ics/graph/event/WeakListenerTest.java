package gr.forth.ics.graph.event;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class WeakListenerTest extends MockObjectTestCase {
    public WeakListenerTest(String testName) {
        super(testName);
    }

    Graph g = new PrimaryGraph();

    public void testEventsPropagatedNormally() {
        Mock graphListenerMock = mock(GraphListener.class);
        graphListenerMock.expects(once()).method("preEvent");
        graphListenerMock.expects(once()).method("nodeToBeAdded");
        graphListenerMock.expects(once()).method("nodeAdded");
        graphListenerMock.expects(once()).method("postEvent");

        GraphListener listener = (GraphListener)graphListenerMock.proxy();
        WeakListener.createAndAttach(g, listener);

        for (int i = 0; i < 10; i++) System.gc();
        g.newNode("testNode");
    }

    public void testEventsStopWhenListenerIsGced() {
        WeakListener.createAndAttach(g, new GraphListener() {
            public void nodeAdded(GraphEvent e) {
                fail();
            }

            public void nodeRemoved(GraphEvent e) {
                fail();
            }

            public void nodeToBeAdded(GraphEvent e) {
                fail();
            }

            public void nodeToBeRemoved(GraphEvent e) {
                fail();
            }

            public void nodeReordered(GraphEvent e) {
                fail();
            }

            public void preEvent() {
                fail();
            }

            public void postEvent() {
                fail();
            }

            public void edgeAdded(GraphEvent e) {
                fail();
            }

            public void edgeRemoved(GraphEvent e) {
                fail();
            }

            public void edgeToBeAdded(GraphEvent e) {
                fail();
            }

            public void edgeToBeRemoved(GraphEvent e) {
                fail();
            }

            public void edgeReordered(GraphEvent e) {
                fail();
            }
        });

        for (int i = 0; i < 10; i++) System.gc();
        g.newNode("testNode");
    }

    public void testNoGcDuringEventProcessing() {
        final Set<Object> invocations = new HashSet<Object>();
        WeakListener.createAndAttach(g, new MyGraphListener(invocations));

        g.newNode("testNode");

        assertEquals(new HashSet<Object>(Arrays.asList(
                "preEvent", "nodeAdded", "nodeToBeAdded", "postEvent")), invocations);
    }

    private static class MyGraphListener implements GraphListener {
        static void gc() {
            for (int i = 0; i < 10; i++) System.gc();
        }

        private final Set<Object> invocations;
        MyGraphListener(Set<Object> invocations) {
            this.invocations = invocations;
        }
        public void preEvent() {
            invocations.add("preEvent");
            gc();
        }

        public void nodeAdded(GraphEvent e) {
            invocations.add("nodeAdded");
        }

        public void nodeRemoved(GraphEvent e) {
            invocations.add("nodeRemoved");
        }

        public void nodeToBeAdded(GraphEvent e) {
            invocations.add("nodeToBeAdded");
        }

        public void nodeToBeRemoved(GraphEvent e) {
            invocations.add("nodeToBeRemoved");
        }

        public void nodeReordered(GraphEvent e) {
            invocations.add("nodeReordered");
        }

        public void postEvent() {
            invocations.add("postEvent");
        }

        public void edgeAdded(GraphEvent e) {
            invocations.add("edgeAdded");
        }

        public void edgeRemoved(GraphEvent e) {
            invocations.add("edgeRemoved");
        }

        public void edgeToBeAdded(GraphEvent e) {
            invocations.add("edgeToBeAdded");
        }

        public void edgeToBeRemoved(GraphEvent e) {
            invocations.add("edgeToBeRemoved");
        }

        public void edgeReordered(GraphEvent e) {
            invocations.add("edgeReordered");
        }
    }
}
