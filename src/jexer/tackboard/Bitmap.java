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
package jexer.tackboard;

import java.lang.reflect.Method;

import jexer.TApplication;
import jexer.bits.Animation;
import jexer.bits.ImageRGB;

/**
 * Bitmap is a raw bitmap image.
 * If java.awt is not available, this class does nothing.
 */
public class Bitmap extends TackboardItem {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The image data.
     */
    private ImageRGB image;

    /**
     * The rendered image data.
     */
    private ImageRGB renderedImage;

    /**
     * Animation to display.
     */
    private Animation animation;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @param z Z coordinate
     * @param image the image
     */
    public Bitmap(final int x, final int y, final int z,
        final ImageRGB image) {

        super(x, y, z);
        this.image = image;
    }

    /**
     * Public constructor with BufferedImage (passed as Object to avoid
     * java.awt dependency). The image will be converted to ImageRGB via
     * reflection.
     *
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @param z Z coordinate
     * @param bufferedImage the image (BufferedImage as Object)
     */
    public Bitmap(final int x, final int y, final int z,
        final Object bufferedImage) {

        super(x, y, z);
        this.image = toImageRGB(bufferedImage);
    }

    /**
     * Public constructor.  Due to the use of a timer, the Bitmap needs to
     * see TApplication to start the Animation.
     *
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @param z Z coordinate
     * @param animation the animation to display
     * @param application the application to set the animation timer on
     */
    public Bitmap(final int x, final int y, final int z,
        final Animation animation, final TApplication application) {

        super(x, y, z);
        this.animation = animation;
        image = animation.getFrame();
        animation.start(application);
    }

    // ------------------------------------------------------------------------
    // Helper methods ---------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Convert BufferedImage (passed as Object) to ImageRGB via reflection.
     *
     * @param bufferedImage the BufferedImage as Object
     * @return the ImageRGB
     */
    private static ImageRGB toImageRGB(final Object bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        try {
            // Use reflection to call getWidth(), getHeight(), getRGB()
            Class<?> clazz = bufferedImage.getClass();
            Method getWidth = clazz.getMethod("getWidth");
            Method getHeight = clazz.getMethod("getHeight");
            Method getRGB = clazz.getMethod("getRGB", int.class, int.class,
                int.class, int.class, int[].class, int.class, int.class);
            
            int width = (Integer) getWidth.invoke(bufferedImage);
            int height = (Integer) getHeight.invoke(bufferedImage);
            int[] pixels = (int[]) getRGB.invoke(bufferedImage, 0, 0,
                width, height, null, 0, width);
            return new ImageRGB(width, height, pixels);
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // TackboardItem ----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another Bitmap instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof Bitmap)) {
            return false;
        }
        Bitmap that = (Bitmap) rhs;
        return (super.equals(rhs)
            && (this.image != null ? this.image.equals(that.image) : that.image == null));
    }

    /**
     * Hashcode uses all fields in equals().
     *
     * @return the hash
     */
    @Override
    public int hashCode() {
        int A = 13;
        int B = 23;
        int hash = A;
        hash = (B * hash) + super.hashCode();
        hash = (B * hash) + (image != null ? image.hashCode() : 0);
        return hash;
    }

    /**
     * Make human-readable description of this item.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        return String.format("(%d, %d, %d) %d X %d", getX(), getY(), getZ(),
            (image != null ? image.getWidth() : 0),
            (image != null ? image.getHeight() : 0));
    }

    /**
     * Get this item rendered to a bitmap, offset to align on a grid of
     * cells with pixel dimensions (textWidth, textHeight).
     *
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     * @return the image, or null if this item does not have any pixels to
     * show
     */
    @Override
    public ImageRGB getImage(final int textWidth, final int textHeight) {
        if (!implAvailable) {
            return null;
        }
        if (dirty) {
            render(textWidth, textHeight);
            dirty = false;
        }
        return renderedImage;
    }

    /**
     * Remove this item from its board.  Subclasses can use this for cleanup
     * also.
     */
    @Override
    public void remove() {
        super.remove();

        if (animation != null) {
            animation.stop();
        }
    }

    // ------------------------------------------------------------------------
    // Bitmap -----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get this item rendered to a bitmap, offset to align on a grid of
     * cells with pixel dimensions (textWidth, textHeight).
     *
     * @param textWidth the width of a text cell
     * @param textHeight the height of a text cell
     */
    private void render(final int textWidth, final int textHeight) {
        if (!implAvailable || image == null) {
            return;
        }
        if (animation != null) {
            ImageRGB newFrame = animation.getFrame();
            if (newFrame != image) {
                image = newFrame;
                renderedImage = null;
            }
        }
        if (image == null) {
            renderedImage = null;
            return;
        }
        int dx = getX() % textWidth;
        int dy = getY() % textHeight;
        if ((dx == 0) && (dy == 0)) {
            renderedImage = image;
            return;
        }

        int columns = (dx + image.getWidth()) / textWidth;
        if ((dx + image.getWidth()) % textWidth > 0) {
            columns++;
        }
        int rows = (dy + image.getHeight()) / textHeight;
        if ((dy + image.getHeight()) % textHeight > 0) {
            rows++;
        }

        renderedImage = new ImageRGB(columns * textWidth, rows * textHeight);
        renderedImage.drawImage(image, dx, dy);
    }

    /**
     * Get the image.
     *
     * @return the image
     */
    protected ImageRGB getRawImage() {
        return image;
    }

    /**
     * Set the image.
     *
     * @param image the new image
     */
    public void setImage(final ImageRGB image) {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
        this.image = image;
        setDirty();
    }

    /**
     * Set the image with BufferedImage (passed as Object to avoid java.awt
     * dependency).
     *
     * @param bufferedImage the new image (BufferedImage as Object)
     */
    public void setImage(final Object bufferedImage) {
        setImage(toImageRGB(bufferedImage));
    }

}
