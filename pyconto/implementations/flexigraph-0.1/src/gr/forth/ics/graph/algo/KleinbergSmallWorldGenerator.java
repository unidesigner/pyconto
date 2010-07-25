package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class KleinbergSmallWorldGenerator {
    private final int latticeSize;
    private final int longRangeDistanceDistributionsSize;
    private final double clusteringExponent;
    private final Random random;

    KleinbergSmallWorldGenerator(Random random, int latticeSize, double clusteringExponent) {
        this.random = random;
        this.latticeSize = latticeSize;
        this.longRangeDistanceDistributionsSize = latticeSize * 1000;
        this.clusteringExponent = clusteringExponent;
    }

    void generate(Graph graph) {
        Generators.createFoldedGrid(graph, latticeSize, latticeSize);
        List<Node> nodes = graph.nodes().drainToList();

        int[] longRangeDistanceDistributions = computeLongDistanceEdgeDistributionSample();

        int numNodes = (int) Math.pow(latticeSize, 2);

        //Add long range connections
        for (int i = 0; i < numNodes; i++) {
            //choose random distance
            int sampleNodeIndex = random.nextInt(longRangeDistanceDistributionsSize);
            int randomDistance = longRangeDistanceDistributions[sampleNodeIndex];
            while (true) {
                int randomNodeIndex = simulatePath(i, randomDistance);
                Node source = nodes.get(i);
                Node target = nodes.get(randomNodeIndex);
                if (!graph.areAdjacent(source, target, Direction.OUT)) {
                    graph.newEdge(source, target);
                    break;
                }
            }
        }
    }

    private int pickValue(boolean[] choices) {
        int totalNumChoicesLeft = 0;
        for (int x = 0; x < choices.length; x++) {
            if (choices[x]) {
                totalNumChoicesLeft++;
            }
        }
        
        double randValue = random.nextDouble();
        int choice;
        out: while (true) {
            for (int i = 1; i <= totalNumChoicesLeft; i++) {
                if (randValue < (double)i / totalNumChoicesLeft) {
                    choice = i;
                    break out;
                }
            }
            throw new AssertionError("Cannot reach here");
        }
        int currentChoice = 1;
        for (int j = 0; j < choices.length; j++) {
            if (choices[j]) {
                if (currentChoice == choice) {
                    return j + 1;
                }
                currentChoice++;
            }
        }
        return currentChoice;
    }

    private int simulatePath(int index, int distance) {
        //1 = up,2 = down,3 = left, 4 = right
        boolean[] currentChoices = new boolean[4];
        Arrays.fill(currentChoices, true);

        int numSteps = 0;
        int currentChoice = 0;
        int newIndex = 0;
        int xCoordinate = index / latticeSize;
        int yCoordinate = index % latticeSize;

        while (numSteps < distance) {
            currentChoice = pickValue(currentChoices);
            switch (currentChoice) {
                case 1:
                    currentChoices[1] = false;
                    newIndex = upIndex(xCoordinate, yCoordinate);
                    break;
                case 2:
                    currentChoices[0] = false;
                    newIndex = downIndex(xCoordinate, yCoordinate);
                    break;
                case 3:
                    currentChoices[3] = false;
                    newIndex = leftIndex(xCoordinate, yCoordinate);
                    break;
                case 4:
                    currentChoices[2] = false;
                    newIndex = rightIndex(xCoordinate, yCoordinate);
                    break;
            }
            xCoordinate = newIndex / latticeSize;
            yCoordinate = newIndex % latticeSize;
            numSteps++;
        }
        return newIndex;
    }

    public double getClusteringExponent() {
        return this.clusteringExponent;
    }

    private int[] computeLongDistanceEdgeDistributionSample() {
        int numLongRangeLevels = latticeSize - 2;
        if ((latticeSize % 2) == 0) {
            numLongRangeLevels = latticeSize - 1;
        }

        double[] probDists = new double[numLongRangeLevels];
        double normalizingFactor = 0;
        int currentDistance = 2;
        for (int i = 0; i < numLongRangeLevels; i++) {
            probDists[i] = Math.pow(currentDistance, -1 *
                    this.clusteringExponent);
            normalizingFactor += probDists[i];
            currentDistance++;
        }

        for (int i = 0; i < numLongRangeLevels; i++) {
            probDists[i] /= normalizingFactor;
        }

        int[] longRangeDistanceDistributions = new int[longRangeDistanceDistributionsSize];


        for (int i = 0; i < longRangeDistanceDistributionsSize; i++) {
            currentDistance = 2;
            double currentCumProb = 0;
            double randomVal = Math.random();

            for (int j = 0; j < numLongRangeLevels; j++) {
                currentCumProb += probDists[j];
                if (randomVal < currentCumProb) {
                    longRangeDistanceDistributions[i] = currentDistance;
                    break;
                }
                currentDistance += 1;
            }
        }
        return longRangeDistanceDistributions;
    }

    private int upIndex(int currentLatticeRow, int currentLatticeColumn) {
        if (currentLatticeRow == 0) {
            return latticeSize * (latticeSize - 1) + currentLatticeColumn;
        } else {
            return (currentLatticeRow - 1) * latticeSize + currentLatticeColumn;
        }
    }

    private int downIndex(int currentLatticeRow, int currentLatticeColumn) {
        if (currentLatticeRow == latticeSize - 1) {
            return currentLatticeColumn;
        } else {
            return (currentLatticeRow + 1) * latticeSize + currentLatticeColumn;
        }
    }

    private int leftIndex(int currentLatticeRow, int currentLatticeColumn) {
        if (currentLatticeColumn == 0) {
            return currentLatticeRow * latticeSize + latticeSize - 1;
        } else {
            return currentLatticeRow * latticeSize + currentLatticeColumn - 1;
        }
    }

    private int rightIndex(int currentLatticeRow, int currentLatticeColumn) {
        if (currentLatticeColumn == latticeSize - 1) {
            return currentLatticeRow * latticeSize;
        } else {
            return currentLatticeRow * latticeSize + currentLatticeColumn + 1;
        }
    }
}
