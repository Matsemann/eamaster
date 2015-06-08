package com.matsemann.testing.simulator.stuff;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btGeneric6DofSpringConstraint;
import com.badlogic.gdx.utils.Disposable;

public class EntityConstraint implements Disposable {

    public final btGeneric6DofSpringConstraint constraint;

    public EntityConstraint(WorldEntity a, WorldEntity b, Vector3 aPoint, Vector3 bPoint) {
//        constraint = new btPoint2PointConstraint(a.btRigidBody, b.btRigidBody, aPoint, bPoint);
        Matrix4 inA = new Matrix4();
        inA.setToTranslation(aPoint);

        Matrix4 inB = new Matrix4();
        inB.setToTranslation(bPoint);
        btGeneric6DofSpringConstraint tmp = new btGeneric6DofSpringConstraint(a.btRigidBody, b.btRigidBody, inA, inB, true);

//        tmp.enableSpring(2, true);
//        tmp.setStiffness(2, 5000000f);
////        tmp.setEquilibriumPoint(2);
//
//        Vector3 tmpV = new Vector3();
//
//        tmp.setLinearLowerLimit(new Vector3(0, -5f, 0));
//        tmp.setLinearUpperLimit(new Vector3(0f, 10f, 0f));
//        tmp.setAngularLowerLimit(new Vector3(0, 0, 0));
//        tmp.setAngularUpperLimit(new Vector3(0, 0, 0));

        constraint = tmp;
    }

    @Override
    public void dispose() {
        constraint.dispose();
    }
}
