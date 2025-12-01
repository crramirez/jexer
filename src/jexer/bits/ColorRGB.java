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
 * ColorRGB is a lightweight RGB color class that replaces java.awt.Color
 * for use in terminal applications that don't require java.desktop.
 */
public final class ColorRGB {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The color black (0, 0, 0).
     */
    public static final ColorRGB BLACK = new ColorRGB(0, 0, 0);

    /**
     * The color white (255, 255, 255).
     */
    public static final ColorRGB WHITE = new ColorRGB(255, 255, 255);

    /**
     * The color red (255, 0, 0).
     */
    public static final ColorRGB RED = new ColorRGB(255, 0, 0);

    /**
     * The color green (0, 255, 0).
     */
    public static final ColorRGB GREEN = new ColorRGB(0, 255, 0);

    /**
     * The color blue (0, 0, 255).
     */
    public static final ColorRGB BLUE = new ColorRGB(0, 0, 255);

    /**
     * The color yellow (255, 255, 0).
     */
    public static final ColorRGB YELLOW = new ColorRGB(255, 255, 0);

    /**
     * The color cyan (0, 255, 255).
     */
    public static final ColorRGB CYAN = new ColorRGB(0, 255, 255);

    /**
     * The color magenta (255, 0, 255).
     */
    public static final ColorRGB MAGENTA = new ColorRGB(255, 0, 255);

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The red component, 0-255.
     */
    private final int red;

    /**
     * The green component, 0-255.
     */
    private final int green;

    /**
     * The blue component, 0-255.
     */
    private final int blue;

    /**
     * The alpha component, 0-255.
     */
    private final int alpha;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param red the red component, 0-255
     * @param green the green component, 0-255
     * @param blue the blue component, 0-255
     */
    public ColorRGB(final int red, final int green, final int blue) {
        this(red, green, blue, 255);
    }

    /**
     * Public constructor.
     *
     * @param red the red component, 0-255
     * @param green the green component, 0-255
     * @param blue the blue component, 0-255
     * @param alpha the alpha component, 0-255
     */
    public ColorRGB(final int red, final int green, final int blue,
        final int alpha) {

        this.red = red & 0xFF;
        this.green = green & 0xFF;
        this.blue = blue & 0xFF;
        this.alpha = alpha & 0xFF;
    }

    /**
     * Public constructor from packed RGB integer.
     *
     * @param rgb the packed RGB value (0x00RRGGBB)
     */
    public ColorRGB(final int rgb) {
        this((rgb >>> 16) & 0xFF, (rgb >>> 8) & 0xFF, rgb & 0xFF, 255);
    }

    /**
     * Public constructor from packed ARGB integer.
     *
     * @param argb the packed ARGB value (0xAARRGGBB)
     * @param hasAlpha if true, the argb value includes an alpha component
     */
    public ColorRGB(final int argb, final boolean hasAlpha) {
        if (hasAlpha) {
            this.alpha = (argb >>> 24) & 0xFF;
            this.red = (argb >>> 16) & 0xFF;
            this.green = (argb >>> 8) & 0xFF;
            this.blue = argb & 0xFF;
        } else {
            this.alpha = 255;
            this.red = (argb >>> 16) & 0xFF;
            this.green = (argb >>> 8) & 0xFF;
            this.blue = argb & 0xFF;
        }
    }

    // ------------------------------------------------------------------------
    // ColorRGB ---------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the red component.
     *
     * @return the red component, 0-255
     */
    public int getRed() {
        return red;
    }

    /**
     * Get the green component.
     *
     * @return the green component, 0-255
     */
    public int getGreen() {
        return green;
    }

    /**
     * Get the blue component.
     *
     * @return the blue component, 0-255
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Get the alpha component.
     *
     * @return the alpha component, 0-255
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Get the color as a packed RGB integer (0x00RRGGBB).
     *
     * @return the packed RGB value
     */
    public int getRGB() {
        return ((alpha & 0xFF) << 24)
            | ((red & 0xFF) << 16)
            | ((green & 0xFF) << 8)
            | (blue & 0xFF);
    }

    /**
     * Create a new ColorRGB with a different alpha value.
     *
     * @param newAlpha the new alpha component, 0-255
     * @return a new ColorRGB with the specified alpha
     */
    public ColorRGB withAlpha(final int newAlpha) {
        return new ColorRGB(red, green, blue, newAlpha);
    }

    /**
     * Create a brighter version of this color.
     *
     * @return a brighter ColorRGB
     */
    public ColorRGB brighter() {
        int r = red;
        int g = green;
        int b = blue;

        int i = (int) (1.0 / (1.0 - 0.7));
        if (r == 0 && g == 0 && b == 0) {
            return new ColorRGB(i, i, i, alpha);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new ColorRGB(Math.min((int) (r / 0.7), 255),
            Math.min((int) (g / 0.7), 255),
            Math.min((int) (b / 0.7), 255),
            alpha);
    }

    /**
     * Create a darker version of this color.
     *
     * @return a darker ColorRGB
     */
    public ColorRGB darker() {
        return new ColorRGB(Math.max((int) (red * 0.7), 0),
            Math.max((int) (green * 0.7), 0),
            Math.max((int) (blue * 0.7), 0),
            alpha);
    }

    /**
     * Comparison check. All fields must match to return true.
     *
     * @param rhs another ColorRGB instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof ColorRGB)) {
            return false;
        }
        ColorRGB that = (ColorRGB) rhs;
        return (red == that.red)
            && (green == that.green)
            && (blue == that.blue)
            && (alpha == that.alpha);
    }

    /**
     * Hashcode uses all fields in equals().
     *
     * @return the hash
     */
    @Override
    public int hashCode() {
        return getRGB();
    }

    /**
     * Make human-readable description of this ColorRGB.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        return "ColorRGB[r=" + red + ",g=" + green + ",b=" + blue
            + ",a=" + alpha + "]";
    }

    /**
     * Decode a string into a ColorRGB. The string can be:
     * - A decimal number (e.g., "16777215")
     * - A hex number starting with # (e.g., "#FFFFFF")
     * - A hex number starting with 0x (e.g., "0xFFFFFF")
     *
     * @param nm the string to decode
     * @return the ColorRGB
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static ColorRGB decode(final String nm)
        throws NumberFormatException {

        String str = nm.trim();
        int radix = 10;
        int index = 0;

        if (str.startsWith("#")) {
            index = 1;
            radix = 16;
        } else if (str.startsWith("0x") || str.startsWith("0X")) {
            index = 2;
            radix = 16;
        }

        int rgb = Integer.parseInt(str.substring(index), radix);
        return new ColorRGB(rgb);
    }
}
