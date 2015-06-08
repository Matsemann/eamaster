package com.matsemann.ea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {


    // Singleton stuff
    private static final TaskManager instance = new TaskManager();

    private TaskManager() {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        createTasks();
    }

    public static TaskManager getInstance() {
        return instance;
    }

    List<RunTask> tasks = new ArrayList<>();

    private void createTasks() {
//        tasks.add(new RunTask("NSGA-IIId-perm", "wheel-4obj-perm", 20, 20000, "populationSize=100"));
//        tasks.add(new RunTask("NSGA-IIId-free", "wheel-4obj-free", 20, 20000, "populationSize=100"));


//        tasks.add(new RunTask("NSGA-IIId", "wheel-2obj-free", 20, 20000, "populationSize=100"));
//        tasks.add(new RunTask("NSGA-IIId-4", "wheel-4obj-free", 20, 20000, "populationSize=100"));





//        tasks.add(new RunTask("NSGA-IId", "wheel-4obj-perm", 10, 20000, "populationSize=100"));
//        tasks.add(new RunTask("NSGAIIId-pop200-1d", "wheel-4obj-free", 20, 20000, "populationSize=200,divisions=1"));
    }


    private void createMoreTasks() {
        // add additional tasks during runtime if needed
    }

    public RunTask getNextTask() {
        createMoreTasks();
        if (tasks.size() > 0) {
            RunTask runTask = tasks.get(0);
            tasks.remove(0);
            return runTask;
        } else {
            return null;
        }
    }

    public class RunTask {
        public String algorithm, problem;
        public int seeds, nfe;
        public Map<String, String> properties;

        public RunTask(String algorithm, String problem, int seeds, int nfe, String properties) {
            this.algorithm = algorithm;
            this.problem = problem;
            this.seeds = seeds;
            this.nfe = nfe;
            this.properties = toMap(properties);
        }


        private Map<String, String> toMap(String values) {
            Map<String, String> map = new HashMap<>();

            String[] props = values.split(",");

            for (int i = 0; i < props.length; i++) {
                String prop = props[i];
                String[] kv = prop.split("=");
                map.put(kv[0], kv[1]);
            }

            return map;
        }
    }
}
