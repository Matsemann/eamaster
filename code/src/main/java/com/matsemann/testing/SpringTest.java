package com.matsemann.testing;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.matsemann.common.LoggerUtil;
import com.matsemann.testing.simulator.stuff.WorldEntity;
import org.slf4j.Logger;
import com.matsemann.testing.simulator.stuff.EntityConstructor;
import com.matsemann.testing.simulator.stuff.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringTest implements ApplicationListener {

    Logger logger = LoggerUtil.getLogger(getClass());


    // Create world, camera, rendering etc.
    // Load a model
    // Add some constraints

    private Map<String, ModelInstance> instances;
    private Map<String, Model> models;

    private PerspectiveCamera camera;
    private CameraInputController cameraInputController;
    private Environment environment;
    private ModelBatch batch;
    private World world;
    private List<WorldEntity> tetrisObjects;

    private List<Disposable> disposables; // TODO add all here?
    private WorldEntity boxStatic, boxRotation;
    private WorldEntity boxDynamic;

//    private ModelBatch shadowBatch;
//    private DirectionalShadowLight shadowLight;


    @Override
    public void create() {
        logger.info("Setting up camera");

        camera = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(35f, 35f, 35f);
        camera.lookAt(0, 5, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        cameraInputController = new CameraInputController(camera);
//        cameraInputController.activateKey = Input.Keys.CONTROL_LEFT;
        Gdx.input.setInputProcessor(cameraInputController);

        logger.info("Creating models and instances");
        instances = new HashMap<>();
        models = new HashMap<>();
        ModelBuilder builder = new ModelBuilder();

        models.put("ground", builder.createBox(40f, 5f, 40f,
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                Usage.Position | Usage.Normal));
        models.put("box", builder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                Usage.Position | Usage.Normal));
        models.put("sphere", builder.createSphere(5f, 5f, 5f, 20, 20,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                Usage.Position | Usage.Normal));
        models.put("cursor", builder.createBox(0.5f, 0.5f, 0.5f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                Usage.Position | Usage.Normal));

        G3dModelLoader loader = new G3dModelLoader(new JsonReader());
        models.put("tetris", loader.loadModel(Gdx.files.internal("data/tetris.g3dj")));
        models.put("hub", loader.loadModel(Gdx.files.internal("data/hubreal.g3dj")));
        models.put("rim", loader.loadModel(Gdx.files.internal("data/rimreal.g3dj")));



        logger.info("Let there be light");
        batch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));

        environment.add(new DirectionalLight().set(0.3f, 0.3f, 0.3f, -5f, -3f, -1f));
        environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 5f, 8f, 0f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 5f, -8f, 10f));
//        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, -15f, 25f, -10f, 1000f));
        // TODO test shadowligth
//        shadowBatch = new ModelBatch(new DepthShaderProvider());
//        shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 0.1f, 300f);
//        shadowLight.set(0.6f, 0.6f, 0.6f, -15f, -25f, -10f);
//        environment.add(shadowLight);
//        environment.shadowMap =shadowLight ;


        logger.info("Initializing Bullet");
        Bullet.init();

        world = new World("MainWorld");
//        world.bulletWorld.setGravity(new Vector3(0, 0, 0));
        // Create world with gravity

        // sphere
        btCollisionShape sphereCollisionShape = new btSphereShape(2.5f);
        EntityConstructor sphereConstructor = new EntityConstructor("sphere", models.get("sphere"), sphereCollisionShape, 2f, true);
        sphereConstructor.info.setRestitution(0.8f);
        sphereConstructor.info.setRollingFriction(0.9f);
        WorldEntity sphere = sphereConstructor.construct();
        sphere.instance.transform.translate(0, 30f, 0);
        sphere.update();
        world.addEntity(sphere);
        sphere.btRigidBody.applyCentralImpulse(new Vector3(2f, -10f, 0f));

        // ground
        btCollisionShape groundCollisionShape = new btBoxShape(new Vector3(20f, 2.5f, 20f));
        EntityConstructor groundConstructor = new EntityConstructor("ground", models.get("ground"), groundCollisionShape, 0f);
        groundConstructor.info.setRestitution(0.7f);
        groundConstructor.info.setRollingFriction(0.05f);
        WorldEntity ground = groundConstructor.construct();
        ground.instance.transform.translate(0, 0, 0);
        ground.update();
        world.addEntity(ground);

        // Boxes
        btCollisionShape boxShape = new btBoxShape(new Vector3(2.5f, 2.5f, 2.5f));
        EntityConstructor staticBoxConstructor = new EntityConstructor("box", models.get("box"), boxShape, 0f, true);
        staticBoxConstructor.info.setLinearDamping(0.1f);
        staticBoxConstructor.info.setAngularDamping(0.1f);
        EntityConstructor dynamicBoxConstructor = new EntityConstructor("box", models.get("box"), boxShape, 1f, true);
        dynamicBoxConstructor.info.setLinearDamping(0.1f);
        dynamicBoxConstructor.info.setAngularDamping(0.1f);


        boxStatic = staticBoxConstructor.construct();
        boxRotation = dynamicBoxConstructor.construct();
        boxDynamic = dynamicBoxConstructor.construct();

        boxStatic.instance.transform.translate(-20, 20, 0);
        boxRotation.instance.transform.translate(-17.5f, 20, 0);
        boxStatic.setColor(0, 0.8f, 0f, 0, true);
        boxRotation.setColor(0, 0.8f, 0.8f, 0, true);
        boxStatic.update();
        boxRotation.update();

        boxDynamic.instance.transform.translate(-10, 20, 0);
//        boxDynamic.instance.transform.rotate(1f, 0, 0, 10f);
        boxDynamic.setColor(0.8f, 0, 0.8f, 0, true);
        boxDynamic.update();

        world.addEntity(boxStatic);
        world.addEntity(boxRotation);
        world.addEntity(boxDynamic);

        Matrix4 frameInStatic = new Matrix4();
        frameInStatic.setTranslation(2.5f, 0, 0);

        Matrix4 frameInRotation = new Matrix4();


        // static -> rotation
        btGeneric6DofConstraint rotCon = new btGeneric6DofConstraint(boxStatic.btRigidBody, boxRotation.btRigidBody, frameInStatic, frameInRotation, true);


//
//        rotCon.setLinearLowerLimit(new Vector3(1, 0, 0));
//        rotCon.setLinearUpperLimit(new Vector3(-1, 0, 0));

        rotCon.setAngularLowerLimit(new Vector3(1, 1, 1));
        rotCon.setAngularUpperLimit(new Vector3(-1, -1, -1));

        rotCon.setDbgDrawSize(5f);
        world.bulletWorld.addConstraint(rotCon, true);

        // rotation -> dynamic
        Matrix4 frameInRotationSpring = new Matrix4();
        frameInRotationSpring.setTranslation(2.5f, 0, 0);

        Matrix4 frameInDynamic = new Matrix4();
        frameInDynamic.setTranslation(-2.5f, 0, 0);

        btGeneric6DofSpringConstraint springCon = new btGeneric6DofSpringConstraint(boxRotation.btRigidBody, boxDynamic.btRigidBody, frameInRotationSpring, frameInDynamic, true);
        springCon.setLinearLowerLimit(new Vector3(1, 0, 0));
        springCon.setLinearUpperLimit(new Vector3(-1, 0, 0));
        springCon.setAngularLowerLimit(new Vector3(1, 1, 1));
        springCon.setAngularUpperLimit(new Vector3(-1, -1, -1));


        springCon.enableSpring(0, true);
        springCon.setStiffness(0, 10f);
        springCon.setDamping(0, 0.05f);
        springCon.setEquilibriumPoint(0, 6f);

        world.bulletWorld.addConstraint(springCon, true);
//        c.enableSpring(1, true);
//        c.setStiffness(1, 39.478f);
//        c.setDamping(1, 0.03f);
//        c.setEquilibriumPoint(1, 0f);
//        c.enableSpring(2, true);
//        c.setStiffness(2, 39.478f);
//        c.setDamping(2, 0.03f);
//        c.setEquilibriumPoint(2, 0f);


//        Matrix4 frameInA2 = new Matrix4();
//        frameInA2.setTranslation(-2.5f, 0, 0);
//
//        Matrix4 frameInB2 = new Matrix4(boxDynamic.instance.transform);
//        frameInB2.setTranslation(2.5f, 0, 0);

//        c2 = new btGeneric6DofSpringConstraint( boxDynamic.btRigidBody, boxRotation.btRigidBody, frameInB2, frameInA2, true);
//        c2.setLinearUpperLimit(new Vector3(50, 0, 0));
//        c2.setLinearLowerLimit(new Vector3(-50, 0, 0));
//        c2.setDbgDrawSize(5f);
//
//        c2.setAngularLowerLimit(new Vector3(0, 0, 0f));
//        c2.setAngularUpperLimit(new Vector3(0, 0, 0f));
//
//        c2.enableSpring(0, true);
//        c2.setStiffness(0, 10f);
//        c2.setDamping(0, 0.03f);
//        c2.setEquilibriumPoint(0, 0f);


//        world.bulletWorld.addConstraint(c2, false);

        btCollisionShape cursorCollisionShape = new btSphereShape(0.5f);
        EntityConstructor cursorConstructor = new EntityConstructor("cursor", models.get("cursor"), cursorCollisionShape, 0f, false);


//        cursor = cursorConstructor.construct();
//        cursor.instance.transform.setToTranslation(-12.5f, 16f, 0f);
//        world.addModel(cursor.instance);

        // Tetris

        tetrisObjects = new ArrayList<>();
        logger.info("Creating tetris blocks");
        EntityConstructor tetrisConstructor = new EntityConstructor("tetris", models.get("tetris"), 1f, true);
        tetrisConstructor.info.setRestitution(0.3f);
//
        for (int i = 0; i < 1; i++) {
            Vector3 pos = new Vector3(MathUtils.random(-15f, 15f), MathUtils.random(5f, 15f), MathUtils.random(-15f, 15f));
            Vector3 rot = new Vector3(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f));
            float angle = MathUtils.random(-90f, 90f);

            WorldEntity tetris1 = tetrisConstructor.construct(pos, rot, angle);

            tetris1.setColor(MathUtils.random(0, 1f), MathUtils.random(0, 1f), MathUtils.random(0, 1f), 1, true);

            world.addEntity(tetris1);
            tetrisObjects.add(tetris1);

        }
        for (int i = 0; i < 10; i++) {
            Vector3 pos = new Vector3(MathUtils.random(-15f, 15f), MathUtils.random(5f, 15f), MathUtils.random(-15f, 15f));
            Vector3 rot = new Vector3(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f));
            float angle = MathUtils.random(-90f, 90f);

            WorldEntity tetris1 = dynamicBoxConstructor.construct(pos, rot, angle);

            tetris1.setColor(MathUtils.random(0, 1f), MathUtils.random(0, 1f), MathUtils.random(0, 1f), 1, true);

            world.addEntity(tetris1);

        }

        logger.info("Creating hub");

        btCompoundShape hubShape = new btCompoundShape();

        btCylinderShape btCylinderShape = new btCylinderShapeZ(new Vector3(0.25f, 0.25f, 0.025f)); // høyde, dunno, bredde
        Matrix4 cylinderPos = new Matrix4();
        cylinderPos.translate(0, 0, 0.275f);
//        cylinderPos.rotate(1f, 0f, 0f, 90f);


        btCylinderShape btCylinderShape2 = new btCylinderShapeZ(new Vector3(0.25f, 0.25f, 0.025f));
        Matrix4 cylinderPos2 = new Matrix4();
        cylinderPos2.translate(0, 0, -0.275f);
//        cylinderPos2.rotate(1f, 0f, 0f, 90f);


        btCylinderShape btCylinderShape3 = new btCylinderShapeZ(new Vector3(0.03f, 0.03f, 0.75f));
        Matrix4 cylinderPos3 = new Matrix4();
        cylinderPos3.translate(0, 0, 0);
//        cylinderPos3.rotate(1f, 0f, 0f, 90f);

        hubShape.addChildShape(cylinderPos, btCylinderShape);
        hubShape.addChildShape(cylinderPos2, btCylinderShape2);
        hubShape.addChildShape(cylinderPos3, btCylinderShape3);

//        btTriangleIndexVertexArray i2 = new btTriangleIndexVertexArray(models.get("hub").meshParts);
//        btGImpactMeshShape shape4 = new btGImpactMeshShape(i2);
//        shape4.setLocalScaling(new Vector3(1f, 1f, 1f));
//        shape4.setMargin(0.2f);
//        shape4.updateBound();

        EntityConstructor hubConstructor = new EntityConstructor("hub", models.get("hub"), hubShape, 1f, true);
        WorldEntity hub = hubConstructor.construct();
        hub.instance.transform.setTranslation(10, 10, 10);
        hub.update();
        world.addEntity(hub);


        logger.info("Creating rim");
        Array<MeshPart> meshParts = models.get("rim").meshParts;
        btTriangleIndexVertexArray i = new btTriangleIndexVertexArray(meshParts);
        btGImpactMeshShape shape3 = new btGImpactMeshShape(i);
        shape3.setLocalScaling(new Vector3(1f, 1f, 1f));
        shape3.setMargin(0.05f);
        shape3.updateBound();
//        shape3.setLocalScaling(new Vector3(0.001f, 0.001f, 0.001f));
//        btCollisionShape shape3 = new btBvhTriangleMeshShape(i, false);


        EntityConstructor rimConstructor = new EntityConstructor("rim", models.get("rim"), shape3, 1f, true);
        WorldEntity rim = rimConstructor.construct();
        rim.instance.transform.setTranslation(10, 13, 10);
        rim.instance.transform.rotate(1f, 0f, 0f, 90f);
        rim.update();
        world.addEntity(rim);



        // FPS, bullet info for debug

        //

        logger.info("Setup complete");

        tetrisConstructor.dispose();
        groundConstructor.dispose();
        sphereConstructor.dispose();


    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
        cameraInputController.update();
    }

    boolean applyForce = false;
    float force = 50f;
    Ray ray;

    @Override
    public void render() {
        cameraInputController.update();

        ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            world.toggleDebug();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            applyForce = !applyForce;
//            boxDynamic.btRigidBody.applyTorqueImpulse(new Vector3(10, 10, 10));
//            sphere3.btRigidBody.applyCentralForce(new Vector3(-50, 0, 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            world.click(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            world.click(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            world.click(5);
        }

        world.hover(ray);

        if (applyForce) {
            if (force < 50) {
                force += 0.1f;
            }
            boxDynamic.btRigidBody.applyCentralForce(new Vector3(force, 0, 0));
        }


//        logger.debug("Impulse: {}", c.getAppliedImpulse());
//        logger.debug("Feedback: {} \t {}", vec, vec2);


        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            for (WorldEntity t : tetrisObjects) {
                t.btRigidBody.applyImpulse(new Vector3(0, 10f, 0), new Vector3(MathUtils.random(-0.5f, 0.5f), MathUtils.random(-0.5f, 0.5f), MathUtils.random(-0.5f, 0.5f)));
            }
        }

        world.update(Gdx.graphics.getRawDeltaTime());


        Vector3 posA = new Vector3();
        Vector3 posB = new Vector3();



//        c.getCalculatedTransformA().getTranslation(posA);
//        c.getCalculatedTransformB().getTranslation(posB);

//        cursor.instance.transform.setToTranslation(posA);
//        float dst = posA.dst(posB);
//        logger.debug("Position {} \t {} \t dst {}", posA, posB, dst);





        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glClearColor(0, 0, 0, 1);

        batch.begin(camera);
        world.render(batch, environment);
        batch.end();

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
        batch.dispose();

        models.values().forEach(Model::dispose);


        models.clear();
        tetrisObjects.clear();
        // TODO shapes må leve og disposes! Kan uansett beholde Constructors da jeg i mitt
        // tilfelle skal ha flere verdener
        world.dispose();
        world = null;
        System.gc();
    }
}
