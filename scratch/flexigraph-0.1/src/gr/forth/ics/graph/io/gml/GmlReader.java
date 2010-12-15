package gr.forth.ics.graph.io.gml;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.io.GraphReader;
import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.layout.Locator;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class GmlReader implements GraphReader {

    private static final GmlReader instance = new GmlReader();

    protected GmlReader() {
    }

    public static GmlReader instance() {
        return instance;
    }

    public void read(Graph graph, Locator locator, InputStream in) throws IOException {
        Deque<Handler> stack = new ArrayDeque<Handler>();
        stack.push(Handlers.ROOT);
        State state = new State(graph, locator, new Scanner(in));

        while (!stack.isEmpty() && state.hasNextLine()) {
            Handler handler = stack.peek();
            Handler next = handler.proceed(state);
            if (next != null) {
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        if (stack.size() > 1) {
            throw new IllegalStateException("Not well-formed input");
        }
    }
}


class State {
    final Graph graph;
    private final Scanner scanner;
    final Locator locator;
    private final Map<Integer, Node> nodes = new HashMap<Integer, Node>();
    private int lineNo = 0;

    private Node lastNode;
    private Edge lastEdge;
    private Rectangle2D.Double rectangle = new Rectangle.Double();
    private Node lastSource;
    private List<GPoint> lastBends = new ArrayList<GPoint>();
    private Double lastX;

    State(Graph graph, Locator locator, Scanner scanner) {
        this.graph = graph;
        this.scanner = scanner;
        this.locator = locator;
    }

    Node getOrCreate(int id) {
        Integer oid = id;
        Node node = nodes.get(oid);
        if (node == null) {
            node = graph.newNode();
            nodes.put(oid, node);
        }
        return node;
    }

    void resetNodeRectangle() {
        rectangle = new Rectangle.Double();
    }

    void resetBends() {
        lastBends = new ArrayList<GPoint>();
    }

    Node getLastNode() {
        if (lastNode == null) {
            throw error("Expected a node to be in context");
        }
        return lastNode;
    }

    void setLastSource(Node node) { lastSource = node; }

    Node getLastSource() {
        if (lastSource == null) {
            throw error("Expected a node source to be in context");
        }
        return lastSource;
    }

    void setLastNode(Node node) { lastNode = node; }

    Edge getLastEdge() {
        if (lastEdge == null) {
            throw error("Expected an edge to be in context");
        }
        return lastEdge;
    }

    void setLastX(Double x) { lastX = x; }

    double getLastX() {
        if (lastX == null) {
            throw error("Expected an X coordinate to be in context");
        }
        return lastX;
    }

    void setLastEdge(Edge edge) { lastEdge = edge; }

    List<GPoint> getLastBends() {
        return lastBends;
    }

    Rectangle2D.Double getRectangle() {
        return rectangle;
    }
    
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    private static final Pattern whitespace = Pattern.compile("\\s+");

    String[] nextLine() {
        String[] line = whitespace.split(scanner.nextLine());
        if (line[0].equals("")) {
            line = Arrays.copyOfRange(line, 1, line.length);
        }
        line[0] = line[0].toLowerCase();
        lineNo++;
        return line;
    }

    private void expectLine(String expectedLine) {
        String line = scanner.nextLine().trim();
        lineNo++;
        if (!line.equals(expectedLine)) {
            throw expectationError(expectedLine, line);
        }
    }

    private RuntimeException expectationError(String expected, String found) {
        return error("Expected '" + expected + "' at line " + lineNo + ", found '" + found + "'");
    }

    private RuntimeException error(String error) {
        return new IllegalStateException(error + " at line " + lineNo);
    }

    void nest() {
        expectLine("[");
    }
    
    int parseInt(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw expectationError("integer", token);
        }
    }

    double parseDouble(String token) {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw expectationError("double", token);
        }
    }

    private static final Pattern quotation = Pattern.compile("\"(.*?)\"");
    String parseQuoted(String token) {
        Matcher matcher = quotation.matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw expectationError("quoted (\"...\") text", token);
        }
    }
}
interface Handler {
    Handler proceed(State state);
}

enum Handlers implements Handler {
    ROOT() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("graph")) {
                    state.nest();
                    return GRAPH;
                }
            }
            return null;
        }
    },
    GRAPH() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();

                if (line[0].equals("node")) {
                    state.nest();
                    return NODE;
                } else if (line[0].equals("edge")) {
                    state.nest();
                    return EDGE;
                } else if (line[0].equals("label")) {
                    state.graph.tuple().setValue(state.parseQuoted(line[1]));
                } else if (line[0].equals("]")) {
                    return null;
                }
            }
            return null;
        }
    },

    NODE() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("id")) {
                    int id = state.parseInt(line[1]);
                    state.setLastNode(state.getOrCreate(id));
                } else if (line[0].equals("label")) {
                    state.getLastNode().setValue(state.parseQuoted(line[1]));
                } else if (line[0].equals("graphics")) {
                    state.nest();
                    return NODE_GRAPHICS;
                } else if (line[0].equals("[")) {
                    return IGNORED_BLOCK;
                } else if (line[0].equals("]")) {
                    return null;
                }
            }
            return null;
        }
    },

    NODE_GRAPHICS() {
        public Handler proceed(State state) {
            state.resetNodeRectangle();
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("x")) {
                    state.getRectangle().x = state.parseDouble(line[1]);
                } else if (line[0].equals("y")) {
                    state.getRectangle().y = state.parseDouble(line[1]);
                } else if (line[0].equals("w")) {
                    state.getRectangle().width = state.parseDouble(line[1]);
                } else if (line[0].equals("h")) {
                    state.getRectangle().height = state.parseDouble(line[1]);
                } else if (line[0].equals("]")) {
                    Rectangle2D.Double rect = state.getRectangle();
                    //actually, (x,y) represents the midpoint!
                    rect.x -= rect.width / 2;
                    rect.y -= rect.height / 2;
                    state.locator.setLocation(state.getLastNode(), GRect.wrap(rect).midPoint());
                    return null;
                }
            }
            return null;
        }
    },

    EDGE() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("id")) {
                } else if (line[0].equals("source")) {
                    state.setLastSource(state.getOrCreate(state.parseInt(line[1])));
                } else if (line[0].equals("target")) {
                    Node target = state.getOrCreate(state.parseInt(line[1]));
                    Edge edge = state.graph.newEdge(state.getLastSource(), target);
                    state.setLastEdge(edge);
                } else if (line[0].equals("label")) {
                    state.getLastEdge().setValue(state.parseQuoted(line[1]));
                } else if (line[0].equals("graphics")) {
                    state.nest();
                    return EDGE_GRAPHICS;
                } else if (line[0].equals("[")) {
                    return IGNORED_BLOCK;
                } else if (line[0].equals("]")) {
                    return null;
                }
            }
            return null;
        }
    },

    EDGE_GRAPHICS() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("line")) {
                    state.nest();
                    state.resetBends();
                    return EDGE_LINE;
                } else if (line[0].equals("]")) {
                    return null;
                }
            }
            return null;
        }
    },

    EDGE_LINE() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("point")) {
                    state.nest();
                    return POINT;
                } else if (line[0].equals("]")) {
                    state.locator.setBends(state.getLastEdge(), state.getLastBends());
                    return null;
                }
            }
            return null;
        }
    },

    POINT() {
        public Handler proceed(State state) {
            state.setLastX(null);
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("x")) {
                    state.setLastX(state.parseDouble(line[1]));
                } else if (line[0].equals("y")) {
                    double x = state.getLastX();
                    double y = state.parseDouble(line[1]);
                    state.getLastBends().add(new GPoint(x, y));
                    state.setLastX(null);
                } else if (line[0].equals("]")) {
                    return null;
                }
            }
            return null;
        }
    },

    IGNORED_BLOCK() {
        public Handler proceed(State state) {
            while (state.hasNextLine()) {
                String[] line = state.nextLine();
                if (line[0].equals("]")) {
                    return null;
                }
            }
            return null;
        }
    };
}