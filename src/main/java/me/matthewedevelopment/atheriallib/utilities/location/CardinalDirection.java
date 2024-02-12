package me.matthewedevelopment.atheriallib.utilities.location;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public enum CardinalDirection {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH_WEST, WEST, NORTH_WEST, SOUTH;
    public  static BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }



    public static CardinalDirection get(float yaw) {
        double rotation = (yaw - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 22.5D)) {
            return NORTH;
        }
        if ((22.5D <= rotation) && (rotation < 67.5D)) {
            return NORTH_EAST;
        }
        if ((67.5D <= rotation) && (rotation < 112.5D)) {
            return EAST;
        }
        if ((112.5D <= rotation) && (rotation < 157.5D)) {
            return SOUTH_EAST;
        }
        if ((157.5D <= rotation) && (rotation < 202.5D)) {
            return SOUTH;
        }
        if ((202.5D <= rotation) && (rotation < 247.5D)) {
            return SOUTH_WEST;
        }
        if ((247.5D <= rotation) && (rotation < 292.5D)) {
            return WEST;
        }
        if ((292.5D <= rotation) && (rotation < 337.5D)) {
            return NORTH_WEST;
        }
        if ((337.5D <= rotation) && (rotation < 360.0D)) {
            return NORTH;
        }
        return null;
    }
}
