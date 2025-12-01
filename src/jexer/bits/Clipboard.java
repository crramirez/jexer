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

/**
 * Clipboard provides convenience methods to copy text and images to and from
 * a shared clipboard. This base class provides a local-only clipboard
 * implementation. The system clipboard integration is available via
 * ClipboardImpl which is loaded via reflection if available (requires
 * java.desktop module).
 */
public class Clipboard {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The image last copied to the clipboard.
     */
    private BufferedImage image = null;

    /**
     * The text string last copied to the clipboard.
     */
    private String text = null;

    /**
     * If true, we have already tried to load ClipboardImpl.
     */
    private static boolean triedImpl = false;

    /**
     * If true, ClipboardImpl is available.
     */
    private static boolean implAvailable = false;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     */
    public Clipboard() {
        // NOP - local-only clipboard
    }

    // ------------------------------------------------------------------------
    // Factory ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Create a new Clipboard instance. This will attempt to load ClipboardImpl
     * (AWT-based system clipboard) via reflection. If not available, returns
     * a local-only clipboard implementation.
     *
     * @return a Clipboard instance
     */
    public static Clipboard getClipboard() {
        // Try to load ClipboardImpl via reflection
        if (!triedImpl) {
            triedImpl = true;
            try {
                Class.forName("jexer.backend.ClipboardImpl");
                implAvailable = true;
            } catch (ClassNotFoundException e) {
                implAvailable = false;
            }
        }

        if (implAvailable) {
            try {
                return (Clipboard) Class.forName("jexer.backend.ClipboardImpl")
                    .getDeclaredConstructor()
                    .newInstance();
            } catch (Exception e) {
                // Fall back to local clipboard
                return new Clipboard();
            }
        } else {
            // Use local-only implementation
            return new Clipboard();
        }
    }

    // ------------------------------------------------------------------------
    // Clipboard --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Copy an image to the clipboard.
     *
     * @param image image to copy
     */
    public void copyImage(final BufferedImage image) {
        this.image = image;
    }

    /**
     * Copy a text string to the clipboard.
     *
     * @param text string to copy
     */
    public void copyText(final String text) {
        this.text = text;
    }

    /**
     * Obtain an image from the clipboard.
     *
     * @return image from the clipboard, or null if no image is available
     */
    public BufferedImage pasteImage() {
        return image;
    }

    /**
     * Obtain a text string from the clipboard.
     *
     * @return text string from the clipboard, or null if no text is
     * available
     */
    public String pasteText() {
        return text;
    }

    /**
     * Returns true if the clipboard has an image.
     *
     * @return true if an image is available from the clipboard
     */
    public boolean isImage() {
        return (image != null);
    }

    /**
     * Returns true if the clipboard has a text string.
     *
     * @return true if a text string is available from the clipboard
     */
    public boolean isText() {
        return (text != null);
    }

    /**
     * Returns true if the clipboard is empty.
     *
     * @return true if the clipboard is empty
     */
    public boolean isEmpty() {
        return ((isText() == false) && (isImage() == false));
    }

    /**
     * Clear whatever is on the local clipboard. Note that this will not
     * clear the system clipboard.
     */
    public void clear() {
        image = null;
        text = null;
    }

    // ------------------------------------------------------------------------
    // Protected methods for subclasses ---------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the local image.
     *
     * @return the local image
     */
    protected BufferedImage getLocalImage() {
        return image;
    }

    /**
     * Set the local image.
     *
     * @param image the image to set
     */
    protected void setLocalImage(final BufferedImage image) {
        this.image = image;
    }

    /**
     * Get the local text.
     *
     * @return the local text
     */
    protected String getLocalText() {
        return text;
    }

    /**
     * Set the local text.
     *
     * @param text the text to set
     */
    protected void setLocalText(final String text) {
        this.text = text;
    }

}
