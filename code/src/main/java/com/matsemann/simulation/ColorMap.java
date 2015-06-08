package com.matsemann.simulation;

import com.badlogic.gdx.graphics.Color;

public class ColorMap {

    Color[] palette;

    private float min, max;

    public ColorMap(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public Color getColor(float value) {
        if (value < min) {
            return palette[0];
        } else if (value >= max) {
            return palette[palette.length - 1];
        } else {
            int id = (int) (palette.length * (value - min) / (max - min));
            return palette[id];
        }
    }

    public ColorMap createYlOrRd() {
        float[][] colors = new float[][]{{255, 255, 255}, {255, 255, 224}, {255, 255, 192}, {255, 255, 160}, {255, 255, 129}, {255, 255, 97}, {255, 255, 65}, {255, 255, 33}, {255, 242, 23}, {255, 224, 19}, {255, 207, 16}, {255, 189, 12}, {255, 171, 9}, {255, 153, 5}, {255, 135, 2}, {250, 117, 0}, {241, 100, 0}, {232, 83, 0}, {223, 65, 0}, {214, 48, 0}, {205, 30, 0}, {195, 13, 0}, {183, 0, 0}, {161, 0, 0}, {138, 0, 0}, {116, 0, 1}, {94, 0, 1}, {72, 0, 1}, {49, 0, 1}, {27, 0, 2}};
        generate(colors);
        return this;
    }

    public ColorMap createYlGrRd() {
//        float[][] colors = new float[][]{{255,255,166}, {249,250,164}, {243,243,161}, {237,237,159}, {231,231,157}, {224,225,154}, {218,219,152}, {212,213,150}, {202,202,147}, {191,191,144}, {180,180,142}, {169,168,139}, {159,157,136}, {148,145,134}, {137,134,131}, {131,122,123}, {130,108,109}, {129,95,96}, {129,81,82}, {128,68,68}, {127,54,55}, {127,41,41}, {123,30,30}, {112,26,26}, {101,21,21}, {89,17,17}, {78,13,13}, {67,9,9}, {55,4,4}, {44,0,0}};
        float[][] colors = new float[][]{{255,255,203}, {249,250,201}, {243,243,198}, {237,237,196}, {231,231,193}, {224,225,190}, {218,219,188}, {212,213,185}, {206,207,183}, {200,201,181}, {195,196,179}, {189,190,177}, {183,185,176}, {178,179,174}, {172,174,172}, {166,165,165}, {159,154,154}, {153,143,142}, {146,131,131}, {140,120,119}, {133,109,108}, {127,97,97}, {121,86,85}, {116,74,73}, {111,61,61}, {106,49,49}, {101,37,36}, {96,25,24}, {91,12,12}, {87,0,0}};
        generate(colors);
        return this;
    }

    private void generate(float[][] colors) {
        palette = new Color[colors.length];

        for (int i = 0; i < colors.length; i++) {
            palette[i] = new Color(colors[i][0]/255f, colors[i][1]/255f, colors[i][2]/255f, 1);
        }
    }
}
