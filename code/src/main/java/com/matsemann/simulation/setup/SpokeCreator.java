package com.matsemann.simulation.setup;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConeTwistConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btGeneric6DofSpringConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.matsemann.simulation.ColorMap;
import com.matsemann.simulation.entity.Spoke;
import com.matsemann.simulation.entity.Box;
import com.matsemann.simulation.entity.Hub;
import com.matsemann.simulation.entity.Rim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpokeCreator {

    private String name;
    private Hub hub;
    private Rim rim;

//    private Vector3 tmpVector = new Vector3(0, 0, 0);


    public List<Spoke> create(String name, SpokeAngles spokeAngles, Map<String, Model> models, Hub hub, Rim rim) {
        this.name = name;
        this.hub = hub;
        this.rim = rim;

        List<Box> boxes = createBoxes(models.get("box"));
        List<Spoke> spokes = createSpokes(models.get("spoke"));

        setupConstraints(spokeAngles, boxes, spokes);

        return spokes;
    }

    private void setupConstraints(SpokeAngles spokeAngles, List<Box> boxes, List<Spoke> spokes) {
        int side = 1;
        for (int i = 0; i < SimulationConstants.NUM_SPOKES; i++) {
            setupConstraint(i, side, spokeAngles.get(i % SimulationConstants.NUM_SPOKES_ONE_SIDE), boxes.get(i), spokes.get(i));

            if (i == SimulationConstants.NUM_SPOKES_ONE_SIDE - 1) {
                side = -1;
            }
        }
    }

    private void setupConstraint(int nr, int side, float spokeAngle, Box box, Spoke spoke) {
        float onHubRotation = nr * SimulationConstants.SPOKE_ANGLE_DISTANCE;
        if (side == -1) {
            onHubRotation += SimulationConstants.SPOKE_ANGLE_DISTANCE / 2f;// + 45f; // TODO possibly add 180 here, or something
        }
        float onRimRotation = onHubRotation + spokeAngle;

        // box on rim
        Vector3 boxOnRim = new Vector3(SimulationConstants.HUB_CENTER_TO_RIM, 0, 0);
        boxOnRim.rotate(onRimRotation, 0, 0, 1);
        // TODO take into account spokeAngle

        box.modelInstance.transform.setTranslation(boxOnRim.x, boxOnRim.y, 0);
        box.modelInstance.transform.rotate(0, 0, 1, onRimRotation + 180);
        box.update();
        Matrix4 inBox = new Matrix4();

        Matrix4 onRim = new Matrix4();
        onRim.setToTranslation(boxOnRim.x, boxOnRim.y, 0);
        onRim.rotate(0, 0, 1, onRimRotation + 180);

        btConeTwistConstraint coneTwistConstraint = new btConeTwistConstraint(rim.rigidBody, box.rigidBody, onRim, inBox);
        coneTwistConstraint.setLimit(SimulationConstants.CONE_TWIST_LIMIT, SimulationConstants.CONE_TWIST_LIMIT, 0);

        // Spring on hub
        float hubFromCenter = SimulationConstants.HUB_FLANGE_DISTANCE / 2f;
        Vector3 onHub = new Vector3(SimulationConstants.HUB_FLANGE_RADIUS, 0,side * hubFromCenter);
        onHub.rotate(onHubRotation, 0, 0, 1);

        Matrix4 springOnHub = new Matrix4();
        springOnHub.setToTranslation(onHub.x, onHub.y, onHub.z);

        Matrix4 springOnBox = new Matrix4();
        springOnBox.setTranslation(SimulationConstants.ROTATING_BOX_DISTANCE, 0, 0);

        btGeneric6DofSpringConstraint springConstraint = new btGeneric6DofSpringConstraint(box.rigidBody, hub.rigidBody, springOnBox, springOnHub, true);
        springConstraint.setAngularLowerLimit(new Vector3(1, 0, 0));
        springConstraint.setLinearUpperLimit(new Vector3(-1, 0, 0));
        springConstraint.setAngularLowerLimit(new Vector3(1, 1, 1));
        springConstraint.setAngularUpperLimit(new Vector3(-1, -1, -1));
        springConstraint.setDbgDrawSize(2f);

        float length = new Vector3(boxOnRim).sub(onHub).len() -SimulationConstants.ROTATING_BOX_DISTANCE -SimulationConstants.DEFAULT_TENSION_LENGTH;

        springConstraint.enableSpring(0, true);
        springConstraint.setStiffness(0, SimulationConstants.SPRING_STIFFNESS);
        springConstraint.setDamping(0, SimulationConstants.SPRING_DAMPENING);
        springConstraint.setEquilibriumPoint(0, length); // TODO

        spoke.box = box;
        spoke.coneTwistConstraint = coneTwistConstraint;
        spoke.springConstraint = springConstraint;
        spoke.pointA.set(onHub);
        spoke.setInitialLength(length);// + 5f; // TODO
    }


    private List<Box> createBoxes(Model boxModel) {
        float boxSize = SimulationConstants.BOX_SIZE;
        float boxMass = SimulationConstants.BOX_MASS;

        btCollisionShape boxShape = new btBoxShape(new Vector3(boxSize, boxSize, boxSize));
        Vector3 boxInertia = new Vector3(0, 0, 0);
        boxShape.calculateLocalInertia(boxMass, boxInertia);


        btRigidBody.btRigidBodyConstructionInfo boxInfo = new btRigidBody.btRigidBodyConstructionInfo(boxMass, null, boxShape, boxInertia);
        boxInfo.setLinearDamping(0.1f);
        boxInfo.setAngularDamping(0.1f);

        List<Box> boxes = new ArrayList<>();

        for (int i = 0; i < SimulationConstants.NUM_SPOKES; i++) {
            boxes.add(new Box(name + "box" + i, boxModel, boxInfo));
        }

        boxes.get(0).disposables.add(boxShape); // One of them needs to dispose the shape when done
        boxInfo.dispose();

        return boxes;
    }





    private List<Spoke> createSpokes(Model spokeModel) {
        List<Spoke> spokes = new ArrayList<>();
        ColorMap colorMap = new ColorMap(0, 3000).createYlGrRd();

        for (int i = 0; i < SimulationConstants.NUM_SPOKES; i++) {
            spokes.add(new Spoke(name + "spoke" + i, spokeModel, colorMap));
        }

        return spokes;
    }


    // create boxes

    // create spokes

    // create constraints

    // last spoke disposes the shape, ey?
}
