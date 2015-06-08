package com.matsemann.simulation.entity;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btGImpactMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Array;
import com.matsemann.simulation.setup.SimulationConstants;

public class Rim extends Entity {
    public Rim(String name, Model model) {
        btCollisionShape shape = createShape(model);

        float rimMass = SimulationConstants.RIM_MASS;
        Vector3 localInertia = new Vector3(0, 0, 0);
        shape.calculateLocalInertia(rimMass, localInertia);

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(rimMass, null, shape, localInertia);
//        info.setLinearDamping(0.1f);
//        info.setAngularDamping(0.1f);

        setup(name, model, info);


        info.dispose();
    }

    private btCollisionShape createShape(Model model) {
        Array<MeshPart> meshParts = model.meshParts;
        btTriangleIndexVertexArray btArray = new btTriangleIndexVertexArray(meshParts);

        btGImpactMeshShape shape = new btGImpactMeshShape(btArray);
        shape.setLocalScaling(new Vector3(1f, 1f, 1f));
        shape.setMargin(0.05f);
        shape.updateBound();

        disposables.add(btArray);
        disposables.add(shape);

        return shape;
    }
}
