/*
 * Jexer - Java Text User Interface
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2025 Autumn Lamonte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * @author Autumn Lamonte ♥
 * @version 1
 */
package jexer.bits;

/**
 * A lightweight RGB color class that does not depend on java.awt.Color.
 * This allows the core jexer library to work without the java.desktop module.
 */
public class ColorRGB {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The red component of the color (0-255).
     */
    private final int red;

    /**
     * The green component of the color (0-255).
     */
    private final int green;

    /**
     * The blue component of the color (0-255).
     */
    private final int blue;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param red the red component (0-255)
     * @param green the green component (0-255)
     * @param blue the blue component (0-255)
     */
    public ColorRGB(final int red, final int green, final int blue) {
        this.red = red & 0xFF;
        this.green = green & 0xFF;
        this.blue = blue & 0xFF;
    }

    /**
     * Public constructor from RGB int value.
     *
     * @param rgb the RGB value as 0xRRGGBB
     */
    public ColorRGB(final int rgb) {
        this.red = (rgb >>> 16) & 0xFF;
        this.green = (rgb >>> 8) & 0xFF;
        this.blue = rgb & 0xFF;
    }

    // ------------------------------------------------------------------------
    // ColorRGB ---------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the red component.
     *
     * @return the red component (0-255)
     */
    public int getRed() {
        return red;
    }

    /**
     * Get the green component.
     *
     * @return the green component (0-255)
     */
    public int getGreen() {
        return green;
    }

    /**
     * Get the blue component.
     *
     * @return the blue component (0-255)
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Get the RGB value as a single int.
     *
     * @return the RGB value as 0xRRGGBB
     */
    public int getRGB() {
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    /**
     * Create a java.awt.Color from this ColorRGB.
     * This method uses reflection to avoid compile-time dependency on java.awt.
     *
     * @return a java.awt.Color instance, or null if java.awt.Color is not available
     */
    public Object toAwtColor() {
        try {
            Class<?> colorClass = Class.forName("java.awt.Color");
            return colorClass.getConstructor(int.class, int.class, int.class)
                .newInstance(red, green, blue);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create a ColorRGB from a java.awt.Color.
     *
     * @param awtColor the java.awt.Color object
     * @return a ColorRGB instance
     */
    public static ColorRGB fromAwtColor(final Object awtColor) {
        if (awtColor == null) {
            return new ColorRGB(0, 0, 0);
        }
        try {
            int r = (Integer) awtColor.getClass().getMethod("getRed").invoke(awtColor);
            int g = (Integer) awtColor.getClass().getMethod("getGreen").invoke(awtColor);
            int b = (Integer) awtColor.getClass().getMethod("getBlue").invoke(awtColor);
            return new ColorRGB(r, g, b);
        } catch (Exception e) {
            return new ColorRGB(0, 0, 0);
        }
    }

    /**
     * Check if this color is equal to another.
     *
     * @param obj the other object
     * @return true if the colors are equal
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ColorRGB)) {
            return false;
        }
        ColorRGB other = (ColorRGB) obj;
        return (red == other.red) && (green == other.green) && (blue == other.blue);
    }

    /**
     * Get the hash code for this color.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return getRGB();
    }

    /**
     * Get a string representation of this color.
     *
     * @return a string in the format "ColorRGB[r=R,g=G,b=B]"
     */
    @Override
    public String toString() {
        return String.format("ColorRGB[r=%d,g=%d,b=%d]", red, green, blue);
    }
}
