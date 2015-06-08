package com.matsemann.simulation.setup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class SpokeAngles implements Serializable {
    private static final long serialVersionUID = 22219991665828140L;

    public float[] angles;

    public SpokeAngles() {
        angles = new float[SimulationConstants.NUM_SPOKES_ONE_SIDE];
    }

    public float get(int i) {
        return angles[i];
    }

    @Override
    public String toString() {
        return Arrays.toString(angles);
    }

    public void createNx(int n) {
        for (int i = 0; i < angles.length; i++) {
            if (i % 2 == 0) {
                angles[i] = n * SimulationConstants.SPOKE_ANGLE_DISTANCE;
            } else {
                angles[i] = -n * SimulationConstants.SPOKE_ANGLE_DISTANCE;
            }
        }
    }

    public void createCrowFoot() {
        for (int i = 0; i < angles.length; i++) {
            switch (i % 4) {
                case 0: angles[i] = 0; break;
                case 1: angles[i] = 2 * SimulationConstants.SPOKE_ANGLE_DISTANCE; break;
                case 2: angles[i] = 0; break;
                case 3: angles[i] = -2 * SimulationConstants.SPOKE_ANGLE_DISTANCE; break;
            }
        }
    }

    public void create2l2t() {
        for (int i = 0; i < angles.length; i++) {
            switch (i % 4) {
                case 0: case 1: angles[i] = 2 * SimulationConstants.SPOKE_ANGLE_DISTANCE; break;
                case 2: case 3: angles[i] = -2 * SimulationConstants.SPOKE_ANGLE_DISTANCE; break;
            }
        }
    }

    public void createRandom() {
        Random rnd = new Random();

        for (int i = 0; i < angles.length; i++) {
            angles[i] = rnd.nextFloat() * 180 - 90;
        }
    }

    public void createBack() {
        for (int i = 0; i < angles.length; i++) {
            angles[i] = -2*SimulationConstants.SPOKE_ANGLE_DISTANCE;
        }
    }

    public void createSingel() {
        for (int i = 0; i < angles.length; i++) {
            if (i < 8) {
                angles[i] = 5 * SimulationConstants.SPOKE_ANGLE_DISTANCE;
            } else {
                angles[i] = -5 * SimulationConstants.SPOKE_ANGLE_DISTANCE;
            }
        }
        angles[0] = 0;
    }

    public void createPermutation(int[] perm) {
        if (perm.length != angles.length) {
            throw new IllegalArgumentException("Wrong size of perm");
        }

        for (int i = 0; i < perm.length; i++) {
            int val = perm[i];
            int diff = val - i;
            angles[i] = diff * SimulationConstants.SPOKE_ANGLE_DISTANCE;
            if (angles[i] < -90f) {
                angles[i] += 360f;
            } else if (angles[i] > 90f) {
                angles[i] -= 360f;
            }
        }
    }

    public void createFromArray(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            angles[i] = (float) arr[i];
        }
    }
}
