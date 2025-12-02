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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jexer.TButton;
import jexer.bits.Cell;
import jexer.bits.CellAttributes;
import jexer.bits.ImageRGB;

/**
 * TButtonDrawHelper provides AWT-based drawing for TButton styles that
 * require graphical rendering (ROUND, DIAMOND, ARROW_LEFT, ARROW_RIGHT).
 * This class is in jexer-java-desktop.jar and loaded via reflection.
 */
public class TButtonDrawHelper {

    /**
     * Private constructor - this class only has static methods.
     */
    private TButtonDrawHelper() {
        // NOP
    }

    /**
     * Convert a BufferedImage to ImageRGB.
     *
     * @param bufferedImage the BufferedImage to convert
     * @return the ImageRGB
     */
    private static ImageRGB toImageRGB(final BufferedImage bufferedImage) {
        return ImageRGBUtils.toImageRGB(bufferedImage);
    }

    /**
     * Draw the button ends for non-SQUARE styles and populate the Cell arrays.
     *
     * @param style the button style
     * @param cellWidth the width of a cell in pixels
     * @param cellHeight the height of a cell in pixels
     * @param shadowColor the shadow color attributes
     * @param rectangleColor the background/rectangle color attributes
     * @param buttonColor the button foreground color attributes
     * @param shadowRgb the shadow color as java.awt.Color
     * @param rectangleRgb the rectangle color as java.awt.Color
     * @param buttonRgb the button color as java.awt.Color
     * @param inButtonPress whether the button is being pressed
     * @param imageIdBase base for generating unique image IDs
     * @return an array of 6 Cell objects: [leftEdgeChar, rightEdgeChar,
     *         leftEdgeShadowChar, rightEdgeShadowCharTop,
     *         rightEdgeShadowCharBottom, shadowCharBottom]
     */
    public static Cell[] drawEnds(final TButton.Style style,
        final int cellWidth, final int cellHeight,
        final CellAttributes shadowColor,
        final CellAttributes rectangleColor,
        final CellAttributes buttonColor,
        final java.awt.Color shadowRgb,
        final java.awt.Color rectangleRgb,
        final java.awt.Color buttonRgb,
        final boolean inButtonPress,
        final int imageIdBase) {

        Cell leftEdgeChar = new Cell(buttonColor);
        Cell rightEdgeChar = new Cell(buttonColor);
        Cell leftEdgeShadowChar = new Cell(shadowColor);
        Cell rightEdgeShadowCharTop = new Cell(shadowColor);
        Cell rightEdgeShadowCharBottom = new Cell(shadowColor);
        Cell shadowCharBottom = new Cell(shadowColor);

        BufferedImage image = new BufferedImage(cellWidth * 2, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        BufferedImage shadowImage = new BufferedImage(cellWidth * 2,
            cellHeight * 2, BufferedImage.TYPE_INT_ARGB);

        // Draw the shadow first, so that it be underneath the right edge.
        Graphics2D gr2s = shadowImage.createGraphics();
        gr2s.setColor(rectangleRgb);
        gr2s.fillRect(0, 0, cellWidth * 2, cellHeight * 2);
        gr2s.setColor(shadowRgb);

        int [] xPoints;
        int [] yPoints;
        switch (style) {
        case ROUND:
            gr2s.fillOval(0, cellHeight / 2, cellWidth * 2, cellHeight);
            break;
        case DIAMOND:
            xPoints = new int[4];
            yPoints = new int[4];
            xPoints[0] = 0;
            xPoints[1] = cellWidth;
            xPoints[2] = 2 * cellWidth;
            xPoints[3] = cellWidth;
            yPoints[0] = cellHeight;
            yPoints[1] = cellHeight / 2;
            yPoints[2] = cellHeight;
            yPoints[3] = cellHeight + cellHeight / 2;
            gr2s.fillPolygon(xPoints, yPoints, 4);
            break;
        case ARROW_LEFT:
            xPoints = new int[6];
            yPoints = new int[6];
            xPoints[0] = 0;
            xPoints[1] = cellWidth;
            xPoints[2] = 2 * cellWidth;
            xPoints[3] = cellWidth;
            xPoints[4] = 2 * cellWidth;
            xPoints[5] = cellWidth;
            yPoints[0] = cellHeight;
            yPoints[1] = cellHeight / 2;
            yPoints[2] = cellHeight / 2;
            yPoints[3] = cellHeight;
            yPoints[4] = cellHeight + cellHeight / 2;
            yPoints[5] = cellHeight + cellHeight / 2;
            gr2s.fillPolygon(xPoints, yPoints, 6);
            break;
        case ARROW_RIGHT:
            xPoints = new int[6];
            yPoints = new int[6];
            xPoints[0] = 2 * cellWidth;
            xPoints[1] = cellWidth;
            xPoints[2] = 0;
            xPoints[3] = cellWidth;
            xPoints[4] = 0;
            xPoints[5] = cellWidth;
            yPoints[0] = cellHeight;
            yPoints[1] = cellHeight / 2;
            yPoints[2] = cellHeight / 2;
            yPoints[3] = cellHeight;
            yPoints[4] = cellHeight + cellHeight / 2;
            yPoints[5] = cellHeight + cellHeight / 2;
            gr2s.fillPolygon(xPoints, yPoints, 6);
            break;
        case SQUARE:
            // Not possible, handled in TButton directly.
            gr2s.dispose();
            return null;
        }
        gr2s.dispose();
        // gr2s now has the shadow bits, shifted half a cell down from 0.

        Graphics2D gr2 = image.createGraphics();
        gr2.setColor(rectangleRgb);
        gr2.fillRect(0, 0, cellWidth * 2, cellHeight);
        if (!inButtonPress) {
            gr2.setColor(shadowRgb);
            gr2.fillRect(cellWidth, cellHeight / 2, cellWidth,
                cellHeight - (cellHeight / 2));
        }
        gr2.setColor(buttonRgb);
        switch (style) {
        case ROUND:
            gr2.fillOval(0, 0, cellWidth * 2, cellHeight);
            break;
        case DIAMOND:
            xPoints = new int[4];
            yPoints = new int[4];
            xPoints[0] = 0;
            xPoints[1] = cellWidth;
            xPoints[2] = 2 * cellWidth;
            xPoints[3] = cellWidth;
            yPoints[0] = cellHeight / 2;
            yPoints[1] = 0;
            yPoints[2] = cellHeight / 2;
            yPoints[3] = cellHeight;
            gr2.fillPolygon(xPoints, yPoints, 4);
            break;
        case ARROW_LEFT:
            xPoints = new int[6];
            yPoints = new int[6];
            xPoints[0] = 0;
            xPoints[1] = cellWidth;
            xPoints[2] = 2 * cellWidth;
            xPoints[3] = cellWidth;
            xPoints[4] = 2 * cellWidth;
            xPoints[5] = cellWidth;
            yPoints[0] = cellHeight / 2;
            yPoints[1] = 0;
            yPoints[2] = 0;
            yPoints[3] = cellHeight / 2;
            yPoints[4] = cellHeight;
            yPoints[5] = cellHeight;
            gr2.fillPolygon(xPoints, yPoints, 6);
            break;
        case ARROW_RIGHT:
            xPoints = new int[6];
            yPoints = new int[6];
            xPoints[0] = 2 * cellWidth;
            xPoints[1] = cellWidth;
            xPoints[2] = 0;
            xPoints[3] = cellWidth;
            xPoints[4] = 0;
            xPoints[5] = cellWidth;
            yPoints[0] = cellHeight / 2;
            yPoints[1] = 0;
            yPoints[2] = 0;
            yPoints[3] = cellHeight / 2;
            yPoints[4] = cellHeight;
            yPoints[5] = cellHeight;
            gr2.fillPolygon(xPoints, yPoints, 6);
            break;
        case SQUARE:
            // Not possible.
            gr2.dispose();
            return null;
        }
        gr2.dispose();
        // gr2 now has the foreground ends, on both halves.

        int imageId = imageIdBase;

        // Left edge: left half of image
        BufferedImage cellImage = new BufferedImage(cellWidth, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        gr2 = cellImage.createGraphics();
        gr2.drawImage(image.getSubimage(0, 0, cellWidth, cellHeight),
            0, 0, null);
        gr2.dispose();
        imageId++;
        leftEdgeChar.setImage(toImageRGB(cellImage), imageId & 0x7FFFFFFF);
        leftEdgeChar.setOpaqueImage();

        // Right edge: right half of image
        cellImage = new BufferedImage(cellWidth, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        gr2 = cellImage.createGraphics();
        gr2.drawImage(image.getSubimage(cellWidth, 0, cellWidth, cellHeight),
            0, 0, null);
        gr2.dispose();
        imageId++;
        rightEdgeChar.setImage(toImageRGB(cellImage), imageId & 0x7FFFFFFF);
        rightEdgeChar.setOpaqueImage();

        // Left shadow edge: bottom-left half of shadowImage
        cellImage = new BufferedImage(cellWidth, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        gr2s = cellImage.createGraphics();
        gr2s.drawImage(shadowImage.getSubimage(0, cellHeight,
                cellWidth, cellHeight), 0, 0, null);
        gr2s.dispose();
        imageId++;
        leftEdgeShadowChar.setImage(toImageRGB(cellImage), imageId & 0x7FFFFFFF);
        leftEdgeShadowChar.setOpaqueImage();

        // Right shadow edge top: top-right half of shadowImage
        cellImage = new BufferedImage(cellWidth, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        gr2s = cellImage.createGraphics();
        gr2s.drawImage(shadowImage.getSubimage(cellWidth, 0,
                cellWidth, cellHeight), 0, 0, null);
        gr2s.dispose();
        imageId++;
        rightEdgeShadowCharTop.setImage(toImageRGB(cellImage), imageId & 0x7FFFFFFF);
        rightEdgeShadowCharTop.setOpaqueImage();

        // Right shadow edge bottom: bottom-right half of shadowImage
        cellImage = new BufferedImage(cellWidth, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        gr2s = cellImage.createGraphics();
        gr2s.drawImage(shadowImage.getSubimage(cellWidth, cellHeight,
                cellWidth, cellHeight), 0, 0, null);
        gr2s.dispose();
        imageId++;
        rightEdgeShadowCharBottom.setImage(toImageRGB(cellImage), imageId & 0x7FFFFFFF);
        rightEdgeShadowCharBottom.setOpaqueImage();

        cellImage = new BufferedImage(cellWidth, cellHeight,
            BufferedImage.TYPE_INT_ARGB);
        gr2s = cellImage.createGraphics();
        gr2s.setColor(rectangleRgb);
        gr2s.fillRect(0, 0, cellWidth, cellHeight);
        gr2s.setColor(shadowRgb);
        gr2s.fillRect(0, 0, cellWidth, cellHeight / 2);
        gr2s.dispose();
        imageId++;
        shadowCharBottom.setImage(toImageRGB(cellImage), imageId & 0x7FFFFFFF);
        shadowCharBottom.setOpaqueImage();

        return new Cell[] {
            leftEdgeChar,
            rightEdgeChar,
            leftEdgeShadowChar,
            rightEdgeShadowCharTop,
            rightEdgeShadowCharBottom,
            shadowCharBottom
        };
    }

}
