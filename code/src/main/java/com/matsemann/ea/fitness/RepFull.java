package com.matsemann.ea.fitness;

import com.badlogic.gdx.math.Vector2;
import com.matsemann.simulation.result.SimulationResult;

public class RepFull {
    public float dst;
    public float rotZ;
    public float rot;
    public float spokes;

    public RepFull() {

    }

    public RepFull(SimulationResult result) {
        dst = new Vector2(result.maxDstXY, result.maxDstZ).len();
        rotZ = result.maxRotZ;
        rot = result.maxRotX + result.maxRotY;
        spokes = (result.spokeMax + result.spokeMaxDiff);
        // TODO normalize? NSGAIII doesn't need the paper says
    }

    @Override
    public String toString() {
        return "RepFull{" +
                "dst=" + dst +
                ", rotZ=" + rotZ +
                ", rot=" + rot +
                ", spokes=" + spokes +
                '}';
    }
}
