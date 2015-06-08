package com.matsemann.testing.simulator.stuff;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import org.slf4j.Logger;
import com.matsemann.common.LoggerUtil;

public class EntityConstructor implements Disposable {


    private Logger logger = LoggerUtil.getLogger(getClass());
    private String name;
    private int counter = 0;

    public Model model;
    public boolean disableDeactivation;
    public btRigidBody.btRigidBodyConstructionInfo info;

    public EntityConstructor(String name, Model model, btCollisionShape shape, float mass) {
        createConstructionInfo(name, model, mass, shape, false);
    }

    public EntityConstructor(String name, Model model, btCollisionShape shape, float mass, boolean disableDeactivation) {
        createConstructionInfo(name, model, mass, shape, disableDeactivation);
    }

    public EntityConstructor(String name, Model model, float mass) {
        this(name, model, mass, false);
    }

    /**
     * Generates a convex hull based on the model as the shape
     */
    public EntityConstructor(String name, Model model, float mass, boolean disableDeactivation) {
        Mesh modelMesh = model.meshes.get(0);
        btCollisionShape shape = new btConvexHullShape(modelMesh.getVerticesBuffer(), modelMesh.getNumVertices(), modelMesh.getVertexSize());


//        btTriangleIndexVertexArray i = new btTriangleIndexVertexArray(model.meshParts);
//        btGImpactMeshShape shapeImp = new btGImpactMeshShape(i);
//
//        shapeImp.setLocalScaling(new Vector3(1f, 1f, 1f));
//        shapeImp.setMargin(0.05f);
//        shapeImp.updateBound();
//        optimizing the shape
//        btShapeHull hull = new btShapeHull((btConvexShape) shape);
//        hull.buildHull(shape.getMargin());

//        shape = new btConvexHullShape(hull);

//        btCompoundShape shape = new btCompoundShape();
//        shape.addChildShape(new Matrix4(), shapeImp);

        createConstructionInfo(name, model, mass, shape, disableDeactivation);
    }


    private void createConstructionInfo(String name, Model model,float mass, btCollisionShape shape, boolean disableDeactivation) {
        this.name = name;
        this.model = model;
        this.disableDeactivation = disableDeactivation;

        logger.debug("Creating constructor '{}'", name);

        Vector3 localInertia = new Vector3(0, 0, 0);
        if (mass > 0f) {
            shape.calculateLocalInertia(mass, localInertia);
            logger.debug("Inertia for '{}' calculated to '{}'", name, localInertia);
        }

        if (name.equals("rim")) {
//            localInertia.set(25f, 25f, 50f);
        }

        info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
    }

    public WorldEntity construct() {
        return construct(new Vector3(), new Vector3(), 0f);
    }

    public WorldEntity construct(Vector3 position, Vector3 rotation, float angle) {
        Matrix4 transform = new Matrix4();
        transform.translate(position);
        transform.rotate(rotation, angle);


        counter++;
        String newName = name + counter;
        logger.debug("Constructor '{}' is constructing '{}' with position '{}'", name, newName, position);

        return new WorldEntity(newName, new ModelInstance(model, transform), new btRigidBody(info), disableDeactivation);
    }

    @Override
    public void dispose() {
        info.dispose();
        // TODO dispose shape?? Will crash
    }
}
