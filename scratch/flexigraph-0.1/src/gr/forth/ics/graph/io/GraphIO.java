package gr.forth.ics.graph.io;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.graph.layout.Locators;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A collection of utilities for reading and writing graphs into various serialization {@link Format formats}.
 * 
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class GraphIO {
    private GraphIO() { }

    /**
     * Writes a graph using the specified format to a target output stream. The stream is not closed afterwards.
     *
     * @param format the format to use
     * @param graph the graph to write
     * @param out the output stream on which to write the graph
     * @throws IOException if an error occurs during writing
     */
    public static void write(Format format, InspectableGraph graph, OutputStream out) throws IOException {
        format.writer().write(graph, Locators.emptyLocator(), out);
    }

    /**
     * Reads a graph using the specified format from an input stream. The stream is not closed afterwards.
     *
     * @param format the format to use
     * @param graph the graph to populate
     * @param in the input stream from which to read the graph
     * @throws IOException if an error occurs during reading
     */
    public static void read(Format format, Graph graph, InputStream in) throws IOException {
        format.reader().read(graph, Locators.emptyLocator(), in);
    }

    /**
     * Writes a graph using the specified format to a target file. The file is replaced if already exists,
     * or created otherwise.
     *
     * @param format the format to use
     * @param graph the graph to write
     * @param file the file on which to write the graph
     * @throws IOException if an error occurs during writing
     */
    public static void write(Format format, InspectableGraph graph, File file) throws IOException {
        write(format, graph, Locators.emptyLocator(), file);
    }

    /**
     * Writes a graph using the specified format to a target file. The file is replaced if already exists,
     * or created otherwise.
     *
     * @param format the format to use
     * @param graph the graph to write
     * @param locator if the format {@link Format#supportsLayout() supports layout information},
     *      the layout described by this locator is written as well, otherwise ignored
     * @param file the file on which to write the graph
     * @throws java.io.IOException if an error occurs during writing
     */
    public static void write(Format format, InspectableGraph graph, Locator locator, File file) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            format.writer().write(graph, locator, out);
        } finally {
            out.close();
        }
    }

    /**
     * Reads a graph using the specified format from an input file.
     *
     * @param format the format to use
     * @param graph the graph to populate
     * @param file the file from which to read the graph
     * @throws IOException if an error occurs during reading
     */
    public static void read(Format format, Graph graph, File file) throws IOException {
        read(format, graph, Locators.emptyLocator(), file);
    }

    /**
     * Reads a graph using the specified format from an input file.
     *
     * @param format the format to use
     * @param graph the graph to populate
     * @param locator if the format {@link Format#supportsLayout() supports layout information},
     *      and such information exists in the input, it is used to populate this locator
     * @param file the file from which to read the graph
     * @throws IOException if an error occurs during reading
     */
    public static void read(Format format, Graph graph, Locator locator, File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        read(format, graph, locator, in);
    }

    /**
     * Reads a graph using the specified format from an input stream. The input stream is
     * closed after the operation.
     *
     * @param format the format to use
     * @param graph the graph to populate
     * @param locator if the format {@link Format#supportsLayout() supports layout information},
     *      and such information exists in the input, it is used to populate this locator
     * @param in the input stream from which to read the graph
     * @throws IOException if an error occurs during reading
     */
    public static void read(Format format, Graph graph, Locator locator, InputStream in) throws IOException {
        if (in == null) throw new IllegalArgumentException("null InputStream");
        try {
            format.reader().read(graph, locator, in);
        } finally {
            in.close();
        }
    }
}