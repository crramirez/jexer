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
 * ComplexCell represents a multi-codepoint glyph, as commonly used in color
 * emojis, accented characters, and more.
 */
public class ComplexCell extends Cell {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The codepoints at this cell.
     */
    private int [] codepoints = new int[1];

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor sets default values of the cell to blank.
     *
     * @see #isBlank()
     * @see #reset()
     */
    public ComplexCell() {
        this(' ');
    }

    /**
     * Public constructor sets a single codepoint.  Attributes are the same
     * as default.
     *
     * @param codepoint the codepoint to set to
     * @see #reset()
     */
    public ComplexCell(final int codepoint) {
        super(codepoint);
        codepoints[0] = codepoint;
    }

    /**
     * Public constructor sets multiple codepoints.  Attributes are the same
     * as default.
     *
     * @param codepoints the codepoints to set to
     * @see #reset()
     */
    public ComplexCell(final int [] codepoints) {
        super(codepoints[0]);
        this.codepoints = new int[codepoints.length];
        System.arraycopy(codepoints, 0, this.codepoints, 0, codepoints.length);
    }

    // ------------------------------------------------------------------------
    // Cell -------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Getter for cell character.
     *
     * @return cell character
     */
    @Override
    public int getChar() {
        return codepoints[0];
    }

    /**
     * Setter for cell character.
     *
     * @param ch new cell character
     */
    @Override
    public void setChar(final int ch) {
        codepoints = new int[1];
        codepoints[0] = ch;
    }

    /**
     * Reset this cell to a blank.
     */
    @Override
    public void reset() {
        super.reset();
        codepoints = new int[1];
        codepoints[0] = ' ';
    }

    /**
     * UNset this cell.  It will not be equal to any other cell until it has
     * been assigned attributes and a character.
     */
    @Override
    public void unset() {
        super.reset();
        codepoints = new int[1];
        codepoints[0] = super.getChar();
    }

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another Cell instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof ComplexCell)) {
            return false;
        }

        ComplexCell that = (ComplexCell) rhs;
        if (this.codepoints.length != that.codepoints.length) {
            return false;
        }
        for (int i = 0; i < codepoints.length; i++) {
            if (this.codepoints[i] != that.codepoints[i]) {
                return false;
            }
        }

        return super.equals(rhs);
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
        for (int i = 0; i < codepoints.length; i++) {
            hash = (B * hash) + codepoints[i];
        }
        return hash;
    }

    /**
     * Set my field values to that's field.
     *
     * @param rhs an instance of either Cell or CellAttributes
     */
    @Override
    public void setTo(final Object rhs) {
        if (rhs instanceof ComplexCell) {
            ComplexCell that = (ComplexCell) rhs;
            this.codepoints = new int[codepoints.length];
            System.arraycopy(codepoints, 0, this.codepoints, 0,
                codepoints.length);
        } else {
            this.codepoints = new int[1];
        }
        // Let this throw a ClassCastException
        Cell thatCell = (Cell) rhs;
        this.codepoints[0] = thatCell.getChar();
        super.setTo(thatCell);
    }


    /**
     * Make human-readable description of this Cell.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        // TODO - handle the complex cell
        return super.toString();
    }

    /**
     * Convert this cell into an HTML entity inside a &lt;font&gt; tag.
     *
     * @return the HTML string
     */
    @Override
    public String toHtml() {
        // TODO - handle the complex cell
        return super.toHtml();
    }

}
