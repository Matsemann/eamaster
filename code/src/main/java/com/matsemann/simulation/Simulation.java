package com.matsemann.simulation;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.entity.Hub;
import com.matsemann.simulation.entity.Rim;
import com.matsemann.simulation.entity.Spoke;
import com.matsemann.simulation.entity.WheelWorld;
import com.matsemann.simulation.result.SimulationData;
import com.matsemann.simulation.setup.SimulationConstants;
import com.matsemann.simulation.setup.SpokeAngles;
import com.matsemann.simulation.setup.SpokeCreator;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Simulation implements Disposable {

    Logger logger = LoggerUtil.getLogger(getClass());

    public WheelWorld wheelWorld;
    private Hub hub;
    private Rim rim;
    private List<Spoke> spokes;

    public Map<String, SimulationData> simulationResults = new LinkedHashMap<>();


    public Simulation(String name, Map<String, Model> models, SpokeAngles spokeAngles, boolean visualize) {
        // get loaded shapes & stuff

        // create hub, rim, spokes
        hub = new Hub(name + "hub", models.get("hub"));
        rim = new Rim(name + "rim", models.get("rim"));

        spokes = new SpokeCreator().create(name, spokeAngles, models, hub, rim);

        wheelWorld = new WheelWorld(name + "world", hub, rim, spokes, visualize);
    }

    public void render(ModelBatch batch, Environment environment) {
        wheelWorld.render(batch, environment);
    }
    @Override
    public void dispose() {
        wheelWorld.dispose();
        wheelWorld = null;
        hub = null;
        rim = null;
        spokes = null;
    }

    public Map<String, SimulationData> simulate() {
        reset();
        gatherResults("init");

        forceTests();
        torqueTests();

        return simulationResults;
    }

    private void reset() {
        reset(1f);
    }
    private void reset(float sec) {
        wheelWorld.update(sec);
    }

    private void forceTests() {
        Vector3 force = new Vector3(SimulationConstants.TRANS_FORCE, 0, 0);

        for (int i = 0; i < 4; i++) {
            wheelWorld.rim.rigidBody.applyCentralForce(force);
            wheelWorld.update(3f);
            gatherResults("force" + i);
            force.rotate(90, 0, 0, 1);
            reset();
        }


    }

    private void torqueTests() {
        wheelWorld.rim.rigidBody.applyTorque(new Vector3(0, 0, SimulationConstants.TORQUE_FORCE));
        wheelWorld.update(3f);
        gatherResults("torque1");

        reset(3f);

        wheelWorld.rim.rigidBody.applyTorque(new Vector3(0, 0, -SimulationConstants.TORQUE_FORCE));
        wheelWorld.update(3f);
        gatherResults("torque2");

//        reset(3f);
    }

    private void gatherResults(String name) {
        SimulationData result = new SimulationData(wheelWorld);
        simulationResults.put(name, result);
    }
}
