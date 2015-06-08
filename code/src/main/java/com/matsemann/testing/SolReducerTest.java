package com.matsemann.testing;

import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.ea.reference.SolutionReducer;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.Arrays;

public class SolReducerTest {


    public static void main(String[] args) {
        new SolReducerTest();
    }

    public SolReducerTest() {
        NondominatedPopulation referenceSet = new ReferenceManager("DTLZ2_2").getReferenceSet();

        System.out.println(referenceSet.size());

        for (Solution s : referenceSet) {
            System.out.println(Arrays.toString(s.getObjectives()));
        }


        NondominatedPopulation reduce = new SolutionReducer().reduce(referenceSet, 3);

        System.out.println("-------------------");
        System.out.println(reduce.size());



        for (Solution s : reduce) {
            System.out.println(Arrays.toString(s.getObjectives()));
        }


    }
}
