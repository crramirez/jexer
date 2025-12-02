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

import jexer.backend.Backend;

/**
 * ColorEmojiGlyphMaker provides access to the color emoji image files
 * located in "emojis/" on the classpath.  This is a stub class that
 * delegates to ColorEmojiGlyphMakerImpl via reflection when the
 * java-desktop JAR is available.
 */
public class ColorEmojiGlyphMaker {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Whether the implementation is available.
     */
    private static boolean implAvailable = true;

    /**
     * The implementation class.
     */
    private static Class<?> implClass;

    static {
        try {
            implClass = Class.forName("jexer.backend.ColorEmojiGlyphMakerImpl");
        } catch (ClassNotFoundException e) {
            implAvailable = false;
        }
    }

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Private constructor prevents accidental creation of this class.
     */
    private ColorEmojiGlyphMaker() {}

    // ------------------------------------------------------------------------
    // ColorEmojiGlyphMaker ---------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Make the lookup key string for a sequence of codepoints.
     *
     * @param codePoints the emoji (sequence of Unicode codepoints)
     * @return the lookup key ("0023-FE0F-20E3.png", "1F1E8-1F1E7.png", etc.)
     */
    private static String makeKey(final int [] codePoints) {
        StringBuilder sb = new StringBuilder();
        sb.append("emoji/");
        sb.append(String.format("%04x", codePoints[0]).toUpperCase());
        if ((codePoints.length == 2) && (codePoints[1] == 0xFE0F)) {
            sb.append(".png");
            return sb.toString();
        }
        for (int i = 1; i < codePoints.length; i++) {
            sb.append("-");
            sb.append(String.format("%04x", codePoints[i]).toUpperCase());
        }
        sb.append(".png");
        return sb.toString();
    }

    /**
     * Checks if an emoji glyph for the specified codepoint is available.
     *
     * @param ch the emoji (single Unicode codepoint) for which a glyph is
     * needed.
     * @return true if a glyph for the character is available; false
     * otherwise.
     */
    public static boolean canDisplay(final int ch) {
        int [] codePoints = new int[1];
        codePoints[0] = ch;
        return canDisplay(codePoints);
    }

    /**
     * Checks if an emoji glyph for the specified codepoint(s) is available.
     *
     * @param codePoints the emoji (sequence of Unicode codepoints) for which
     * a glyph is needed.
     * @return true if a glyph for the character is available; false
     * otherwise.
     */
    public static boolean canDisplay(final int [] codePoints) {
        String key = makeKey(codePoints);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        java.net.URL url = loader.getResource(key);
        if (url == null) {
            return false;
        }
        return true;
    }

    /**
     * Get an emoji image for a complex cell.
     *
     * @param complexCell the emoji to draw
     * @param cellWidth the width of the text cell to draw into
     * @param cellHeight the height of the text cell to draw into
     * @param backend the backend that can obtain the correct background
     * color
     * @param blinkVisible if true, the cell is visible if it is blinking
     * @return the glyph as an image, or null if java-desktop not available
     */
    public static ImageRGB getImage(final ComplexCell complexCell,
        final int cellWidth, final int cellHeight, final Backend backend,
        final boolean blinkVisible) {

        if (!implAvailable) {
            return null;
        }

        try {
            return (ImageRGB) implClass.getMethod("getImage",
                ComplexCell.class, int.class, int.class, Backend.class,
                boolean.class).invoke(null, complexCell, cellWidth, cellHeight,
                backend, blinkVisible);
        } catch (Exception e) {
            return null;
        }
    }

}
