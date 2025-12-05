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
package jexer.bits;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * ImageUtils provides image utility methods with fallback when java.desktop
 * is not available. When the jexer-java-desktop JAR is on the classpath,
 * full functionality is available. Otherwise, methods return null or
 * reasonable defaults.
 */
public class ImageUtils {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Selections for fitting the image to the text cells.
     */
    public enum Scale {
        /**
         * Stretch/shrink the image in both directions to fully fill the text
         * area width/height.
         */
        STRETCH,

        /**
         * Scale the image, preserving aspect ratio, to fill the text area
         * width/height (like letterbox).  The background color for the
         * letterboxed area is specified in the backColor argument to
         * scaleImage().
         */
        SCALE,
    }

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Whether the implementation class is available.
     */
    private static Boolean implAvailable = null;

    /**
     * The implementation class.
     */
    private static Class<?> implClass = null;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Private constructor prevents accidental creation of this class.
     */
    private ImageUtils() {}

    // ------------------------------------------------------------------------
    // ImageUtils -------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Check if the implementation is available.
     *
     * @return true if ImageUtilsImpl is available
     */
    private static boolean isImplAvailable() {
        if (implAvailable == null) {
            try {
                implClass = Class.forName("jexer.desktop.ImageUtilsImpl");
                implAvailable = true;
            } catch (ClassNotFoundException e) {
                implAvailable = false;
            }
        }
        return implAvailable;
    }

    /**
     * Check if any pixels in an image have not-0% alpha value.
     *
     * @param image the image to check
     * @return true if every pixel is fully transparent
     */
    public static boolean isFullyTransparent(final ImageRGB image) {
        if (image == null) {
            return true;
        }
        int[] pixels = image.getPixels();
        if (pixels.length == 0) {
            return true;
        }
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >>> 24) & 0xFF;
            if (alpha != 0x00) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if any pixels in an image have not-100% alpha value.
     *
     * @param image the image to check
     * @return true if every pixel is fully opaque
     */
    public static boolean isFullyOpaque(final ImageRGB image) {
        if (image == null) {
            return true;
        }
        int[] pixels = image.getPixels();
        if (pixels.length == 0) {
            return true;
        }
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >>> 24) & 0xFF;
            if (alpha != 0xFF) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scale an image to be scaleFactor size and/or stretch it to fit a
     * target box.
     *
     * @param image the image to scale
     * @param width the width in pixels for the destination image
     * @param height the height in pixels for the destination image
     * @param scale the scaling type
     * @param backColor the background color to use for Scale.SCALE
     * @return the scaled image, or null if java.desktop is not available
     */
    @SuppressWarnings("unchecked")
    public static ImageRGB scaleImage(final ImageRGB image,
                                      final int width, final int height,
                                      final Scale scale, final ColorRGB backColor) {

        if (!isImplAvailable()) {
            return null;
        }
        try {
            // Get the Scale enum from the impl class
            Class<?> scaleEnumClass = Class.forName("jexer.desktop.ImageUtilsImpl$Scale");
            Object implScale = Enum.valueOf((Class<Enum>) scaleEnumClass,
                scale.name());

            Method method = implClass.getMethod("scaleImage",
                ImageRGB.class, int.class, int.class, scaleEnumClass, ColorRGB.class);
            return (ImageRGB) method.invoke(null, image, width, height, implScale, backColor);
        } catch (Exception e) {
            // SQUASH
            return null;
        }
    }

    /**
     * Read an image from a file.
     *
     * @param file the file to read
     * @return the image, or null on error or if java.desktop is not available
     */
    public static ImageRGB readImage(final File file) {
        if (!isImplAvailable()) {
            return null;
        }
        try {
            Method method = implClass.getMethod("readImage", File.class);
            return (ImageRGB) method.invoke(null, file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Read an image from an input stream.
     *
     * @param inputStream the input stream to read
     * @return the image, or null on error or if java.desktop is not available
     */
    public static ImageRGB readImage(final InputStream inputStream) {
        if (!isImplAvailable()) {
            return null;
        }
        try {
            Method method = implClass.getMethod("readImage", InputStream.class);
            return (ImageRGB) method.invoke(null, inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Open an image as an Animation.
     *
     * @param filename the name of the file that contains an animation
     * @return the animation, or null on error or if java.desktop is not available
     */
    public static Animation getAnimation(final String filename) {
        if (!isImplAvailable()) {
            return null;
        }
        try {
            Method method = implClass.getMethod("getAnimation", String.class);
            return (Animation) method.invoke(null, filename);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Open an image as an Animation.
     *
     * @param file the file that contains an animation
     * @return the animation, or null on error or if java.desktop is not available
     */
    public static Animation getAnimation(final File file) {
        if (!isImplAvailable()) {
            return null;
        }
        try {
            Method method = implClass.getMethod("getAnimation", File.class);
            return (Animation) method.invoke(null, file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Open an image as an Animation.
     *
     * @param url the URL that contains an animation
     * @return the animation, or null on error or if java.desktop is not available
     */
    public static Animation getAnimation(final URL url) {
        if (!isImplAvailable()) {
            return null;
        }
        try {
            Method method = implClass.getMethod("getAnimation", URL.class);
            return (Animation) method.invoke(null, url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Open an image as an Animation.
     *
     * @param inputStream the inputStream that contains an animation
     * @return the animation, or null on error or if java.desktop is not available
     */
    public static Animation getAnimation(final InputStream inputStream) {
        if (!isImplAvailable()) {
            return null;
        }
        try {
            Method method = implClass.getMethod("getAnimation", InputStream.class);
            return (Animation) method.invoke(null, inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Report the absolute distance in RGB space between two RGB colors.
     *
     * @param first the first color
     * @param second the second color
     * @return the distance
     */
    public static int rgbDistance(final int first, final int second) {
        int red   = (first >>> 16) & 0xFF;
        int green = (first >>>  8) & 0xFF;
        int blue  =  first         & 0xFF;
        int red2   = (second >>> 16) & 0xFF;
        int green2 = (second >>>  8) & 0xFF;
        int blue2  =  second         & 0xFF;
        double diff = Math.pow(red2 - red, 2);
        diff += Math.pow(green2 - green, 2);
        diff += Math.pow(blue2 - blue, 2);
        return (int) Math.sqrt(diff);
    }

    /**
     * Move from one point in RGB space to another, by a certain fraction.
     *
     * @param start the starting point color
     * @param end the ending point color
     * @param fraction the amount of movement between start and end, between
     * 0.0 (start) and 1.0 (end).
     * @return the final color
     */
    public static int rgbMove(final int start, final int end,
        final double fraction) {

        if (fraction <= 0) {
            return start;
        }
        if (fraction >= 1) {
            return end;
        }

        int red   = (start >>> 16) & 0xFF;
        int green = (start >>>  8) & 0xFF;
        int blue  =  start         & 0xFF;
        int red2   = (end >>> 16) & 0xFF;
        int green2 = (end >>>  8) & 0xFF;
        int blue2  =  end         & 0xFF;

        int rgbRed   =   red + (int) (fraction * (  red2 - red));
        int rgbGreen = green + (int) (fraction * (green2 - green));
        int rgbBlue  =  blue + (int) (fraction * ( blue2 - blue));

        rgbRed   = Math.min(Math.max(  rgbRed, 0), 255);
        rgbGreen = Math.min(Math.max(rgbGreen, 0), 255);
        rgbBlue  = Math.min(Math.max( rgbBlue, 0), 255);

        return (rgbRed << 16) | (rgbGreen << 8) | rgbBlue;
    }

    /**
     * Compute the average RGB value of an entire image, including pixels
     * that may be partially or fully transparent.
     *
     * @param image the image to check
     * @return the average color
     */
    public static int rgbAverage(final ImageRGB image) {
        return rgbAverage(image, false);
    }

    /**
     * Compute the average RGB value of an entire image.
     *
     * @param image the image to check
     * @param onlyOpaque if true, only count pixels that are fully opaque
     * @return the average color
     */
    public static int rgbAverage(final ImageRGB image,
        final boolean onlyOpaque) {

        if (image == null) {
            return 0xFF000000;
        }
        int[] rgbArray = image.getPixels();

        if (rgbArray.length == 0) {
            return 0xFF000000;
        }

        // Compute the average color.
        long totalRed = 0;
        long totalGreen = 0;
        long totalBlue = 0;
        long count = 0;
        for (int i = 0; i < rgbArray.length; i++) {
            int argb = rgbArray[i];
            if ((onlyOpaque == true) && (((argb >>> 24) & 0xFF) != 0xFF)) {
                continue;
            }
            count++;
            int red   = (argb >>> 16) & 0xFF;
            int green = (argb >>>  8) & 0xFF;
            int blue  =  argb         & 0xFF;
            totalRed   += red;
            totalGreen += green;
            totalBlue  += blue;
        }
        if (count == 0) {
            return 0xFF000000;
        }
        totalRed   = (int) (totalRed   / count);
        totalGreen = (int) (totalGreen / count);
        totalBlue  = (int) (totalBlue  / count);

        int result = (int) ((0xFF << 24) | (totalRed   << 16)
                                         | (totalGreen <<  8)
                                         |  totalBlue);
        return result;
    }

    /**
     * Compute the standard deviation of RGB values of an entire image.
     *
     * @param image the image to check
     * @param averageImage the image's "average" pixel values
     * @return the standard deviation
     * @throws IllegalArgumentException if the two images are of different
     * dimensions
     */
    public static double rgbStdDev(final ImageRGB image,
        final ImageRGB averageImage) {

        if (image == null || averageImage == null) {
            return 0.0;
        }
        if (image.getWidth() != averageImage.getWidth()) {
            throw new IllegalArgumentException("images have different widths");
        }
        if (image.getHeight() != averageImage.getHeight()) {
            throw new IllegalArgumentException("images have different heights");
        }

        int[] imageRgbArray = image.getPixels();
        int[] averageImageRgbArray = averageImage.getPixels();

        double variance = 0.0;
        for (int i = 0; i < imageRgbArray.length; i++) {
            int rgb1 = imageRgbArray[i];
            int rgb2 = averageImageRgbArray[i];
            double distance = rgbDistance(rgb1, rgb2);
            variance += distance;
        }

        return (variance / (double) imageRgbArray.length);
    }

    /**
     * Create a new ImageRGB with specified dimensions.
     * The original image parameter is ignored since ImageRGB
     * always uses ARGB format.
     *
     * @param image the original image (ignored for ImageRGB)
     * @param width the width of the new image
     * @param height the height of the new image
     * @return the new image
     */
    public static ImageRGB createImage(final ImageRGB image,
        final int width, final int height) {

        return new ImageRGB(width, height);
    }

}
