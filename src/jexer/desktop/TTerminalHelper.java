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
 * @author Autumn Lamonte ♀ Trans Rights
 * @version 1
 */
package jexer.desktop;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jexer.bits.ImageRGB;

/**
 * TTerminalHelper provides helper methods for TTerminal that require
 * java.awt classes for BufferedImage operations.
 */
public final class TTerminalHelper {

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Private constructor prevents instantiation.
     */
    private TTerminalHelper() {
        // Do nothing
    }

    // ------------------------------------------------------------------------
    // TTerminalHelper --------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Convert ImageRGB to BufferedImage.
     *
     * @param imageRGB the ImageRGB to convert
     * @return the BufferedImage
     */
    private static BufferedImage toBufferedImage(final ImageRGB imageRGB) {
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
     * Split a double-width glyph image into left and right cell images.
     *
     * @param imageRGB the double-width glyph image
     * @param textWidth the width of one text cell
     * @param textHeight the height of one text cell
     * @param doubleHeight 0 for normal, 1 for top half, 2 for bottom half
     * @return array of two ImageRGB objects [left, right]
     */
    public static ImageRGB[] splitDoubleWidthImage(final ImageRGB imageRGB,
        final int textWidth, final int textHeight, final int doubleHeight) {

        if (imageRGB == null) {
            return new ImageRGB[] { null, null };
        }

        BufferedImage image = toBufferedImage(imageRGB);
        BufferedImage leftImage = null;
        BufferedImage rightImage = null;

        switch (doubleHeight) {
        case 1:
            // Top half double height
            leftImage = image.getSubimage(0, 0, textWidth, textHeight);
            rightImage = image.getSubimage(textWidth, 0, textWidth, textHeight);
            break;
        case 2:
            // Bottom half double height
            leftImage = image.getSubimage(0, textHeight, textWidth, textHeight);
            rightImage = image.getSubimage(textWidth, textHeight,
                textWidth, textHeight);
            break;
        default:
            // Either single height double-width, or error fallback
            BufferedImage wideImage = new BufferedImage(textWidth * 2,
                textHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D grWide = wideImage.createGraphics();
            grWide.drawImage(image, 0, 0, wideImage.getWidth(),
                wideImage.getHeight(), null);
            grWide.dispose();
            leftImage = wideImage.getSubimage(0, 0, textWidth, textHeight);
            rightImage = wideImage.getSubimage(textWidth, 0, textWidth,
                textHeight);
            break;
        }

        return new ImageRGB[] {
            toImageRGB(leftImage),
            toImageRGB(rightImage)
        };
    }

    /**
     * Get the hash code for an image for use as a cell image ID.
     *
     * @param imageRGB the image
     * @return a positive hash code
     */
    public static int getImageHashCode(final ImageRGB imageRGB) {
        if (imageRGB == null) {
            return 0;
        }
        BufferedImage image = toBufferedImage(imageRGB);
        return Math.abs(image.hashCode());
    }

}
