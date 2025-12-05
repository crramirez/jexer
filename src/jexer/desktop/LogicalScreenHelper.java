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
package jexer.desktop;
import jexer.backend.Screen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jexer.bits.Cell;
import jexer.bits.ImageRGB;
import jexer.bits.ImageUtils;

/**
 * LogicalScreenHelper provides java.desktop-based image compositing
 * operations for LogicalScreen. This class is in the java-desktop JAR.
 */
public class LogicalScreenHelper {

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
     * Create a new ARGB BufferedImage.
     *
     * @param width the width
     * @param height the height
     * @return a new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Blend an image over a background color using alpha compositing.
     *
     * @param sourceImage the source ImageRGB
     * @param backgroundColor the background color as ARGB
     * @param alpha the alpha value (0.0 - 1.0)
     * @return the blended ImageRGB
     */
    public static ImageRGB blendImageOverColor(final ImageRGB sourceImage,
        final int backgroundColor, final float alpha) {

        BufferedImage biImage = toBufferedImage(sourceImage);
        BufferedImage newImage = new BufferedImage(biImage.getWidth(),
            biImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(biImage, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            alpha));
        g2d.setColor(new Color(backgroundColor));
        g2d.fillRect(0, 0, biImage.getWidth(), biImage.getHeight());
        g2d.dispose();
        return toImageRGB(newImage);
    }

    /**
     * Blend one image over another using alpha compositing.
     *
     * @param baseImage the base ImageRGB (drawn first)
     * @param overlayImage the overlay ImageRGB (drawn over base)
     * @param alpha the alpha value (0.0 - 1.0) for the overlay
     * @return the blended ImageRGB
     */
    public static ImageRGB blendImages(final ImageRGB baseImage,
        final ImageRGB overlayImage, final float alpha) {

        BufferedImage biBase = toBufferedImage(baseImage);
        BufferedImage biOverlay = toBufferedImage(overlayImage);
        BufferedImage newImage = new BufferedImage(biOverlay.getWidth(),
            biOverlay.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();
        if (biBase != null) {
            g2d.drawImage(biBase, 0, 0, null);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            alpha));
        g2d.drawImage(biOverlay, 0, 0, null);
        g2d.dispose();
        return toImageRGB(newImage);
    }

    /**
     * Create an image filled with a solid color, then draw an overlay image
     * on top with alpha compositing.
     *
     * @param backgroundColor the background color as ARGB
     * @param overlayImage the overlay ImageRGB
     * @param alpha the alpha value (0.0 - 1.0) for the overlay
     * @return the blended ImageRGB
     */
    public static ImageRGB blendColorThenImage(final int backgroundColor,
        final ImageRGB overlayImage, final float alpha) {

        BufferedImage biOverlay = toBufferedImage(overlayImage);
        BufferedImage newImage = new BufferedImage(biOverlay.getWidth(),
            biOverlay.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.setColor(new Color(backgroundColor));
        g2d.fillRect(0, 0, biOverlay.getWidth(), biOverlay.getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            alpha));
        g2d.drawImage(biOverlay, 0, 0, null);
        g2d.dispose();
        return toImageRGB(newImage);
    }

    /**
     * Perform full screen blending operation. This method handles all the
     * complex alpha compositing for blendScreen().
     *
     * @param thisForegroundPixels array of foreground colors for this screen
     * @param thisBackgroundPixels array of background colors for this screen
     * @param thisOldBackgroundPixels array of original background colors
     * @param overForegroundPixels array of foreground colors for overlay screen
     * @param overBackgroundPixels array of background colors for overlay screen
     * @param width the width of the blend area
     * @param height the height of the blend area
     * @param alpha the alpha value (0 - 255)
     * @return array containing blended [thisForeground, thisBackground, glyphForeground]
     */
    public static int[][] blendScreenColors(
        final int[] thisForegroundPixels,
        final int[] thisBackgroundPixels,
        final int[] thisOldBackgroundPixels,
        final int[] overForegroundPixels,
        final int[] overBackgroundPixels,
        final int width, final int height,
        final int alpha) {

        BufferedImage thisForeground = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        BufferedImage thisBackground = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        BufferedImage overForeground = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        BufferedImage overBackground = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        BufferedImage thisOldBackground = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);

        thisForeground.setRGB(0, 0, width, height, thisForegroundPixels, 0, width);
        thisBackground.setRGB(0, 0, width, height, thisBackgroundPixels, 0, width);
        thisOldBackground.setRGB(0, 0, width, height, thisOldBackgroundPixels, 0, width);
        overForeground.setRGB(0, 0, width, height, overForegroundPixels, 0, width);
        overBackground.setRGB(0, 0, width, height, overBackgroundPixels, 0, width);

        float fAlpha = (float) (alpha / 255.0);
        Graphics2D g2d = thisForeground.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            fAlpha));
        g2d.drawImage(overBackground, 0, 0, null);
        g2d.dispose();

        g2d = thisBackground.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            fAlpha));
        g2d.drawImage(overBackground, 0, 0, null);
        g2d.dispose();

        BufferedImage glyphForeground = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        g2d = glyphForeground.createGraphics();
        g2d.drawImage(thisBackground, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            fAlpha));
        g2d.drawImage(overForeground, 0, 0, null);
        g2d.dispose();

        int[] resultThisFg = thisForeground.getRGB(0, 0, width, height, null, 0, width);
        int[] resultThisBg = thisBackground.getRGB(0, 0, width, height, null, 0, width);
        int[] resultGlyphFg = glyphForeground.getRGB(0, 0, width, height, null, 0, width);

        return new int[][] { resultThisFg, resultThisBg, resultGlyphFg };
    }

}
