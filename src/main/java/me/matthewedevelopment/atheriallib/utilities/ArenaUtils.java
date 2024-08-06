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

    public static boolean inBounds(AtherialXYZLocation pos1, AtherialXYZLocation pos2, AtherialXYZLocation location) {
        // Determine the min and max coordinates for each axis
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        // Check if the location is within the bounds
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
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
        double y = maxY; // Start with the highest Y
        double z = minZ + (maxZ - minZ) * random.nextDouble();

        // Ensure the random position is in a safe location (find the lowest block)
        World world = player.getWorld();
        Location randomLocation = new Location(world, x, y, z);
        int highestY = world.getHighestBlockYAt(randomLocation);
        for (int currentY = highestY; currentY > world.getMinHeight(); currentY--) {
            Location checkLocation = new Location(world, x, currentY, z);
            if (!world.getBlockAt(checkLocation).isEmpty()) {
                // Set Y to one block above the lowest non-air block
                randomLocation.setY(currentY + 1);
                break;
            }
        }

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
