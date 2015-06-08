package com.matsemann.ea.provider;

import com.matsemann.ea.problem.*;
import com.matsemann.ea.reference.ReferenceManager;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;

public class WheelProblemProvider extends ProblemProvider {
    @Override
    public Problem getProblem(String name) {
        if (name.equals("TestProblem")) {
            return new TestProblem();
        } else if (name.equals("wheel-4obj-free") || name.equals("Wheel1")) {
            return new WheelProblem4ObjFree();
        } else if (name.equals("wheel-4obj-perm")) {
            return new WheelProblem4ObjPerm();
        } else if (name.equals("wheel-2obj-free")) {
            return new WheelProblem2ObjFree();
        }
        return null;
    }

    @Override
    public NondominatedPopulation getReferenceSet(String name) {
        if (name.startsWith("wheel") || name.startsWith("Wheel")) {
            return new ReferenceManager(name).getReferenceSet();
        }
        return null;

    }
}
