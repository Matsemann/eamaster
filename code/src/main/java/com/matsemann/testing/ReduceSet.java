package com.matsemann.testing;

import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.ea.reference.SolutionReducer;
import org.moeaframework.core.NondominatedPopulation;

public class ReduceSet {

    public static void main(String[] args) {
        new ReduceSet();
    }

    public ReduceSet() {
        NondominatedPopulation referenceSet = new ReferenceManager("wheel-4obj-free").getReferenceSet();

        SolutionReducer solutionReducer = new SolutionReducer();
        NondominatedPopulation reduce = solutionReducer.reduce(referenceSet, 30);

        new ReferenceManager("wheel-4obj-free-reduced").mergeSolutions(reduce);
    }
}
