package com.matsemann.simulation.result;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.JsonReader;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.Simulation;
import com.matsemann.simulation.setup.SpokeAngles;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SimulationRunner {
    Logger logger = LoggerUtil.getLogger(getClass());


    private Map<String, Model> models = new HashMap<>();
    private int simCount = 0;


    public SimulationRunner() {
        logger.info("Loading models");
        ModelBuilder builder = new ModelBuilder();
        models.put("box", builder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0f, 0f, 0f))),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));

        G3dModelLoader loader = new G3dModelLoader(new JsonReader());
        models.put("hub", loader.loadModel(Gdx.files.internal("data/hubreal.g3dj")));
        models.put("rim", loader.loadModel(Gdx.files.internal("data/rimreal.g3dj")));
        models.put("spoke", loader.loadModel(Gdx.files.internal("data/spoke.g3dj")));


        Bullet.init();
        logger.info("Initialized Bullet");
    }

    public SimulationResult simulate(SpokeAngles angles) {
        Simulation simulation = new Simulation("sim" + simCount++, models, angles, false);
        Map<String, SimulationData> simulationData = simulation.simulate();
        simulation.dispose();

        return new SimulationResult(simulationData);
    }
}
