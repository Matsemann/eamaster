package com.matsemann.ea.problem;

import com.matsemann.simulation.setup.SpokeAngles;
import org.moeaframework.core.Solution;

public interface WheelProblem {
    public SpokeAngles interpret(Solution solution);
}
