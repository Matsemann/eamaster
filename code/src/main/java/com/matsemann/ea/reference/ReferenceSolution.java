package com.matsemann.ea.reference;

import com.matsemann.ea.ipc.Task;
import com.matsemann.simulation.setup.SpokeAngles;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Holding stuff so it can easily be converted to and from JSON
 */
public class ReferenceSolution implements Serializable {

    private static final long serialVersionUID = -65675673300959375L;


    public Task task;
    public double[] objectives;

    public ReferenceSolution() {

    }

    public ReferenceSolution(Task task, double[] objectives) {
        this.task = task;
        this.objectives = objectives;
    }

    @Override
    public String toString() {
        return "ReferenceSolution{" +
                "task=" + task +
                ", objectives=" + Arrays.toString(objectives) +
                '}';
    }
}
