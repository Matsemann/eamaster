package com.matsemann.testing.simulator.stuff;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btTypedConstraint;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.PerformanceCounter;
import org.slf4j.Logger;
import com.matsemann.common.CounterUtil;
import com.matsemann.common.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

public class World implements Disposable {

    private final Logger logger = LoggerUtil.getLogger(getClass());
    private final String name;

    private final btDefaultCollisionConfiguration collisionConfiguration;
    private final btCollisionDispatcher dispatcher;
    private final btDbvtBroadphase broadPhase;
    private final btSequentialImpulseConstraintSolver constraintSolver;
    public final btDiscreteDynamicsWorld bulletWorld;
    private final DebugDrawer debugDrawer;
    private boolean drawShapes = true;

    private final List<WorldEntity> entities = new ArrayList<>();
    private final List<ModelInstance> instances = new ArrayList<>();
    private final List<btTypedConstraint> constraints = new ArrayList<>();

    private final PerformanceCounter worldCounter;

    private final int maxSteps = 1000;
    private final float stepSize = 1f / 60f;
    private final ClosestRayResultCallback rayCallback;

    private WorldEntity lastSelected, lastHovered;

    public World(String name) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadPhase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        bulletWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfiguration);

        debugDrawer = new DebugDrawer();


        btGImpactCollisionAlgorithm.registerAlgorithm(dispatcher);

        bulletWorld.setDebugDrawer(debugDrawer);
        bulletWorld.setGravity(new Vector3(0, -10f, 0));

        worldCounter = CounterUtil.getCounter("worldTick" + name);
        this.name = name;

        rayCallback = new ClosestRayResultCallback(new Vector3(), new Vector3());

        logger.info("Iterations: {}", bulletWorld.getSolverInfo().getNumIterations());
        logger.info("Erp: {}", bulletWorld.getSolverInfo().getErp());
//
//        bulletWorld.getSolverInfo().setNumIterations(20);
//        bulletWorld.getSolverInfo().setErp(0.01f);
    }

    /**
     * Stuff to both Bullet and Render
     * @param entity
     */
    public void addEntity(WorldEntity entity) {
        logger.debug("Adding '{}' to world '{}'", entity.getName(), name);

        entities.add(entity);
        entity.btRigidBody.setUserValue(entities.size());
        bulletWorld.addRigidBody(entity.btRigidBody);
    }
    /**
     * Stuff to both Bullet and Render
     * @param entity
     */
    public void addEntity(WorldEntity entity, short group, short mask) {
        logger.debug("Adding '{}' to world '{}'", entity.getName(), name);

        entities.add(entity);
        entity.btRigidBody.setUserValue(entities.size());
        bulletWorld.addRigidBody(entity.btRigidBody, group, mask);
    }

    /**
     * Render only
     */
    public void addModel(ModelInstance instance) {
        instances.add(instance);
    }

    public void addConstraint(EntityConstraint constraint) {
//        addConstraint(constraint, false);
    }
    public void addConstraint(btTypedConstraint constraint, boolean disableCollisions) {
        constraints.add(constraint);
        bulletWorld.addConstraint(constraint, disableCollisions); // TODO
    }

    public void toggleDebug() {
        if (getDebugMode() == 0 && drawShapes) {
            debugDrawer.setDebugMode(
                    btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
                    | btIDebugDraw.DebugDrawModes.DBG_DrawFeaturesText
                    | btIDebugDraw.DebugDrawModes.DBG_DrawText
                    | btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints
                    | btIDebugDraw.DebugDrawModes.DBG_DrawConstraints
//                    | btIDebugDraw.DebugDrawModes.DBG_DrawAabb
            );
            logger.debug("Activating debugdrawer");
        } else if (getDebugMode() > 0 && drawShapes) {
            logger.debug("Hiding shapes");
            drawShapes = false;
        } else {
            logger.debug("Debug drawing OFF");
            drawShapes = true;
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_NoDebug);

        }
    }

    public void setDebugMode(int mode, boolean drawShapes) {
        debugDrawer.setDebugMode(mode);
        this.drawShapes = drawShapes;
    }

    public int getDebugMode() {
        return debugDrawer.getDebugMode();
    }

    public void update(float deltaTime) {
        worldCounter.tick();
        worldCounter.start();

        bulletWorld.stepSimulation(deltaTime, maxSteps, stepSize);

        worldCounter.stop();
    }

    public void render(ModelBatch batch, Environment environment) {
        if (drawShapes) {
            for (WorldEntity entity : entities) {
                batch.render(entity.instance, environment);
            }

            batch.render(instances);
        }

        if (getDebugMode() > 0) {
            Camera camera = batch.getCamera();
            batch.flush();
            debugDrawer.begin(camera);
            bulletWorld.debugDrawWorld();
            debugDrawer.end();
//            batch.begin(camera);
        }
    }

    public void click(int button) {
        if (button == 6) {
            logger.info("Freeze!");
            for (WorldEntity entity : entities) {
                entity.btRigidBody.clearForces();
                entity.btRigidBody.setAngularVelocity(new Vector3(0, 0, 0));
                entity.btRigidBody.setLinearVelocity(new Vector3(0, 0, 0));

            }
        }


        if (lastHovered == null) {
            return;
        }

        if (button == 3) {
            Quaternion rotation = new Quaternion();
            lastHovered.btRigidBody.getCenterOfMassTransform().getRotation(rotation);
            Vector3 rot = new Vector3();
            float axisAngle = rotation.getAxisAngle(rot);
            Vector3 rotate = new Vector3(0f, 0, 10).rotate(rot, axisAngle);
//            lastHovered.btRigidBody.applyCentralForce(rotate);
            lastHovered.btRigidBody.applyForce(new Vector3(5, 0, 0), new Vector3(0, 0f, 2.5f));
        } else if (button == 4) {
//            lastHovered.btRigidBody.applyCentralImpulse(new Vector3(0, 0, 50f));
            lastHovered.btRigidBody.applyTorqueImpulse(new Vector3(0, 5, 0f));
        } else if (button == 5) {
            lastHovered.btRigidBody.applyTorqueImpulse(new Vector3(0, 0, 500f));
        }

    }

    public void hover(Ray ray) {
        Vector3 from = new Vector3(ray.origin);
        Vector3 to = new Vector3(ray.direction).scl(100f).add(from);

        rayCallback.setCollisionObject(null);

        rayCallback.setClosestHitFraction(1f);
        rayCallback.setRayFromWorld(from);
        rayCallback.setRayToWorld(to);

//        rayCallback.setCollisionFilterMask((short) -1);
        rayCallback.setCollisionFilterGroup((short) 64);

        bulletWorld.rayTest(from, to, rayCallback);


        if (rayCallback.hasHit()) {
            btCollisionObject hitObj = rayCallback.getCollisionObject();

            WorldEntity hitEnt = (WorldEntity) hitObj.userData;

            if (hitEnt != lastHovered) {
                if (lastHovered != null) {
                    lastHovered.restoreColors();
                }

                lastHovered = hitEnt;
                hitEnt.applyColorFunc(c -> c.add(0.2f, 0.2f, 0.2f, 0f));
            }
        } else {
            if (lastHovered != null) {
                lastHovered.restoreColors();
                lastHovered = null;
            }
        }
    }

    @Override
    public void dispose() {
        // TODO, og kanskje snu rekkefølge på ting?

        bulletWorld.dispose();
        constraintSolver.dispose();
        broadPhase.dispose();
        dispatcher.dispose();
        collisionConfiguration.dispose();

        for (WorldEntity d : entities) {
            d.dispose(); // TODO på sikt ikke dispose her, da andre verdner vil bruke objektene?
            // Nvm, kan lage nye
        }

        for (btTypedConstraint c : constraints) {
            c.dispose();
        }

        CounterUtil.disposeCounter(worldCounter);

    }
}
