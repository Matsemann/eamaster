package com.matsemann.ea.ipc;

import com.matsemann.ea.problem.WheelProblem;
import com.matsemann.simulation.setup.SpokeAngles;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the evalution function to one that can communicate with other processes
 */
public class DistributedNSGAII extends NSGAII {


    public DistributedNSGAII(Problem problem, NondominatedSortingPopulation population, EpsilonBoxDominanceArchive archive, Selection selection, Variation variation, Initialization initialization) {
        super(problem, population, archive, selection, variation, initialization);
    }


    @Override
    public void evaluateAll(Iterable<Solution> solutions) {
//        super.evaluateAll(solutions);

        if (!(problem instanceof WheelProblem)) {
            throw new IllegalArgumentException("Wheel problems only");
        }

        List<Task> workToDo = new ArrayList<>();

        for (Solution solution : solutions) {
            SpokeAngles spokeAngles = ((WheelProblem) problem).interpret(solution);
            Task task = new Task(spokeAngles);
            solution.setAttribute("task", task);

            workToDo.add(task);
        }

        // Blocks until all tasks are done
        Distributer.getInstance().runTasks(workToDo);

//
        for (Solution solution : solutions) {
            evaluate(solution);
        }
    }
}
