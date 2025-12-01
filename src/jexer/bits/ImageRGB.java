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

import java.util.Arrays;

/**
 * A lightweight image class that stores ARGB pixel data without requiring
 * java.desktop module. This class provides similar functionality to
 * java.awt.image.BufferedImage for basic pixel manipulation, but can be
 * used in environments where AWT is not available.
 *
 * <p>
 * For actual rendering operations that require AWT Graphics, use the
 * toBufferedImage() method to convert to a BufferedImage, which is
 * available in the jexer-java-desktop.jar addon.
 * </p>
 */
public class ImageRGB {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Image type constant for TYPE_INT_ARGB.
     */
    public static final int TYPE_INT_ARGB = 2;

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The width of the image.
     */
    private final int width;

    /**
     * The height of the image.
     */
    private final int height;

    /**
     * The pixel data in ARGB format.
     */
    private final int[] pixels;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @param imageType the image type (only TYPE_INT_ARGB is supported)
     */
    public ImageRGB(final int width, final int height, final int imageType) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        // Initialize to transparent black
        Arrays.fill(pixels, 0);
    }

    /**
     * Public constructor for TYPE_INT_ARGB image.
     *
     * @param width the width of the image
     * @param height the height of the image
     */
    public ImageRGB(final int width, final int height) {
        this(width, height, TYPE_INT_ARGB);
    }

    /**
     * Public constructor from existing pixel data.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @param pixels the pixel data (will be copied)
     */
    public ImageRGB(final int width, final int height, final int[] pixels) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        if (pixels == null || pixels.length != width * height) {
            throw new IllegalArgumentException("Pixels array must match width * height");
        }
        this.width = width;
        this.height = height;
        this.pixels = Arrays.copyOf(pixels, pixels.length);
    }

    // ------------------------------------------------------------------------
    // ImageRGB ---------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the width of the image.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the image.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the image type.
     *
     * @return TYPE_INT_ARGB
     */
    public int getType() {
        return TYPE_INT_ARGB;
    }

    /**
     * Get a single pixel value.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the ARGB pixel value
     */
    public int getRGB(final int x, final int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new ArrayIndexOutOfBoundsException("Coordinates out of bounds");
        }
        return pixels[y * width + x];
    }

    /**
     * Get multiple pixel values.
     *
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param w the width of the region
     * @param h the height of the region
     * @param rgbArray the array to store the pixels (or null to create new)
     * @param offset the offset into rgbArray
     * @param scansize the scan line stride for rgbArray
     * @return the array of ARGB pixel values
     */
    public int[] getRGB(final int startX, final int startY,
        final int w, final int h, int[] rgbArray, final int offset,
        final int scansize) {

        if (startX < 0 || startY < 0 || startX + w > width || startY + h > height) {
            throw new ArrayIndexOutOfBoundsException("Coordinates out of bounds");
        }

        if (rgbArray == null) {
            rgbArray = new int[offset + h * scansize];
        }

        for (int row = 0; row < h; row++) {
            int srcOffset = (startY + row) * width + startX;
            int dstOffset = offset + row * scansize;
            System.arraycopy(pixels, srcOffset, rgbArray, dstOffset, w);
        }

        return rgbArray;
    }

    /**
     * Set a single pixel value.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rgb the ARGB pixel value
     */
    public void setRGB(final int x, final int y, final int rgb) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new ArrayIndexOutOfBoundsException("Coordinates out of bounds");
        }
        pixels[y * width + x] = rgb;
    }

    /**
     * Set multiple pixel values.
     *
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param w the width of the region
     * @param h the height of the region
     * @param rgbArray the array of ARGB pixel values
     * @param offset the offset into rgbArray
     * @param scansize the scan line stride for rgbArray
     */
    public void setRGB(final int startX, final int startY,
        final int w, final int h, final int[] rgbArray, final int offset,
        final int scansize) {

        if (startX < 0 || startY < 0 || startX + w > width || startY + h > height) {
            throw new ArrayIndexOutOfBoundsException("Coordinates out of bounds");
        }

        for (int row = 0; row < h; row++) {
            int srcOffset = offset + row * scansize;
            int dstOffset = (startY + row) * width + startX;
            System.arraycopy(rgbArray, srcOffset, pixels, dstOffset, w);
        }
    }

    /**
     * Fill a rectangular region with a color.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width of the region
     * @param h the height of the region
     * @param rgb the ARGB color value
     */
    public void fillRect(final int x, final int y, final int w, final int h,
        final int rgb) {

        int x1 = Math.max(0, x);
        int y1 = Math.max(0, y);
        int x2 = Math.min(width, x + w);
        int y2 = Math.min(height, y + h);

        for (int row = y1; row < y2; row++) {
            int offset = row * width + x1;
            Arrays.fill(pixels, offset, offset + (x2 - x1), rgb);
        }
    }

    /**
     * Draw another image onto this image at the specified location.
     * Supports alpha blending for transparent pixels.
     *
     * @param src the source image to draw
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void drawImage(final ImageRGB src, final int x, final int y) {
        if (src == null) {
            return;
        }

        int srcW = src.getWidth();
        int srcH = src.getHeight();

        // Compute intersection
        int srcX = 0;
        int srcY = 0;
        int dstX = x;
        int dstY = y;
        int drawW = srcW;
        int drawH = srcH;

        if (dstX < 0) {
            srcX = -dstX;
            drawW += dstX;
            dstX = 0;
        }
        if (dstY < 0) {
            srcY = -dstY;
            drawH += dstY;
            dstY = 0;
        }
        if (dstX + drawW > width) {
            drawW = width - dstX;
        }
        if (dstY + drawH > height) {
            drawH = height - dstY;
        }

        if (drawW <= 0 || drawH <= 0) {
            return;
        }

        // Copy pixels with alpha blending
        for (int row = 0; row < drawH; row++) {
            for (int col = 0; col < drawW; col++) {
                int srcPixel = src.getRGB(srcX + col, srcY + row);
                int srcAlpha = (srcPixel >>> 24) & 0xFF;

                if (srcAlpha == 0xFF) {
                    // Fully opaque, just copy
                    setRGB(dstX + col, dstY + row, srcPixel);
                } else if (srcAlpha > 0) {
                    // Alpha blend
                    int dstPixel = getRGB(dstX + col, dstY + row);
                    int blended = blendPixels(srcPixel, dstPixel);
                    setRGB(dstX + col, dstY + row, blended);
                }
                // srcAlpha == 0: fully transparent, don't modify destination
            }
        }
    }

    /**
     * Blend two ARGB pixels using Porter-Duff "over" operation.
     *
     * @param src the source pixel (top)
     * @param dst the destination pixel (bottom)
     * @return the blended pixel
     */
    private int blendPixels(final int src, final int dst) {
        int srcA = (src >>> 24) & 0xFF;
        int srcR = (src >>> 16) & 0xFF;
        int srcG = (src >>> 8) & 0xFF;
        int srcB = src & 0xFF;

        int dstA = (dst >>> 24) & 0xFF;
        int dstR = (dst >>> 16) & 0xFF;
        int dstG = (dst >>> 8) & 0xFF;
        int dstB = dst & 0xFF;

        // Alpha compositing
        int outA = srcA + (dstA * (255 - srcA)) / 255;
        if (outA == 0) {
            return 0;
        }

        int outR = (srcR * srcA + dstR * dstA * (255 - srcA) / 255) / outA;
        int outG = (srcG * srcA + dstG * dstA * (255 - srcA) / 255) / outA;
        int outB = (srcB * srcA + dstB * dstA * (255 - srcA) / 255) / outA;

        return (outA << 24) | (outR << 16) | (outG << 8) | outB;
    }

    /**
     * Create a copy of this image.
     *
     * @return a new ImageRGB with the same pixel data
     */
    public ImageRGB copy() {
        return new ImageRGB(width, height, pixels);
    }

    /**
     * Get direct access to the pixel array. Use with caution.
     *
     * @return the internal pixel array
     */
    public int[] getPixels() {
        return pixels;
    }

    /**
     * Check if this image has any transparent pixels.
     *
     * @return true if any pixel has alpha less than 255
     */
    public boolean hasTransparentPixels() {
        for (int pixel : pixels) {
            if (((pixel >>> 24) & 0xFF) != 0xFF) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this image is fully transparent (all pixels have alpha 0).
     *
     * @return true if all pixels are fully transparent
     */
    public boolean isFullyTransparent() {
        for (int pixel : pixels) {
            if (((pixel >>> 24) & 0xFF) != 0x00) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if this image is fully opaque (all pixels have alpha 255).
     *
     * @return true if all pixels are fully opaque
     */
    public boolean isFullyOpaque() {
        for (int pixel : pixels) {
            if (((pixel >>> 24) & 0xFF) != 0xFF) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generate a hash code based on the pixel data.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + width;
        hash = 31 * hash + height;
        hash = 31 * hash + Arrays.hashCode(pixels);
        return hash;
    }

    /**
     * Check equality with another image.
     *
     * @param obj the object to compare
     * @return true if the images have the same dimensions and pixel data
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImageRGB)) {
            return false;
        }
        ImageRGB other = (ImageRGB) obj;
        if (width != other.width || height != other.height) {
            return false;
        }
        return Arrays.equals(pixels, other.pixels);
    }

    /**
     * Create a string representation of this image.
     *
     * @return a string with the image dimensions
     */
    @Override
    public String toString() {
        return String.format("ImageRGB[width=%d, height=%d]", width, height);
    }

}
