package gr.forth.ics.graph.io.gml;

import gr.forth.ics.graph.io.*;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.FlexiGraph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.Tuple;
import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.Locator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import gr.forth.ics.util.Args;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class GmlWriter implements GraphWriter {
    private static final GmlWriter instance = new GmlWriter();

    protected GmlWriter() { }

    public static GmlWriter instance() { return instance; }

    public void write(InspectableGraph graph, Locator locator, OutputStream out) throws IOException {
        new Serializer(out, locator).write(graph);
    }

    private static class Serializer {
        private final PrintWriter out;
        private final Locator locator;
        private int identation;

        Serializer(OutputStream out, Locator locator) {
            this(new PrintWriter(out), locator);
        }

        Serializer(Writer out, Locator locator) {
            Args.notNull(out, "Writer");
            Args.notNull(locator, "Locator");

            if (out instanceof PrintWriter) {
                this.out = (PrintWriter) out;
            } else {
                this.out = new PrintWriter(out);
            }
            this.locator = locator;
        }

        void write(InspectableGraph g) throws IOException {
            final Object idKey = new Object();
            out.print("Creator \"");
            out.print(getClass());
            out.println("\"");
            out.print("Version \"");
            out.print(FlexiGraph.version());
            out.println("\"");
            out.println("graph");
            open();
            tabs();
            out.println("hierarchic\t1");
            label(g.tuple());
            tabs();
            out.println("directed\t1");
            int id = 0;
            for (Node n : g.nodes()) {
                tabs();
                out.println("node");
                open();
                n.putWeakly(idKey, id);
                tabs();
                out.print("id\t");
                out.println(id++);
                label(n);
                GPoint p = locator.getLocation(n);
                if (p != null) {
                    tabs();
                    out.println("graphics");
                    open();
                    point(p);
                    close();
                }
                close();
            }
            for (Edge e : g.edges()) {
                final int id1 = e.n1().getInt(idKey);
                final int id2 = e.n2().getInt(idKey);
                tabs();
                out.println("edge");
                open();
                tabs();
                out.println("source\t" + id1);
                tabs();
                out.println("target\t" + id2);
                label(e);

                final List<GPoint> bends = locator.getBendsSafe(e);
                if (bends.size() > 0) {
                    final GPoint p1 = locator.getLocation(e.n1());
                    final GPoint p2 = locator.getLocation(e.n2());
                    if (p1 != null && p2 != null) {
                        tabs();
                        out.println("graphics");
                        open();
                        tabs();
                        out.println("Line");
                        open();
                        List<GPoint> points = new ArrayList<GPoint>(bends.size() +
                                2);
                        points.add(p1);
                        points.addAll(bends);
                        points.add(p2);
                        for (GPoint p : points) {
                            tabs();
                            out.println("point");
                            open();
                            point(p);
                            close(); //end point
                        }
                        close(); //end line
                    }
                    close(); //end graphics
                }
                close();
            }
            close();
            out.flush();
        }

        private void label(Tuple tuple) {
            Object value = tuple.getValue();
            if (value == null) {
                return;
            }
            out.print("label\t\"");
            out.print(value);
            out.println("\"");
        }

        private void tabs() {
            for (int i = 0; i < identation; i++) {
                out.print('\t');
            }
        }

        private void open() {
            tabs();
            out.println("[");
            identation++;
        }

        private void close() {
            identation--;
            tabs();
            out.println("]");
        }

        private void point(GPoint p) {
            tabs();
            out.print("x\t");
            out.println(p.x);
            tabs();
            out.print("y\t");
            out.println(p.y);
        }
    }
}
