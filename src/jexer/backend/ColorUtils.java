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
package jexer.backend;

import java.awt.Color;

import jexer.bits.CellAttributes;

/**
 * Utility class for converting Cell attributes to AWT colors.
 */
public class ColorUtils {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    private static Color MYBLACK;
    private static Color MYRED;
    private static Color MYGREEN;
    private static Color MYYELLOW;
    private static Color MYBLUE;
    private static Color MYMAGENTA;
    private static Color MYCYAN;
    private static Color MYWHITE;
    private static Color MYBOLD_BLACK;
    private static Color MYBOLD_RED;
    private static Color MYBOLD_GREEN;
    private static Color MYBOLD_YELLOW;
    private static Color MYBOLD_BLUE;
    private static Color MYBOLD_MAGENTA;
    private static Color MYBOLD_CYAN;
    private static Color MYBOLD_WHITE;

    /**
     * When true, all the MYBLACK, MYRED, etc. colors are set.
     */
    private static boolean colorsInitialized = false;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Private constructor to prevent instantiation.
     */
    private ColorUtils() {
    }

    // ------------------------------------------------------------------------
    // ColorUtils -------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Setup colors to match DOS color palette.
     */
    private static void initializeColors() {
        if (colorsInitialized) {
            return;
        }
        synchronized (ColorUtils.class) {
            if (colorsInitialized) {
                return;
            }
            MYBLACK         = new Color(0x00, 0x00, 0x00);
            MYRED           = new Color(0xa8, 0x00, 0x00);
            MYGREEN         = new Color(0x00, 0xa8, 0x00);
            MYYELLOW        = new Color(0xa8, 0x54, 0x00);
            MYBLUE          = new Color(0x00, 0x00, 0xa8);
            MYMAGENTA       = new Color(0xa8, 0x00, 0xa8);
            MYCYAN          = new Color(0x00, 0xa8, 0xa8);
            MYWHITE         = new Color(0xa8, 0xa8, 0xa8);
            MYBOLD_BLACK    = new Color(0x54, 0x54, 0x54);
            MYBOLD_RED      = new Color(0xfc, 0x54, 0x54);
            MYBOLD_GREEN    = new Color(0x54, 0xfc, 0x54);
            MYBOLD_YELLOW   = new Color(0xfc, 0xfc, 0x54);
            MYBOLD_BLUE     = new Color(0x54, 0x54, 0xfc);
            MYBOLD_MAGENTA  = new Color(0xfc, 0x54, 0xfc);
            MYBOLD_CYAN     = new Color(0x54, 0xfc, 0xfc);
            MYBOLD_WHITE    = new Color(0xfc, 0xfc, 0xfc);

            // Apply custom colors from system properties if present
            setCustomSystemColors();

            colorsInitialized = true;
        }
    }

    /**
     * Setup colors to match those provided in system properties.
     */
    private static void setCustomSystemColors() {
        MYBLACK   = getCustomColor("jexer.Swing.color0", MYBLACK);
        MYRED     = getCustomColor("jexer.Swing.color1", MYRED);
        MYGREEN   = getCustomColor("jexer.Swing.color2", MYGREEN);
        MYYELLOW  = getCustomColor("jexer.Swing.color3", MYYELLOW);
        MYBLUE    = getCustomColor("jexer.Swing.color4", MYBLUE);
        MYMAGENTA = getCustomColor("jexer.Swing.color5", MYMAGENTA);
        MYCYAN    = getCustomColor("jexer.Swing.color6", MYCYAN);
        MYWHITE   = getCustomColor("jexer.Swing.color7", MYWHITE);
        MYBOLD_BLACK   = getCustomColor("jexer.Swing.color8", MYBOLD_BLACK);
        MYBOLD_RED     = getCustomColor("jexer.Swing.color9", MYBOLD_RED);
        MYBOLD_GREEN   = getCustomColor("jexer.Swing.color10", MYBOLD_GREEN);
        MYBOLD_YELLOW  = getCustomColor("jexer.Swing.color11", MYBOLD_YELLOW);
        MYBOLD_BLUE    = getCustomColor("jexer.Swing.color12", MYBOLD_BLUE);
        MYBOLD_MAGENTA = getCustomColor("jexer.Swing.color13", MYBOLD_MAGENTA);
        MYBOLD_CYAN    = getCustomColor("jexer.Swing.color14", MYBOLD_CYAN);
        MYBOLD_WHITE   = getCustomColor("jexer.Swing.color15", MYBOLD_WHITE);
    }

    /**
     * Setup one color to match the RGB value provided in system properties.
     *
     * @param key the system property key
     * @param defaultColor the default color to return if key is not set, or
     * incorrect
     * @return a color from the RGB string, or defaultColor
     */
    private static Color getCustomColor(final String key,
        final Color defaultColor) {

        String rgb = System.getProperty(key);
        if (rgb == null) {
            return defaultColor;
        }
        if (rgb.startsWith("#")) {
            rgb = rgb.substring(1);
        }
        int rgbInt = 0;
        try {
            rgbInt = Integer.parseInt(rgb, 16);
        } catch (NumberFormatException e) {
            return defaultColor;
        }
        Color color = new Color((rgbInt & 0xFF0000) >>> 16,
            (rgbInt & 0x00FF00) >>> 8,
            (rgbInt & 0x0000FF));

        return color;
    }

    /**
     * Convert a CellAttributes foreground color to an AWT Color.
     *
     * @param attr the text attributes
     * @return the AWT Color
     */
    public static Color attrToForegroundColor(final CellAttributes attr) {
        if (!colorsInitialized) {
            initializeColors();
        }

        int rgb = attr.getForeColorRGB();
        if (rgb >= 0) {
            int red     = (rgb >>> 16) & 0xFF;
            int green   = (rgb >>>  8) & 0xFF;
            int blue    =  rgb         & 0xFF;

            return new Color(red, green, blue);
        }

        if (attr.isBold()) {
            if (attr.getForeColor().equals(jexer.bits.Color.BLACK)) {
                return MYBOLD_BLACK;
            } else if (attr.getForeColor().equals(jexer.bits.Color.RED)) {
                return MYBOLD_RED;
            } else if (attr.getForeColor().equals(jexer.bits.Color.BLUE)) {
                return MYBOLD_BLUE;
            } else if (attr.getForeColor().equals(jexer.bits.Color.GREEN)) {
                return MYBOLD_GREEN;
            } else if (attr.getForeColor().equals(jexer.bits.Color.YELLOW)) {
                return MYBOLD_YELLOW;
            } else if (attr.getForeColor().equals(jexer.bits.Color.CYAN)) {
                return MYBOLD_CYAN;
            } else if (attr.getForeColor().equals(jexer.bits.Color.MAGENTA)) {
                return MYBOLD_MAGENTA;
            } else if (attr.getForeColor().equals(jexer.bits.Color.WHITE)) {
                return MYBOLD_WHITE;
            }
        } else {
            if (attr.getForeColor().equals(jexer.bits.Color.BLACK)) {
                return MYBLACK;
            } else if (attr.getForeColor().equals(jexer.bits.Color.RED)) {
                return MYRED;
            } else if (attr.getForeColor().equals(jexer.bits.Color.BLUE)) {
                return MYBLUE;
            } else if (attr.getForeColor().equals(jexer.bits.Color.GREEN)) {
                return MYGREEN;
            } else if (attr.getForeColor().equals(jexer.bits.Color.YELLOW)) {
                return MYYELLOW;
            } else if (attr.getForeColor().equals(jexer.bits.Color.CYAN)) {
                return MYCYAN;
            } else if (attr.getForeColor().equals(jexer.bits.Color.MAGENTA)) {
                return MYMAGENTA;
            } else if (attr.getForeColor().equals(jexer.bits.Color.WHITE)) {
                return MYWHITE;
            }
        }
        throw new IllegalArgumentException("Invalid color: " +
            attr.getForeColor().getValue());
    }

    /**
     * Convert a CellAttributes background color to an AWT Color.
     *
     * @param attr the text attributes
     * @return the AWT Color
     */
    public static Color attrToBackgroundColor(final CellAttributes attr) {
        if (!colorsInitialized) {
            initializeColors();
        }

        int rgb = attr.getBackColorRGB();
        if (rgb >= 0) {
            int red     = (rgb >>> 16) & 0xFF;
            int green   = (rgb >>>  8) & 0xFF;
            int blue    =  rgb         & 0xFF;

            return new Color(red, green, blue);
        }

        if (attr.getBackColor().equals(jexer.bits.Color.BLACK)) {
            return MYBLACK;
        } else if (attr.getBackColor().equals(jexer.bits.Color.RED)) {
            return MYRED;
        } else if (attr.getBackColor().equals(jexer.bits.Color.BLUE)) {
            return MYBLUE;
        } else if (attr.getBackColor().equals(jexer.bits.Color.GREEN)) {
            return MYGREEN;
        } else if (attr.getBackColor().equals(jexer.bits.Color.YELLOW)) {
            return MYYELLOW;
        } else if (attr.getBackColor().equals(jexer.bits.Color.CYAN)) {
            return MYCYAN;
        } else if (attr.getBackColor().equals(jexer.bits.Color.MAGENTA)) {
            return MYMAGENTA;
        } else if (attr.getBackColor().equals(jexer.bits.Color.WHITE)) {
            return MYWHITE;
        }
        throw new IllegalArgumentException("Invalid color: " +
            attr.getBackColor().getValue());
    }
}
