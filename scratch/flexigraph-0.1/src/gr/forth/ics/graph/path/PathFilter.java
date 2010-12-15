package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Tuple;

/**
 *
 * @deprecated Use {@link Traverser}
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */public abstract class PathFilter {
    protected void init(Path initialPath) { }
    
    public abstract boolean accept(Path path);
    
    protected void end() { }
    
    
    public static PathFilter hamilton() {
        return new NoDuplicateFilter() {
            public boolean accept(Path path) {
                return acceptAndMark(path.tailNode());
            }
        };
    }
    
    public static PathFilter euler() {
        return new NoDuplicateFilter() {
            public boolean accept(Path path) {
                if (path.size() == 0) {
                    return true;
                }
                return acceptAndMark(path.tailEdge());
            }
        };
    }
    
    private static abstract class NoDuplicateFilter extends PathFilter {
        private Object marked;
        
        protected void init(Path initialPath) {
            marked = new Object();
        }
        
        protected void end() {
            marked = null;
        }
        
        protected boolean acceptAndMark(Tuple tuple) {
            if (tuple.has(marked)) {
                return false;
            }
            tuple.putWeakly(marked, null);
            return true;
        }
    }
}
