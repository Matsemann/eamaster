package com.matsemann.simulation.entity;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeZ;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.matsemann.simulation.setup.SimulationConstants;

public class Hub extends Entity {


    public Hub(String name, Model model) {

        btCompoundShape shape = createShape();
        Vector3 localInertia = new Vector3(0, 0, 0);
        float mass = SimulationConstants.HUB_MASS;

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);


        setup(name, model, info);

        info.dispose();
    }

    private btCompoundShape createShape() {
        btCompoundShape hubShape = new btCompoundShape();

        float halfDiameter = SimulationConstants.HUB_FLANGE_RADIUS_TOTAL;
        float halfWidth = 0.25f;
        float fromCenter = SimulationConstants.HUB_FLANGE_DISTANCE / 2f;

        float axleRadius = SimulationConstants.AXLE_RADIUS;
        float axleHalfLength = SimulationConstants.AXLE_LENGTH / 2f;

        btCylinderShape shapeFlange1 = new btCylinderShapeZ(new Vector3(halfDiameter, halfDiameter, halfWidth));
        Matrix4 flange1Pos = new Matrix4();
        flange1Pos.translate(0, 0, fromCenter);

        btCylinderShape shapeFlange2 = new btCylinderShapeZ(new Vector3(halfDiameter, halfDiameter, halfWidth));
        Matrix4 flange2Pos = new Matrix4();
        flange2Pos.translate(0, 0, -fromCenter);

        btCylinderShape axleShape = new btCylinderShapeZ(new Vector3(axleRadius, axleRadius, axleHalfLength));
        Matrix4 axlePos = new Matrix4();

        hubShape.addChildShape(flange1Pos, shapeFlange1);
        hubShape.addChildShape(flange2Pos, shapeFlange2);
        hubShape.addChildShape(axlePos, axleShape);

        disposables.add(shapeFlange1);
        disposables.add(shapeFlange2);
        disposables.add(axleShape);
        disposables.add(hubShape);

        return hubShape;
    }

}
