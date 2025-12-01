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

import java.awt.image.BufferedImage;
import java.util.HashMap;

import jexer.backend.Backend;

/**
 * GlyphMaker presents unified interface to all of its supported fonts to
 * clients. This base class provides a stub implementation that returns null
 * for images. The actual AWT-based implementation is in GlyphMakerImpl which
 * is loaded via reflection if available (requires java.desktop module).
 */
public class GlyphMaker {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Cache of font bundles by size.
     */
    private static HashMap<Integer, GlyphMaker> makers = new HashMap<Integer, GlyphMaker>();

    /**
     * If true, we have already tried to load GlyphMakerImpl.
     */
    private static boolean triedImpl = false;

    /**
     * If true, GlyphMakerImpl is available.
     */
    private static boolean implAvailable = false;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Protected constructor for subclasses.
     */
    protected GlyphMaker() {
        // NOP
    }

    // ------------------------------------------------------------------------
    // GlyphMaker -------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Obtain the GlyphMaker instance for a particular font size. This will
     * attempt to load GlyphMakerImpl (AWT-based) via reflection. If not
     * available, returns a stub implementation that returns null for images.
     *
     * @param fontSize the size of these fonts in pixels
     * @return the instance
     */
    public static GlyphMaker getInstance(final int fontSize) {
        synchronized (GlyphMaker.class) {
            GlyphMaker maker = makers.get(fontSize);
            if (maker == null) {
                // Try to load GlyphMakerImpl via reflection
                if (!triedImpl) {
                    triedImpl = true;
                    try {
                        Class.forName("jexer.backend.GlyphMakerImpl");
                        implAvailable = true;
                    } catch (ClassNotFoundException e) {
                        implAvailable = false;
                    }
                }

                if (implAvailable) {
                    try {
                        maker = (GlyphMaker) Class.forName("jexer.backend.GlyphMakerImpl")
                            .getMethod("getInstance", int.class)
                            .invoke(null, fontSize);
                    } catch (Exception e) {
                        // Fall back to stub
                        maker = new GlyphMaker();
                    }
                } else {
                    // Use stub implementation
                    maker = new GlyphMaker();
                }
                makers.put(fontSize, maker);
            }
            return maker;
        }
    }

    /**
     * Get a glyph image.
     *
     * @param cell the character to draw
     * @param cellWidth the width of the text cell to draw into
     * @param cellHeight the height of the text cell to draw into
     * @param backend the backend that can obtain the correct background
     * color
     * @return the glyph as an image, or null if not available
     */
    public BufferedImage getImage(final Cell cell, final int cellWidth,
        final int cellHeight, final Backend backend) {

        return getImage(cell, cellWidth, cellHeight, backend, true);
    }

    /**
     * Get a glyph image.
     *
     * @param cell the character to draw
     * @param cellWidth the width of the text cell to draw into
     * @param cellHeight the height of the text cell to draw into
     * @param backend the backend that can obtain the correct background
     * color
     * @param blinkVisible if true, the cell is visible if it is blinking
     * @return the glyph as an image, or null if not available
     */
    public BufferedImage getImage(final Cell cell, final int cellWidth,
        final int cellHeight, final Backend backend,
        final boolean blinkVisible) {

        // Stub implementation returns null
        return null;
    }

    /**
     * Check if a CJK font is available.
     *
     * @return true if a CJK font is available
     */
    public boolean isCjk() {
        return false;
    }

    /**
     * Check if an emoji font is available.
     *
     * @return true if an emoji font is available
     */
    public boolean isEmoji() {
        return false;
    }

    /**
     * Check if a fallback font is available.
     *
     * @return true if a fallback font is available
     */
    public boolean isFallback() {
        return false;
    }

    /**
     * Checks if a fallback font has a glyph for the specified character.
     *
     * @param codePoint the character (Unicode code point) for which a glyph
     * is needed.
     * @return true if this Font has a glyph for the character; false
     * otherwise.
     */
    public boolean canDisplay(final int codePoint) {
        return false;
    }

}
