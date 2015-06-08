package com.matsemann.ea.ipc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsemann.common.LoggerUtil;
import com.matsemann.simulation.result.SimulationResult;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Communicates with a program running elsewhere, sends it tasks
 * and reads the results
 */
public class WorkerCommunicator extends Thread {
    Logger logger = LoggerUtil.getLogger(getClass());

    private Socket socket;
    private BlockingQueue<Task> taskQueue;
    private CountDownLatch latch;
    private ObjectMapper mapper = new ObjectMapper();

    public WorkerCommunicator(Socket socket, BlockingQueue<Task> taskQueue) {
        this.socket = socket;
        this.taskQueue = taskQueue;
        try {
            this.socket.setSoTimeout(10000);
        } catch (SocketException e) {
            logger.info("Couldn't set timeout: {}", e);
        }
        logger.info("Worker created");
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        Task currentTask = null;

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            while (true) {
//                logger.info("Waiting for task");
                currentTask = taskQueue.take();

//                logger.info("Got task");

                String jsonAngles = mapper.writeValueAsString(currentTask.angles);
                out.println(jsonAngles);

//                logger.info("Sent: {}", jsonAngles);

                String result = in.readLine();
                currentTask.result = mapper.readValue(result, SimulationResult.class);

//                logger.info("Got: {}", result);

                latch.countDown();
            }
        } catch (IOException | InterruptedException e) {
            try {
                if (currentTask != null) taskQueue.put(currentTask);
            } catch (InterruptedException e1) {
                e1.printStackTrace(); // lol fuck it
            }

            logger.error("Connection died or timed out: {}", e);
        } catch (NullPointerException e) {
            logger.error("NPE: {}", e);
            try {
                if (currentTask != null) taskQueue.put(currentTask);
            } catch (InterruptedException e1) {
                e1.printStackTrace(); // lol fuck it
            }
        }
    }
}
