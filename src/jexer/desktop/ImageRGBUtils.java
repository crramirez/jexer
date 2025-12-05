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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jexer.bits.ImageRGB;

/**
 * ImageUtils provides conversion utilities between ImageRGB and
 * java.awt.image.BufferedImage. This class is part of the jexer-java-desktop
 * addon and requires the java.desktop module.
 */
public class ImageRGBUtils {

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Private constructor prevents accidental creation of this class.
     */
    private ImageRGBUtils() {}

    // ------------------------------------------------------------------------
    // ImageRGBUtils ----------------------------------------------------------
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

        int[] pixels = imageRGB.getRGB(0, 0, width, height, null, 0, width);
        result.setRGB(0, 0, width, height, pixels, 0, width);

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
     * Scale an ImageRGB using AWT Graphics2D.
     *
     * @param imageRGB the image to scale
     * @param newWidth the new width
     * @param newHeight the new height
     * @return the scaled ImageRGB
     */
    public static ImageRGB scaleImage(final ImageRGB imageRGB,
        final int newWidth, final int newHeight) {

        if (imageRGB == null) {
            return null;
        }

        BufferedImage src = toBufferedImage(imageRGB);
        BufferedImage dest = new BufferedImage(newWidth, newHeight,
            BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return toImageRGB(dest);
    }

    /**
     * Draw an ImageRGB onto a Graphics2D context.
     *
     * @param g the Graphics2D context
     * @param imageRGB the image to draw
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public static void drawImage(final Graphics2D g, final ImageRGB imageRGB,
        final int x, final int y) {

        if (imageRGB == null || g == null) {
            return;
        }

        BufferedImage bi = toBufferedImage(imageRGB);
        g.drawImage(bi, x, y, null);
    }

    /**
     * Create an ImageRGB from part of a BufferedImage.
     *
     * @param bufferedImage the source BufferedImage
     * @param x the x coordinate of the region
     * @param y the y coordinate of the region
     * @param w the width of the region
     * @param h the height of the region
     * @return the ImageRGB containing the specified region
     */
    public static ImageRGB getSubimage(final BufferedImage bufferedImage,
        final int x, final int y, final int w, final int h) {

        if (bufferedImage == null) {
            return null;
        }

        int[] pixels = bufferedImage.getRGB(x, y, w, h, null, 0, w);
        return new ImageRGB(w, h, pixels);
    }

}
