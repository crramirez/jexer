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

import java.awt.image.BufferedImage;

import jexer.TApplication;
import jexer.bits.Animation;
import jexer.bits.ImageRGB;
import jexer.tackboard.TackboardItem;

/**
 * BitmapImpl provides the AWT-based implementation for Bitmap operations.
 * This class is in jexer.backend and will be included in the java-desktop JAR.
 */
public class BitmapImpl {

    /**
     * Convert ImageRGB to BufferedImage.
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
     * Render a bitmap image to align on a grid of cells.
     *
     * @param image the source image
     * @param x the X pixel coordinate
     * @param y the Y pixel coordinate
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     * @return the rendered image, or null if no rendering needed
     */
    public static BufferedImage render(final BufferedImage image,
        final int x, final int y, final int textWidth, final int textHeight) {

        if (image == null) {
            return null;
        }

        int dx = x % textWidth;
        int dy = y % textHeight;
        if ((dx == 0) && (dy == 0)) {
            return image;
        }

        int columns = (dx + image.getWidth()) / textWidth;
        if ((dx + image.getWidth()) % textWidth > 0) {
            columns++;
        }
        int rows = (dy + image.getHeight()) / textHeight;
        if ((dy + image.getHeight()) % textHeight > 0) {
            rows++;
        }

        BufferedImage renderedImage = new BufferedImage(columns * textWidth,
            rows * textHeight, BufferedImage.TYPE_INT_ARGB);

        java.awt.Graphics gr = renderedImage.getGraphics();
        gr.setColor(java.awt.Color.BLACK);
        gr.drawImage(image, dx, dy, null, null);
        gr.dispose();
        return renderedImage;
    }

}
