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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;
import jexer.bits.StringUtils;

/**
 * FontHelper provides font-related operations for the java-desktop JAR.
 * This includes font enumeration and text rendering.
 */
public class FontHelper {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Private constructor prevents accidental creation of this class.
     */
    private FontHelper() {}

    // ------------------------------------------------------------------------
    // FontHelper -------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the list of available font family names.
     *
     * @return array of font family names
     */
    public static String[] getAvailableFontFamilyNames() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().
            getAvailableFontFamilyNames();
    }

    /**
     * Create a new Font.
     *
     * @param fontName the font name
     * @param style the style (Font.PLAIN, Font.BOLD, etc.)
     * @param size the font size in points
     * @return the Font object
     */
    public static Object createFont(final String fontName, final int style,
        final int size) {

        return new Font(fontName, style, size);
    }

    /**
     * Render text to an image.
     *
     * @param text the text to render
     * @param font the font object (java.awt.Font)
     * @param fontSize the font size
     * @param color the text color
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     * @return the rendered image as ImageRGB
     */
    public static ImageRGB renderText(final String text, final Object font,
        final int fontSize, final ColorRGB color, final int textWidth,
        final int textHeight) {

        Font awtFont = (Font) font;

        // Estimate the pixels needed to render the text.
        int width = 0;
        int height = 0;
        String [] rawLines = text.split("\n");
        for (int i = 0; i < rawLines.length; i++) {
            int lineWidth = StringUtils.width(rawLines[i]) * textWidth;
            width = Math.max(width, lineWidth);
        }
        width *= (fontSize / 2);
        height = (rawLines.length + 1) * (int) (fontSize * 1.5);

        if (width <= 0 || height <= 0) {
            return null;
        }

        BufferedImage newImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = newImage.createGraphics();
        gr.setFont(awtFont);
        gr.setColor(new java.awt.Color(color.getRed(), color.getGreen(),
            color.getBlue(), color.getAlpha()));

        // Because this is text, let's enable anti-aliasing.
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = gr.getFontMetrics();
        int maxDescent = fm.getMaxDescent();
        int maxAscent = fm.getMaxAscent();

        for (int i = 0; i < rawLines.length; i++) {
            gr.drawString(rawLines[i], 0,
                (fontSize * (i + 1)) + maxAscent - maxDescent);
        }
        gr.dispose();

        // Convert to ImageRGB
        int[] pixels = newImage.getRGB(0, 0, width, height, null, 0, width);
        return new ImageRGB(width, height, pixels);
    }

}
