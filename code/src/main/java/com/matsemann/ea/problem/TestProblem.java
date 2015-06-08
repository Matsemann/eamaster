package com.matsemann.ea.problem;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import java.io.Serializable;
import java.util.Map;

public class TestProblem extends AbstractProblem {

    public TestProblem() {
        super(5, 2);
    }

    @Override
    public Solution newSolution() {
        Solution newSolution = new Solution(numberOfVariables, numberOfObjectives);

        for (int i = 0; i < numberOfVariables; i++) {
            newSolution.setVariable(i, new RealVariable(0, 10));
        }

        return newSolution;
    }

    @Override
    public void evaluate(Solution solution) {
        double[] variables = EncodingUtils.getReal(solution);

        double x = 0, y = 0;
        for (int i = 0; i < variables.length; i++) {
            x -= variables[i];
            y = variables[i]*i;
        }

        solution.setObjective(0, x);
        solution.setObjective(1, y);
    }

    public static NondominatedPopulation getReferenceSet() {

        NondominatedPopulation population = new NondominatedPopulation();
        population.add(new Solution(new double[]{-50, 40}));
        population.add(new Solution(new double[]{-40, 0}));

//        return null;
        return population;
    }
}
