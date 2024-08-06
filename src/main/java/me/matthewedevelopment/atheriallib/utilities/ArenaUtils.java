package me.matthewedevelopment.atheriallib.utilities;

import me.matthewedevelopment.atheriallib.utilities.location.AtherialXYZLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

public class ArenaUtils {
    public static boolean isCube(AtherialXYZLocation pos1, AtherialXYZLocation pos2) {
        // Get the x and z coordinates of pos1 and pos2
        double x1 = pos1.getX();
        double z1 = pos1.getZ();
        double x2 = pos2.getX();
        double z2 = pos2.getZ();

        // Calculate the absolute differences
        double xDifference = Math.abs(x2 - x1);
        double zDifference = Math.abs(z2 - z1);

        // Check if the region forms a square in the x-z plane
        return xDifference == zDifference;
    }

    private static Random random = new Random();

    public static void randomTeleport(Player player, AtherialXYZLocation pos1, AtherialXYZLocation pos2) {
        // Determine the min and max coordinates
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        // Generate random coordinates within the bounds
        double x = minX + (maxX - minX) * random.nextDouble();
        double y = minY + (maxY - minY) * random.nextDouble();
        double z = minZ + (maxZ - minZ) * random.nextDouble();

        // Ensure the random position is in a safe location (e.g., above ground)
        World world = player.getWorld();
        Location randomLocation = new Location(world, x, y, z);
        randomLocation.setY(world.getHighestBlockYAt(randomLocation) + 1); // Set Y to the top block's height + 1

        // Teleport the player
        player.teleport(randomLocation);
    }

    public static AtherialXYZLocation getCenter(AtherialXYZLocation pos1, AtherialXYZLocation pos2) {
        // Calculate the center point by averaging the coordinates
        int centerX = (pos1.getX() + pos2.getX()) / 2;
        int centerY = (pos1.getY() + pos2.getY()) / 2;
        int centerZ = (pos1.getZ() + pos2.getZ()) / 2;

        // Return a new AtherialXYZLocation object with the center coordinates
        return new AtherialXYZLocation(null, centerX, centerY, centerZ);
    }

}
