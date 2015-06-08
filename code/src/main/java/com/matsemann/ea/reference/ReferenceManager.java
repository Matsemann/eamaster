package com.matsemann.ea.reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsemann.common.LoggerUtil;
import com.matsemann.ea.ipc.Task;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class ReferenceManager {

    Logger logger = LoggerUtil.getLogger(getClass());

    String problem;

    public ReferenceManager(String problem) {
        this.problem = problem;
    }

    public void mergeSolutions(NondominatedPopulation newPopulation) {

        logger.info("Input is {} solutions", newPopulation.size());

        NondominatedPopulation existing = getReferenceSet();
        int nrChanged = 0;

        for (Solution solution : newPopulation) {
            if (existing.add(solution)) {
                nrChanged++;
            }
        }

        ReferenceSolution[] referenceSolutions = new ReferenceSolution[existing.size()];

        int count = 0;
        for (Solution solution : existing) {
            ReferenceSolution rs = new ReferenceSolution((Task) solution.getAttribute("task"), solution.getObjectives());
            referenceSolutions[count++] = rs;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File("pf/" + problem + ".txt"), referenceSolutions); // clears the original file
            logger.info("Merged {} new solutions for {}", nrChanged, problem);
        } catch (IOException e) {
            logger.info("Error writing to file for problem {}", problem, e);
        }

    }

    public NondominatedPopulation getReferenceSet() {
        NondominatedPopulation population = new NondominatedPopulation();

        ObjectMapper mapper = new ObjectMapper();

        try {
            ReferenceSolution[] referenceSolutions = mapper.readValue(new File("pf/" + problem + ".txt"), ReferenceSolution[].class);

            for (ReferenceSolution rs : referenceSolutions) {
                Solution solution = new Solution(rs.objectives);
                solution.setAttribute("task", rs.task);
                population.add(solution);
            }
            logger.info("Getting reference solutions for {}", problem);

        } catch (IOException e) {
            logger.error("Error reading reference solutions for problem {}", problem, e);
        }

        return population;
    }
}
