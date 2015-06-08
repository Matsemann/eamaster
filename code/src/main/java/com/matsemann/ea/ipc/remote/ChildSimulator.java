package com.matsemann.ea.ipc.remote;

import com.badlogic.gdx.ApplicationListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.result.SimulationResult;
import com.matsemann.simulation.result.SimulationRunner;
import com.matsemann.simulation.setup.SpokeAngles;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChildSimulator implements ApplicationListener {
    Logger logger = LoggerUtil.getLogger(getClass());


    private SimulationRunner simulationRunner;

    private boolean shouldRun = false;

    private String ip;
    private int port;


    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    String fromServer;
    ObjectMapper mapper;

    @Override
    public void create() {
        logger.info("Creating simulation runner");
        simulationRunner = new SimulationRunner();
    }

    public void startListening(String ip, int port) {
        this.ip = ip;
        this.port = port;
        logger.info("Ready to start");
        lastCheck = System.currentTimeMillis();

        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(20000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            shouldRun = true;
            mapper = new ObjectMapper();
            logger.info("Socket created, local port {}, port", socket.getLocalPort(), socket.getPort());
        } catch (IOException e) {
            logger.info("Failed starting socket, {}", e);
            shouldRun = false;
        }

    }

    long lastCheck = 0;

    private boolean shouldCreateConnection() {
        long now = System.currentTimeMillis();

        return now > (lastCheck + 1000 * 60);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        if (!shouldRun) {
            if (shouldCreateConnection()) {
                logger.info("Trying new connection");
                startListening(ip, port);
            }
//            logger.info("Not ready to run");
            return;
        }

        try {
            if (!in.ready()) {
                if (System.currentTimeMillis() < lastCheck + 1000 * 20) {
//                    logger.info("returning");
                    return;
                }
                logger.info("not returning");
            }
//            logger.info("Reading from server");

            fromServer = in.readLine();
            logger.info("Got from server: {}", fromServer);
            SpokeAngles angles = mapper.readValue(fromServer, SpokeAngles.class);

            SimulationResult simulationResult = simulationRunner.simulate(angles);

            String json = mapper.writeValueAsString(simulationResult);
            logger.info("Returning: {}", json);
            lastCheck = System.currentTimeMillis();

            out.println(json);

        } catch (Exception e) {
            logger.error("Exception: {}", e);
            shouldRun = false;
            lastCheck = System.currentTimeMillis();
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

    }

    public void stop() {
        logger.info("Stop called");
        shouldRun = false;
    }
}
