package me.matthewedevelopment.atheriallib.utilities.location;

import me.matthewedevelopment.atheriallib.utilities.number.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class AtherialLocation {
    private String world;
    private double x,y,z;
    private float yaw, pitch;

    public AtherialLocation(String world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public AtherialLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public AtherialLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Location toLocation(){
        if (this.world==null) return null;
        return toLocation(null);
    }
    public Location toLocation(String world) {
        if (this.world==null){
            if (yaw==-1){
                return new Location(Bukkit.getWorld(world),x,y,z);
            }
            return new Location(Bukkit.getWorld(world),x,y,z,pitch,yaw);
        }
        if (yaw==-1){
            return new Location(Bukkit.getWorld(this.world),x,y,z);
        }
        return new Location(Bukkit.getWorld(this.world),x,y,z,pitch,yaw);
    }

    public String toFancyString() {
        StringBuilder output = new StringBuilder();
        if (this.world!=null){
            output.append(world).append(',').append(' ');
        }
        output.append((int)x).append(',').append(' ')
                .append((int)y).append(',').append(' ')
                .append((int)z);
        return output.toString();
    }
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (this.world!=null){
            output.append(world).append(',');
        }

        if (yaw!=-1){
            DecimalFormat decimalFormat = new DecimalFormat("###.####");
            output.append(decimalFormat.format(x)).append(',')
                    .append(decimalFormat.format(y)).append(',')
                    .append(decimalFormat.format(z)).append(',').append(pitch).append(',').append(yaw);
        } else {
            output.append((int)x).append(',')
                    .append((int)y).append(',')
                    .append((int)z);
        }
        return output.toString();
    }

    public static AtherialLocation fromLocation(Location location, boolean block,  boolean includeWorld) {
        if (block){
            if (includeWorld){
                return new AtherialLocation(location.getWorld().getName(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
            } else {
                return new AtherialLocation(location.getBlockX(),location.getBlockY(),location.getBlockZ());
            }
        } else {
            if (includeWorld){
                return new AtherialLocation(location.getWorld().getName(),location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
            } else {
                return new AtherialLocation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
            }
        }
    }
    public static AtherialLocation fromPlayerLocation(Location location, boolean world) {
        return fromLocation(location,false, world);
    }
    public static AtherialLocation fromLocation(Location location, boolean block) {
        return fromLocation(location,block, true);
    }
    public static AtherialLocation fromString(String input) {
        if (input==null||input.equalsIgnoreCase("null")||input.isEmpty())return null;

        String[] split = input.split(",");
        String world = null;
        if (!NumberUtils.isNumber(split[0])){
            world = split[0];
        }
        double x,y,z = 0;

        float yaw=-1,pitch =-1;
        if (world==null) {
            x =NumberUtils.getNumber(split[0]);
            y =NumberUtils.getNumber(split[1]);
            z =NumberUtils.getNumber(split[2]);
            if (split.length>3){
                yaw=Float.parseFloat(split[4]);
                pitch=Float.parseFloat(split[3]);
            }
        } else {
            x =NumberUtils.getNumber(split[1]);
            y =NumberUtils.getNumber(split[2]);
            z =NumberUtils.getNumber(split[3]);
            if (split.length>4){
                yaw=Float.parseFloat(split[5]);
                pitch=Float.parseFloat(split[4]);
            }
        }
        return new AtherialLocation(world,x,y,z,pitch,yaw);
    }

    public AtherialLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
