/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering.colors;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;


/** CustomColor
 * will represent color or complex colors
 * in a more efficient way than awt's Color or minecraft's color ints.
 */
public class CustomColor {
    public float
            r, // The RED   value of the color(0.0f -> 1.0f)
            g, // The GREEN value of the color(0.0f -> 1.0f)
            b, // The BLUE  value of the color(0.0f -> 1.0f)
            a; // The ALPHA value of the color(0.0f -> 1.0f)

    public CustomColor(float r, float g, float b) { this(r,g,b,1.0f); }

    public CustomColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public CustomColor(){}

    public CustomColor(CustomColor c) { this(c.r, c.g, c.b, c.a); }

    /** applyColor
     * Will set the color to OpenGL's active color
     */
    public void applyColor() {
        GlStateManager.color(r,g,b,a);
    }

    public CustomColor setA(float a) {
        this.a = a;
        return this;
    }

    public static CustomColor fromString(String string, float a) {
        if(string.length() == 6) {
            try {
                float r = ((float) Integer.parseInt(string.substring(0, 2), 16) / 255f);
                float g = ((float) Integer.parseInt(string.substring(2, 4), 16) / 255f);
                float b = ((float) Integer.parseInt(string.substring(4, 6), 16) / 255f);
                return new CustomColor(r,g,b,a);
            } catch(Exception ignored) { }
        } else if (string.length() == 3) {
            // "rgb" -> "rrggbb"
            try {
                float r = (Integer.parseInt(string.substring(0, 1), 16) * 0x11 / 255f);
                float g = (Integer.parseInt(string.substring(1, 2), 16) * 0x11 / 255f);
                float b = (Integer.parseInt(string.substring(2, 3), 16) * 0x11 / 255f);
                return new CustomColor(r,g,b,a);
            } catch(Exception ignored) { }
        }
        return fromString(DigestUtils.sha1Hex(string).substring(0,6),a);
    }

    public static CustomColor fromHSV(float h, float s, float v, float a) {
        if(v == 0f) {
            return new CustomColor(0.0f,0.0f,0.0f,a);
        } else if(s == 0f) {
            return new CustomColor(v,v,v,a);
        } else {
            h = h % 1f;

            float vh = h * 6;
            if (vh == 6)
                vh = 0;
            int vi = MathHelper.fastFloor(vh);
            float v1 = v * (1 - s);
            float v2 = v * (1 - s * (vh - vi));
            float v3 = v * (1 - s * (1 - (vh - vi)));

            switch(vi) {
                case 0: return new CustomColor(v,v3,v1,a);
                case 1: return new CustomColor(v2,v,v1,a);
                case 2: return new CustomColor(v1,v,v3,a);
                case 3: return new CustomColor(v1,v2,v,a);
                case 4: return new CustomColor(v3,v1,v,a);
                default: return new CustomColor(v,v1,v2,a);
            }
        }
    }

    /**
     * @return float[4]{ h, s, v, a }
     */
    public float[] toHSV() {
        float hue, saturation, value;
        float cmax = Math.max(Math.max(r, g), b);
        float cmin = Math.min(Math.min(r, g), b);

        value = cmax;
        saturation = cmax == 0 ? 0 : (cmax - cmin) / cmax;
        if (saturation == 0) {
            hue = 0;
        } else {
            float redc = (cmax - r) / (cmax - cmin);
            float greenc = (cmax - g) / (cmax - cmin);
            float bluec = (cmax - b) / (cmax - cmin);
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            } else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }

        return new float[]{ hue, saturation, value, a };
    }

    /**
     * `c.toInt() & 0xFFFFFF` to get `0xRRGGBB` (without alpha)
     *
     * @return 0xAARRGGBB
     */
    public int toInt() {
        int r = (int) (this.r * 255);
        int g = (int) (this.g * 255);
        int b = (int) (this.b * 255);
        int a = (int) (this.a * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** HeyZeer0: this is = rgba(1,1,1,1) **/
    @Override
    public String toString() {
        return "rgba(" + r + "," + g + "," + b + "," + a +")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other instanceof CustomColor) {
            CustomColor c = (CustomColor) other;
            return r == c.r && g == c.g && b == c.b && a == c.a;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new float[]{ r, g, b, a });
    }

    /* package-private */ static class SetBase extends CustomColor {
        SetBase(int rgb) {
            super((rgb >> 16) / 255.f, ((rgb >> 8) & 0xFF) / 255.f, (rgb & 0xFF) / 255.f, 1);
        }

        SetBase(float r, float g, float b, float a) {
            super(r, g, b, a);
        }

        // Prevent setA on global references. Create a copy with `new CustomColor(c)` first.
        @Override public CustomColor setA(float a) {
            new UnsupportedOperationException("Cannot set alpha of common color").printStackTrace();
            return this;
        }
    }

}
