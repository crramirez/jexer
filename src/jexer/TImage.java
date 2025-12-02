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
package jexer;

import java.lang.reflect.Method;

import jexer.bits.Animation;
import jexer.bits.Cell;
import jexer.bits.ImageRGB;
import jexer.bits.ImageUtils;
import jexer.bits.UnicodeGlyphImage;
import jexer.event.TCommandEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;

/**
 * TImage renders a piece of a bitmap image or an animated image on screen.
 * If java.awt is not available, this widget does nothing.
 */
public class TImage extends TWidget implements EditMenuUser {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Selections for fitting the image to the text cells.
     */
    public enum Scale {
        NONE,
        STRETCH,
        SCALE,
    }

    /**
     * Selections for approximating the image as text cells.
     */
    public enum DisplayMode {
        BITMAP,
        BLOCKS,
        UNICODE_HALVES,
        UNICODE_SEXTANTS,
        UNICODE_QUADRANTS,
        UNICODE_SIXDOT,
        UNICODE_SIXDOTSOLID,
    }

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Whether java.awt is available.
     */
    private static boolean implAvailable = false;

    /**
     * The implementation class, loaded via reflection.
     */
    private static Class<?> implClass = null;

    /**
     * The implementation object.
     */
    private Object impl = null;

    /**
     * Static initializer to load implementation.
     */
    static {
        try {
            implClass = Class.forName("jexer.backend.TImageImpl");
            implAvailable = true;
        } catch (ClassNotFoundException e) {
            implAvailable = false;
        }
    }

    /**
     * Scaling strategy to use.
     */
    private Scale scale = Scale.NONE;

    /**
     * Display mode to use.
     */
    private DisplayMode displayMode = DisplayMode.BITMAP;

    /**
     * Scaling strategy to use.
     */
    private jexer.bits.ColorRGB scaleBackColor = jexer.bits.ColorRGB.BLACK;

    /**
     * The action to perform when the user clicks on the image.
     */
    private TAction clickAction;

    /**
     * The original image from construction time.
     */
    private ImageRGB originalImage;

    /**
     * The current scaling factor for the image.
     */
    private double scaleFactor = 1.0;

    /**
     * The current clockwise rotation for the image.
     */
    private int clockwise = 0;

    /**
     * If true, this widget was resized and a new scaled image must be
     * produced.
     */
    private boolean resized = false;

    /**
     * Left column of the image.  0 is the left-most column.
     */
    private int left;

    /**
     * Top row of the image.  0 is the top-most row.
     */
    private int top;

    /**
     * The cells containing the broken up image pieces.
     */
    private Cell cells[][];

    /**
     * The number of rows in cells[].
     */
    private int cellRows;

    /**
     * The number of columns in cells[].
     */
    private int cellColumns;

    /**
     * Last text width value.
     */
    private int lastTextWidth = -1;

    /**
     * Last text height value.
     */
    private int lastTextHeight = -1;

    /**
     * Animation to display.
     */
    private Animation animation;

    /**
     * Anti-aliasing support.  It is normally off for performance.
     */
    private boolean antiAlias = false;

    /**
     * If false, the image is fully opaque.
     */
    private boolean maybeTransparent = true;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width number of text cells for width of the image
     * @param height number of text cells for height of the image
     * @param image the image to display
     * @param left left column of the image.  0 is the left-most column.
     * @param top top row of the image.  0 is the top-most row.
     */
    public TImage(final TWidget parent, final int x, final int y,
        final int width, final int height, final ImageRGB image,
        final int left, final int top) {

        this(parent, x, y, width, height, image, left, top, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width number of text cells for width of the image
     * @param height number of text cells for height of the image
     * @param image the image to display
     * @param left left column of the image.  0 is the left-most column.
     * @param top top row of the image.  0 is the top-most row.
     * @param clickAction function to call when mouse is pressed
     */
    @SuppressWarnings("this-escape")
    public TImage(final TWidget parent, final int x, final int y,
        final int width, final int height, final ImageRGB image,
        final int left, final int top, final TAction clickAction) {

        super(parent, x, y, width, height);

        setCursorVisible(false);
        this.originalImage = image;
        this.left = left;
        this.top = top;
        this.clickAction = clickAction;

        sizeToImage(true);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width number of text cells for width of the image
     * @param height number of text cells for height of the image
     * @param animation the animation to display
     * @param left left column of the image.  0 is the left-most column.
     * @param top top row of the image.  0 is the top-most row.
     */
    public TImage(final TWidget parent, final int x, final int y,
        final int width, final int height, final Animation animation,
        final int left, final int top) {

        this(parent, x, y, width, height, animation, left, top, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width number of text cells for width of the image
     * @param height number of text cells for height of the image
     * @param animation the animation to display
     * @param left left column of the image.  0 is the left-most column.
     * @param top top row of the image.  0 is the top-most row.
     * @param clickAction function to call when mouse is pressed
     */
    @SuppressWarnings("this-escape")
    public TImage(final TWidget parent, final int x, final int y,
        final int width, final int height, final Animation animation,
        final int left, final int top, final TAction clickAction) {

        super(parent, x, y, width, height);

        setCursorVisible(false);
        if (implAvailable) {
            animation.start(getApplication());
            this.animation = animation;
            this.originalImage = animation.getFrame();
        }
        this.left = left;
        this.top = top;
        this.clickAction = clickAction;

        sizeToImage(true);
    }

    // ------------------------------------------------------------------------
    // Event handlers ---------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Handle mouse press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (clickAction != null) {
            clickAction.DO(this);
            return;
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (!implAvailable) {
            super.onKeypress(keypress);
            return;
        }

        if (!keypress.getKey().isFnKey()) {
            if (keypress.getKey().getChar() == '+') {
                scaleFactor *= 1.25;
                sizeToImage(true);
                return;
            }
            if (keypress.getKey().getChar() == '-') {
                scaleFactor *= 0.80;
                sizeToImage(true);
                return;
            }
        }
        if (keypress.equals(kbAltUp)) {
            scaleFactor *= 1.25;
            sizeToImage(true);
            return;
        }
        if (keypress.equals(kbAltDown)) {
            scaleFactor *= 0.80;
            sizeToImage(true);
            return;
        }
        if (keypress.equals(kbAltRight)) {
            clockwise++;
            clockwise %= 4;
            sizeToImage(true);
            return;
        }
        if (keypress.equals(kbAltLeft)) {
            clockwise--;
            if (clockwise < 0) {
                clockwise = 3;
            }
            sizeToImage(true);
            return;
        }

        if (keypress.equals(kbShiftLeft)) {
            switch (scale) {
            case NONE:
                setScaleType(Scale.SCALE);
                return;
            case STRETCH:
                setScaleType(Scale.NONE);
                return;
            case SCALE:
                setScaleType(Scale.STRETCH);
                return;
            }
        }
        if (keypress.equals(kbShiftRight)) {
            switch (scale) {
            case NONE:
                setScaleType(Scale.STRETCH);
                return;
            case STRETCH:
                setScaleType(Scale.SCALE);
                return;
            case SCALE:
                setScaleType(Scale.NONE);
                return;
            }
        }
        if (keypress.equals(kbCtrlLeft)) {
            switch (displayMode) {
            case BITMAP:
                setDisplayMode(DisplayMode.UNICODE_SIXDOTSOLID);
                return;
            case BLOCKS:
                setDisplayMode(DisplayMode.BITMAP);
                return;
            case UNICODE_HALVES:
                setDisplayMode(DisplayMode.BLOCKS);
                return;
            case UNICODE_SEXTANTS:
                setDisplayMode(DisplayMode.UNICODE_HALVES);
                return;
            case UNICODE_QUADRANTS:
                setDisplayMode(DisplayMode.UNICODE_SEXTANTS);
                return;
            case UNICODE_SIXDOT:
                setDisplayMode(DisplayMode.UNICODE_QUADRANTS);
                return;
            case UNICODE_SIXDOTSOLID:
                setDisplayMode(DisplayMode.UNICODE_SIXDOT);
                return;
            }
        }
        if (keypress.equals(kbCtrlRight)) {
            switch (displayMode) {
            case BITMAP:
                setDisplayMode(DisplayMode.BLOCKS);
                return;
            case BLOCKS:
                setDisplayMode(DisplayMode.UNICODE_HALVES);
                return;
            case UNICODE_HALVES:
                setDisplayMode(DisplayMode.UNICODE_SEXTANTS);
                return;
            case UNICODE_SEXTANTS:
                setDisplayMode(DisplayMode.UNICODE_QUADRANTS);
                return;
            case UNICODE_QUADRANTS:
                setDisplayMode(DisplayMode.UNICODE_SIXDOT);
                return;
            case UNICODE_SIXDOT:
                setDisplayMode(DisplayMode.UNICODE_SIXDOTSOLID);
                return;
            case UNICODE_SIXDOTSOLID:
                setDisplayMode(DisplayMode.BITMAP);
                return;
            }
        }

        super.onKeypress(keypress);
    }

    /**
     * Handle resize events.
     *
     * @param event resize event
     */
    @Override
    public void onResize(final TResizeEvent event) {
        super.onResize(event);

        if (scale == Scale.NONE) {
            return;
        }
        resized = true;
    }

    /**
     * Handle posted command events.
     *
     * @param command command event
     */
    @Override
    public void onCommand(final TCommandEvent command) {
        if (command.equals(cmCopy)) {
            getClipboard().copyImage(originalImage);
            return;
        }
    }

    /**
     * Stop the animation on close.
     */
    @Override
    public void close() {
        if (animation != null) {
            animation.stop();
        }
    }

    // ------------------------------------------------------------------------
    // TWidget ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Draw the image.
     */
    @Override
    public void draw() {
        if (!implAvailable || originalImage == null) {
            return;
        }

        if (animation != null) {
            ImageRGB newFrame = animation.getFrame();
            if (newFrame != originalImage) {
                originalImage = newFrame;
                sizeToImage(true);
            } else {
                sizeToImage(false);
            }
        } else {
            sizeToImage(false);
        }

        if (cells == null) {
            return;
        }
        for (int x = 0; (x < getWidth()) && (x + left < cellColumns); x++) {
            for (int y = 0; (y < getHeight()) && (y + top < cellRows); y++) {
                if (cells[x + left][y + top] != null) {
                    putCharXY(x, y, cells[x + left][y + top]);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // TImage -----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Size cells[][] according to the screen font size.
     *
     * @param always if true, always resize the cells
     */
    private void sizeToImage(final boolean always) {
        if (!implAvailable || originalImage == null) {
            return;
        }

        if ((getApplication() == null)
            || (getApplication().getBackend() == null)
        ) {
            return;
        }

        scaleBackColor = getApplication().getBackend().attrToBackgroundColor(getWindow().getBackground());

        boolean bleedThrough = true;
        if (System.getProperty("jexer.TImage.bleedThrough",
                "true").equals("false")) {
            bleedThrough = false;
        }

        int textWidth = getScreen().getTextWidth();
        int textHeight = getScreen().getTextHeight();

        // Use reflection to call TImageImpl methods
        try {
            ImageRGB processedImage = originalImage;
            
            // Rotate if needed
            if (clockwise != 0) {
                Method rotateMethod = implClass.getMethod("rotateImage",
                    ImageRGB.class, int.class);
                processedImage = (ImageRGB) rotateMethod.invoke(null,
                    processedImage, clockwise);
            }
            
            // Scale if needed
            if ((scale != Scale.NONE) || (Math.abs(scaleFactor - 1.0) >= 0.03)) {
                Method scaleMethod = implClass.getMethod("scaleImage",
                    ImageRGB.class, double.class, int.class, int.class,
                    int.class, int.class, int.class, boolean.class,
                    jexer.bits.ColorRGB.class);
                processedImage = (ImageRGB) scaleMethod.invoke(null,
                    processedImage, scaleFactor, scale.ordinal(), getWidth(),
                    getHeight(), textWidth, textHeight, antiAlias, scaleBackColor);
            }

            if (processedImage == null) {
                return;
            }

            if ((always == true) ||
                (resized == true) ||
                ((textWidth > 0)
                    && (textWidth != lastTextWidth)
                    && (textHeight > 0)
                    && (textHeight != lastTextHeight))
            ) {
                resized = false;

                cellColumns = processedImage.getWidth() / textWidth;
                if (cellColumns * textWidth < processedImage.getWidth()) {
                    cellColumns++;
                }
                cellRows = processedImage.getHeight() / textHeight;
                if (cellRows * textHeight < processedImage.getHeight()) {
                    cellRows++;
                }

                cells = new Cell[cellColumns][cellRows];

                int imageId = System.identityHashCode(this);
                imageId ^= (int) System.currentTimeMillis();
                for (int x = 0; x < cellColumns; x++) {
                    for (int y = 0; y < cellRows; y++) {

                        int width = textWidth;
                        if ((x + 1) * textWidth > processedImage.getWidth()) {
                            width = processedImage.getWidth() - (x * textWidth);
                        }
                        int height = textHeight;
                        if ((y + 1) * textHeight > processedImage.getHeight()) {
                            height = processedImage.getHeight() - (y * textHeight);
                        }

                        Cell cell = new Cell();
                        cell.setTo(getWindow().getBackground());

                        // Get subimage
                        ImageRGB subImage = processedImage.getSubimage(
                            x * textWidth, y * textHeight, width, height);
                        
                        // Create a full-cell-size image
                        ImageRGB newImage = new ImageRGB(textWidth, textHeight);
                        newImage.drawImage(subImage, 0, 0);

                        cell.setImage(newImage);
                        if (!maybeTransparent) {
                            cell.setOpaqueImage();
                        } else if (!newImage.isFullyTransparent()) {
                            cell.flattenImage(false,
                                getApplication().getBackend());
                        } else {
                            cell.setTo(getWindow().getBackground());
                        }
                        if ((bleedThrough == false)
                            || (displayMode != DisplayMode.BITMAP)
                            || (cell.checkForSingleColor() == false)
                        ) {
                            imageId++;
                            cell.setImageId(imageId & 0x7FFFFFFF);
                        }
                        switch (displayMode) {
                        case BITMAP:
                            cells[x][y] = cell;
                            break;
                        case BLOCKS:
                            if (cell.isImage()) {
                                int rgb = ImageUtils.rgbAverage(cell.getImage());
                                Cell newCell = new Cell(' ');
                                newCell.setForeColorRGB(rgb);
                                newCell.setBackColorRGB(rgb);
                                cells[x][y] = newCell;
                            } else {
                                cells[x][y] = cell;
                            }
                            break;
                        case UNICODE_HALVES:
                            if (cell.isImage()) {
                                UnicodeGlyphImage ch = new UnicodeGlyphImage(cell);
                                cells[x][y] = ch.toHalfBlockGlyph();
                            } else {
                                cells[x][y] = cell;
                            }
                            break;
                        case UNICODE_SEXTANTS:
                            if (cell.isImage()) {
                                UnicodeGlyphImage ch = new UnicodeGlyphImage(cell);
                                cells[x][y] = ch.toSextantBlockGlyph();
                            } else {
                                cells[x][y] = cell;
                            }
                            break;
                        case UNICODE_QUADRANTS:
                            if (cell.isImage()) {
                                UnicodeGlyphImage ch = new UnicodeGlyphImage(cell);
                                cells[x][y] = ch.toQuadrantBlockGlyph();
                            } else {
                                cells[x][y] = cell;
                            }
                            break;
                        case UNICODE_SIXDOT:
                            if (cell.isImage()) {
                                UnicodeGlyphImage ch = new UnicodeGlyphImage(cell);
                                cells[x][y] = ch.toSixDotGlyph();
                                cells[x][y].setBackColorRGB(scaleBackColor.getRGB());
                            } else {
                                cells[x][y] = cell;
                            }
                            break;
                        case UNICODE_SIXDOTSOLID:
                            if (cell.isImage()) {
                                UnicodeGlyphImage ch = new UnicodeGlyphImage(cell);
                                cells[x][y] = ch.toSixDotSolidGlyph();
                            } else {
                                cells[x][y] = cell;
                            }
                            break;
                        }
                    }
                }

                lastTextWidth = textWidth;
                lastTextHeight = textHeight;
            }

            if ((left + getWidth()) > cellColumns) {
                left = cellColumns - getWidth();
            }
            if (left < 0) {
                left = 0;
            }
            if ((top + getHeight()) > cellRows) {
                top = cellRows - getHeight();
            }
            if (top < 0) {
                top = 0;
            }
        } catch (Exception e) {
            // If reflection fails, just don't render
        }
    }

    /**
     * Get anti-aliasing value.
     *
     * @return true if anti-aliasing is enabled
     */
    public boolean isAntiAlias() {
        return antiAlias;
    }

    /**
     * Set anti-aliasing.
     *
     * @param antiAlias if true, anti-aliasing will be enabled
     */
    public void setAntiAlias(final boolean antiAlias) {
        this.antiAlias = antiAlias;
        sizeToImage(true);
    }

    /**
     * Get the top corner to render.
     *
     * @return the top row
     */
    public int getTop() {
        return top;
    }

    /**
     * Set the top corner to render.
     *
     * @param top the new top row
     */
    public void setTop(final int top) {
        this.top = top;
        if (this.top > cellRows - getHeight()) {
            this.top = cellRows - getHeight();
        }
        if (this.top < 0) {
            this.top = 0;
        }
    }

    /**
     * Get the left corner to render.
     *
     * @return the left column
     */
    public int getLeft() {
        return left;
    }

    /**
     * Set the left corner to render.
     *
     * @param left the new left column
     */
    public void setLeft(final int left) {
        this.left = left;
        if (this.left > cellColumns - getWidth()) {
            this.left = cellColumns - getWidth();
        }
        if (this.left < 0) {
            this.left = 0;
        }
    }

    /**
     * Get the number of text cell rows for this image.
     *
     * @return the number of rows
     */
    public int getRows() {
        return cellRows;
    }

    /**
     * Get the number of text cell columns for this image.
     *
     * @return the number of columns
     */
    public int getColumns() {
        return cellColumns;
    }

    /**
     * Get the raw (unprocessed) image.
     *
     * @return the image
     */
    public ImageRGB getImage() {
        return originalImage;
    }

    /**
     * Set the raw image, and reprocess to make the visible image.
     *
     * @param image the new image
     */
    public void setImage(final ImageRGB image) {
        setImage(image, true);
    }

    /**
     * Set the raw image, and reprocess to make the visible image.
     *
     * @param image the new image
     * @param maybeTransparent if false, the image is fully opaque
     */
    public void setImage(final ImageRGB image, final boolean maybeTransparent) {
        this.originalImage = image;
        this.maybeTransparent = maybeTransparent;
        sizeToImage(true);
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    /**
     * Set the image space to an animation, and reprocess to make the visible
     * image.
     *
     * @param animation the new animation
     */
    public void setAnimation(final Animation animation) {
        if (this.animation != null) {
            this.animation.stop();
            this.animation = null;
        }
        this.animation = animation;
        originalImage = animation.getFrame();
        sizeToImage(true);
    }

    /**
     * Get the visible (processed) image.
     *
     * @return the image that is currently on screen
     */
    public ImageRGB getVisibleImage() {
        return originalImage;
    }

    /**
     * Get the scaling strategy.
     *
     * @return Scale.NONE, Scale.STRETCH, etc.
     */
    public Scale getScaleType() {
        return scale;
    }

    /**
     * Set the scaling strategy.
     *
     * @param scale Scale.NONE, Scale.STRETCH, etc.
     */
    public void setScaleType(final Scale scale) {
        this.scale = scale;
        sizeToImage(true);
    }

    /**
     * Get the scale factor.
     *
     * @return the scale factor
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Set the scale factor.  1.0 means no scaling.
     *
     * @param scaleFactor the new scale factor
     */
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
        sizeToImage(true);
    }

    /**
     * Get the image display mode.
     *
     * @return DisplayMode.BITMAP, DisplayMode.UNICODE_HALVES, etc.
     */
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    /**
     * Set the image display mode.
     *
     * @param displayMode DisplayMode.BITMAP, DisplayMode.UNICODE_HALVES, etc.
     */
    public void setDisplayMode(final DisplayMode displayMode) {
        this.displayMode = displayMode;
        sizeToImage(true);
    }

    /**
     * Get the rotation, as degrees.
     *
     * @return the rotation in degrees
     */
    public int getRotation() {
        switch (clockwise) {
        case 0:
            return 0;
        case 1:
            return 90;
        case 2:
            return 180;
        case 3:
            return 270;
        default:
            clockwise = 0;
            sizeToImage(true);
            return 0;
        }
    }

    /**
     * Set the rotation, as degrees clockwise.
     *
     * @param rotation 0, 90, 180, or 270
     */
    public void setRotation(final int rotation) {
        switch (rotation) {
        case 0:
            clockwise = 0;
            break;
        case 90:
            clockwise = 1;
            break;
        case 180:
            clockwise = 2;
            break;
        case 270:
            clockwise = 3;
            break;
        default:
            clockwise = 0;
            break;
        }
        sizeToImage(true);
    }

    // ------------------------------------------------------------------------
    // EditMenuUser -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Check if the cut menu item should be enabled.
     *
     * @return true if the cut menu item should be enabled
     */
    public boolean isEditMenuCut() {
        return false;
    }

    /**
     * Check if the copy menu item should be enabled.
     *
     * @return true if the copy menu item should be enabled
     */
    public boolean isEditMenuCopy() {
        return true;
    }

    /**
     * Check if the paste menu item should be enabled.
     *
     * @return true if the paste menu item should be enabled
     */
    public boolean isEditMenuPaste() {
        return false;
    }

    /**
     * Check if the clear menu item should be enabled.
     *
     * @return true if the clear menu item should be enabled
     */
    public boolean isEditMenuClear() {
        return false;
    }

}
