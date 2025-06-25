package me.matthewedevelopment.atheriallib.utilities.number;

import java.util.Random;

public class RandomUtils {

    public static boolean checkOdds(double percentage) {
        double random = Math.random();
        return random <= percentage / 100.0;
    }

}
