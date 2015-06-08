package com.matsemann.ea.fitness;

import com.badlogic.gdx.math.Vector2;
import com.matsemann.simulation.result.SimulationResult;

public class Rep7 {

    public float maxDstXY, maxDstZ;
    public float maxRotX, maxRotY, maxRotZ;
    public float spokeMax, spokeMaxDiff;

    public Rep7(SimulationResult result) {
        maxDstXY = result.maxDstXY;
        maxDstZ = result.maxDstZ;
        maxRotX = result.maxRotX;
        maxRotY = result.maxRotY;
        maxRotZ = result.maxRotZ;
        spokeMax = result.spokeMax;
        spokeMaxDiff = result.spokeMaxDiff;
    }
}
