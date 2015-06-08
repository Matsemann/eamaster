package com.matsemann.testing;

import com.matsemann.ea.ipc.Task;
import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.simulation.result.SimulationResult;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.HashMap;
import java.util.Map;

public class NormValues {

    public static void main(String[] args) {
        new NormValues();
    }

    /*

    maxDstXY, Min:0.405419, max:0.5499475, avg:0.42782837
    maxDstZ, Min:0.010688636, max:0.053830303, avg:0.016857559
    maxRotX, Min:0.015059864, max:0.7086252, avg:0.15746933
    maxRotY, Min:0.016224137, max:0.3526872, avg:0.14601497
    maxRotZ, Min:10.347974, max:20.271822, avg:11.254939
    spokeMax, Min:2107.8157, max:3163.905, avg:2208.7834
    spokeMaxDiff, Min:1198.0934, max:1758.5621, avg:1282.955

     */

    public NormValues() {
        NondominatedPopulation referenceSet = new ReferenceManager("wheel-4obj-free").getReferenceSet();

        Map<String, Float> max = new HashMap<>();
        Map<String, Float> min = new HashMap<>();
        Map<String, Float> sum = new HashMap<>();

        String[] values = {"maxDstXY", "maxDstZ", "maxRotX", "maxRotY", "maxRotZ", "spokeMax", "spokeMaxDiff"};


        for (String value : values) {
            max.put(value, Float.MIN_VALUE);
            min.put(value, Float.MAX_VALUE);
            sum.put(value, 0f);
        }

        int i = 0;
        for (Solution s : referenceSet) {
            System.out.println(i++);
            Task task = (Task) s.getAttribute("task");
            SimulationResult result = task.result;

            max.put("maxDstXY", Math.max(max.get("maxDstXY"), result.maxDstXY));
            max.put("maxDstZ", Math.max(max.get("maxDstZ"), result.maxDstZ));
            max.put("maxRotX", Math.max(max.get("maxRotX"), result.maxRotX));
            max.put("maxRotY", Math.max(max.get("maxRotY"), result.maxRotY));
            max.put("maxRotZ", Math.max(max.get("maxRotZ"), result.maxRotZ));
            max.put("spokeMax", Math.max(max.get("spokeMax"), result.spokeMax));
            max.put("spokeMaxDiff", Math.max(max.get("spokeMaxDiff"), result.spokeMaxDiff));


            min.put("maxDstXY", Math.min(min.get("maxDstXY"), result.maxDstXY));
            min.put("maxDstZ", Math.min(min.get("maxDstZ"), result.maxDstZ));
            min.put("maxRotX", Math.min(min.get("maxRotX"), result.maxRotX));
            min.put("maxRotY", Math.min(min.get("maxRotY"), result.maxRotY));
            min.put("maxRotZ", Math.min(min.get("maxRotZ"), result.maxRotZ));
            min.put("spokeMax", Math.min(min.get("spokeMax"), result.spokeMax));
            min.put("spokeMaxDiff", Math.min(min.get("spokeMaxDiff"), result.spokeMaxDiff));


            sum.put("maxDstXY", sum.get("maxDstXY") + result.maxDstXY);
            sum.put("maxDstZ", sum.get("maxDstZ") + result.maxDstZ);
            sum.put("maxRotX", sum.get("maxRotX") + result.maxRotX);
            sum.put("maxRotY", sum.get("maxRotY") + result.maxRotY);
            sum.put("maxRotZ", sum.get("maxRotZ") + result.maxRotZ);
            sum.put("spokeMax", sum.get("spokeMax") + result.spokeMax);
            sum.put("spokeMaxDiff", sum.get("spokeMaxDiff") + result.spokeMaxDiff);
        }

        for (String value : values) {
            System.out.println(value + ", Min:" + min.get(value) + ", max:" + max.get(value) + ", avg:" + (sum.get(value) / referenceSet.size()));
        }

    }
}
