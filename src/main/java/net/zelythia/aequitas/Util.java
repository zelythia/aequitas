package net.zelythia.aequitas;

import net.minecraft.util.math.BlockPos;

public class Util {

    public static double distanceSq(double x1, double y1, double x2, double y2) {
        x1 -= x2;
        y1 -= y2;
        return (x1 * x1 + y1 * y1);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        x1 -= x2;
        y1 -= y2;
        return java.lang.Math.sqrt(x1 * x1 + y1 * y1);
    }

    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        x1 -= x2;
        y1 -= y2;
        z1 -= z2;
        return (x1 * x1 + y1 * y1 + z1 * z1);
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        x1 -= x2;
        y1 -= y2;
        z1 -= z2;
        return java.lang.Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
    }

    public static double distanceSq(BlockPos pos1, BlockPos pos2) {
        double x1 = pos1.getX() + 0.5;
        double y1 = pos1.getY() + 0.5;
        double z1 = pos1.getZ() + 0.5;

        x1 -= pos2.getX() + 0.5;
        y1 -= pos2.getY() + 0.5;
        z1 -= pos2.getZ() + 0.5;

        return (x1 * x1 + y1 * y1 + z1 * z1);
    }
}
