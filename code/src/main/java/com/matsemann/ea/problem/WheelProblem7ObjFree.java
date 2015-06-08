package com.matsemann.ea.problem;

import com.matsemann.ea.fitness.Rep7;
import com.matsemann.ea.fitness.RepFull;
import com.matsemann.ea.ipc.Task;
import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.simulation.setup.SimulationConstants;
import com.matsemann.simulation.setup.SpokeAngles;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class WheelProblem7ObjFree  extends AbstractProblem implements WheelProblem {

    public WheelProblem7ObjFree() {
        super(SimulationConstants.NUM_SPOKES_ONE_SIDE, 7);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(numberOfVariables, numberOfObjectives);

        for (int i = 0; i < numberOfVariables; i++) {
            solution.setVariable(i, new RealVariable(-90, 90));
        }

        return solution;
    }

    @Override
    public void evaluate(Solution solution) {
//        SimulationResult simulationResult = (SimulationResult) solution.getAttribute("simulationResult");
        Task task = (Task) solution.getAttribute("task");
        // TODO check if result matches task?
        if (task == null) {
            throw new IllegalStateException("simulationresult is null");
        }

        Rep7 rep = new Rep7(task.result);

        solution.setObjective(0, rep.maxDstXY);
        solution.setObjective(1, rep.maxDstZ);
        solution.setObjective(2, rep.maxRotX);
        solution.setObjective(3, rep.maxRotY);
        solution.setObjective(4, rep.maxRotZ);
        solution.setObjective(5, rep.spokeMax);
        solution.setObjective(6, rep.spokeMaxDiff);
    }

    @Override
    public SpokeAngles interpret(Solution solution) {
        double[] values = EncodingUtils.getReal(solution);

        SpokeAngles angles = new SpokeAngles();
        angles.createFromArray(values);

        return angles;
    }

    public static NondominatedPopulation getReferenceSet() {
        return new ReferenceManager("wheel-7obj").getReferenceSet();
    }
}
