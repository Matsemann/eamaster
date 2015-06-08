package com.matsemann.ea.fitness;

import com.badlogic.gdx.math.Vector2;
import com.matsemann.simulation.result.SimulationResult;

public class Rep2 {
    public float displacement, strength;

    public Rep2(SimulationResult result) {
        displacement = 25 * result.maxDstXY + 1000 * result.maxDstZ + 800 * result.maxRotX + 900 * result.maxRotY;
        strength = 1 * result.maxRotZ + 0.0045f * result.spokeMax + 0.0066f * result.spokeMaxDiff;
    }

}
