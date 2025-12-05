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
 * @author Autumn Lamonte ♈
 * @version 1
 */
package jexer.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

import jexer.bits.Cell;
import jexer.bits.ImageRGB;
import jexer.bits.StringUtils;

/**
 * ECMA48TerminalHelper provides image operations for ECMA48Terminal.
 * This class contains all AWT/ImageIO-dependent code for terminal image output.
 */
public class ECMA48TerminalHelper {

    /**
     * Convert ImageRGB to BufferedImage for AWT Graphics operations.
     *
     * @param imageRGB the ImageRGB to convert
     * @return the BufferedImage, or null if imageRGB is null
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
     * Convert BufferedImage to ImageRGB.
     *
     * @param bufferedImage the BufferedImage to convert
     * @return the ImageRGB, or null if bufferedImage is null
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
     * Create a BufferedImage with specified dimensions.
     * Always creates TYPE_INT_ARGB images.
     *
     * @param width the width of the new image
     * @param height the height of the new image
     * @return the new image
     */
    public static BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Create a blank (all-black) ImageRGB of specified dimensions.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @return the blank ImageRGB
     */
    public static ImageRGB createBlankImage(final int width, final int height) {
        BufferedImage image = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics gr = image.getGraphics();
        gr.setColor(new java.awt.Color(0, 0, 0));
        gr.fillRect(0, 0, width, height);
        gr.dispose();
        return toImageRGB(image);
    }

    /**
     * Convert a horizontal range of cells' image data into a single
     * contiguous ImageRGB.
     *
     * @param cells the cells containing the image data
     * @return the combined ImageRGB
     */
    public static ImageRGB cellsToImageRGB(final List<Cell> cells) {
        if (cells == null || cells.isEmpty()) {
            return null;
        }
        
        int imageWidth = cells.get(0).getImage().getWidth();
        int imageHeight = cells.get(0).getImage().getHeight();
        int fullWidth = cells.size() * imageWidth;
        int fullHeight = imageHeight;

        BufferedImage image = new BufferedImage(fullWidth, fullHeight,
            BufferedImage.TYPE_INT_ARGB);

        int[] rgbArray;
        for (int i = 0; i < cells.size() - 1; i++) {
            int tileWidth = imageWidth;
            int tileHeight = imageHeight;

            try {
                rgbArray = cells.get(i).getImage().getRGB(0, 0,
                    tileWidth, tileHeight, null, 0, tileWidth);
            } catch (Exception e) {
                throw new RuntimeException("Error getting image pixels", e);
            }

            image.setRGB(i * imageWidth, 0, tileWidth, tileHeight,
                rgbArray, 0, tileWidth);
            if (tileHeight < fullHeight) {
                int backgroundColor = 0;
                for (int imageX = 0; imageX < image.getWidth(); imageX++) {
                    for (int imageY = imageHeight; imageY < fullHeight;
                         imageY++) {
                        image.setRGB(imageX, imageY, backgroundColor);
                    }
                }
            }
        }

        // Last cell
        int lastIndex = cells.size() - 1;
        if (lastIndex >= 0) {
            ImageRGB lastCellImage = cells.get(lastIndex).getImage();
            int lastTileWidth = lastCellImage.getWidth();
            int lastTileHeight = lastCellImage.getHeight();
            rgbArray = lastCellImage.getRGB(0, 0, lastTileWidth, lastTileHeight,
                null, 0, lastTileWidth);
            int drawX = lastIndex * imageWidth;
            int drawWidth = Math.min(lastTileWidth, fullWidth - drawX);
            if (drawWidth > 0 && lastTileHeight > 0) {
                // Create a properly sized array for the last tile
                int[] lastRgbArray = new int[drawWidth * lastTileHeight];
                for (int y = 0; y < lastTileHeight; y++) {
                    for (int x = 0; x < drawWidth; x++) {
                        lastRgbArray[y * drawWidth + x] = rgbArray[y * lastTileWidth + x];
                    }
                }
                image.setRGB(drawX, 0, drawWidth, lastTileHeight,
                    lastRgbArray, 0, drawWidth);
            }
        }

        return toImageRGB(image);
    }

    /**
     * Convert a horizontal range of cell's image data into a single
     * contiguous BufferedImage, rescaled and anti-aliased to match the 
     * specified text cell size.
     *
     * @param cells the cells containing image data
     * @param textWidth the text cell width
     * @param textHeight the text cell height
     * @return the BufferedImage resized to the text cell size
     */
    public static BufferedImage cellsToImage(final List<Cell> cells,
        final int textWidth, final int textHeight) {
        
        if (cells == null || cells.isEmpty()) {
            return null;
        }
        
        int imageWidth = cells.get(0).getImage().getWidth();
        int imageHeight = cells.get(0).getImage().getHeight();
        int fullWidth = cells.size() * imageWidth;
        int fullHeight = imageHeight;

        BufferedImage image = new BufferedImage(fullWidth, fullHeight,
            BufferedImage.TYPE_INT_ARGB);

        int[] rgbArray;
        for (int i = 0; i < cells.size() - 1; i++) {
            int tileWidth = imageWidth;
            int tileHeight = imageHeight;

            try {
                rgbArray = cells.get(i).getImage().getRGB(0, 0,
                    tileWidth, tileHeight, null, 0, tileWidth);
            } catch (Exception e) {
                throw new RuntimeException("Error getting image pixels", e);
            }

            image.setRGB(i * imageWidth, 0, tileWidth, tileHeight,
                rgbArray, 0, tileWidth);
            if (tileHeight < fullHeight) {
                int backgroundColor = 0;
                for (int imageX = 0; imageX < image.getWidth(); imageX++) {
                    for (int imageY = imageHeight; imageY < fullHeight;
                         imageY++) {
                        image.setRGB(imageX, imageY, backgroundColor);
                    }
                }
            }
        }

        // Last cell
        int totalWidth = 0;
        for (int i = 0; i < cells.size(); i++) {
            totalWidth += cells.get(i).getImage().getWidth();
        }
        totalWidth -= ((cells.size() - 1) * imageWidth);

        try {
            rgbArray = cells.get(cells.size() - 1).getImage().getRGB(0, 0,
                totalWidth, imageHeight, null, 0, totalWidth);
        } catch (Exception e) {
            // Return what we have so far
            return scaleImageIfNeeded(image, cells.size() * textWidth, textHeight);
        }

        try {
            image.setRGB((cells.size() - 1) * imageWidth, 0, totalWidth,
                imageHeight, rgbArray, 0, totalWidth);
        } catch (Exception e) {
            // Return what we have so far
            return scaleImageIfNeeded(image, cells.size() * textWidth, textHeight);
        }

        if (totalWidth < imageWidth) {
            int backgroundColor = 0;
            for (int imageX = image.getWidth() - totalWidth;
                 imageX < image.getWidth(); imageX++) {

                for (int imageY = 0; imageY < fullHeight; imageY++) {
                    image.setRGB(imageX, imageY, backgroundColor);
                }
            }
        }

        return scaleImageIfNeeded(image, cells.size() * textWidth, textHeight);
    }

    /**
     * Scale a BufferedImage if it doesn't match the target dimensions.
     *
     * @param image the source image
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @return the scaled image if needed, or the original
     */
    private static BufferedImage scaleImageIfNeeded(final BufferedImage image,
        final int targetWidth, final int targetHeight) {
        
        if ((image.getWidth() != targetWidth)
            || (image.getHeight() != targetHeight)) {
            
            BufferedImage newImage = new BufferedImage(targetWidth, targetHeight,
                BufferedImage.TYPE_INT_ARGB);

            Graphics gr = newImage.getGraphics();
            if (gr instanceof Graphics2D) {
                ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            }
            gr.drawImage(image, 0, 0, targetWidth, targetHeight, null);
            gr.dispose();
            return newImage;
        }
        return image;
    }

    /**
     * Scale an ImageRGB to new dimensions with anti-aliasing.
     *
     * @param source the source ImageRGB
     * @param newWidth the new width
     * @param newHeight the new height
     * @return the scaled ImageRGB
     */
    public static ImageRGB scaleImage(final ImageRGB source,
        final int newWidth, final int newHeight) {
        
        if (source == null) {
            return null;
        }

        BufferedImage sourceImage = toBufferedImage(source);
        BufferedImage newImage = new BufferedImage(newWidth, newHeight,
            BufferedImage.TYPE_INT_ARGB);

        Graphics gr = newImage.getGraphics();
        if (gr instanceof Graphics2D) {
            ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) gr).setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        }
        gr.drawImage(sourceImage, 0, 0, newWidth, newHeight, null);
        gr.dispose();

        return toImageRGB(newImage);
    }

    /**
     * Encode an ImageRGB as PNG bytes.
     *
     * @param imageRGB the ImageRGB to encode
     * @return the PNG bytes, or null on failure
     */
    public static byte[] encodePNG(final ImageRGB imageRGB) {
        if (imageRGB == null) {
            return null;
        }
        
        BufferedImage image = toBufferedImage(imageRGB);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream(1024);
        try {
            if (!ImageIO.write(image, "PNG", pngOutputStream)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return pngOutputStream.toByteArray();
    }

    /**
     * Encode an ImageRGB as JPG bytes.
     *
     * @param imageRGB the ImageRGB to encode
     * @return the JPG bytes, or null on failure
     */
    public static byte[] encodeJPG(final ImageRGB imageRGB) {
        if (imageRGB == null) {
            return null;
        }
        
        BufferedImage image = toBufferedImage(imageRGB);
        // Convert from ARGB to RGB, otherwise the JPG encode will fail.
        BufferedImage jpgImage = new BufferedImage(image.getWidth(),
            image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels,
            0, image.getWidth());
        jpgImage.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels,
            0, image.getWidth());

        ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream(1024);
        try {
            if (!ImageIO.write(jpgImage, "JPG", jpgOutputStream)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return jpgOutputStream.toByteArray();
    }

    /**
     * Create a full image for bottom row sixel rendering.
     *
     * @param cellsImageRGB the cells image
     * @param maxPixelX the maximum X pixel
     * @param maxPixelY the maximum Y pixel
     * @param pixelX the pixel X position
     * @param pixelY the pixel Y position
     * @return the full ImageRGB
     */
    public static ImageRGB createBottomRowImage(final ImageRGB cellsImageRGB,
        final int maxPixelX, final int maxPixelY,
        final int pixelX, final int pixelY) {
        
        BufferedImage cellsImage = toBufferedImage(cellsImageRGB);
        BufferedImage fullImage = new BufferedImage(maxPixelX, maxPixelY,
            BufferedImage.TYPE_INT_ARGB);
        Graphics gr = fullImage.getGraphics();
        gr.drawImage(cellsImage, pixelX, pixelY, null);
        gr.dispose();
        return toImageRGB(fullImage);
    }

    /**
     * Create a full BufferedImage for bottom row sixel rendering.
     * This variant accepts and returns BufferedImage directly.
     *
     * @param cellsImage the cells image as BufferedImage
     * @param pixelX the pixel X position
     * @param pixelY the pixel Y position
     * @param maxPixelX the maximum X pixel
     * @param maxPixelY the maximum Y pixel
     * @return the full BufferedImage
     */
    public static BufferedImage createBottomRowSixelImage(final Object cellsImage,
        final int pixelX, final int pixelY, final int maxPixelX, final int maxPixelY) {
        
        if (cellsImage == null || !(cellsImage instanceof BufferedImage)) {
            return null;
        }
        BufferedImage srcImage = (BufferedImage) cellsImage;
        BufferedImage fullImage = new BufferedImage(maxPixelX, maxPixelY,
            BufferedImage.TYPE_INT_ARGB);
        Graphics gr = fullImage.getGraphics();
        gr.drawImage(srcImage, pixelX, pixelY, null);
        gr.dispose();
        return fullImage;
    }

    /**
     * Get a subimage of an ImageRGB.
     *
     * @param source the source ImageRGB
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @return the subimage as ImageRGB
     */
    public static ImageRGB getSubImage(final ImageRGB source,
        final int x, final int y, final int width, final int height) {
        
        if (source == null) {
            return null;
        }
        
        BufferedImage image = toBufferedImage(source);
        BufferedImage subImage = image.getSubimage(x, y, width, height);
        return toImageRGB(subImage);
    }

    /**
     * Encode a subimage as PNG bytes.
     *
     * @param imageRGB the ImageRGB
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @return the PNG bytes, or null on failure
     */
    public static byte[] encodeSubImagePNG(final ImageRGB imageRGB,
        final int x, final int y, final int width, final int height) {
        
        if (imageRGB == null) {
            return null;
        }
        
        BufferedImage image = toBufferedImage(imageRGB);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream(1024);
        try {
            if (!ImageIO.write(image.getSubimage(x, y, width, height),
                    "PNG", pngOutputStream)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return pngOutputStream.toByteArray();
    }

    /**
     * Encode a subimage as JPG bytes.
     *
     * @param imageRGB the ImageRGB
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @return the JPG bytes, or null on failure
     */
    public static byte[] encodeSubImageJPG(final ImageRGB imageRGB,
        final int x, final int y, final int width, final int height) {
        
        if (imageRGB == null) {
            return null;
        }
        
        BufferedImage image = toBufferedImage(imageRGB);
        BufferedImage subImage = image.getSubimage(x, y, width, height);
        
        // Convert from ARGB to RGB, otherwise the JPG encode will fail.
        BufferedImage jpgImage = new BufferedImage(subImage.getWidth(),
            subImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[subImage.getWidth() * subImage.getHeight()];
        subImage.getRGB(0, 0, subImage.getWidth(), subImage.getHeight(), pixels,
            0, subImage.getWidth());
        jpgImage.setRGB(0, 0, subImage.getWidth(), subImage.getHeight(), pixels,
            0, subImage.getWidth());

        ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream(1024);
        try {
            if (!ImageIO.write(jpgImage, "JPG", jpgOutputStream)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return jpgOutputStream.toByteArray();
    }

    /**
     * Get image dimensions from a BufferedImage.
     *
     * @param image the BufferedImage as Object
     * @return int array [width, height], or null if invalid
     */
    public static int[] getImageDimensions(final Object image) {
        if (image == null || !(image instanceof BufferedImage)) {
            return null;
        }
        BufferedImage bi = (BufferedImage) image;
        return new int[] { bi.getWidth(), bi.getHeight() };
    }

    /**
     * Encode a BufferedImage as PNG bytes.
     *
     * @param image the BufferedImage as Object
     * @param maxHeight the maximum height to encode (use full height if negative)
     * @return the PNG bytes, or null on failure
     */
    public static byte[] encodeBufferedImagePNG(final Object image, final int maxHeight) {
        if (image == null || !(image instanceof BufferedImage)) {
            return null;
        }
        BufferedImage bi = (BufferedImage) image;
        int heightToEncode = maxHeight > 0 ? Math.min(bi.getHeight(), maxHeight) : bi.getHeight();
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream(1024);
        try {
            if (!ImageIO.write(bi.getSubimage(0, 0, bi.getWidth(), heightToEncode),
                    "PNG", pngOutputStream)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return pngOutputStream.toByteArray();
    }

    /**
     * Encode a BufferedImage as JPG bytes.
     *
     * @param image the BufferedImage as Object
     * @param maxHeight the maximum height to encode (use full height if negative)
     * @return the JPG bytes, or null on failure
     */
    public static byte[] encodeBufferedImageJPG(final Object image, final int maxHeight) {
        if (image == null || !(image instanceof BufferedImage)) {
            return null;
        }
        BufferedImage bi = (BufferedImage) image;
        int heightToEncode = maxHeight > 0 ? Math.min(bi.getHeight(), maxHeight) : bi.getHeight();
        
        // Convert from ARGB to RGB, otherwise the JPG encode will fail.
        BufferedImage jpgImage = new BufferedImage(bi.getWidth(),
            heightToEncode, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[bi.getWidth() * heightToEncode];
        bi.getRGB(0, 0, bi.getWidth(), heightToEncode, pixels, 0, bi.getWidth());
        jpgImage.setRGB(0, 0, bi.getWidth(), heightToEncode, pixels, 0, bi.getWidth());

        ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream(1024);
        try {
            if (!ImageIO.write(jpgImage, "JPG", jpgOutputStream)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return jpgOutputStream.toByteArray();
    }

    /**
     * Get RGB bytes from a BufferedImage for the Jexer RGB format.
     *
     * @param image the BufferedImage as Object
     * @param maxHeight the maximum height to encode (use full height if negative)
     * @return the RGB bytes, or null on failure
     */
    public static byte[] encodeBufferedImageRGB(final Object image, final int maxHeight) {
        if (image == null || !(image instanceof BufferedImage)) {
            return null;
        }
        BufferedImage bi = (BufferedImage) image;
        int heightToEncode = maxHeight > 0 ? Math.min(bi.getHeight(), maxHeight) : bi.getHeight();
        
        byte[] bytes = new byte[bi.getWidth() * heightToEncode * 3];
        int stride = bi.getWidth();
        for (int px = 0; px < stride; px++) {
            for (int py = 0; py < heightToEncode; py++) {
                int rgb = bi.getRGB(px, py);
                bytes[(py * stride * 3) + (px * 3)]     = (byte) ((rgb >>> 16) & 0xFF);
                bytes[(py * stride * 3) + (px * 3) + 1] = (byte) ((rgb >>>  8) & 0xFF);
                bytes[(py * stride * 3) + (px * 3) + 2] = (byte) ( rgb         & 0xFF);
            }
        }
        return bytes;
    }

}
