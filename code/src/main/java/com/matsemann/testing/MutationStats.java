package com.matsemann.testing;

import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

public class MutationStats {

    public MutationStats() {

        int[] res = new int[128];


//        bitflip(res, 64);

        pm(res, 64, 15);


        for (int i = 0; i < res.length; i++) {
            String s = "";
            for (int j = 0; j < res[i] / 1000.0; j++) {
                s += "+";
            }
            System.out.println(i + "\t\t" + s);
        }

        System.out.println("\n");

        for (int i = 0; i < res.length; i++) {
            int re = res[i];
            System.out.println(i + "\t\t" + re);
        }
    }

    private void pm(int[] res, int val, int rate) {
        for (int i = 0; i < 1000000; i++) {
            RealVariable rv = new RealVariable(val, 0, 128);
            PM.evolve(rv, rate);
            res[((int) rv.getValue())]++;
        }
    }

    private void bitflip(int[] res, int val) {
        for (int i = 0; i < 1000000; i++) {
            BinaryVariable variable = EncodingUtils.newBinary(7);
            EncodingUtils.encode(val, variable);
            BitFlip.evolve(variable, 1.0 / 7.0);
            int result = (int) EncodingUtils.decode(variable);
            res[result]++;
        }
    }

    public static void main(String[] args) {
        new MutationStats();
    }
}
