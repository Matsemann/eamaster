package com.matsemann.ea.ipc;

import com.matsemann.simulation.result.SimulationResult;
import com.matsemann.simulation.setup.SpokeAngles;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 2221281061665828140L;


    public SpokeAngles angles;
    public SimulationResult result;

    public Task() {

    }

    public Task(SpokeAngles angles) {
        this.angles = angles;
    }

    public Task(SpokeAngles angles, SimulationResult result) {
        this.angles = angles;
        this.result = result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "angles=" + angles +
                ", result=" + result +
                '}';
    }
}
