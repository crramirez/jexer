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
package jexer.backend;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

import jexer.bits.Cell;
import jexer.bits.UnicodeGlyphImage;

/**
 * UnicodeGlyphEncoder turns a BufferedImage into single character from the
 * Unicode block-drawing elements ("Symbols For Legacy Computing").
 */
public class UnicodeGlyphEncoder {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Verbosity level for analysis mode.
     */
    private int verbosity = 0;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     */
    @SuppressWarnings("this-escape")
    public UnicodeGlyphEncoder() {
        reloadOptions();
    }

    // ------------------------------------------------------------------------
    // UnicodeGlyphEncoder ---------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Reload options from System properties.
     */
    public void reloadOptions() {
        // NOP
    }

    /**
     * Create a T.416 RGB parameter sequence for a single RGB color.
     *
     * @param sb StringBuilder to append result to
     * @param colorRGB a 24-bit RGB value for foreground color
     * @param foreground if true, this is a foreground color
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[42m"
     */
    private void colorRGB(final StringBuilder sb,
        final int colorRGB, final boolean foreground) {

        int colorRed     = (colorRGB >>> 16) & 0xFF;
        int colorGreen   = (colorRGB >>>  8) & 0xFF;
        int colorBlue    =  colorRGB         & 0xFF;

        if (foreground) {
            sb.append("\033[38;2;");
        } else {
            sb.append("\033[48;2;");
        }
        sb.append(String.format("%d;%d;%dm", colorRed, colorGreen, colorBlue));
    }

    /**
     * Create a string representing a bitmap.
     *
     * @param bitmap the bitmap data
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String toUnicodeGlyph(final BufferedImage bitmap) {
        StringBuilder sb = new StringBuilder(24);

        assert (bitmap != null);

        Cell cell = (new UnicodeGlyphImage(bitmap)).toHalfBlockGlyph();
        colorRGB(sb, cell.getBackColorRGB(), false);
        colorRGB(sb, cell.getForeColorRGB(), true);
        sb.append(Character.toChars(cell.getChar()));
        return sb.toString();
    }

    /**
     * Convert all filenames to sixel.
     *
     * @param args[] the filenames to read
     */
    public static void main(final String [] args) {
        if ((args.length == 0)
            || ((args.length == 1) && args[0].equals("-v"))
            || ((args.length == 1) && args[0].equals("-vv"))
        ) {
            System.err.println("USAGE: java jexer.backend.UnicodeGlyphEncoder [  -v | -vv ] { file1 [ file2 ... ] }");
            System.exit(-1);
        }

        UnicodeGlyphEncoder encoder = new UnicodeGlyphEncoder();
        int successCount = 0;

        for (int i = 0; i < args.length; i++) {
            if ((i == 0) && args[i].equals("-v")) {
                encoder.verbosity = 1;
                continue;
            }
            if ((i == 0) && args[i].equals("-vv")) {
                encoder.verbosity = 10;
                continue;
            }

            try {
                BufferedImage image = ImageIO.read(new FileInputStream(args[i]));
                int count = 1;
                for (int j = 0; j < count; j++) {
                    // Emit the image.
                    System.out.println(encoder.toUnicodeGlyph(image));
                } // for (int j = 0; j < count; j++)
            } catch (Exception e) {
                System.err.println("Error reading file:");
                e.printStackTrace();
            }

        } // for (int i = 0; i < args.length; i++)

        if (successCount == args.length) {
            System.exit(0);
        } else {
            System.exit(successCount);
        }
    }

}
