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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;

/**
 * TImageHelper provides AWT-based image operations for TImage, TTerminal,
 * and TTextPicture.  This class is in the java-desktop JAR.
 */
public class TImageHelper {

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
    private TImageHelper() {}

    // ------------------------------------------------------------------------
    // TImageHelper -----------------------------------------------------------
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
     * Create a new BufferedImage with the same color model.
     *
     * @param width the width
     * @param height the height
     * @return a new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Check if a BufferedImage is fully transparent.
     *
     * @param image the image to check
     * @return true if all pixels are fully transparent
     */
    public static boolean isFullyTransparent(final BufferedImage image) {
        if (image == null) {
            return true;
        }
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(),
            null, 0, image.getWidth());
        for (int pixel : pixels) {
            if ((pixel & 0xFF000000) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scale an image.
     *
     * @param imageRGB the image to scale
     * @param scaleType 0=NONE, 1=STRETCH, 2=SCALE
     * @param factor scale factor for NONE mode
     * @param width destination width in text cells
     * @param height destination height in text cells
     * @param textWidth width of one text cell in pixels
     * @param textHeight height of one text cell in pixels
     * @param antiAlias true to use anti-aliasing
     * @param backColor background color for SCALE mode letterboxing
     * @return the scaled image
     */
    public static ImageRGB scaleImage(final ImageRGB imageRGB,
        final int scaleType, final double factor, final int width,
        final int height, final int textWidth, final int textHeight,
        final boolean antiAlias, final ColorRGB backColor) {

        if (imageRGB == null) {
            return null;
        }

        BufferedImage image = toBufferedImage(imageRGB);
        
        if ((scaleType == 0) && (Math.abs(factor - 1.0) < 0.03)) {
            // If we are within 3% of 1.0, just return the original image.
            return imageRGB;
        }

        int destWidth = 0;
        int destHeight = 0;
        int x = 0;
        int y = 0;

        BufferedImage newImage = null;

        switch (scaleType) {
        case 0: // NONE
            destWidth = (int) (image.getWidth() * factor);
            destHeight = (int) (image.getHeight() * factor);
            newImage = createImage(Math.max(1, destWidth), Math.max(1, destHeight));
            break;
        case 1: // STRETCH
            destWidth = Math.max(1, width) * textWidth;
            destHeight = Math.max(1, height) * textHeight;
            newImage = createImage(destWidth, destHeight);
            break;
        case 2: // SCALE
            double a = (double) image.getWidth() / image.getHeight();
            double b = (double) (width * textWidth) / (height * textHeight);

            if (a > b) {
                // Horizontal letterbox
                destWidth = Math.max(1, width) * textWidth;
                destHeight = (int) (destWidth / a);
                y = ((Math.max(1, height) * textHeight) - destHeight) / 2;
                if (y < 0) y = 0;
            } else {
                // Vertical letterbox
                destHeight = Math.max(1, height) * textHeight;
                destWidth = (int) (destHeight * a);
                x = ((Math.max(1, width) * textWidth) - destWidth) / 2;
                if (x < 0) x = 0;
            }
            newImage = createImage(
                Math.max(1, width) * textWidth,
                Math.max(1, height) * textHeight);
            break;
        default:
            return imageRGB;
        }

        Graphics gr = newImage.createGraphics();
        if (gr instanceof Graphics2D) {
            if (antiAlias) {
                ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            } else {
                ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
                ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            }
        }
        if (scaleType == 2 && backColor != null) {
            gr.setColor(new java.awt.Color(backColor.getRed(),
                backColor.getGreen(), backColor.getBlue()));
            gr.fillRect(0, 0, width * textWidth, height * textHeight);
        }
        gr.drawImage(image, x, y, destWidth, destHeight, null);
        gr.dispose();

        return toImageRGB(newImage);
    }

    /**
     * Rotate an image.
     *
     * @param imageRGB the image to rotate
     * @param clockwise number of 90-degree clockwise turns
     * @return the rotated image
     */
    public static ImageRGB rotateImage(final ImageRGB imageRGB,
        final int clockwise) {

        if (imageRGB == null || clockwise % 4 == 0) {
            return imageRGB;
        }

        int w = imageRGB.getWidth();
        int h = imageRGB.getHeight();
        int[] srcPixels = imageRGB.getPixels();
        int[] dstPixels;
        int newW, newH;

        if (clockwise % 4 == 1) {
            // 90 degrees clockwise
            newW = h;
            newH = w;
            dstPixels = new int[newW * newH];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    dstPixels[y + x * newW] = srcPixels[x + (h - 1 - y) * w];
                }
            }
        } else if (clockwise % 4 == 2) {
            // 180 degrees clockwise
            newW = w;
            newH = h;
            dstPixels = new int[newW * newH];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    dstPixels[x + y * newW] = srcPixels[(w - 1 - x) + (h - 1 - y) * w];
                }
            }
        } else {
            // 270 degrees clockwise (clockwise % 4 == 3)
            newW = h;
            newH = w;
            dstPixels = new int[newW * newH];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    dstPixels[y + x * newW] = srcPixels[(w - 1 - x) + y * w];
                }
            }
        }

        return new ImageRGB(newW, newH, dstPixels);
    }

    /**
     * Create a subimage from an image.
     *
     * @param imageRGB the source image
     * @param x the x coordinate of the subimage
     * @param y the y coordinate of the subimage
     * @param width the width of the subimage
     * @param height the height of the subimage
     * @return the subimage
     */
    public static ImageRGB getSubimage(final ImageRGB imageRGB,
        final int x, final int y, final int width, final int height) {

        if (imageRGB == null) {
            return null;
        }
        return imageRGB.getSubimage(x, y, width, height);
    }

    /**
     * Draw an image onto another image.
     *
     * @param dest the destination image
     * @param src the source image to draw
     * @param x the x coordinate to draw at
     * @param y the y coordinate to draw at
     * @param width the width to draw (scales if different from src)
     * @param height the height to draw (scales if different from src)
     * @return the modified destination image
     */
    public static ImageRGB drawImage(final ImageRGB dest, final ImageRGB src,
        final int x, final int y, final int width, final int height) {

        if (dest == null || src == null) {
            return dest;
        }

        BufferedImage destImage = toBufferedImage(dest);
        BufferedImage srcImage = toBufferedImage(src);

        Graphics2D gr = destImage.createGraphics();
        gr.drawImage(srcImage, x, y, width, height, null);
        gr.dispose();

        return toImageRGB(destImage);
    }

}
