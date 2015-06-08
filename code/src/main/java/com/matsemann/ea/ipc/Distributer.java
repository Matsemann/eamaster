package com.matsemann.ea.ipc;

import com.matsemann.common.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class Distributer {

    // Singleton stuff
    private static final Distributer instance = new Distributer();

    private Distributer() {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        createListener();
    }

    public static Distributer getInstance() {
        return instance;
    }


    // The class itself
    Logger logger = LoggerUtil.getLogger(getClass());


    private CountDownLatch latch;
    private List<WorkerCommunicator> workers = new CopyOnWriteArrayList<>();
    private BlockingQueue<Task> taskQueue = new ArrayBlockingQueue<>(100);

    /**
     * Listens for new socket connections and create new workers
     */
    private void createListener() {
        logger.info("Distributer is creating socket listener");
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(1337);
                while (true) {
                    Socket newSocket = serverSocket.accept();
                    logger.info("Got new connection, local port {}, port {} ", newSocket.getLocalPort(), newSocket.getPort());
//                    System.out.println("Got a new connection");
                    WorkerCommunicator worker = new WorkerCommunicator(newSocket, taskQueue);
                    worker.setLatch(latch);
                    workers.add(worker);
                    worker.start();
//                    Worker worker = new Worker(newSocket, work, latch);
//                    workers.add(worker);
//                    worker.start();
                }
            } catch (IOException e) {
                logger.error("Couldn't create more sockets {}", e);
            }
        }).start();
    }


    public void runTasks(List<Task> tasks) {
        long start = System.currentTimeMillis();
        // create new latch, add to workers
        latch = new CountDownLatch(tasks.size());

        workers.removeIf(w -> !w.isAlive());

        for (WorkerCommunicator worker : workers) {
            worker.setLatch(latch);
        }

        // Add stuff to queue, the workers will take it
        for (Task task : tasks) {
            try {
                taskQueue.put(task);
            } catch (InterruptedException e) {
                logger.error("Error adding tasks {}", e);
            }
        }

        // Wait until everything is done
        try {
//            logger.info("Waiting for latch");
            latch.await();
            logger.info("Time spent: {}, workers: {}", System.currentTimeMillis() - start, workers.size());
        } catch (InterruptedException e) {
            logger.error("Error waiting for latch {}", e);

        }

    }
}
