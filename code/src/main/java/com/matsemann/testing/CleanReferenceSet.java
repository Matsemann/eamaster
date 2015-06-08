package com.matsemann.testing;

import com.matsemann.ea.reference.ReferenceManager;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class CleanReferenceSet {

    public static void main(String[] args) {
        new CleanReferenceSet();
    }

    public CleanReferenceSet() {
        NondominatedPopulation referenceSet = new ReferenceManager("wheel-4obj-perm").getReferenceSet();

        NondominatedPopulation newPopulation = new NondominatedPopulation();

        for (Solution s : referenceSet) {
            if (s.getObjective(0) > 5 || s.getObjective(1) > 90 || s.getObjective(2) > 5 || s.getObjective(3) > 10000) {
                continue;
            } else {
                newPopulation.add(s);
            }
        }

        new ReferenceManager("wheel-4obj-perm-cleaned").mergeSolutions(newPopulation);
    }
}
