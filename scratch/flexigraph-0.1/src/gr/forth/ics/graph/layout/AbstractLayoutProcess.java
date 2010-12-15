package gr.forth.ics.graph.layout;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class AbstractLayoutProcess implements LayoutProcess {
    public AbstractLayoutProcess() {
    }
    
    private void checkDone() {
        if (isDone()) {
            throw new IllegalStateException();
        }
    }

    public void step(Locator locator) {
        checkDone();
        stepImpl(locator);
    }

    public void run(Locator locator) {
        checkDone();
        do {
            step(locator);
        } while (!isDone());
    }
    
    protected abstract void stepImpl(Locator locator);
}
