package me.matthewedevelopment.atheriallib.utilities.location;

import me.matthewedevelopment.atheriallib.utilities.number.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.Objects;

public class AtherialXYZLocation {
    private String world;
    private int x,y,z;

    public AtherialXYZLocation(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AtherialXYZLocation){
            AtherialXYZLocation atherialLocation = (AtherialXYZLocation) obj;
            boolean worldMatch = true;
            if (atherialLocation!=null&&world!=null){
                worldMatch = atherialLocation.world.equals(world);
            }
            return worldMatch && atherialLocation.x == x && atherialLocation.y == y && atherialLocation.z == z;
        }
        return false;
    }


    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Location toLocation(){
        if (this.world==null) return null;
        return toLocation(null);
    }
    public Location toLocation(String world) {
        if (this.world==null){

            return new Location(Bukkit.getWorld(world),x,y,z);
        }

        return new Location(Bukkit.getWorld(this.world),x,y,z);
    }

    public String toFancyString() {
        StringBuilder output = new StringBuilder();
        if (this.world!=null){
            output.append(world).append(',').append(' ');
        }
        output.append(x).append(',').append(' ')
                .append(y).append(',').append(' ')
                .append(z);
        return output.toString();
    }
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (this.world!=null){
            output.append(world).append(',');
        }

        output.append(x).append(',')
                .append(y).append(',')
                .append(z);
        return output.toString();
    }

    public AtherialXYZLocation increaseY(int amt) {
        y+= amt;
        return this;

    }
    public AtherialXYZLocation increaseX(int amt) {
        x+= amt;
        return this;

    }
    public AtherialXYZLocation increaseZ(int amt) {
        z+= amt;
        return this;

    }
    public static AtherialXYZLocation fromLocation(Location location, boolean worldIncluded) {
        if (worldIncluded){
            return new AtherialXYZLocation(location.getWorld().getName(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
        } else {
            return new AtherialXYZLocation(null, location.getBlockX(),location.getBlockY(),location.getBlockZ());
        }
    }

    public static AtherialXYZLocation fromString(String input) {
        if (input==null||input.equalsIgnoreCase("null")||input.isEmpty())return null;

        String[] split = input.split(",");
        String world = null;
        if (!NumberUtils.isNumber(split[0])){
            world = split[0];
        }
        int x,y,z = 0;

        float yaw=-1,pitch =-1;
        if (world==null) {
            x =NumberUtils.getInteger(split[0]);
            y =NumberUtils.getInteger(split[1]);
            z =NumberUtils.getInteger(split[2]);

        } else {
            x =NumberUtils.getInteger(split[1]);
            y =NumberUtils.getInteger(split[2]);
            z =NumberUtils.getInteger(split[3]);

        }
        return new AtherialXYZLocation(world,x,y,z);
    }


}
