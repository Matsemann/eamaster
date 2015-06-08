package com.matsemann.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.matsemann.common.LoggerUtil;
import com.matsemann.ea.fitness.RepFull;
import com.matsemann.simulation.result.SimulationResult;
import com.matsemann.simulation.result.SimulationRunner;
import com.matsemann.simulation.setup.SpokeAngles;
import org.slf4j.Logger;

public class SimulationTest implements ApplicationListener {

    Logger logger = LoggerUtil.getLogger(getClass());


    public static void main(String[] args) {
        SimulationTest simulationTest = new SimulationTest();
        LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
        c.width = 1;
        c.height = 1;
        LwjglApplication lwjglApplication = new LwjglApplication(simulationTest, c);
    }

    public SimulationTest() {
    }

    @Override
    public void create() {
        SimulationRunner runner = new SimulationRunner();
        SpokeAngles angles;
        SimulationResult res;
        RepFull repFull;

        logger.info("3x");
        angles = new SpokeAngles();
        angles.createNx(3);
        res = runner.simulate(angles);
        logger.info("Result1: {}", res);
        repFull = new RepFull(res);
        logger.info("Rep: {}", repFull);


        logger.info("radial");
        angles = new SpokeAngles();
        res = runner.simulate(angles);
        logger.info("Result1: {}", res);
        repFull = new RepFull(res);
        logger.info("Rep: {}", repFull);


        logger.info("random");
        angles = new SpokeAngles();
        angles.createRandom();
        res = runner.simulate(angles);
        logger.info("Result1: {}", res);
        repFull = new RepFull(res);
        logger.info("Rep: {}", repFull);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
