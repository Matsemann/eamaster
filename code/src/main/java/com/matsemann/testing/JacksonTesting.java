package com.matsemann.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsemann.ea.ipc.Task;
import com.matsemann.ea.reference.ReferenceSolution;
import com.matsemann.simulation.result.SimulationResult;
import com.matsemann.simulation.setup.SpokeAngles;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class JacksonTesting {

    public static void main(String[] args) {
        SpokeAngles angles = new SpokeAngles();
        angles.createRandom();

        SimulationResult result = new SimulationResult();
        result.spokeMaxDiff = 1;
        result.spokeMax = 2;
        result.maxDstXY = 3;
        result.maxRotY = 0.010101019f;

        Task task = new Task();
        task.result = result;
        task.angles = angles;

        ReferenceSolution[] rs = new ReferenceSolution[2];
        rs[0] = new ReferenceSolution(task, new double[]{1, 2, 3, 4});
        rs[1] = new ReferenceSolution(task, new double[]{2, 3, 4, 5});

        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            System.out.println(Arrays.toString(rs));
            objectMapper.writeValue(new File("test.txt"), rs);
            ReferenceSolution[] rs2 = objectMapper.readValue(new File("test.txt"), ReferenceSolution[].class);
            System.out.println(Arrays.toString(rs2));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
