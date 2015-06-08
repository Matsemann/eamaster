package com.matsemann.simulation;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.utils.JsonReader;
import com.matsemann.ea.fitness.RepFull;
import com.matsemann.simulation.entity.Entity;
import com.matsemann.simulation.entity.Spoke;
import com.matsemann.simulation.result.SimulationData;
import com.matsemann.simulation.result.SimulationResult;
import com.matsemann.simulation.setup.SimulationConstants;
import com.matsemann.simulation.setup.SpokeAngles;
import org.slf4j.Logger;
import com.matsemann.common.LoggerUtil;

import java.util.HashMap;
import java.util.Map;

public class WheelRenderer implements ApplicationListener {

    Logger logger = LoggerUtil.getLogger(getClass());

    private Camera camera;
    private CameraInputController cameraInputController;
    private Environment environment;
    private ModelBatch batch;

    private Map<String, Model> models;
    private Simulation simulation;
    private SpokeAngles spokeAngles;
    ClosestRayResultCallback rayResultCallback;
    Entity lastHovered, selected;


    @Override
    public void create() {

        logger.info("Setting up camera");

        camera = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        camera.position.set(40f, 30f, 40f);
        camera.position.set(45f, 29f, 73f);
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        camera.position.set(4f, 3f, 4f);
        camera.lookAt(0, 2f, 0);
        camera.near = 1f;
        camera.far = 500f;
        camera.update();

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);
//        Gdx.input.setInputProcessor(new InputMultiplexer(cameraController, this, new GestureDetector(this)));


        logger.info("Loading models");
        models = new HashMap<>();
        ModelBuilder builder = new ModelBuilder();
        models.put("box", builder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0f, 0f, 0f))),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));

        G3dModelLoader loader = new G3dModelLoader(new JsonReader());
        models.put("hub", loader.loadModel(Gdx.files.internal("data/hubreal.g3dj")));
        models.put("rim", loader.loadModel(Gdx.files.internal("data/rimreal.g3dj")));
        models.put("spoke", loader.loadModel(Gdx.files.internal("data/spoke.g3dj")));


        logger.info("Let there be light");
        batch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));

        environment.add(new DirectionalLight().set(0.3f, 0.3f, 0.3f, -5f, -3f, -1f));
        environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 5f, 8f, 0f));
        environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -3f, -2f, -5f));


        logger.info("Initializing Bullet");
        Bullet.init();

        logger.info("Creating simulation");
        // create world & stuff?
        spokeAngles = new SpokeAngles();
//        spokeAngles.create2l2t();
//        spokeAngles.createCrowFoot();
//        spokeAngles.createNx(3);
//        spokeAngles.createPer3mutation(new int[]{3, 14, 5, 0, 7, 2, 9, 4, 11, 6, 13, 8, 15, 10, 1, 12}); // 3x as well!

//        spokeAngles.createPermutation(new int[]{14, 2, 3, 15, 7, 5, 6, 10, 4, 9, 8, 11, 12, 0, 1, 13});
//        spokeAngles.createPermutation(new int[]{2, 0, 1, 5, 3, 8, 10, 4, 12, 6, 11, 7, 9, 15, 13, 14});
//        spokeAngles.createPermutation(new int[]{0, 15, 3, 2, 4, 9, 6, 7, 8, 5, 10, 11, 12, 13, 14, 1});
        spokeAngles.createRandom();
//        spokeAngles.createBack();
//        spokeAngles.createSingel();
        simulation = new Simulation("sim1", models, spokeAngles, true);

        rayResultCallback = new ClosestRayResultCallback(new Vector3(), new Vector3());
        rayResultCallback.setCollisionFilterGroup(SimulationConstants.RAY_GROUP);

        logger.info("Setup complete");
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;// / 15f;
        camera.viewportWidth = width;// / 15f;
        camera.update();
        cameraInputController.update();
    }

    public void setAngles(SpokeAngles angles) {
        int spokesToDraw = simulation.wheelWorld.spokesToDraw;
        simulation.dispose();

        simulation = new Simulation("sim", models, angles, true);
        simulation.wheelWorld.spokesToDraw = spokesToDraw;
    }

    float force = 0;

    @Override
    public void render() {

        hover(camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()));
        click();

        simulation.wheelWorld.update(Gdx.graphics.getRawDeltaTime());
//        simulation.wheelWorld.rim.rigidBody.applyCentralForce(new Vector3(force, 0, 0));
//        force += 100;
//        if (force > 12000) {
//            force = 12000;
//        }


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        Gdx.gl.glClearColor(1, 1, 1, 1);

        batch.begin(camera);
        simulation.render(batch, environment);
        // render world
        batch.end();


    }

    float totalRotation = 0;

    private void click() {

        // Rednering
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            simulation.wheelWorld.toggleDebug();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (simulation.wheelWorld.spokesToDraw == SimulationConstants.NUM_SPOKES) {
                simulation.wheelWorld.spokesToDraw = SimulationConstants.NUM_SPOKES_ONE_SIDE;
            } else {
                simulation.wheelWorld.spokesToDraw = SimulationConstants.NUM_SPOKES;
            }
        }

        // Selection
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lastHovered != null) {
            if (selected != null) {
                selected.restoreColors();
            }
            selected = lastHovered;
            lastHovered.restoreColors();
            lastHovered = null;
            selected.applyColorFunc(c -> c.sub(0.4f, 0.4f, 0.4f, 0));
            logger.info("Selected {}", selected.name);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && selected != null) {
            selected.restoreColors();
            selected = null;
        }


        // Camera controlling
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            float speed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 0.2f : 1.5f;
            speed *= Gdx.graphics.getRawDeltaTime() * 360;
            totalRotation += speed;
            camera.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 0, 1), speed);
            camera.update();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                logger.info("Resetting camera up");
                camera.up.set(0, 1, 0);
            } else {
                logger.info("Resetting camera rotation");
                camera.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 0, 1), -totalRotation);
                totalRotation = 0;
            }
            camera.update();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            camera.position.set(0, 0, 90);
            camera.lookAt(0, 0, 0);
            camera.up.set(0, 1, 0);
            camera.update();
            logger.info("Setting camera front view");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            camera.position.set(100, 0, 0);
            camera.lookAt(0, 0, 0);
            camera.up.set(0, 1, 0);
            camera.update();
            logger.info("Setting camera side view");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            camera.position.set(45f, 29f, 73f);
            camera.lookAt(0, 2, 0);
            camera.up.set(0, 1, 0);
            camera.update();
            logger.info("Setting camera default view");
        }



        // Effects
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7) && selected != null) {
            if (selected instanceof Spoke) {
                ((Spoke) selected).setDeltaInitialLength(-0.3f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8) && selected != null) {
            if (selected instanceof Spoke) {
                ((Spoke) selected).setDeltaInitialLength(0.3f);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {

            Vector3 cPos = simulation.wheelWorld.rim.rigidBody.getCenterOfMassPosition();
            logger.info("RimPos: {}, length: {}", cPos, cPos.len());
            Quaternion q = new Quaternion();
            Vector3 v = new Vector3();
            simulation.wheelWorld.rim.rigidBody.getCenterOfMassTransform().getRotation(q);
            float f = q.getAxisAngle(v);
            v.scl(f);
            logger.info("RimRot: {}", v);

            logger.info("Camera: {}", camera.position);
            logger.info("Camera: {}", camera.direction);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            logger.info("Simulation:");
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                if (i%50 == 0) logger.info("Progress: {}", i);
                Simulation sim2 = new Simulation("sim2", models, spokeAngles, false);
                Map<String, SimulationData> simulate = sim2.simulate();
                sim2.dispose();
                SimulationResult res = new SimulationResult(simulate);
                RepFull rep = new RepFull(res);
                logger.info("Data: {}", res);
                logger.info("Rep: {}", rep);
            }
            logger.info("Time used: {}", System.currentTimeMillis() - start);
//            System.gc();
        }

//        if (lastHovered == null) {
//            return;
//        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            Vector3 force = new Vector3();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                force.x = SimulationConstants.TRANS_FORCE;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                force.x = -SimulationConstants.TRANS_FORCE;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                force.y = SimulationConstants.TRANS_FORCE;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                force.y = -SimulationConstants.TRANS_FORCE;
            }

            Quaternion rotation = new Quaternion();
            simulation.wheelWorld.rim.rigidBody.getCenterOfMassTransform().getRotation(rotation);
//            simulation.wheelWorld.rim.rigidBody.getWorldTransform().getRotation(rotation);
            Vector3 rot = new Vector3();
            float angleAroundY = rotation.getAngleAround(0, 1, 0);
            float axisAngle = rotation.getAxisAngle(rot);
//            logger.info("Rotation: {}", rot);
            Vector3 forcePoint = new Vector3(30, 0, 0);
//            force.rotate(angleAroundY, 0, 1, 0);
//            force.rotate(rot, axisAngle);
//            force.y  = 0;
//            forcePoint.rotate(rot, axisAngle);

//            logger.info("Force: {}", force);
            simulation.wheelWorld.rim.rigidBody.applyCentralForce(force);
//            simulation.wheelWorld.rim.rigidBody.applyForce(force, forcePoint);
//            simulation.wheelWorld.rim.rigidBody.

//            simulation.wheelWorld.rim.rigidBody.
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            Vector3 force = new Vector3();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                force.z = -45000;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                force.z = 45000;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                force.y = 25000;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                force.y = -35000;
            }
            simulation.wheelWorld.rim.rigidBody.applyTorque(force);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            System.gc();
            logger.info("Gc");
        }

    }

    Vector3 from = new Vector3();
    Vector3 to = new Vector3();

    private void hover(Ray ray) {
        from.set(ray.origin);
        to.set(ray.direction).scl(500f).add(from);

        rayResultCallback.setCollisionObject(null);

        rayResultCallback.setClosestHitFraction(1f);
        rayResultCallback.setRayFromWorld(from);
        rayResultCallback.setRayToWorld(to);

        simulation.wheelWorld.bulletWorld.rayTest(from, to, rayResultCallback);


        if (rayResultCallback.hasHit()) {
            btCollisionObject hitObj = rayResultCallback.getCollisionObject();

            Entity hitEnt = (Entity) hitObj.userData;

            if (hitEnt != lastHovered && (selected == null || hitEnt != selected)) {
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        logger.info("Disposing");


        simulation.dispose();
        simulation = null;
        batch.dispose();
//        System.gc();
    }
}
