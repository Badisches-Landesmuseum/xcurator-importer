package de.dreipc.xcurator.xcuratorimportservice.utils;

//https://reflectoring.io/spring-boot-elasticsearch/
public class ColorUtil {

    private ColorUtil() {
        // empty
    }

    public static boolean isHex(String hexValue) {
        return hexValue.startsWith("#") && hexValue.length() == 7;
    }

    public static int[] hex2HSL(String rgbHex) {
        var rgb = hex2RGB(rgbHex);
        var hlsFloat = RGB2HSL(rgb[0], rgb[1], rgb[2]);

        return new int[]{
                (int) Math.round(hlsFloat[0] * 360.0), // H (0 - 360 degree)
                (int) Math.round(hlsFloat[1] * 100.0), // S  (0 - 100 percent)
                (int) Math.round(hlsFloat[2] * 100)     // L  (0 - 100 percent)
        };
    }

    public static int[] hex2RGB(String colorStr) {
        if (!isHex(colorStr))
            throw new IllegalArgumentException("Given rgb hexadecimal value (" + colorStr + ") is incorrect. ");
        return new int[]{
                Integer.valueOf(colorStr.substring(1, 3), 16), // R
                Integer.valueOf(colorStr.substring(3, 5), 16), // G
                Integer.valueOf(colorStr.substring(5, 7), 16)  // B
        };
    }

    private static double[] RGB2HSL(int red, int green, int blue) {
        final double r = red / 255d;
        final double g = green / 255d;
        final double b = blue / 255d;

        final double max = Math.max(Math.max(r, g), b);
        final double min = Math.min(Math.min(r, g), b);
        double h = 0d, s = 0d, l = (max + min) / 2d;
        if (max == min) {
            h = s = 0d; //gray scale
        } else {//from  ww w.  j  a  v  a  2 s. co m
            final double d = max - min;
            s = l > 0.5 ? d / (2d - max - min) : d / (max + min);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6d : 0d);
            } else if (max == g) {
                h = (b - r) / d + 2d;
            } else if (max == b) {
                h = (r - g) / d + 4d;
            }
            h /= 6d;
        }
        return new double[]{h, s, l};
    }
}
