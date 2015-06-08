package com.matsemann.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.WheelRenderer;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.*;

public class SwingTestRunner extends JFrame {

    Logger logger = LoggerUtil.getLogger(getClass());
    private final LwjglCanvas lwjglCanvas;


    public SwingTestRunner() {
        logger.info("Starting application");


        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.width = 1200;
        configuration.height = 900;
        configuration.samples = 4;

        logger.info("Config is: {}x{}", configuration.width, configuration.height);

        ApplicationListener app = new WheelRenderer();
        lwjglCanvas = new LwjglCanvas(app, configuration);


        logger.info("Application listener used is {}", app.getClass());

        setName("Test frame ooo");

        JButton b = new JButton("lol");
        b.addActionListener(e -> {
            logger.info("Button clicked");
//            ((WheelRenderer) app).doStuff();
//            lwjglCanvas.stop();
//            getContentPane().remove(lwjglCanvas.getCanvas());
        });

        Container c = getContentPane();
        lwjglCanvas.getCanvas().setBounds(0, 0, 1200, 900);
        b.setBounds(900, 0, 100, 900);
        c.add(lwjglCanvas.getCanvas(), BorderLayout.LINE_START);
        c.add(b, BorderLayout.CENTER);

        pack();
        setVisible(true);
        setSize(1300, 900);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    @Override
    public void dispose() {
        logger.info("Disposing");
        lwjglCanvas.exit();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingTestRunner::new);
    }
}
