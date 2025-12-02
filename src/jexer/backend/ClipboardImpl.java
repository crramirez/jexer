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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import jexer.bits.Clipboard;
import jexer.bits.ImageRGB;

/**
 * ClipboardImpl provides AWT-based clipboard access that integrates with
 * the system clipboard. This is the java.desktop-based implementation that
 * extends the base Clipboard class.
 */
public class ClipboardImpl extends Clipboard {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The system clipboard, or null if it is not available.
     */
    private java.awt.datatransfer.Clipboard systemClipboard = null;

    /**
     * Convert ImageRGB to BufferedImage.
     *
     * @param imageRGB the ImageRGB to convert
     * @return the BufferedImage
     */
    private static BufferedImage toBufferedImage(final ImageRGB imageRGB) {
        if (imageRGB == null) {
            return null;
        }
        BufferedImage image = new BufferedImage(imageRGB.getWidth(),
            imageRGB.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int[] pixels = imageRGB.getPixels();
        image.setRGB(0, 0, imageRGB.getWidth(), imageRGB.getHeight(),
            pixels, 0, imageRGB.getWidth());
        return image;
    }

    /**
     * Convert BufferedImage to ImageRGB.
     *
     * @param image the BufferedImage to convert
     * @return the ImageRGB
     */
    private static ImageRGB toImageRGB(final BufferedImage image) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        return new ImageRGB(width, height, pixels);
    }

    /**
     * ImageSelection is used to hold an image while on the clipboard.
     */
    private class ImageSelection implements Transferable {

        /**
         * Returns an array of DataFlavor objects indicating the flavors the
         * data can be provided in. The array should be ordered according to
         * preference for providing the data (from most richly descriptive to
         * least descriptive).
         *
         * @return an array of data flavors in which this data can be
         * transferred
         */
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        /**
         * Returns whether or not the specified data flavor is supported for
         * this object.
         *
         * @param flavor the requested flavor for the data
         * @return boolean indicating whether or not the data flavor is
         * supported
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        /**
         * Returns an object which represents the data to be transferred. The
         * class of the object returned is defined by the representation
         * class of the flavor.
         *
         * @param flavor the requested flavor for the data
         * @throws IOException if the data is no longer available in the
         * requested flavor.
         * @throws UnsupportedFlavorException if the requested data flavor is
         * not supported.
         */
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {

            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return toBufferedImage(getLocalImage());
        }
    }

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     */
    public ClipboardImpl() {
        try {
            systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (java.awt.HeadlessException e) {
            // SQUASH
        }
    }

    // ------------------------------------------------------------------------
    // ClipboardImpl ----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Copy an image to the clipboard.
     *
     * @param image image to copy
     */
    @Override
    public void copyImage(final ImageRGB image) {
        super.copyImage(image);
        if (systemClipboard != null) {
            ImageSelection imageSelection = new ImageSelection();
            systemClipboard.setContents(imageSelection, null);
        }
    }

    /**
     * Copy a text string to the clipboard.
     *
     * @param text string to copy
     */
    @Override
    public void copyText(final String text) {
        super.copyText(text);
        if (systemClipboard != null) {
            StringSelection stringSelection = new StringSelection(text);
            systemClipboard.setContents(stringSelection, null);
        }
    }

    /**
     * Obtain an image from the clipboard.
     *
     * @return image from the clipboard, or null if no image is available
     */
    @Override
    public ImageRGB pasteImage() {
        if (systemClipboard != null) {
            getClipboardImage();
        }
        return super.pasteImage();
    }

    /**
     * Obtain a text string from the clipboard.
     *
     * @return text string from the clipboard, or null if no text is
     * available
     */
    @Override
    public String pasteText() {
        if (systemClipboard != null) {
            getClipboardText();
        }
        return super.pasteText();
    }

    /**
     * Returns true if the clipboard has an image.
     *
     * @return true if an image is available from the clipboard
     */
    @Override
    public boolean isImage() {
        if (getLocalImage() == null) {
            getClipboardImage();
        }
        return (getLocalImage() != null);
    }

    /**
     * Returns true if the clipboard has a text string.
     *
     * @return true if a text string is available from the clipboard
     */
    @Override
    public boolean isText() {
        if (getLocalText() == null) {
            getClipboardText();
        }
        return (getLocalText() != null);
    }

    /**
     * Copy image from the clipboard to local storage.
     */
    private void getClipboardImage() {
        if (systemClipboard != null) {
            Transferable contents = systemClipboard.getContents(null);
            if (contents != null) {
                if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    try {
                        Image img = (Image) contents.getTransferData(DataFlavor.imageFlavor);
                        BufferedImage image = new BufferedImage(img.getWidth(null),
                            img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        java.awt.Graphics gr = image.getGraphics();
                        gr.drawImage(img, 0, 0, null);
                        gr.dispose();
                        setLocalImage(toImageRGB(image));
                    } catch (IOException e) {
                        // SQUASH
                    } catch (UnsupportedFlavorException e) {
                        // SQUASH
                    }
                }
            }
        }
    }

    /**
     * Copy text string from the clipboard to local storage.
     */
    private void getClipboardText() {
        if (systemClipboard != null) {
            Transferable contents = systemClipboard.getContents(null);
            if (contents != null) {
                if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                        setLocalText(text);
                    } catch (IOException e) {
                        // SQUASH
                    } catch (UnsupportedFlavorException e) {
                        // SQUASH
                    }
                }
            }
        }
    }

}
