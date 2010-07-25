package gr.forth.ics.graph.io.pajek;

import gr.forth.ics.graph.io.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.Locator;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class PajekReader implements GraphReader {
    private static final PajekReader instance = new PajekReader();

    private PajekReader() { }

    public static PajekReader instance() { return instance; }

    private static final Pattern nodeHeaderPattern = Pattern.compile("\\*Vertices\\s+(\\d+)");
    private static final Pattern nodeLinePattern = Pattern.compile("\\s+(\\d+)\\s+([^\\s]+)");
    private static final Pattern edgeHeaderPattern = Pattern.compile("\\*Edges\\s+");
    private static final Pattern edgeLinePattern = Pattern.compile("(\\d+)\\s+([^\\s]+)");

    public void read(Graph graph, Locator locator, InputStream in) throws IOException {
        try {
            Map<Integer, Node> nodes = new HashMap<Integer, Node>();

            Scanner sc = new Scanner(in);
            Matcher m = match(sc.nextLine(), nodeHeaderPattern);
            final int totalNodes = Integer.parseInt(m.group(1));
            for (int i = 0; i < totalNodes; i++) {
                m = match(sc.nextLine(), nodeLinePattern);
                nodes.put(Integer.parseInt(m.group(1)), graph.newNode(m.group(2)));
            }
            m = match(sc.nextLine(), edgeHeaderPattern);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                m = match(line, edgeLinePattern);
                Node n1 = nodes.get(Integer.parseInt(m.group(1)));
                Node n2 = nodes.get(Integer.parseInt(m.group(2)));
                if (n1 == null || n2 == null) {
                    throw new ParseException("A node identifier is undefined in line: '" + line + "'");
                }
                graph.newEdge(n1, n2);
            }
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    private Matcher match(String line, Pattern pattern) throws IOException {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            throw new ParseException("Line: '" + line + " does not conform to expected pattern: " + pattern);
        }
        return matcher;
    }
}
