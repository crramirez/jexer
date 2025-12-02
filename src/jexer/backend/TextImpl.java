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
 * @author Autumn Lamonte
 * @version 1
 */
package jexer.backend;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;
import jexer.bits.StringUtils;

/**
 * TextImpl provides the AWT-based implementation for Text (tackboard text)
 * operations. This class is in jexer.backend and will be included in the
 * java-desktop JAR.
 */
public final class TextImpl {

    /**
     * Private constructor to prevent instantiation.
     */
    private TextImpl() {
    }

    /**
     * Convert a ColorRGB to java.awt.Color.
     *
     * @param colorRGB the ColorRGB to convert
     * @return the java.awt.Color
     */
    private static java.awt.Color toAwtColor(final ColorRGB colorRGB) {
        return new java.awt.Color(colorRGB.getRed(), colorRGB.getGreen(),
            colorRGB.getBlue(), colorRGB.getAlpha());
    }

    /**
     * Convert BufferedImage to ImageRGB.
     *
     * @param bufferedImage the BufferedImage to convert
     * @return the ImageRGB
     */
    private static ImageRGB toImageRGB(final BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
        return new ImageRGB(width, height, pixels);
    }

    /**
     * Render text to an image.
     *
     * @param text the text to render
     * @param fontName the font name
     * @param fontSize the font size in points
     * @param color the color
     * @param textWidth the width of a text cell (used for estimating width)
     * @return the rendered image
     */
    public static ImageRGB renderText(final String text, final String fontName,
        final int fontSize, final ColorRGB color, final int textWidth) {

        Font font = new Font(fontName, Font.PLAIN, fontSize);
        return renderTextWithFont(text, font, fontSize, color, textWidth);
    }

    /**
     * Render text to an image with a Font object.
     *
     * @param text the text to render
     * @param font the font
     * @param fontSize the font size in points
     * @param color the color
     * @param textWidth the width of a text cell (used for estimating width)
     * @return the rendered image
     */
    public static ImageRGB renderTextWithFont(final String text, final Font font,
        final int fontSize, final ColorRGB color, final int textWidth) {

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

        BufferedImage newImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = newImage.createGraphics();
        gr.setFont(font);
        gr.setColor(toAwtColor(color));

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

        return toImageRGB(newImage);
    }

}
