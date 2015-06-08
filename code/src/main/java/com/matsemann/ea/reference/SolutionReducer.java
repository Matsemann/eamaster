package com.matsemann.ea.reference;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolutionReducer {

    public NondominatedPopulation reduce(NondominatedPopulation population, int size) {


        List<ReducedSolution> rs = new ArrayList<>();

        int objectives = population.get(0).getNumberOfObjectives();
        double[] minVal = new double[objectives];
        double[] maxVal = new double[objectives];
        Arrays.fill(minVal, Double.POSITIVE_INFINITY);
        Arrays.fill(maxVal, Double.NEGATIVE_INFINITY);

        for (Solution s : population) {
            for (int i = 0; i < objectives; i++) {
                minVal[i] = Math.min(minVal[i], s.getObjective(i));
                maxVal[i] = Math.max(maxVal[i], s.getObjective(i));
            }
        }

        for (Solution solution : population) {
            rs.add(new ReducedSolution(solution, objectives, minVal, maxVal));
        }

        while (rs.size() > size) {
            ReducedSolution min1 = null, min2 = null;
            double minDst = Double.POSITIVE_INFINITY;

            for (ReducedSolution r1 : rs) {
                for (ReducedSolution r2 : rs) {
                    if (r1 == r2) {
                        continue;
                    }

                    double dst = distance(r1, r2);
                    if (dst < minDst) {
                        minDst = dst;
                        min1 = r1;
                        min2 = r2;
                    }
                }
            }

            min1.merge(min2);
            rs.remove(min2);
        }

        NondominatedPopulation reducedPopulation = new NondominatedPopulation();

        for (ReducedSolution r : rs) {
            reducedPopulation.add(r.getClosestToCenter());
        }

        reducedPopulation.addAll(getExtremePoints(population));

        return reducedPopulation;
    }

    private List<Solution> getExtremePoints(NondominatedPopulation population) {
        List<Solution> extremeSolutions = new ArrayList<>();

        int numberOfObjectives = population.get(0).getNumberOfObjectives();

        for (int i = 0; i < numberOfObjectives; i++) {

            double minVal = Double.POSITIVE_INFINITY;
            Solution best = null;

            for (Solution s : population) {
                double value = s.getObjective(i);
                if (value < minVal) {
                    minVal = value;
                    best = s;
                }
            }

            extremeSolutions.add(best);
        }

        return extremeSolutions;
    }


    private double distance(Solution r1, Solution r2) {
        double distance = 0.0;

        for (int i = 0; i < r1.getNumberOfObjectives(); i++) {
            distance += Math.pow(Math.abs(r1.getObjective(i) -
                    r2.getObjective(i)), 2);
        }

        return Math.pow(distance, 1.0 / 2.0);
    }


    public class ReducedSolution extends Solution {

        List<Solution> solutions = new ArrayList<>();
        private double[] minVal;
        private double[] maxVal;

        public ReducedSolution(Solution solution, int objectives, double[] minVal, double[] maxVal) {
//            super(solution.getObjectives());
            super(0, objectives);

            this.minVal = minVal;
            this.maxVal = maxVal;
            solutions.add(solution);
            calculateCenter();
        }

        private void calculateCenter() {
            int objectives = getNumberOfObjectives();

            for (int i = 0; i < objectives; i++) {
                double sum = 0;

                for (Solution s : solutions) {
                    sum += s.getObjective(i);
                }

                double normalized = normalize(sum / solutions.size(), i);
                setObjective(i, normalized);
            }
        }

        private double normalize(double value, int objective) {
            return (value - minVal[objective]) / (maxVal[objective] - minVal[objective]);
        }

        public Solution getClosestToCenter() {
            double minDst = Double.POSITIVE_INFINITY;
            Solution closest = null;

            for (Solution s : solutions) {
                double distance = distance(this, s);
                if (distance < minDst) {
                    minDst = distance;
                    closest = s;
                }
            }

            return closest;
        }

        public void merge(ReducedSolution other) {
            solutions.addAll(other.solutions);
            calculateCenter();
        }

    }
}
