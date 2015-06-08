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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.matsemann.common.LoggerUtil;
import com.matsemann.testing.simulator.stuff.EntityConstructor;
import com.matsemann.testing.simulator.stuff.World;
import com.matsemann.testing.simulator.stuff.WorldEntity;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WheelTest implements ApplicationListener {

    Logger logger = LoggerUtil.getLogger(getClass());


    private Map<String, Model> models;

    private PerspectiveCamera camera;
    private CameraInputController cameraInputController;
    private Environment environment;
    private ModelBatch batch;
    private World world;

    private WorldEntity boxStatic, boxStatic2;
    private WorldEntity boxDynamic;
    private btGeneric6DofSpringConstraint c, c2;
    private btJointFeedback jointFeedback;
    private WorldEntity cursor;
    private btTriangleIndexVertexArray btTriangleIndexVertexArray;

    private List<btCollisionShape> shapes = new ArrayList<>();


    @Override
    public void create() {
        logger.info("Setting up camera");

        camera = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(40f, 30f, 40f);
        camera.lookAt(0, 0f, 0);
        camera.near = 1f;
        camera.far = 500f;
        camera.update();

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);



        logger.info("Creating models and instances");
        models = new HashMap<>();
        ModelBuilder builder = new ModelBuilder();

        models.put("box", builder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                Usage.Position | Usage.Normal));
        models.put("minibox", builder.createBox(.5f, .5f, .5f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                Usage.Position | Usage.Normal));
        models.put("cursor", builder.createBox(0.5f, 0.5f, 0.5f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                Usage.Position | Usage.Normal));



        G3dModelLoader loader = new G3dModelLoader(new JsonReader());
        models.put("hub", loader.loadModel(Gdx.files.internal("data/hubreal.g3dj")));
        models.put("rim", loader.loadModel(Gdx.files.internal("data/rimreal.g3dj")));
        models.put("sphere", loader.loadModel(Gdx.files.internal("data/sphere.g3dj")));
        models.put("spoke", loader.loadModel(Gdx.files.internal("data/spoke.g3dj")));



        logger.info("Let there be light");
        batch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));

        environment.add(new DirectionalLight().set(0.3f, 0.3f, 0.3f, -5f, -3f, -1f));
        environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 5f, 8f, 0f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 5f, -8f, 10f));


        logger.info("Initializing Bullet");
        Bullet.init();

        world = new World("MainWorld");
        world.bulletWorld.setGravity(new Vector3(0, 0, 0));



        logger.info("Creating Sphere around us!");
        btCollisionShape sphereShape = new btSphereShape(25f);
        shapes.add(sphereShape);
        EntityConstructor sphereConstructor = new EntityConstructor("sphere", models.get("sphere"), sphereShape, 0f);
        WorldEntity sphere = sphereConstructor.construct();
        world.addModel(sphere.instance);
        sphere.setColor(1, 1, 1, 1, true);

        logger.info("Creating hub");

        btCompoundShape hubShape = new btCompoundShape();

        btCylinderShape btCylinderShape = new btCylinderShapeZ(new Vector3(2.5f, 2.5f, 0.25f)); // høyde, dunno, bredde
        Matrix4 cylinderPos = new Matrix4();
        cylinderPos.translate(0, 0, 2.75f);


        btCylinderShape btCylinderShape2 = new btCylinderShapeZ(new Vector3(2.5f, 2.5f, 0.25f));
        Matrix4 cylinderPos2 = new Matrix4();
        cylinderPos2.translate(0, 0, -2.75f);


        btCylinderShape btCylinderShape3 = new btCylinderShapeZ(new Vector3(0.3f, 0.3f, 7.5f));
        Matrix4 cylinderPos3 = new Matrix4();
        cylinderPos3.translate(0, 0, 0);

        hubShape.addChildShape(cylinderPos, btCylinderShape);
        hubShape.addChildShape(cylinderPos2, btCylinderShape2);
        hubShape.addChildShape(cylinderPos3, btCylinderShape3);

        EntityConstructor hubConstructor = new EntityConstructor("hub", models.get("hub"), hubShape, 0f, true);
        WorldEntity hub = hubConstructor.construct();
        hub.instance.transform.setTranslation(0, 0, 0);
        hub.update();
        world.addEntity(hub);

        shapes.add(hubShape);
        shapes.add(btCylinderShape);
        shapes.add(btCylinderShape2);
        shapes.add(btCylinderShape3);


        logger.info("Creating rim");
        Array<MeshPart> meshParts = models.get("rim").meshParts;
        btTriangleIndexVertexArray = new btTriangleIndexVertexArray(meshParts);
        btGImpactMeshShape rimShape = new btGImpactMeshShape(btTriangleIndexVertexArray);
        rimShape.setLocalScaling(new Vector3(1f, 1f, 1f));
        rimShape.setMargin(0.05f);
        rimShape.updateBound();


        EntityConstructor rimConstructor = new EntityConstructor("rim", models.get("rim"), rimShape, 1f, true);
        rimConstructor.info.setLinearDamping(0.3f);
        rimConstructor.info.setAngularDamping(0.3f);

        WorldEntity rim = rimConstructor.construct();
        rim.instance.transform.setTranslation(0, 0, 0);
//        rim.instance.transform.rotate(1f, 0f, 0f, 90f);
        rim.update();
        world.addEntity(rim);

        shapes.add(rimShape);



        logger.info("Creating boxes");
        btCollisionShape miniBoxShape = new btBoxShape(new Vector3(1f, 1f, 1f));
        shapes.add(miniBoxShape);
        EntityConstructor miniBoxConstructor = new EntityConstructor("minibox", models.get("minibox"), miniBoxShape, .1f, true);
        miniBoxConstructor.info.setLinearDamping(0.8f);
        miniBoxConstructor.info.setAngularDamping(0.8f);
//        miniBoxConstructor.info.setRestitution(0.9f);

//        Vector3 pos = new Vector3(0.225f, 0, 0);
        Vector3 pos = new Vector3(2.25f, 0, 0);
        Vector3 rimPos = new Vector3(31.25f, 0, 0);
        float rotation = 0;

//        pos.rotate(90, 0, 0, 1);
//        rimPos.rotate(90, 0, 0, 1);
//        rotation+= 90;
//     pos.rotate(90, 0, 0, 1);
//        rimPos.rotate(90, 0, 0, 1);
//        rotation+= 90;

//        pos.rotate(180, 0, 0, 1);
//        rimPos.rotate(180, 0, 0, 1);
//        rotation+= 180;
//
//
        pos.rotate(120, 0, 0, 1);
        rimPos.rotate(120, 0, 0, 1);
        rotation+= 120;

        float direction = 1;
        for (int i = 0; i < 32; i++) {
            WorldEntity miniBox = miniBoxConstructor.construct();
            miniBox.instance.transform.setTranslation(rimPos.x, rimPos.y, 0);
            miniBox.instance.transform.rotate(0, 0, 1, rotation+180);
            miniBox.setColor(0.7f, 0, 0, 0, true);
            miniBox.update();
            world.addEntity(miniBox, (short) 128, (short) 64);// (short) (~((short) 128)));
//            world.addEntity(miniBox);


            Matrix4 onRim = new Matrix4();
            onRim.setToTranslation(rimPos.x, rimPos.y, 0);
            onRim.rotate(0, 0, 1, rotation + 180);


            Matrix4 inBox = new Matrix4();
//            inBox.translate(-5f, 0, 0);

            btConeTwistConstraint constraint = new btConeTwistConstraint(rim.btRigidBody, miniBox.btRigidBody, onRim, inBox);
            constraint.setLimit(0.785398f, 0.785398f, 0);
            constraint.setDbgDrawSize(3f);
            world.addConstraint(constraint, true);



            Matrix4 onHub = new Matrix4();
            onHub.setToTranslation(pos.x, pos.y, 2.75f * direction);

            Matrix4 onBoxRotation = new Matrix4();
            onBoxRotation.setTranslation(5f, 0, 0);
//            onBoxRotation.rotate(0, 0, 1, rotation);

//            onRim.rotate(0, 0, 1, rotation);


            btGeneric6DofSpringConstraint constraintRim = new btGeneric6DofSpringConstraint( miniBox.btRigidBody, hub.btRigidBody, onBoxRotation, onHub, true);
            constraintRim.setLinearLowerLimit(new Vector3(1, 0, 0));// upper < lower = free
            constraintRim.setLinearUpperLimit(new Vector3(-1, 0, 0));
            constraintRim.setAngularLowerLimit(new Vector3(1, 1, 1));
            constraintRim.setAngularUpperLimit(new Vector3(-1, -1, -1));

            constraintRim.setDbgDrawSize(3f);
            constraintRim.enableSpring(0, true);
            constraintRim.setStiffness(0, 500f);
            constraintRim.setDamping(0, 0.02f);
            constraintRim.setEquilibriumPoint(0, 22f);

            world.addConstraint(constraintRim, true);


            pos.rotate(22.5f, 0, 0, 1);
            rimPos.rotate(22.5f, 0, 0, 1);
            rotation+= 22.5f;

            if (i == 15) {
                pos.rotate(11.25f, 0, 0, 1);
                rimPos.rotate(11.25f, 0, 0, 1);
                rotation+= 11.25f;
                direction *= -1;
            }
        }


        logger.info("Creating spokes");
        btCollisionShape spokeShape = new btCylinderShapeX(new Vector3(.5f, .09f, 10f)); // lengde, bredde, ?
        shapes.add(spokeShape);
        EntityConstructor spokeConstructor = new EntityConstructor("spoke1_", models.get("spoke"), spokeShape, 0f, true);


        WorldEntity spoke1 = spokeConstructor.construct(new Vector3(0, 0, 15), new Vector3(), 0);
        spoke1.instance.transform.scale(29, 1f, 1);
        spoke1.btRigidBody.getCollisionShape().setLocalScaling(new Vector3(29, 1, 1));
//        spoke1.update();

        world.addEntity(spoke1);

//        spokeShape.setLocalScaling(new Vector3(2, 1, 1));


        logger.info("Disposing constructors");

        hubConstructor.dispose();
        rimConstructor.dispose();
        sphereConstructor.dispose();
        miniBoxConstructor.dispose();
        // FPS, bullet info for debug

        //

        logger.info("Setup complete");

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
        cameraInputController.update();
    }

    Ray ray;

    @Override
    public void render() {
        cameraInputController.update();


        ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            world.toggleDebug();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            world.click(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            world.click(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            world.click(5);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            world.click(6);
        }

        world.hover(ray);


        world.update(Gdx.graphics.getRawDeltaTime());


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

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

        shapes.forEach(btCollisionShape::dispose);
        shapes.clear();

        world.dispose();
        world = null;
        System.gc();
    }
}
