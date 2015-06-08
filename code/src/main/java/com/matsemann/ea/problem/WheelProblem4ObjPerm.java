package com.matsemann.ea.problem;

import com.matsemann.ea.fitness.RepFull;
import com.matsemann.ea.ipc.Task;
import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.simulation.setup.SimulationConstants;
import com.matsemann.simulation.setup.SpokeAngles;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class WheelProblem4ObjPerm extends AbstractProblem implements WheelProblem {


    public WheelProblem4ObjPerm() {
        super(1, 4);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(numberOfVariables, numberOfObjectives);

        solution.setVariable(0, new Permutation(SimulationConstants.NUM_SPOKES_ONE_SIDE));

        return solution;
    }

    @Override
    public void evaluate(Solution solution) {
        Task task = (Task) solution.getAttribute("task");

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
        int[] permutation = EncodingUtils.getPermutation(solution.getVariable(0));

        SpokeAngles angles = new SpokeAngles();
        angles.createPermutation(permutation);

        return angles;
    }

    public static NondominatedPopulation getReferenceSet() {
        return new ReferenceManager("wheel-4obj").getReferenceSet();
    }

}
