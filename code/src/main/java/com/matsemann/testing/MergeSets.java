package com.matsemann.testing;

import com.matsemann.ea.reference.ReferenceManager;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class MergeSets {

    public static void main(String[] args) {
        new MergeSets();
    }

    public MergeSets() {
        NondominatedPopulation free = new ReferenceManager("wheel-4obj-free").getReferenceSet();
        NondominatedPopulation perm = new ReferenceManager("wheel-4obj-perm").getReferenceSet();

        NondominatedPopulation combined = new NondominatedPopulation();

        for (Solution solution : free) {
            combined.add(solution);
        }

        for (Solution solution : perm) {
            combined.add(solution);
        }

        System.out.println(combined.size());

        new ReferenceManager("wheel-4obj-combined").mergeSolutions(combined);
    }
}
