package gr.forth.ics.util;

import java.util.*;

public class EventSupport<L> {
    private List<L> listeners = null;
    
    public List<L> getListeners() {
        return listeners != null ? Collections.unmodifiableList(listeners) : Collections.<L>emptyList();
    }
    
    private void lazyInit() {
        if (listeners == null) {
            listeners = new InverseArrayList<L>();
        }
    }
    
    public void addListener(L listener) {
        if (listener == null) {
            return;
        }
        lazyInit();
        listeners.add(listener);
    }
    
    public void removeListener(L listener) {
        if (listener == null || listeners == null) {
            return;
        }
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            listeners = null;
        }
    }
    
    public int getListenerCount() {
        if (listeners == null) {
            return 0;
        }
        return listeners.size();
    }
    
    public boolean isEmpty() {
        if (listeners == null) {
            return true;
        }
        return listeners.isEmpty();
    }
    
    private class InverseArrayList<L> extends ArrayList<L> {
          public Iterator<L> iterator() {
                return new Iterator<L>() {
                      int pos = size() - 1;
                      
                      public boolean hasNext() {
                            return pos >= 0;
                      }
                      
                      public L next() {
                            if (!hasNext()) {
                                  throw new NoSuchElementException();
                            }
                            return get(pos--);
                      }
                      
                      public void remove() {
                            throw new UnsupportedOperationException();
                      }
                };
          }
    }
}
