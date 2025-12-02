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

import java.awt.Font;
import java.lang.reflect.Method;

import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;
import jexer.bits.StringUtils;

/**
 * Text is a raw bitmap image of rendered text.
 * If java.awt is not available, this class does nothing.
 */
public class Text extends Bitmap {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The text.
     */
    private String text;

    /**
     * The font name (may be null if font object is used).
     */
    private String fontName;

    /**
     * The font object (may be null if fontName is used).
     */
    private Font font;

    /**
     * The font size in points.
     */
    private int fontSize;

    /**
     * The color.
     */
    private ColorRGB color;

    /**
     * The implementation class.
     */
    private static Class<?> implClass;

    /**
     * Static initializer to load implementation.
     */
    static {
        try {
            implClass = Class.forName("jexer.backend.TextImpl");
        } catch (ClassNotFoundException e) {
            implClass = null;
        }
    }

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @param z Z coordinate
     * @param text the text string
     * @param fontName the font name
     * @param fontSize the font size in points
     * @param color the color of the text
     */
    public Text(final int x, final int y, final int z,
        final String text, final String fontName, final int fontSize,
        final ColorRGB color) {

        super(x, y, z, (ImageRGB) null);

        this.text = text;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.color = color;
    }

    /**
     * Public constructor with Font object.
     *
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @param z Z coordinate
     * @param text the text string
     * @param font the font
     * @param fontSize the font size in points
     * @param color the color of the text
     */
    public Text(final int x, final int y, final int z,
        final String text, final Font font, final int fontSize,
        final ColorRGB color) {

        super(x, y, z, (ImageRGB) null);

        this.text = text;
        this.font = font;
        this.fontName = (font != null) ? font.getFamily() : null;
        this.fontSize = fontSize;
        this.color = color;
    }

    // ------------------------------------------------------------------------
    // TackboardItem ----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another Text instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof Text)) {
            return false;
        }
        Text that = (Text) rhs;
        return (super.equals(rhs)
            && (this.text.equals(that.text))
            && (this.fontSize == that.fontSize)
            && (this.color.equals(that.color)));
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
        hash = (B * hash) + text.hashCode();
        hash = (B * hash) + fontSize;
        hash = (B * hash) + color.hashCode();
        return hash;
    }

    /**
     * Make human-readable description of this item.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        return String.format("(%d, %d, %d) '%s'", getX(), getY(), getZ(),
            text);
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
        if (dirty) {
            if (implClass != null) {
                try {
                    if (font != null) {
                        Method renderMethod = implClass.getMethod("renderTextWithFont",
                            String.class, Font.class, int.class, ColorRGB.class,
                            int.class);
                        ImageRGB renderedImage = (ImageRGB) renderMethod.invoke(null,
                            text, font, fontSize, color, textWidth);
                        setImage(renderedImage);
                    } else {
                        Method renderMethod = implClass.getMethod("renderText",
                            String.class, String.class, int.class, ColorRGB.class,
                            int.class);
                        ImageRGB renderedImage = (ImageRGB) renderMethod.invoke(null,
                            text, fontName, fontSize, color, textWidth);
                        setImage(renderedImage);
                    }
                } catch (Exception e) {
                    // If reflection fails, just don't render
                }
            }
        }
        return super.getImage(textWidth, textHeight);
    }

    // ------------------------------------------------------------------------
    // Text -------------------------------------------------------------------
    // ------------------------------------------------------------------------

}
