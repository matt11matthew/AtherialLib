package me.matthewedevelopment.atheriallib.utilities.location;

import me.matthewedevelopment.atheriallib.utilities.number.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.Objects;

public class PlayerAtherialLocation {
    private double x,y,z;
    private float yaw, pitch;

    public PlayerAtherialLocation(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerAtherialLocation){
            PlayerAtherialLocation atherialLocation = (PlayerAtherialLocation) obj;
            boolean worldMatch = true;

            if (worldMatch&&(int)atherialLocation.x==(int)x&&(int)atherialLocation.y==(int)y&&(int)atherialLocation.z==(int)z){
                return true;
            }
        }
        return false;
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


    public Location toLocation(String world) {

        return new Location(Bukkit.getWorld(world),x,y,z,pitch,yaw);
    }

    public String toFancyString() {
        StringBuilder output = new StringBuilder();

        output.append((int)x).append(',').append(' ')
                .append((int)y).append(',').append(' ')
                .append((int)z);
        return output.toString();
    }
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();


        DecimalFormat decimalFormat = new DecimalFormat("###.####");
        output.append(decimalFormat.format(x)).append(',')
                .append(decimalFormat.format(y)).append(',')
                .append(decimalFormat.format(z)).append(',').append(pitch).append(',').append(yaw);
        return output.toString();
    }

    public PlayerAtherialLocation increaseY(double amt) {
        y+= amt;
        return this;

    }
    public PlayerAtherialLocation increaseX(double amt) {
        x+= amt;
        return this;

    }
    public PlayerAtherialLocation increaseZ(double amt) {
        z+= amt;
        return this;

    }
    public static PlayerAtherialLocation fromLocation(Location location) {

        return new PlayerAtherialLocation(location.getX(),location.getY(),location.getZ(),location.getPitch(),location.getYaw());
    }

    public static PlayerAtherialLocation fromString(String input) {
        if (input==null||input.equalsIgnoreCase("null")||input.isEmpty())return null;

        String[] split = input.split(",");

        double x =NumberUtils.getNumber(split[0]);
        double y =NumberUtils.getNumber(split[1]);
        double  z =NumberUtils.getNumber(split[2]);
        float pitch=Float.parseFloat(split[3]);
        float yaw=Float.parseFloat(split[4]);
        return new PlayerAtherialLocation(x,y,z,pitch,yaw);
    }



}
