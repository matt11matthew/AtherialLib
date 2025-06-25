package me.matthewedevelopment.atheriallib.utilities.number;

import java.util.Random;

public class IntegerRange  {
    private int min, max;
    private static Random RANDOM;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        if (min==max)return String.valueOf(min);
        return min+"-"+max;
    }

    public int getRandom() {
        if (RANDOM == null) {
            RANDOM = new Random();
        }
        if (min == max) return min; // If min and max are the same, just return one of them.
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static IntegerRange parse(String input) {
         try {
             if (input.contains("-")) {
                 String[] split = input.split("-");
                 return new IntegerRange(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
             }
             int min = Integer.parseInt(input);
             return new IntegerRange(min, min);
         } catch (Exception e) {
             return null;
         }
    }
}
