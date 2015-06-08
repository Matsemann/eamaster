package com.matsemann.common;

import com.badlogic.gdx.utils.PerformanceCounter;

import java.util.HashMap;
import java.util.Map;

public class CounterUtil {

    private static Map<String, PerformanceCounter> counters = new HashMap<>();

    public static PerformanceCounter getCounter(String name) {
        PerformanceCounter counter = counters.get(name);
        if (counter == null) {
            counter = new PerformanceCounter(name);
            counters.put(name, counter);
        }
        return counter;
    }

    public static void disposeCounter(PerformanceCounter counter) {
        counters.remove(counter.name);
    }
}
