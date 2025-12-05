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

/**
 * A simple rectangle class that replaces java.awt.Rectangle to avoid
 * java.desktop dependencies.
 */
public class Rectangle {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The X coordinate.
     */
    private int x;

    /**
     * The Y coordinate.
     */
    private int y;

    /**
     * The width.
     */
    private int width;

    /**
     * The height.
     */
    private int height;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param width the width
     * @param height the height
     */
    public Rectangle(final int x, final int y, final int width,
        final int height) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // ------------------------------------------------------------------------
    // Rectangle --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the X coordinate.
     *
     * @return the X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y coordinate.
     *
     * @return the Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Get the width.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the X coordinate.
     *
     * @param x the new X coordinate
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Set the Y coordinate.
     *
     * @param y the new Y coordinate
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Set the width.
     *
     * @param width the new width
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Set the height.
     *
     * @param height the new height
     */
    public void setHeight(final int height) {
        this.height = height;
    }

}
