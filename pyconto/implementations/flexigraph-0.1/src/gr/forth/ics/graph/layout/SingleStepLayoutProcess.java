package gr.forth.ics.graph.layout;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class SingleStepLayoutProcess extends AbstractLayoutProcess {
    private boolean isDone = false;
    
    public SingleStepLayoutProcess() {
    }

    @Override
    public void step(Locator locator) {
        super.step(locator);
        isDone = true;
    }

    public boolean isDone() {
        return isDone;
    }
}
