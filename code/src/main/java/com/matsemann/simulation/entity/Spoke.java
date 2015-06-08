package com.matsemann.simulation.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeX;
import com.badlogic.gdx.physics.bullet.dynamics.btConeTwistConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btGeneric6DofSpringConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.matsemann.simulation.ColorMap;
import com.matsemann.simulation.setup.SimulationConstants;

import java.util.function.Consumer;

public class Spoke extends Entity {

    public float currentForce, length;
    public Box box;
    public btGeneric6DofSpringConstraint springConstraint;
    public btConeTwistConstraint coneTwistConstraint;

    float initialLength;
    public Vector3 pointA = new Vector3();
    Vector3 pointB = new Vector3();
    Vector3 distance = new Vector3();
    Vector3 scaling = new Vector3();
    Vector3 rotate = new Vector3(1, 0, 0);

    private Consumer<Color> colorFunction;
    private ColorMap colorMap;
    private Vector3 middle = new Vector3();


    public Spoke(String name, Model model, ColorMap colorMap) {
        this.colorMap = colorMap;
        btCylinderShapeX spokeShape = new btCylinderShapeX(new Vector3(0.5f, SimulationConstants.SPOKE_RADIUS, SimulationConstants.SPOKE_RADIUS));
        disposables.add(spokeShape);

        Vector3 spokeInertia = new Vector3(0, 0, 0);
        btRigidBody.btRigidBodyConstructionInfo spokeInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, null, spokeShape, spokeInertia);

        setup(name, model, spokeInfo);

        spokeInfo.dispose();

    }

    public void updateSpokeInternal() {
        box.modelInstance.transform.getTranslation(pointB);
        distance.set(pointB).sub(pointA);
        length = distance.len();

        currentForce = (length - initialLength - SimulationConstants.ROTATING_BOX_DISTANCE) * SimulationConstants.SPRING_STIFFNESS;
    }

    public void updateSpokeRender() {
        // calculate stuff
        middle.set(pointA).add(pointB).scl(0.5f);

        // set visible
        modelInstance.transform.setToTranslation(middle);
        modelInstance.transform.rotate(rotate, distance.nor());
        update();
        modelInstance.transform.scale(length, 2, 2);
//        modelInstance.transform.scale(length, 1.5f, 1.5f);
        scaling.set(length, 1, 1);
        rigidBody.getCollisionShape().setLocalScaling(scaling);

        setColor(colorMap.getColor(currentForce), true);

        if (colorFunction != null) {
            applyColorFunc(colorFunction);
        }

    }

    public void setInitialLength(float initialLength) {
        this.initialLength = initialLength;
        springConstraint.setEquilibriumPoint(0, initialLength);
    }

    public void setDeltaInitialLength(float delta) {
        this.initialLength += delta;
        springConstraint.setEquilibriumPoint(0, initialLength);
    }

    @Override
    public void restoreColors() {
        colorFunction = null;
        super.restoreColors();
    }

    @Override
    public void applyColorFunc(Consumer<Color> function) {
        colorFunction = function;
        super.applyColorFunc(function);
    }

    @Override
    public void dispose() {
        box.dispose();
        springConstraint.dispose();
        coneTwistConstraint.dispose();
        super.dispose();
    }
}
