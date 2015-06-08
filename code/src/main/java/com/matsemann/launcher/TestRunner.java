package com.matsemann.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.WheelRenderer;
import org.slf4j.Logger;

public class TestRunner {

    Logger logger = LoggerUtil.getLogger(getClass());

    private void run() {
        logger.info("Starting application");


        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();

        configuration.width = 1200;
        configuration.height = 900;


        configuration.samples = 4;

        logger.info("Config is: {}x{}", configuration.width, configuration.height);


        ApplicationListener test;


//        test = new BulletTestCollection();
//        test = new SpringTest();
//        test = new WheelTest();
        test = new WheelRenderer();
//        test = new ShadowMappingTest();

        logger.info("Application listener used is {}", test.getClass());

        new LwjglApplication(test, configuration);
    }

    public static void main(String[] args) {
        new TestRunner().run();
    }
}
