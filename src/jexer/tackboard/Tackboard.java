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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jexer.backend.Screen;
import jexer.bits.Cell;
import jexer.bits.ColorRGB;
import jexer.bits.ImageRGB;

/**
 * Tackboard maintains a collection of TackboardItems to draw on a Screen.
 * If java.awt is not available, the tackboard will simply not render anything.
 */
public class Tackboard {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Whether the implementation is available.
     */
    private static boolean implAvailable = false;

    /**
     * The implementation class.
     */
    private static Class<?> implClass;

    /**
     * Static initializer to check for java.awt availability.
     */
    static {
        try {
            implClass = Class.forName("jexer.backend.TackboardImpl");
            implAvailable = true;
        } catch (ClassNotFoundException e) {
            implAvailable = false;
        }
    }

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The items on this board.
     */
    private ArrayList<TackboardItem> items = new ArrayList<TackboardItem>();

    /**
     * Last text width value.
     */
    private int lastTextWidth = -1;

    /**
     * Last text height value.
     */
    private int lastTextHeight = -1;

    /**
     * Dirty flag, if true then getImage() needs to generate a rendering
     * aligned to the text cells.
     */
    private boolean dirty = true;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     */
    public Tackboard() {
        // NOP
    }

    // ------------------------------------------------------------------------
    // Tackboard --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Set dirty flag.
     */
    public final void setDirty() {
        dirty = true;
    }

    /**
     * Add an item to the board.
     *
     * @param item the item to add
     */
    public void addItem(final TackboardItem item) {
        item.setTackboard(this);
        items.add(item);
        dirty = true;
    }

    /**
     * Get the list of items.
     *
     * @return the list of items
     */
    public List<TackboardItem> getItems() {
        return items;
    }

    /**
     * Get the number of items on this board.
     *
     * @return the number of items
     */
    public int size() {
        return items.size();
    }

    /**
     * Remove everything on this board.
     */
    public void clear() {
        while (items.size() > 0) {
            // Give every item a shot to cleanup if it needs to.
            TackboardItem item = items.get(0);
            item.remove();
        }
        dirty = false;
    }

    /**
     * Draw everything to the screen.
     *
     * @param screen the screen to render to
     * @param transparent if true, allow partially transparent images to be
     * drawn to the screen
     */
    public void draw(final Screen screen, final boolean transparent) {
        // If implementation is not available, do nothing
        if (!implAvailable) {
            return;
        }

        Collections.sort(items);
        int cellWidth = screen.getTextWidth();
        int cellHeight = screen.getTextHeight();
        boolean redraw = dirty;

        if ((lastTextWidth == -1)
            || (lastTextWidth != cellWidth)
            || (lastTextHeight != cellHeight)
        ) {
            // We need to force a redraw because the cell grid dimensions
            // have changed.
            redraw = true;
            lastTextWidth = cellWidth;
            lastTextHeight = cellHeight;
        }

        int imageId = System.identityHashCode(this);
        imageId ^= (int) System.currentTimeMillis();

        for (TackboardItem item: items) {
            if (redraw) {
                item.setDirty();
            }
            ImageRGB image = item.getImage(cellWidth, cellHeight);
            if (image == null) {
                continue;
            }

            int x = item.getX();
            int y = item.getY();
            int textX = x / cellWidth;
            int textY = y / cellHeight;
            int width = image.getWidth();
            int height = image.getHeight();

            int columns = width / cellWidth;
            int rows = height / cellHeight;

            if ((textX + columns < 0)
                || (textY + rows < 0)
                || (textX >= screen.getWidth())
                || (textY >= screen.getHeight())
            ) {
                // No cells of this item will be visible on the screen.
                continue;
            }

            for (int sy = 0; sy < rows; sy++) {
                if ((sy + textY < 0)
                    || (sy + textY >= screen.getHeight())
                ) {
                    continue;
                }
                for (int sx = 0; sx < columns; sx++) {
                    while (sx + textX < 0) {
                        sx++;
                    }
                    if (sx + textX >= screen.getWidth()) {
                        break;
                    }

                    Cell oldCell = screen.getCharXY(sx + textX, sy + textY, true);
                    if (oldCell == null) {
                        continue;
                    }

                    ImageRGB newImage = image.getSubimage(sx * cellWidth,
                        sy * cellHeight, cellWidth, cellHeight);

                    if (newImage.isFullyTransparent()) {
                        continue;
                    }

                    if (oldCell.isImage()) {
                        // Blit this image over that one.
                        ImageRGB oldImage = oldCell.getImage(true);
                        oldImage.drawImage(newImage, 0, 0);
                        imageId++;
                        oldCell.setImage(oldImage, imageId & 0x7FFFFFFF);
                    } else {
                        // Old cell is text only, just add the image.
                        if (!transparent) {
                            ColorRGB bgColor = screen.getBackend().
                                attrToBackgroundColor(oldCell);
                            ImageRGB backImage = new ImageRGB(cellWidth, cellHeight);
                            backImage.fillRect(0, 0, cellWidth, cellHeight,
                                bgColor.getRGB());
                            backImage.drawImage(newImage, 0, 0);
                            imageId++;
                            oldCell.setImage(backImage, imageId & 0x7FFFFFFF);
                        } else {
                            imageId++;
                            oldCell.setImage(newImage, imageId & 0x7FFFFFFF);
                        }
                    }
                    screen.putCharXY(sx + textX, sy + textY, oldCell);
                }
            }
        }

        dirty = false;
    }

}
