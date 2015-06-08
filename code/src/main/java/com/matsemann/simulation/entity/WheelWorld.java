package com.matsemann.simulation.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.setup.SimulationConstants;
import org.slf4j.Logger;

import java.util.List;

public class WheelWorld implements Disposable {
    private final Logger logger = LoggerUtil.getLogger(getClass());

    public String name;
    public Hub hub;
    public Rim rim;
    public List<Spoke> spokes;

    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btDbvtBroadphase broadPhase;
    private btSequentialImpulseConstraintSolver constraintSolver;
    public btDiscreteDynamicsWorld bulletWorld;
    private DebugDrawer debugDrawer;

    private ClosestRayResultCallback rayCallback;
    private boolean drawShapes = true;
    public int spokesToDraw = SimulationConstants.NUM_SPOKES;

    public WheelWorld(String name, Hub hub, Rim rim, List<Spoke> spokes, boolean visualized) {
        this.name = name;
        this.hub = hub;
        this.rim = rim;
        this.spokes = spokes;

        createWorld(visualized);
        addStuffToWorld();
    }

    private void createWorld(boolean visualized) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadPhase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        bulletWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfiguration);

        if (visualized) {
            debugDrawer = new DebugDrawer();
            bulletWorld.setDebugDrawer(debugDrawer);
            rayCallback = new ClosestRayResultCallback(new Vector3(), new Vector3());
        }

//        btGImpactCollisionAlgorithm.registerAlgorithm(dispatcher); // TODO: Collision between GImpact not needed?

        bulletWorld.setGravity(new Vector3(0, 0f, 0));


    }

    private void addStuffToWorld() {
        bulletWorld.addRigidBody(hub.rigidBody);
        bulletWorld.addRigidBody(rim.rigidBody);

        spokes.forEach(s -> {
            bulletWorld.addRigidBody(s.box.rigidBody, SimulationConstants.BOX_GROUP, (short) 0);
            bulletWorld.addRigidBody(s.rigidBody, SimulationConstants.SPOKE_GROUP, SimulationConstants.RAY_GROUP);
            s.rigidBody.setCollisionFlags(s.rigidBody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);
            bulletWorld.addConstraint(s.coneTwistConstraint, true);
            bulletWorld.addConstraint(s.springConstraint, true);
        });
    }

    public void update(float deltaTime) {
        bulletWorld.stepSimulation(deltaTime, SimulationConstants.MAX_STEPS, SimulationConstants.STEP_SIZE);
        spokes.forEach(Spoke::updateSpokeInternal);
    }

    public void render(ModelBatch batch, Environment environment) {
        if (drawShapes) {
            batch.render(hub.modelInstance, environment);
            batch.render(rim.modelInstance, environment);

            for (int i = 0; i < spokesToDraw; i++) {
                spokes.get(i).updateSpokeRender();
                batch.render(spokes.get(i).modelInstance, environment);
            }
//            spokes.forEach(s -> batch.render(s.modelInstance, environment));
//            bulletWorld.updateAabbs();
        }


        if (debugDrawer.getDebugMode() > 0) {
            Camera camera = batch.getCamera();
            batch.flush();
            debugDrawer.begin(camera);
            bulletWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }


    public void toggleDebug() {
        if (debugDrawer.getDebugMode() == 0 && drawShapes) {
            debugDrawer.setDebugMode(
                    btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
                            | btIDebugDraw.DebugDrawModes.DBG_DrawFeaturesText
                            | btIDebugDraw.DebugDrawModes.DBG_DrawText
                            | btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints
                            | btIDebugDraw.DebugDrawModes.DBG_DrawConstraints
//                    | btIDebugDraw.DebugDrawModes.DBG_DrawAabb
            );
            logger.debug("Activating debugdrawer");
        } else if (debugDrawer.getDebugMode() > 0 && drawShapes) {
            logger.debug("Hiding shapes");
            drawShapes = false;
        } else {
            logger.debug("Debug drawing OFF");
            drawShapes = true;
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_NoDebug);

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

        if (debugDrawer != null) {
            debugDrawer.dispose();
        }
        if (rayCallback != null) {
            rayCallback.dispose();
        }

        hub.dispose();
        rim.dispose();
        spokes.forEach(Spoke::dispose);

        hub = null;
        rim = null;
        spokes.clear();
        spokes = null;
    }
}
