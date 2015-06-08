package com.matsemann.simulation.result;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.matsemann.simulation.entity.Spoke;
import com.matsemann.simulation.entity.WheelWorld;

import java.util.Arrays;

public class SimulationData {

    public Vector3 position;
    public Vector3 rotation;
    public float[] spokeForces;

    public SimulationData(WheelWorld wheelWorld) {


        position = new Vector3(wheelWorld.rim.rigidBody.getCenterOfMassPosition());


        Quaternion q = new Quaternion();
        rotation = new Vector3();
        wheelWorld.rim.rigidBody.getCenterOfMassTransform().getRotation(q);
        float f = q.getAxisAngle(rotation);
        rotation.scl(f);

        spokeForces = new float[wheelWorld.spokes.size()];

        for (int i = 0; i < wheelWorld.spokes.size(); i++) {
            spokeForces[i] = wheelWorld.spokes.get(i).currentForce;
        }

    }

    @Override
    public String toString() {
        return "Pos: " + position + ", rot: " + rotation + ", forces: " + Arrays.toString(spokeForces);
    }
}
