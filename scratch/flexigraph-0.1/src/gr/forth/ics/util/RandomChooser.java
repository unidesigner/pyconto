package gr.forth.ics.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * A random choice maker, where each choice is associated with a probability. This implementation
 * is based on the fast <em>alias</em> method, where for each random choice two random
 * numbers are generated and only a single table lookup performed.
 *
 * @param <T> the type of the choices to be made
 * @see <a href="http://cg.scs.carleton.ca/~luc/rnbookindex.html">L. Devroye, Non-Uniform Random Variate Generation, 1986, p. 107</a>
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class RandomChooser<T> {
    private final double[] probs;
    private final int[] indexes;
    private final List<T> events;
    
    private final Random random;
    
    private RandomChooser(List<Double> weights, List<T> events, Random random) {
        double sum = 0.0;
        for (double prob : weights) sum += prob;

        this.probs = new double[weights.size()];
        for (int i = 0; i < weights.size(); i++) {
            probs[i] = weights.get(i) * weights.size() / sum; //average = 1.0
        }

        Deque<Integer> smaller = new ArrayDeque<Integer>(weights.size() / 2 + 2);
        Deque<Integer> greater = new ArrayDeque<Integer>(weights.size() / 2 + 2);
        for (int i = 0; i < probs.length; i++) {
            if (probs[i] < 1.0) {
                smaller.push(i);
            } else {
                greater.push(i);
            }
        }
        indexes = new int[weights.size()];
        while (!smaller.isEmpty()) {
            Integer i = smaller.pop();
            Integer k = greater.peek();
            indexes[i] =  k;
            probs[k] -= (1 - probs[i]);
            if (probs[k] < 1.0) {
                greater.pop();
                smaller.push(k);
            }
        }
        this.events = events;
        this.random = random;
    }

    /**
     * Returns a random choice.
     *
     * @return a random choice
     * @see RandomChooserBuilder about how to configure the available choices
     */
    public T choose() {
        int index = random.nextInt(probs.length);
        double x = random.nextDouble();
        return x < probs[index] ? events.get(index) : events.get(indexes[index]);
    } 

    /**
     * Creates a builder of a {@link RandomChooser} instance. The builder is responsible
     * for configuring the choices and probabilities of the random chooser.
     *
     * @param <T> the type of the choices that will be randomly made
     * @return a builder of a {@code RandomChooser} object
     */
    public static <T> RandomChooserBuilder<T> newInstance() {
        return new RandomChooserBuilder<T>();
    }

    /**
     * A builder of {@link RandomChooser}.
     * 
     * @param <T> the type of the choices that the created {@code RandomChooser} will make
     */
    public static class RandomChooserBuilder<T> {
        private final List<Double> probs = new ArrayList<Double>();
        private final List<T> events = new ArrayList<T>();
        private Random random = new Random(0);

        private RandomChooserBuilder() { }

        /**
         * Adds the possibility of a given choice, weighted by a relative probability.
         * (Relative means that it is not needed that all probabilities have sum {@code 1.0}).
         *
         * @param choice a possible choice
         * @param prob the relative probability of the choice; must be {@code >= 0}
         * @return this
         */
        public RandomChooserBuilder<T> choice(T choice, double prob) {
            Args.gte(prob, 0.0);
            Args.notNull(choice);
            probs.add(prob);
            events.add(choice);
            return this;
        }

        /**
         * Specifies the random number generator to be used by the created {@link RandomChooser}.
         *
         * @param random the random number generator to use
         * @return this
         */
        public RandomChooserBuilder<T> setRandom(Random random) {
            this.random = random;
            return this;
        }

        /**
         * Builds a {@link RandomChooser} instance, ready to make random choices based on the
         * probabilities configured by this builder.
         *
         * @return a {@code RandomChooser}
         */
        public RandomChooser<T> build() {
            if (probs.isEmpty()) {
                throw new IllegalStateException("No choice was defined");
            }
            return new RandomChooser<T>(
                    new ArrayList<Double>(probs),
                    new ArrayList<T>(events),
                    random);
        }
    }
}
