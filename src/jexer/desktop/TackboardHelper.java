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
package jexer.desktop;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;
import jexer.bits.StringUtils;

/**
 * TackboardHelper provides AWT-based operations for the tackboard package.
 * This class is in the java-desktop JAR.
 */
public class TackboardHelper {

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
    private TackboardHelper() {}

    // ------------------------------------------------------------------------
    // TackboardHelper --------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Convert an ImageRGB to a BufferedImage.
     *
     * @param imageRGB the ImageRGB to convert
     * @return the BufferedImage
     */
    public static BufferedImage toBufferedImage(final ImageRGB imageRGB) {
        if (imageRGB == null) {
            return null;
        }
        int width = imageRGB.getWidth();
        int height = imageRGB.getHeight();
        BufferedImage result = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, width, height, imageRGB.getPixels(), 0, width);
        return result;
    }

    /**
     * Convert a BufferedImage to an ImageRGB.
     *
     * @param bufferedImage the BufferedImage to convert
     * @return the ImageRGB
     */
    public static ImageRGB toImageRGB(final BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
        return new ImageRGB(width, height, pixels);
    }

    /**
     * Create a new BufferedImage.
     *
     * @param width the width
     * @param height the height
     * @return a new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Render a bitmap image offset to align on a grid of cells.
     *
     * @param image the source image as ImageRGB
     * @param x the x position
     * @param y the y position
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     * @return the rendered image as ImageRGB
     */
    public static ImageRGB renderBitmap(final ImageRGB image,
        final int x, final int y, final int textWidth, final int textHeight) {

        if (image == null) {
            return null;
        }
        BufferedImage srcImage = toBufferedImage(image);
        
        int dx = x % textWidth;
        int dy = y % textHeight;
        if ((dx == 0) && (dy == 0)) {
            return image;
        }

        int columns = (dx + srcImage.getWidth()) / textWidth;
        if ((dx + srcImage.getWidth()) % textWidth > 0) {
            columns++;
        }
        int rows = (dy + srcImage.getHeight()) / textHeight;
        if ((dy + srcImage.getHeight()) % textHeight > 0) {
            rows++;
        }

        BufferedImage renderedImage = new BufferedImage(columns * textWidth,
            rows * textHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics gr = renderedImage.getGraphics();
        gr.drawImage(srcImage, dx, dy, null, null);
        gr.dispose();

        return toImageRGB(renderedImage);
    }

    /**
     * Render text to an image.
     *
     * @param text the text to render
     * @param fontName the font name
     * @param fontSize the font size
     * @param color the text color
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     * @return the rendered image as ImageRGB
     */
    public static ImageRGB renderText(final String text, final String fontName,
        final int fontSize, final ColorRGB color, final int textWidth,
        final int textHeight) {

        Font font = new Font(fontName, Font.PLAIN, fontSize);
        return renderText(text, font, fontSize, color, textWidth, textHeight);
    }

    /**
     * Render text to an image.
     *
     * @param text the text to render
     * @param font the font object
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

        return toImageRGB(newImage);
    }

    /**
     * Draw an image onto a destination image.
     *
     * @param dest the destination image
     * @param src the source image
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the modified image
     */
    public static ImageRGB drawImage(final ImageRGB dest, final ImageRGB src,
        final int x, final int y) {

        if (dest == null || src == null) {
            return dest;
        }

        BufferedImage destImage = toBufferedImage(dest);
        BufferedImage srcImage = toBufferedImage(src);

        Graphics2D gr = destImage.createGraphics();
        gr.drawImage(srcImage, x, y, null);
        gr.dispose();

        return toImageRGB(destImage);
    }

    /**
     * Check if an image is fully transparent.
     *
     * @param image the image to check
     * @return true if all pixels are fully transparent
     */
    public static boolean isFullyTransparent(final ImageRGB image) {
        if (image == null) {
            return true;
        }
        int[] pixels = image.getPixels();
        for (int pixel : pixels) {
            if ((pixel & 0xFF000000) != 0) {
                return false;
            }
        }
        return true;
    }

}
