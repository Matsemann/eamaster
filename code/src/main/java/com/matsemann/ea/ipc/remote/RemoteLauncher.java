package com.matsemann.ea.ipc.remote;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.matsemann.common.LoggerUtil;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RemoteLauncher extends JFrame {

    Logger logger = LoggerUtil.getLogger(getClass());

    private final JTextField ipField;
    private ChildSimulator simulator;
    private LwjglApplication lwjglApplication;
    private final JButton runButton;

    public RemoteLauncher() {
        int id = new Random().nextInt(9999);

        logger.info("Starting, with id {}", id);


        setTitle("Simulator " + id);

        ipField = new JTextField("129.241.102.124");
        ipField.setSize(150, 50);

        runButton = new JButton("Run");
        runButton.addActionListener(e -> run());

        JLabel label = new JLabel("Don't close the other window, close this");

        FlowLayout flowLayout = new FlowLayout();
        setLayout(flowLayout);


        getContentPane().add(ipField);
        getContentPane().add(runButton);
        getContentPane().add(label);

        pack();
        setVisible(true);
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void run() {
        logger.info("Run button pressed");
        runButton.setEnabled(false);

        String ip = ipField.getText();
        int port = 1337;


        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.height = 1;
        conf.width = 1;
        conf.x = getX();
        conf.y = getY();

        simulator = new ChildSimulator();
        lwjglApplication = new LwjglApplication(simulator, conf);
        simulator.startListening(ip, port);

        SwingUtilities.invokeLater(this::toFront);
    }

    @Override
    public void dispose() {
        logger.info("Disposing main window");
        if (simulator != null) {
            simulator.stop();
        }
        if (lwjglApplication != null) {
            lwjglApplication.exit();
        }

        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RemoteLauncher::new);
    }
}
