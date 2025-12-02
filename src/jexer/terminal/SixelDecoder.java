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
package jexer.terminal;

import java.util.HashMap;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;

/**
 * SixelDecoder parses a buffer of sixel image data into an ImageRGB.
 * This is a stub class that delegates to SixelDecoderImpl via reflection
 * when the java-desktop JAR is available.
 */
public class SixelDecoder {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The implementation instance, loaded via reflection.
     */
    private Object impl;

    /**
     * Whether the implementation is available.
     */
    private static boolean implAvailable = true;

    /**
     * The implementation class.
     */
    private static Class<?> implClass;

    /**
     * Whether the image might have transparent pixels.
     */
    private boolean transparent = false;

    static {
        try {
            implClass = Class.forName("jexer.backend.SixelDecoderImpl");
        } catch (ClassNotFoundException e) {
            implAvailable = false;
        }
    }

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param buffer the sixel data to parse
     * @param palette palette to use, or null for a private palette
     * @param background the background color to use
     * @param maybeTransparent if true, transparency in the image will be
     * honored
     */
    public SixelDecoder(final String buffer,
        final HashMap<Integer, ColorRGB> palette, final ColorRGB background,
        final boolean maybeTransparent) {

        if (implAvailable) {
            try {
                impl = implClass.getConstructor(String.class, HashMap.class,
                    ColorRGB.class, boolean.class).newInstance(buffer, palette,
                    background, maybeTransparent);
            } catch (Exception e) {
                implAvailable = false;
            }
        }
    }

    // ------------------------------------------------------------------------
    // SixelDecoder -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * If true, this image might have transparent pixels.
     *
     * @return whether this image might have transparent pixels
     */
    public boolean isTransparent() {
        if (impl != null) {
            try {
                return (Boolean) implClass.getMethod("isTransparent").invoke(impl);
            } catch (Exception e) {
                // Fall through
            }
        }
        return transparent;
    }

    /**
     * Get the image.
     *
     * @return the sixel data as an image, or null if java-desktop not available
     */
    public ImageRGB getImage() {
        if (impl != null) {
            try {
                return (ImageRGB) implClass.getMethod("getImage").invoke(impl);
            } catch (Exception e) {
                // Fall through
            }
        }
        return null;
    }

}
