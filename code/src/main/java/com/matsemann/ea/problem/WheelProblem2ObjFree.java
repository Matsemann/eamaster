package com.matsemann.ea.problem;

import com.matsemann.ea.fitness.Rep2;
import com.matsemann.ea.fitness.RepFull;
import com.matsemann.ea.ipc.Task;
import com.matsemann.simulation.setup.SimulationConstants;
import com.matsemann.simulation.setup.SpokeAngles;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class WheelProblem2ObjFree extends AbstractProblem implements WheelProblem  {

    public WheelProblem2ObjFree() {
        super(SimulationConstants.NUM_SPOKES_ONE_SIDE, 2);
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
        Task task = (Task) solution.getAttribute("task");

        if (task == null) {
            throw new IllegalStateException("simulationresult is null");
        }

        Rep2 rep = new Rep2(task.result);

        solution.setObjective(0, rep.displacement);
        solution.setObjective(1, rep.strength);
    }

    @Override
    public SpokeAngles interpret(Solution solution) {
        double[] values = EncodingUtils.getReal(solution);

        SpokeAngles angles = new SpokeAngles();
        angles.createFromArray(values);

        return angles;
    }
}
