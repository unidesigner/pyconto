package gr.forth.ics.graph.io;

import gr.forth.ics.graph.io.gml.GmlReader;
import gr.forth.ics.graph.io.gml.GmlWriter;
import gr.forth.ics.graph.io.pajek.PajekReader;
import gr.forth.ics.graph.io.pajek.PajekWriter;

/**
 * Defines serialization (input and output) formats for graphs.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public enum Format {
    /**
     * The GML format.
     * @see "http://www.infosun.fim.uni-passau.de/Graphlet/GML/"
     */
    GML() {
        @Override GraphWriter writer() {
            return GmlWriter.instance();
        }

        @Override GraphReader reader() {
            return GmlReader.instance();
        }

        public boolean supportsLayout() {
            return true;
        }
    },
    /**
     * The Pajek format.
     *
     * <p> (Please report broken links)
     * 
     * @see "http://vlado.fmf.uni-lj.si/pub/networks/pajek/"
     * @see "http://cneurocvs.rmki.kfki.hu/igraph/doc/html/ch15s05.html"
     */
    PAJEK() {
        @Override GraphWriter writer() {
            return PajekWriter.instance();
        }

        @Override GraphReader reader() {
            return PajekReader.instance();
        }

        public boolean supportsLayout() {
            return false;
        }
    }
    ;

    GraphWriter writer() {
        throw new UnsupportedOperationException("Writing not supported with this format");
    }

    GraphReader reader() {
        throw new UnsupportedOperationException("Reading not supported with this format");
    }

    public abstract boolean supportsLayout();
}
