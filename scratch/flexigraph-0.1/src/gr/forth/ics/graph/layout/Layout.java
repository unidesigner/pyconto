package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.InspectableGraph;

public interface Layout {
    LayoutProcess layout(InspectableGraph graph);
}
