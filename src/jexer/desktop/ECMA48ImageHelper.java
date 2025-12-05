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
import jexer.backend.Backend;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import jexer.bits.ColorRGB;
import jexer.bits.ComplexCell;
import jexer.bits.GlyphMaker;
import jexer.bits.ImageRGB;
import jexer.bits.ImageUtils;
import jexer.bits.StringUtils;
import jexer.terminal.DisplayLine;

/**
 * ECMA48ImageHelper provides image rendering capabilities for ECMA48.
 * This class uses java.awt classes and is packaged in the java-desktop JAR.
 */
public class ECMA48ImageHelper {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The text width in pixels.
     */
    private int textWidth;

    /**
     * The text height in pixels.
     */
    private int textHeight;

    /**
     * The backend to get color information from.
     */
    private Backend backend;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param textWidth the text width in pixels
     * @param textHeight the text height in pixels
     * @param backend the backend
     */
    public ECMA48ImageHelper(final int textWidth, final int textHeight,
        final Backend backend) {

        this.textWidth = textWidth;
        this.textHeight = textHeight;
        this.backend = backend;
    }

    // ------------------------------------------------------------------------
    // ECMA48ImageHelper ------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Set text dimensions.
     *
     * @param textWidth the text width in pixels
     * @param textHeight the text height in pixels
     */
    public void setTextDimensions(final int textWidth, final int textHeight) {
        this.textWidth = textWidth;
        this.textHeight = textHeight;
    }

    /**
     * Convert ImageRGB to BufferedImage for AWT Graphics operations.
     *
     * @param imageRGB the ImageRGB to convert
     * @return the BufferedImage, or null if imageRGB is null
     */
    public BufferedImage toBufferedImage(final ImageRGB imageRGB) {
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
     * @return the ImageRGB, or null if bufferedImage is null
     */
    public ImageRGB toImageRGB(final BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
        return new ImageRGB(width, height, pixels);
    }

    /**
     * Parse a Jexer image file (PNG or JPG) and return the ImageRGB.
     *
     * @param type 1 for PNG, 2 for JPG
     * @param data the base64-encoded image data
     * @return the ImageRGB and scroll/transparency info, or null if invalid
     */
    public ImageRGB parseJexerImageFile(final int type, final String data) {
        BufferedImage bImage = null;
        boolean maybeTransparent = false;
        try {
            byte [] bytes = StringUtils.fromBase64(data.getBytes());

            switch (type) {
            case 1:
                if ((bytes[0] != (byte) 0x89)
                    || (bytes[1] != 'P')
                    || (bytes[2] != 'N')
                    || (bytes[3] != 'G')
                    || (bytes[4] != (byte) 0x0D)
                    || (bytes[5] != (byte) 0x0A)
                    || (bytes[6] != (byte) 0x1A)
                    || (bytes[7] != (byte) 0x0A)
                ) {
                    // File does not have PNG header, bail out.
                    return null;
                }
                maybeTransparent = true;
                break;

            case 2:
                if ((bytes[0] != (byte) 0XFF)
                    || (bytes[1] != (byte) 0xD8)
                    || (bytes[2] != (byte) 0xFF)
                ) {
                    // File does not have JPG header, bail out.
                    return null;
                }
                break;

            default:
                // Unsupported type, bail out.
                return null;
            }

            bImage = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            // SQUASH
            return null;
        }
        if (bImage == null) {
            return null;
        }
        int imageWidth = bImage.getWidth();
        int imageHeight = bImage.getHeight();
        if ((imageWidth < 1)
            || (imageWidth > 10000)
            || (imageHeight < 1)
            || (imageHeight > 10000)
        ) {
            return null;
        }
        if (maybeTransparent) {
            if (bImage.getTransparency() == java.awt.Transparency.OPAQUE) {
                maybeTransparent = false;
            }
        }

        return toImageRGB(bImage);
    }

    /**
     * Check if a Jexer image file (PNG or JPG) has transparency.
     *
     * @param type 1 for PNG, 2 for JPG
     * @param data the base64-encoded image data
     * @return true if the image has transparency
     */
    public boolean isJexerImageFileTransparent(final int type, final String data) {
        if (type == 1) {
            // PNG may have transparency
            BufferedImage bImage = null;
            try {
                byte [] bytes = StringUtils.fromBase64(data.getBytes());
                bImage = ImageIO.read(new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                return false;
            }
            if (bImage == null) {
                return false;
            }
            return (bImage.getTransparency() != java.awt.Transparency.OPAQUE);
        }
        return false;
    }

    /**
     * Parse an iTerm2 inline image and return the ImageRGB.
     *
     * @param data the base64-encoded image data
     * @return the ImageRGB, or null if invalid
     */
    public ImageRGB parseIterm2Image(final String data) {
        BufferedImage image = null;
        try {
            byte [] bytes = StringUtils.fromBase64(data.getBytes());
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            return null;
        }
        if (image == null) {
            return null;
        }
        return toImageRGB(image);
    }

    /**
     * Check if an iTerm2 inline image has transparency.
     *
     * @param data the base64-encoded image data
     * @return true if the image has transparency
     */
    public boolean isIterm2ImageTransparent(final String data) {
        BufferedImage image = null;
        try {
            byte [] bytes = StringUtils.fromBase64(data.getBytes());
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            return false;
        }
        if (image == null) {
            return false;
        }
        return (image.getTransparency() != java.awt.Transparency.OPAQUE);
    }

    /**
     * Convert an image to cells and populate the cells array.
     *
     * @param imageRGB the image to convert
     * @param transparent if true, the backend supports transparent images
     * @param maybeTransparent if true, this image format might have transparency
     * @return a 2D array of ComplexCell objects representing the image
     */
    public ComplexCell[][] imageToCellArray(final ImageRGB imageRGB,
        final boolean transparent, final boolean maybeTransparent) {

        if (imageRGB == null) {
            return null;
        }

        BufferedImage image = toBufferedImage(imageRGB);

        int cellColumns = image.getWidth() / textWidth;
        while (cellColumns * textWidth < image.getWidth()) {
            cellColumns++;
        }
        int cellRows = image.getHeight() / textHeight;
        while (cellRows * textHeight < image.getHeight()) {
            cellRows++;
        }

        // Break the image up into an array of cells.
        int imageId = System.identityHashCode(this);
        imageId ^= (int) System.currentTimeMillis();
        ComplexCell[][] cells = new ComplexCell[cellColumns][cellRows];
        for (int x = 0; x < cellColumns; x++) {
            for (int y = 0; y < cellRows; y++) {
                int width = textWidth;
                if ((x + 1) * textWidth > image.getWidth()) {
                    width = image.getWidth() - (x * textWidth);
                }
                int height = textHeight;
                if ((y + 1) * textHeight > image.getHeight()) {
                    height = image.getHeight() - (y * textHeight);
                }

                ComplexCell cell = new ComplexCell();

                BufferedImage imageSlice = image.getSubimage(x * textWidth,
                    y * textHeight, width, height);

                if (isBufferedImageFullyTransparent(imageSlice)) {
                    // There is nothing more to do, this entire image is
                    // empty.

                    // NOP
                } else {
                    BufferedImage newImage;
                    newImage = new BufferedImage(textWidth, textHeight,
                        BufferedImage.TYPE_INT_ARGB);
                    java.awt.Graphics gr = newImage.getGraphics();
                    gr.setColor(java.awt.Color.BLACK);
                    if (!transparent) {
                        gr.fillRect(0, 0, newImage.getWidth(),
                            newImage.getHeight());
                    }
                    gr.drawImage(imageSlice, 0, 0, null, null);
                    gr.dispose();

                    imageId++;
                    cell.setImage(toImageRGB(newImage), imageId & 0x7FFFFFFF);

                    if (maybeTransparent) {
                        cell.isTransparentImage();
                    } else {
                        cell.setOpaqueImage();
                    }
                }
                cells[x][y] = cell;
            }
        }
        return cells;
    }

    /**
     * Blend two images together for transparent cell rendering.
     *
     * @param bottomImage the bottom image (old cell)
     * @param topImage the top image (new cell)
     * @return the blended image
     */
    public ImageRGB blendImages(final ImageRGB bottomImage,
        final ImageRGB topImage) {

        BufferedImage newImage = new BufferedImage(textWidth,
            textHeight, BufferedImage.TYPE_INT_ARGB);

        java.awt.Graphics gr = newImage.getGraphics();
        gr.setColor(java.awt.Color.BLACK);
        gr.drawImage(toBufferedImage(bottomImage), 0, 0, null, null);
        gr.drawImage(toBufferedImage(topImage), 0, 0, null, null);
        gr.dispose();

        return toImageRGB(newImage);
    }

    /**
     * Check if an image is fully transparent.
     *
     * @param imageRGB the image to check
     * @return true if the image is fully transparent
     */
    public boolean isFullyTransparent(final ImageRGB imageRGB) {
        if (imageRGB == null) {
            return true;
        }
        return ImageUtils.isFullyTransparent(imageRGB);
    }

    /**
     * Check if a BufferedImage is fully transparent.
     *
     * @param image the image to check
     * @return true if the image is fully transparent
     */
    private boolean isBufferedImageFullyTransparent(final BufferedImage image) {
        if (image == null) {
            return true;
        }
        int[] rgbArray = image.getRGB(0, 0, image.getWidth(), image.getHeight(),
            null, 0, image.getWidth());
        for (int i = 0; i < rgbArray.length; i++) {
            int alpha = (rgbArray[i] >>> 24) & 0xFF;
            if (alpha != 0x00) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse and scale an iTerm2 image.
     *
     * @param data the base64-encoded image data
     * @param displayWidth the target display width in pixels
     * @param displayHeight the target display height in pixels
     * @param preserveAspectRatio if true, preserve aspect ratio during scaling
     * @param backgroundColor the background color for scaling
     * @return the scaled ImageRGB, or null if invalid
     */
    public ImageRGB parseAndScaleIterm2Image(final String data,
        final int displayWidth, final int displayHeight,
        final boolean preserveAspectRatio, final ColorRGB backgroundColor) {

        BufferedImage image = null;
        try {
            byte [] bytes = StringUtils.fromBase64(data.getBytes());
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            return null;
        }
        if (image == null) {
            return null;
        }

        int fileImageWidth = image.getWidth();
        int fileImageHeight = image.getHeight();

        if ((fileImageWidth < 1)
            || (fileImageWidth > 10000)
            || (fileImageHeight < 1)
            || (fileImageHeight > 10000)
        ) {
            return null;
        }

        // Scale if needed
        if (preserveAspectRatio
            && ((displayWidth != fileImageWidth)
                || (displayHeight != fileImageHeight))
        ) {
            image = scaleBufferedImage(image, displayWidth, displayHeight,
                true, backgroundColor);
        } else if ((displayWidth != fileImageWidth)
            || (displayHeight != fileImageHeight)
        ) {
            image = scaleBufferedImage(image, displayWidth, displayHeight,
                false, backgroundColor);
        }

        return toImageRGB(image);
    }

    /**
     * Scale a BufferedImage.
     *
     * @param image the image to scale
     * @param width the target width
     * @param height the target height
     * @param preserveAspectRatio if true, preserve aspect ratio (letterbox)
     * @param backColor the background color for letterboxing
     * @return the scaled image
     */
    private BufferedImage scaleBufferedImage(final BufferedImage image,
        final int width, final int height,
        final boolean preserveAspectRatio, final ColorRGB backColor) {

        BufferedImage newImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);

        int x = 0;
        int y = 0;
        int destWidth = width;
        int destHeight = height;

        if (preserveAspectRatio) {
            double a = (double) image.getWidth() / image.getHeight();
            double b = (double) width / height;
            double h = (double) height / image.getHeight();
            double w = (double) width / image.getWidth();

            if (a > b) {
                destHeight = (int) (image.getWidth() / a * w);
                destWidth = (int) (image.getWidth() * w);
                y = (height - destHeight) / 2;
            } else {
                destHeight = (int) (image.getHeight() * h);
                destWidth = (int) (image.getHeight() * a * h);
                x = (width - destWidth) / 2;
            }
        }

        java.awt.Graphics gr = newImage.createGraphics();
        if (preserveAspectRatio && backColor != null) {
            gr.setColor(new java.awt.Color(backColor.getRed(),
                backColor.getGreen(), backColor.getBlue(),
                backColor.getAlpha()));
            gr.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        }
        gr.drawImage(image, x, y, destWidth, destHeight, null);
        gr.dispose();
        return newImage;
    }

    /**
     * Get the dimensions of an iTerm2 image.
     *
     * @param data the base64-encoded image data
     * @return an array of [width, height], or null if invalid
     */
    public int[] getIterm2ImageDimensions(final String data) {
        BufferedImage image = null;
        try {
            byte [] bytes = StringUtils.fromBase64(data.getBytes());
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            return null;
        }
        if (image == null) {
            return null;
        }
        return new int[] { image.getWidth(), image.getHeight() };
    }

}
