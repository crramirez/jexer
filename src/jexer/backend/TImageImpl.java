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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;

/**
 * TImageImpl provides the AWT-based implementation for TImage operations.
 * This class is in jexer.backend and will be included in the java-desktop JAR.
 */
public final class TImageImpl {

    /**
     * Private constructor to prevent instantiation.
     */
    private TImageImpl() {
    }

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
     * Rotate an image clockwise.
     *
     * @param image the image to rotate
     * @param clockwise number of 90-degree turns clockwise
     * @return the rotated image
     */
    public static ImageRGB rotateImage(final ImageRGB image,
        final int clockwise) {

        if (clockwise % 4 == 0) {
            return image;
        }

        BufferedImage srcImg = toBufferedImage(image);
        BufferedImage newImage = null;

        if (clockwise % 4 == 1) {
            // 90 degrees clockwise
            newImage = new BufferedImage(srcImg.getHeight(), srcImg.getWidth(),
                BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < srcImg.getWidth(); x++) {
                for (int y = 0; y < srcImg.getHeight(); y++) {
                    newImage.setRGB(y, x,
                        srcImg.getRGB(x, srcImg.getHeight() - 1 - y));
                }
            }
        } else if (clockwise % 4 == 2) {
            // 180 degrees clockwise
            newImage = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < srcImg.getWidth(); x++) {
                for (int y = 0; y < srcImg.getHeight(); y++) {
                    newImage.setRGB(x, y,
                        srcImg.getRGB(srcImg.getWidth() - 1 - x,
                            srcImg.getHeight() - 1 - y));
                }
            }
        } else if (clockwise % 4 == 3) {
            // 270 degrees clockwise
            newImage = new BufferedImage(srcImg.getHeight(), srcImg.getWidth(),
                BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < srcImg.getWidth(); x++) {
                for (int y = 0; y < srcImg.getHeight(); y++) {
                    newImage.setRGB(y, x,
                        srcImg.getRGB(srcImg.getWidth() - 1 - x, y));
                }
            }
        }

        return toImageRGB(newImage);
    }

    /**
     * Scale an image.
     *
     * @param image the image to scale
     * @param factor the scale factor
     * @param scaleType the scale type (0=NONE, 1=STRETCH, 2=SCALE)
     * @param width the width in text cells
     * @param height the height in text cells
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     * @param antiAlias whether to use anti-aliasing
     * @param scaleBackColor the background color for letterboxing
     * @return the scaled image
     */
    public static ImageRGB scaleImage(final ImageRGB image,
        final double factor, final int scaleType, final int width,
        final int height, final int textWidth, final int textHeight,
        final boolean antiAlias, final ColorRGB scaleBackColor) {

        if ((scaleType == 0) && (Math.abs(factor - 1.0) < 0.03)) {
            return image;
        }

        BufferedImage srcImg = toBufferedImage(image);
        if (srcImg == null) {
            return null;
        }

        int destWidth = 0;
        int destHeight = 0;
        int x = 0;
        int y = 0;

        BufferedImage newImage = null;

        switch (scaleType) {
        case 0: // NONE
            destWidth = (int) (srcImg.getWidth() * factor);
            destHeight = (int) (srcImg.getHeight() * factor);
            newImage = new BufferedImage(Math.max(1, destWidth),
                Math.max(1, destHeight), BufferedImage.TYPE_INT_ARGB);
            break;
        case 1: // STRETCH
            destWidth = Math.max(1, width) * textWidth;
            destHeight = Math.max(1, height) * textHeight;
            newImage = new BufferedImage(destWidth, destHeight,
                BufferedImage.TYPE_INT_ARGB);
            break;
        case 2: // SCALE
            double a = (double) srcImg.getWidth() / srcImg.getHeight();
            double b = (double) (width * textWidth) / (height * textHeight);

            if (a > b) {
                destWidth = Math.max(1, width) * textWidth;
                destHeight = (int) (destWidth / a);
                y = ((Math.max(1, height) * textHeight) - destHeight) / 2;
            } else {
                destHeight = Math.max(1, height) * textHeight;
                destWidth = (int) (destHeight * a);
                x = ((Math.max(1, width) * textWidth) - destWidth) / 2;
            }
            newImage = new BufferedImage(Math.max(1, width) * textWidth,
                Math.max(1, height) * textHeight, BufferedImage.TYPE_INT_ARGB);
            break;
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
        if (scaleType == 2) {
            gr.setColor(new java.awt.Color(scaleBackColor.getRed(),
                scaleBackColor.getGreen(), scaleBackColor.getBlue()));
            gr.fillRect(0, 0, width * textWidth, height * textHeight);
        }
        gr.drawImage(srcImg, x, y, destWidth, destHeight, null);
        gr.dispose();
        return toImageRGB(newImage);
    }

}
