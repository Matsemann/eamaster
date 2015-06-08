package com.matsemann.common;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlotSaver implements ChartChangeListener {
    // Singleton stuff
    private static final PlotSaver instance = new PlotSaver();

    private PlotSaver() {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    public static PlotSaver getInstance() {
        return instance;
    }



    private void renderToSvg(JFreeChart chart) {
//        int width = 1200, height = 1800; // høy
//        int width = 2400, height = 1800; // lang
//        int width = 1200, height = 800; // brukt før

        // 535 small, 630 legend
//        int width = 1200, height = 535;
//        int width = 1200, height = 630;

//        int width = 400, height = 535; // box


        int width = 600, height = 540; // objectives


        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(width, height);
        chart.draw(svgGraphics2D, new Rectangle(width, height));

        String svgElement = svgGraphics2D.getSVGElement();
        try {
            SVGUtils.writeToSVG(new File("latestChart.svg"), svgElement);
            System.out.println("made chart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chartChanged(ChartChangeEvent event) {
        JFreeChart chart = event.getChart();
        if (chart != null) {
            renderToSvg(chart);
        }
    }
}
