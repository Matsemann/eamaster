package com.matsemann.ea.problem;

import com.matsemann.ea.fitness.RepFull;
import com.matsemann.ea.ipc.Task;
import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.simulation.result.SimulationResult;
import com.matsemann.simulation.result.SimulationRunner;
import com.matsemann.simulation.setup.SimulationConstants;
import com.matsemann.simulation.setup.SpokeAngles;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class WheelProblem4ObjFree extends AbstractProblem implements WheelProblem {

//    SimulationRunner runner;

    public WheelProblem4ObjFree() {
        super(SimulationConstants.NUM_SPOKES_ONE_SIDE, 4);
//        runner = new SimulationRunner();
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

        RepFull rep = new RepFull(task.result);

        if (rep.dst > 5 || rep.rotZ > 90 || rep.rot > 5 || rep.spokes > 10000) {
            rep = new RepFull();
            rep.spokes = 10000;
            rep.dst = 5;
            rep.rotZ = 90;
            rep.rot = 5;
        }

        solution.setObjective(0, rep.dst);
        solution.setObjective(1, rep.rotZ);
        solution.setObjective(2, rep.rot);
        solution.setObjective(3, rep.spokes);


    }

    @Override
    public SpokeAngles interpret(Solution solution) {
        double[] values = EncodingUtils.getReal(solution);

        SpokeAngles angles = new SpokeAngles();
        angles.createFromArray(values);

        return angles;
    }


}
