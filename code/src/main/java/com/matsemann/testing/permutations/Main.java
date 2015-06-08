package com.matsemann.testing.permutations;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    List<int[]> perms = new ArrayList<>();
    int n = 4;

    public Main() {
        permutation(10);
        System.out.println(perms.size());

//        List<int[]> correct = perms.stream().filter(p -> check(p, 1)).collect(Collectors.toList());
//        List<int[]> correct2 = perms.stream().filter(p -> check(p, 2)).collect(Collectors.toList());
//        List<int[]> correct3 = perms.stream().filter(p -> check(p, 3)).collect(Collectors.toList());
//        List<int[]> correct4 = perms.stream().filter(p -> check(p, 4)).collect(Collectors.toList());
//        List<int[]> correct5 = perms.stream().filter(p -> check(p, 5)).collect(Collectors.toList());

//        System.out.println("Correct1: " + correct.size());
//        System.out.println("Correct2: " + correct2.size());
//        System.out.println("Correct3: " + correct3.size());
//        System.out.println("Correct4: " + correct4.size());
//        System.out.println("Correct5: " + correct5.size());
//        for (int[] ints : correct) {
//            for (int anInt : ints) {
//                System.out.print(anInt);
//            }
//            System.out.println();
//        }
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

    public void permutation(int length) {
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        permutation(arr, 0);
    }


    private void permutation(int[] arr, int pos) {
        if (pos == arr.length) {
            if (check(arr, n)) {
                perms.add(arr);
            }
            return;
        }

        int tmp = arr[pos];
        for (int i = pos; i < arr.length; i++) {
            int[] copy = arr.clone();
            copy[pos] = copy[i];
            copy[i] = tmp;
            permutation(copy, pos+1);
        }


    }
}
