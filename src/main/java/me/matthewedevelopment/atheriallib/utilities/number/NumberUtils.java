package me.matthewedevelopment.atheriallib.utilities.number;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 12/17/2023 at 4:47 PM for the project AtherialLib
 */
public class NumberUtils {
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String formatMoney(double number) {
        if (number < 1000) {
            return String.format("%.0f", number); // No formatting for numbers less than 1000
        } else if (number < 1000000) {
            return String.format("%.1fK", number / 1000); // For thousands
        } else if (number < 1000000000) {
            return String.format("%.1fM", number / 1000000); // For millions
        } else if (number < 1000000000000L) {
            return String.format("%.1fB", number / 1000000000); // For billions
        } else if (number < 1000000000000000L) {
            return String.format("%.1fT", number / 1000000000000L); // For trillions
        } else {
            return String.format("%.1fQ", number / 1000000000000000L); // For quadrillions
        }
    }
    public static int[] range(int min, int max) {
        List<Integer> integers = new ArrayList<>();
        if (min == max) {
            return new int[]{min, max};
        }
        for (int i = min; i <= max; i++) {
            integers.add(i);
        }
        int[] ints = new int[integers.size() - 1];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = integers.get(i);
        }
        return ints;

    }


    public static int getInteger(String input) {
        return Integer.parseInt(input);
    }

    public static double getNumber(String input) {
        if (!isInteger(input) && isDouble(input)) {
            return getDouble(input);
        }
        return getInteger(input);
    }

    public static boolean isNumber(String input) {
        return isInteger(input) || isDouble(input);
    }

    public static double getDouble(String input) {
        return Double.parseDouble(input);
    }

    public static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
