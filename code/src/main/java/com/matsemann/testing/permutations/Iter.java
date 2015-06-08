package com.matsemann.testing.permutations;

public class Iter {



    public static void main(String[] args) {
        new Iter();
    }


    public Iter() {
        int s = 16;
        int n = 4;

        Integer[] integers = new Integer[s];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = i;
        }

        PermUtil<Integer> permUtil = new PermUtil<Integer>(integers);

        Integer[] next = permUtil.next();
        int count = 0;
        int tenthousands = 0;
        while (next != null) {
            if (check(next, n)) {
                count++;
                if (count % 10000 == 0) {
                    tenthousands++;
                }
            }
            System.out.println(tenthousands);
            next = permUtil.next();
        }

        System.out.println("Count; " + count);
        System.out.println("Count tenthousands: " + tenthousands);
    }




    public boolean check(Integer[] arr, int n) {
        for (int i = 0; i < arr.length; i++) {
            if(!check(arr, n, i)) {
                return false;
            }
        }
        return true;

    }

    private boolean check(Integer[] arr, int n, int i) {
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
