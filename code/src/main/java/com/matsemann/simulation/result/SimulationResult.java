package com.matsemann.simulation.result;

import com.badlogic.gdx.math.Vector2;
import com.matsemann.simulation.setup.SimulationConstants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class SimulationResult implements Serializable {


    public float maxDstXY, maxDstZ;
    public float maxRotX, maxRotY, maxRotZ;
    public float spokeMax, spokeMaxDiff;

    public SimulationResult() {

    }

    public SimulationResult(Map<String, SimulationData> simulationData) {
        Vector2 tmp = new Vector2();

        float startDstZ = simulationData.get("init").position.z;
        float startRotZ = simulationData.get("init").rotation.z;

        for (SimulationData data : simulationData.values()) {
            // biggest distance |x+y|
            maxDstXY = Math.max(maxDstXY, tmp.set(data.position.x, data.position.y).len());
            // biggest distance |z| from initial
            maxDstZ = Math.max(maxDstZ, Math.abs(startDstZ - data.position.z));

            maxRotX = Math.max(maxRotX, Math.abs(data.rotation.x));
            maxRotY = Math.max(maxRotY, Math.abs(data.rotation.y));
            maxRotZ = Math.max(maxRotZ, Math.abs(startRotZ - data.rotation.z));
        }

        float[] spokeMaxes = new float[SimulationConstants.NUM_SPOKES];
        float[] spokeMins = new float[SimulationConstants.NUM_SPOKES];

        Arrays.fill(spokeMaxes, Float.NEGATIVE_INFINITY);
        Arrays.fill(spokeMins, Float.POSITIVE_INFINITY);

        for (SimulationData data : simulationData.values()) {
            for (int i = 0; i < data.spokeForces.length; i++) {
                float spokeForce = data.spokeForces[i];
                spokeMaxes[i] = Math.max(spokeMaxes[i], spokeForce);
                spokeMins[i] = Math.min(spokeMins[i], spokeForce);
            }
        }

        for (int i = 0; i < spokeMaxes.length; i++) {
            spokeMax = Math.max(spokeMax, spokeMaxes[i]);
            spokeMaxDiff = Math.max(spokeMaxDiff, spokeMaxes[i] - spokeMins[i]);
        }




        // biggest dst z

        // biggest rotZ relative to start of the two torquetests
        // biggest rotY
        // biggest rotX

        // biggest spoke value
        // biggest difference
            // biggest of every
            // min of every

        // if extreme values, set everything to max?
        // absolute values!
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "maxDstXY=" + maxDstXY +
                ", maxDstZ=" + maxDstZ +
                ", maxRotX=" + maxRotX +
                ", maxRotY=" + maxRotY +
                ", maxRotZ=" + maxRotZ +
                ", spokeMax=" + spokeMax +
                ", spokeMaxDiff=" + spokeMaxDiff +
                '}';
    }
}
