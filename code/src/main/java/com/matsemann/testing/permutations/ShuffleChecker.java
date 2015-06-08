package com.matsemann.testing.permutations;

import org.moeaframework.core.PRNG;

import java.util.Arrays;

public class ShuffleChecker {

    public static void main(String[] args) {
        new ShuffleChecker();
    }

    public ShuffleChecker() {

        int ok = 0;

        for (int i = 0; i < 600000; i++) {
            int[] arr = createArr(16);
            PRNG.shuffle(arr);
            if(check(arr, 4)) {
                ok++;
                System.out.println(Arrays.toString(arr));
            }
        }
        System.out.println(ok);

    }

    private int[] createArr(int size) {
        int[] ints = new int[size];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = size - i - 1;
        }
        return ints;
    }


    public boolean check(int[] arr, int n) {
        for (int i = 0; i < arr.length; i++) {
            if(!check(arr, n, i)) {
                return false;
            }
        }
        return true;

    }

    private boolean check(int[] arr, int n, int i) {
        int var = arr[i];
        if (i - n <= var && var <= i + n) {
            return true;
        } else if (i - n < 0) {
            int upper = (i - n) + arr.length;
            return (upper <= var);
        } else if (i + n >= arr.length) {
            int lower = (i + n) - arr.length;
            return (var <= lower);
        }
        return false;
    }
}
