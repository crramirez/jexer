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

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * UnicodeGlyphEncoder turns a BufferedImage into single character from the
 * Unicode block-drawing elements ("Symbols For Legacy Computing").
 */
public class UnicodeGlyphEncoder {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * When run from the command line, we need both the "image", and the
     * foreground/background colors.
     */
    private class EncodingResult {
        /**
         * The encoded image.
         */
        public String encodedImage;

        /**
         * The 2-color palette used by this image.
         */
        public Palette palette;
    }

    /**
     * Palette is used to manage the conversion of images between 24-bit RGB
     * color and a palette of paletteSize colors.
     */
    private class Palette {

        /**
         * ColorIdx records a RGB color and its palette index.
         */
        private class ColorIdx {

            /**
             * The 24-bit RGB color.
             */
            public int color;

            /**
             * The population count for this color.
             */
            public int count = 0;

            /**
             * Public constructor.
             *
             * @param color the 24-bit RGB color
             */
            public ColorIdx(final int color, final int index) {
                this.color = color;
                this.count = 0;
            }

            /**
             * Public constructor.  Count is set to 1, index to -1.
             *
             * @param color the 24-bit RGB color
             */
            public ColorIdx(final int color) {
                this.color = color;
                this.count = 1;
            }

            /**
             * Hash only on color.
             *
             * @return the hash
             */
            @Override
            public int hashCode() {
                return color;
            }

            /**
             * Generate a human-readable string for this entry.
             *
             * @return a human-readable string
             */
            @Override
            public String toString() {
                return String.format("color %06x count %d", color, count);
            }
        }

        /**
         * A bucket contains colors that will all be mapped to the same
         * weighted average color value.
         */
        private class Bucket {

            /**
             * The colors in this bucket.
             */
            private ArrayList<ColorIdx> colors;

            /**
             * The palette index for this bucket.  For now this points to a
             * simple average of all the colors, or black if no colors are in
             * this bucket.
             */
            public int index = 0;

            // The minimum and maximum, and "total" component values in this
            // bucket.
            private int minRed   = 0xFF;
            private int maxRed   = 0;
            private int minGreen = 0xFF;
            private int maxGreen = 0;
            private int minBlue  = 0xFF;
            private int maxBlue  = 0;

            // The last computed average() value.
            private int lastAverage = -1;

            /**
             * Public constructor.
             *
             * @param n the expected number of colors that will be in this
             * bucket
             */
            public Bucket(final int n) {
                reset(n);
            }

            /**
             * Reset the stats.
             *
             * @param n the expected number of colors that will be in this
             * bucket
             */
            private void reset(final int n) {
                colors      = new ArrayList<ColorIdx>(n);
                minRed      = 0xFF;
                maxRed      = 0;
                minGreen    = 0xFF;
                maxGreen    = 0;
                minBlue     = 0xFF;
                maxBlue     = 0;
                lastAverage = -1;
                index       = 0;
            }

            /**
             * Get the index associated with all of the colors in this
             * bucket.
             *
             * @return the index
             */
            public int getIndex() {
                return index;
            }

            /**
             * Add a color to the bucket.
             *
             * @param color the color to add
             */
            public void add(final ColorIdx color) {
                colors.add(color);

                int rgb   = color.color;
                int red   = (rgb >>> 16) & 0xFF;
                int green = (rgb >>>  8) & 0xFF;
                int blue  =  rgb         & 0xFF;
                if (red > maxRed) {
                    maxRed = red;
                }
                if (red < minRed) {
                    minRed = red;
                }
                if (green > maxGreen) {
                    maxGreen = green;
                }
                if (green < minGreen) {
                    minGreen = green;
                }
                if (blue > maxBlue) {
                    maxBlue = blue;
                }
                if (blue < minBlue) {
                    minBlue = blue;
                }
            }

            /**
             * Partition this bucket into two buckets, split along the color
             * with the maximum range.
             *
             * @return the other bucket
             */
            public Bucket partition() {
                int redDiff = Math.max(0, (maxRed - minRed));
                int greenDiff = Math.max(0, (maxGreen - minGreen));
                int blueDiff = Math.max(0, (maxBlue - minBlue));
                if (verbosity >= 5) {
                    System.err.printf("partn colors %d Δr %d Δg %d Δb %d\n",
                        colors.size(), redDiff, greenDiff, blueDiff);
                }

                if ((redDiff > greenDiff) && (redDiff > blueDiff)) {
                    // Partition on red.
                    if (verbosity >= 5) {
                        System.err.println("    RED");
                    }
                    Collections.sort(colors, new Comparator<ColorIdx>() {
                        public int compare(ColorIdx c1, ColorIdx c2) {
                            int red1 = (c1.color >>> 16) & 0xFF;
                            int red2 = (c2.color >>> 16) & 0xFF;
                            return red1 - red2;
                        }
                    });
                } else if ((greenDiff > blueDiff) && (greenDiff > redDiff)) {
                    // Partition on green.
                    if (verbosity >= 5) {
                        System.err.println("    GREEN");
                    }
                    Collections.sort(colors, new Comparator<ColorIdx>() {
                        public int compare(ColorIdx c1, ColorIdx c2) {
                            int green1 = (c1.color >>> 8) & 0xFF;
                            int green2 = (c2.color >>> 8) & 0xFF;
                            return green1 - green2;
                        }
                    });
                } else {
                    // Partition on blue.
                    if (verbosity >= 5) {
                        System.err.println("    BLUE");
                    }
                    Collections.sort(colors, new Comparator<ColorIdx>() {
                        public int compare(ColorIdx c1, ColorIdx c2) {
                            int blue1 = c1.color & 0xFF;
                            int blue2 = c2.color & 0xFF;
                            return blue1 - blue2;
                        }
                    });
                }

                int oldN = colors.size();

                List<ColorIdx> newBucketColors;
                newBucketColors = colors.subList(oldN / 2, oldN);
                Bucket newBucket = new Bucket(newBucketColors.size());
                for (ColorIdx color: newBucketColors) {
                    newBucket.add(color);
                }

                List<ColorIdx> newColors;
                newColors = colors.subList(0, oldN - newBucketColors.size());
                reset(newColors.size());
                for (ColorIdx color: newColors) {
                    add(color);
                }
                assert (newBucketColors.size() + newColors.size() == oldN);
                return newBucket;
            }

            /**
             * Average the colors in this bucket.
             *
             * @return an averaged RGB value
             */
            public int average() {
                if (lastAverage != -1) {
                    return lastAverage;
                }

                // Compute the average color.
                long totalRed = 0;
                long totalGreen = 0;
                long totalBlue = 0;
                long count = 0;
                for (ColorIdx color: colors) {
                    int rgb = color.color;
                    int red   = (rgb >>> 16) & 0xFF;
                    int green = (rgb >>>  8) & 0xFF;
                    int blue  =  rgb         & 0xFF;
                    totalRed   += color.count * red;
                    totalGreen += color.count * green;
                    totalBlue  += color.count * blue;
                    count += color.count;
                }
                if (count == 0) {
                    lastAverage = 0xFF000000;
                    return lastAverage;
                }
                totalRed   = (int) (totalRed   / count);
                totalGreen = (int) (totalGreen / count);
                totalBlue  = (int) (totalBlue  / count);

                lastAverage = (int) ((0xFF << 24) | (totalRed   << 16)
                                                  | (totalGreen <<  8)
                                                  |  totalBlue);
                return lastAverage;
            }

            /**
             * Generate a human-readable string for this entry.
             *
             * @return a human-readable string
             */
            @Override
            public String toString() {
                return String.format("bucket %d colors avg RGB %06x index %d",
                    colors.size(), average(), index);
            }
        };

        /**
         * Number of colors in this palette is always 2.
         */
        private final int paletteSize = 2;

        /**
         * Map of colors used in the image by RGB.
         */
        private HashMap<Integer, ColorIdx> colorMap = null;

        /**
         * The image from the constructor.
         */
        private int [] rawImage;

        /**
         * The width of the image.
         */
        private int rawImageWidth;

        /**
         * The width of the image.
         */
        private int rawImageHeight;

        /**
         * The buckets produced by median cut.
         */
        private ArrayList<Bucket> buckets;

        /**
         * The RGB colors of the palette.
         */
        private int [] rgbColors;

        /**
         * Public constructor.
         *
         * @param image a bitmap image
         */
        public Palette(final BufferedImage image) {

            assert (image.getWidth() > 0);
            assert (image.getHeight() > 0);

            int numColors = paletteSize;

            rawImageWidth = image.getWidth();
            rawImageHeight = image.getHeight();
            int totalPixels = rawImageWidth * rawImageHeight;

            if (verbosity >= 1) {
                System.err.printf("Image: %dx%d (%d px)\n",
                    rawImageWidth, rawImageHeight, totalPixels);
            }

            int [] rgbArray = image.getRGB(0, 0,
                rawImageWidth, rawImageHeight, null, 0, rawImageWidth);
            rawImage = rgbArray;
            colorMap = new HashMap<Integer, ColorIdx>(rawImageWidth * rawImageHeight);
            for (int i = 0; i < rgbArray.length; i++) {
                int colorRGB = rgbArray[i];
                if ((colorRGB & 0xFF000000) != 0xFF000000) {
                    // Partially-transparent pixels become black.
                    if (verbosity >= 10) {
                        System.err.printf("EH? color at %d is %08x\n", i,
                            colorRGB);
                    }
                    rgbArray[i] = 0xFF000000;
                    colorRGB = 0xFF000000;
                }

                ColorIdx colorIdx = colorMap.get(colorRGB);
                if (colorIdx == null) {
                    colorIdx = new ColorIdx(colorRGB);
                    colorMap.put(colorRGB, colorIdx);
                } else {
                    colorIdx.count++;
                }
            } // for (int i = 0; i < rgbArray.length; i++)

            if (verbosity >= 1) {
                System.err.printf("# colors in image: %d palette size %d\n",
                    colorMap.size(), paletteSize);
            }

            assert (colorMap.size() > 0);
            medianCut();
        }

        /**
         * Perform median cut algorithm to generate a palette that fits
         * within the palette size.
         */
        public void medianCut() {

            // Populate the "total" bucket.
            Bucket bucket = new Bucket(colorMap.size());
            for (ColorIdx colorIdx: colorMap.values()) {
                bucket.add(colorIdx);

                int rgb = colorIdx.color;
                int red   = (rgb >>> 16) & 0xFF;
                int green = (rgb >>>  8) & 0xFF;
                int blue  =  rgb         & 0xFF;
            }

            int numColors = paletteSize;

            // Find the number of buckets we can have based on the palette
            // size.
            int log2 = 31 - Integer.numberOfLeadingZeros(numColors);
            int totalBuckets = 1 << log2;
            if (verbosity >= 1) {
                System.err.println("Total buckets possible: " + totalBuckets);
            }

            buckets = new ArrayList<Bucket>(totalBuckets);
            buckets.add(bucket);
            while (buckets.size() < totalBuckets) {
                int n = buckets.size();
                for (int i = 0; i < n; i++) {
                    buckets.add(buckets.get(i).partition());
                }
            }
            assert (buckets.size() == totalBuckets);

            // Buckets are partitioned.  Now assign them to the palette.
            int idx = 0;
            rgbColors = new int[buckets.size()];
            for (Bucket b: buckets) {
                int rgb = b.average() | 0xFF000000;
                b.index = idx;
                rgbColors[idx] = rgb;
                idx++;
            }

            if (verbosity >= 5) {
                System.err.printf("COLOR MAP: %d entries\n",
                    rgbColors.length);
                for (int i = 0; i < rgbColors.length; i++) {
                    System.err.printf("   %03d %08x\n", i,
                        rgbColors[i]);
                }
            }

        }

        /**
         * Search through the palette and find the best RGB match in the
         * palette.
         *
         * @param red the red component
         * @param green the green component
         * @param blue the blue component
         * @return the palette index of the nearest color in RGB space
         */
        private int findNearestColor(final int red, final int green,
            final int blue) {

            int bestDistance = 0xFFFFFF;
            int rgbIdx = -1;
            for (int i = 0; i < rgbColors.length; i++) {
                int rgb = rgbColors[i];
                int red2   = (rgb >>> 16) & 0xFF;
                int green2 = (rgb >>>  8) & 0xFF;
                int blue2  =  rgb         & 0xFF;
                int distance = (red2 - red) * (red2 - red)
                                + (green2 - green) * (green2 - green)
                                + (blue2 - blue) * (blue2 - blue);
                if (rgbIdx < 0) {
                    bestDistance = distance;
                    rgbIdx = 0;
                    continue;
                }

                if (distance < bestDistance) {
                    bestDistance = distance;
                    rgbIdx = i;
                }
            }
            return rgbIdx;
        }

        /**
         * Dither an image to a paletteSize palette.  The dithered image
         * cells will contain indexes into the palette.
         *
         * @return the dithered image rgb data.  Every pixel is an index into
         * the palette.
         */
        public int [] ditherImage() {
            int [] rgbArray = rawImage;

            int height = rawImageHeight;
            int width = rawImageWidth;
            for (int imageY = 0; imageY < height; imageY++) {
                for (int imageX = 0; imageX < width; imageX++) {
                    int oldPixel = rgbArray[imageX + (width * imageY)];
                    if (verbosity >= 100) {
                        System.err.printf("opaque oldPixel(%d, %d) %08x\n",
                            imageX, imageY, oldPixel);
                    }
                    int colorIdx = 0;
                    int color = oldPixel;

                    int red   = (color >>> 16) & 0xFF;
                    int green = (color >>>  8) & 0xFF;
                    int blue  =  color         & 0xFF;
                    colorIdx = findNearestColor(red, green, blue);

                    assert (colorIdx >= 0);
                    assert (colorIdx < rgbColors.length);
                    int newPixel = rgbColors[colorIdx];
                    rgbArray[imageX + (width * imageY)] = colorIdx;

                    int oldRed   = (oldPixel >>> 16) & 0xFF;
                    int oldGreen = (oldPixel >>>  8) & 0xFF;
                    int oldBlue  =  oldPixel         & 0xFF;

                    int newRed   = (newPixel >>> 16) & 0xFF;
                    int newGreen = (newPixel >>>  8) & 0xFF;
                    int newBlue  =  newPixel         & 0xFF;

                    int redError   = (  oldRed - newRed)   / 16;
                    int greenError = (oldGreen - newGreen) / 16;
                    int blueError  = ( oldBlue - newBlue)  / 16;

                    if (imageX < rawImageWidth - 1) {
                        int pXpY = rgbArray[imageX + 1 + (width * imageY)];
                        red   = ((pXpY >>> 16) & 0xFF) + (7 * redError);
                        green = ((pXpY >>>  8) & 0xFF) + (7 * greenError);
                        blue  = ( pXpY         & 0xFF) + (7 * blueError);
                        pXpY = (0xFF << 24) | ((red & 0xFF) << 16)
                             | ((green & 0xFF) << 8) | (blue & 0xFF);
                        rgbArray[imageX + 1 + (width * imageY)] = pXpY;
                        if (imageY < rawImageHeight - 1) {
                            int pXpYp = rgbArray[imageX + 1 + (width * (imageY + 1))];
                            red   = ((pXpYp >>> 16) & 0xFF) + redError;
                            green = ((pXpYp >>>  8) & 0xFF) + greenError;
                            blue  = ( pXpYp         & 0xFF) + blueError;
                            pXpYp = (0xFF << 24) | ((red & 0xFF) << 16)
                                  | ((green & 0xFF) << 8) | (blue & 0xFF);
                            rgbArray[imageX + 1 + (width * (imageY + 1))] = pXpYp;
                        }
                    } else if (imageY < rawImageHeight - 1) {
                        int pXmYp = rgbArray[imageX - 1 + (width * (imageY + 1))];
                        int pXYp = rgbArray[imageX + (width * (imageY + 1))];

                        red   = ((pXmYp >>> 16) & 0xFF) + (3 * redError);
                        green = ((pXmYp >>>  8) & 0xFF) + (3 * greenError);
                        blue  = ( pXmYp         & 0xFF) + (3 * blueError);
                        pXmYp = (0xFF << 24) | ((red & 0xFF) << 16)
                              | ((green & 0xFF) << 8) | (blue & 0xFF);
                        rgbArray[imageX - 1 + (width * (imageY + 1))] = pXmYp;

                        red   = ((pXYp >>> 16) & 0xFF) + (5 * redError);
                        green = ((pXYp >>>  8) & 0xFF) + (5 * greenError);
                        blue  = ( pXYp         & 0xFF) + (5 * blueError);
                        pXYp = (0xFF << 24) | ((red & 0xFF) << 16)
                             | ((green & 0xFF) << 8) | (blue & 0xFF);
                        rgbArray[imageX + (width * (imageY + 1))] = pXYp;
                    }
                } // for (int imageY = 0; imageY < height; imageY++)
            } // for (int imageX = 0; imageX < width; imageX++)

            return rgbArray;
        }
    }

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
     * Create a single glyph that best represents this entire image.
     *
     * @param bitmap the bitmap data
     * @return the encoded string and
     */
    private EncodingResult toGlyphResult(final BufferedImage bitmap) {

        // Start with 1k potential total output.
        StringBuilder sb = new StringBuilder(1024);

        assert (bitmap != null);

        EncodingResult result = new EncodingResult();

        // Anaylze the picture and generate a palette.
        Palette palette = new Palette(bitmap);
        result.palette = palette;

        // Dither the image.  We don't bother wrapping it in a BufferedImage.
        int [] rgbArray = palette.ditherImage();

        if (rgbArray == null) {
            result.encodedImage = "";
            return result;
        }

        if (true) {
            // DEBUG: Write the bitmap out to PNG.
            int [] pngArray = new int[rgbArray.length];
            for (int i = 0; i < rgbArray.length; i++) {
                pngArray[i] = palette.rgbColors[rgbArray[i]];
            }
            bitmap.setRGB(0, 0, bitmap.getWidth(), bitmap.getHeight(),
                pngArray, 0, bitmap.getWidth());
            java.io.FileOutputStream pngOutputStream;
            try {
                pngOutputStream = new java.io.FileOutputStream("test.png");
                ImageIO.write(bitmap, "PNG", pngOutputStream);
            } catch (java.io.IOException e) {
                // We failed to render image.
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        }

        /*
         * At this point the rgbArray should contain only 0's and 1's, and
         * rgbColor[0] and rgbColor[1] contain 24-bit ints for background and
         * foreground color.
         *
         * What's left now is mapping the image area to the portions of a
         * Unicode drawing "canvas" (halves, quadrants, sextants, octants,
         * braille, etc.) and then literally counting how many 1's are in
         * each area: if the count is above 50% total coverage for that area,
         * then it is foreground color.  The selection/matrix of foreground
         * colors is then used to determine the glyph.
         *
         * For now, we will just use halves because my normal home font kinda
         * sucks.  Later we can add more schemes.
         */
        final boolean debugThisCode = true;
        if (debugThisCode) {
            for (int i = 0; i < rgbArray.length; i++) {
                assert ((rgbArray[i] == 0) || (rgbArray[i] == 1));
            }
        }

        int ch = findHalfGlyph(rgbArray, bitmap.getWidth(),
            bitmap.getHeight());
        colorRGB(sb, palette.rgbColors[0], false);
        colorRGB(sb, palette.rgbColors[1], true);
        sb.append(Character.toChars(ch));
        result.encodedImage = sb.toString();
        // System.err.printf("%s", result.encodedImage);
        return result;
    }

    /**
     * Determine the Unicode half-glyph that most closely matches this 2-bit
     * image.
     *
     * @param data the image data as a sequence of 0's and 1's
     * @param width the width of the image
     * @param height the height of the image
     */
    private int findHalfGlyph(final int [] data, final int width,
        final int height) {

        /*
         * The map of image area to bits:
         *
         *   -----------------------
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *  |    0x01   |    0x02   |
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *   -----------------------
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *  |    0x04   |    0x08   |
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *   -----------------------
         */
        final int [] HALVES = {
            // 0x00 - Empty - only background
            ' ',
            // 0x01 - Upper left quadrant, treat like upper half.
            0x2580,
            // 0x02 - Upper right quadrant, treat like upper half.
            0x2580,
            // 0x03 - Full upper half - 0x2580 - ▀
            0x2580,
            // 0x04 - Bottom left quadrant, treat like bottom half.
            0x2584,
            // 0x05 - Full left half - 0x258c - ▌
            0x258c,
            // 0x06 - Upper right quadrant and lower left quadrant - treat
            // like full foreground block.
            0x2588,
            // 0x07 - Upper half and left half - treat like full foreground
            // block.
            0x2588,
            // 0x08 - Bottom right quadrant, treat like bottom half.
            0x2584,
            // 0x09 - Upper left quadrant and lower right quadrant - treat
            // like full foreground block.
            0x2588,
            // 0x0a - Full right half - 0x2590 - ▐
            0x2590,
            // 0x0b - Upper half and right half - treat like full foreground
            // block.
            0x2588,
            // 0x0c - Full bottom half - 0x2584 - ▄
            0x2584,
            // 0x0d - Bottom half and left half - treat like full foreground
            // block.
            0x2588,
            // 0x0e - Bottom half and right half - treat like full foreground
            // block.
            0x2588,
            // 0x0f - Full foreground block - 0x2588 - █
            0x2588,
        };
        int foregroundMap = 0x00;
        int quadrantSize = height * width / 4 / 2;

        int count = 0;
        for (int y = 0; y < height / 2; y++) {
            for (int x = 0; x < width / 2; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("top-left count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x01;
        }

        count = 0;
        for (int y = 0; y < height / 2; y++) {
            for (int x = width / 2; x < width; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("top-right count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x02;
        }

        count = 0;
        for (int y = height / 2; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("bottom-left count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x04;
        }

        count = 0;
        for (int y = height / 2; y < height; y++) {
            for (int x = width / 2; x < width; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("bottom-right count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x08;
        }

        return HALVES[foregroundMap];
    }

    /**
     * Determine the Unicode quadrant glyph that most closely matches this
     * 2-bit image.
     *
     * @param data the image data as a sequence of 0's and 1's
     * @param width the width of the image
     * @param height the height of the image
     */
    private int findQuadrantGlyph(final int [] data, final int width,
        final int height) {

        /*
         * The map of image area to bits:
         *
         *   -----------------------
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *  |    0x01   |    0x02   |
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *   -----------------------
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *  |    0x04   |    0x08   |
         *  |           |           |
         *  |           |           |
         *  |           |           |
         *   -----------------------
         */
        final int [] QUADRANTS = {
            // 0x00 - Empty - only background
            ' ',
            // 0x01 - Upper left quadrant - 0x2598 - ▘
            0x2598,
            // 0x02 - Upper right quadrant - 0x259d - ▝
            0x259d,
            // 0x03 - Full upper half - 0x2580 - ▀
            0x2580,
            // 0x04 - Bottom left quadrant - 0x2596 - ▖
            0x2596,
            // 0x05 - Full left half - 0x258c - ▌
            0x258c,
            // 0x06 - Upper right quadrant and lower left quadrant - 0x259e - ▞
            0x259e,
            // 0x07 - Upper half and left half - 0x259b - ▛
            0x259b,
            // 0x08 - Bottom right quadrant - 0x2597 - ▗
            0x2584,
            // 0x09 - Upper left quadrant and lower right quadrant - 0x259a - ▚
            0x259a,
            // 0x0a - Full right half - 0x2590 - ▐
            0x2590,
            // 0x0b - Upper half and right half - 0x259c - ▜
            0x2588,
            // 0x0c - Full bottom half - 0x2584 - ▄
            0x2584,
            // 0x0d - Bottom half and left half - 0x2599 - ▙
            0x2599,
            // 0x0e - Bottom half and right half - 0x259f - ▟
            0x259f,
            // 0x0f - Full foreground block - 0x2588 - █
            0x2588,
        };
        int foregroundMap = 0x00;
        int quadrantSize = height * width / 4 / 2;

        int count = 0;
        for (int y = 0; y < height / 2; y++) {
            for (int x = 0; x < width / 2; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("top-left count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x01;
        }

        count = 0;
        for (int y = 0; y < height / 2; y++) {
            for (int x = width / 2; x < width; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("top-right count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x02;
        }

        count = 0;
        for (int y = height / 2; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("bottom-left count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x04;
        }

        count = 0;
        for (int y = height / 2; y < height; y++) {
            for (int x = width / 2; x < width; x++) {
                count += data[(y * width) + x];
            }
        }
        if (verbosity >= 5) {
            System.err.printf("bottom-right count: %d / %d\n",
                count, quadrantSize);
        }
        if (count > quadrantSize) {
            foregroundMap |= 0x08;
        }

        return QUADRANTS[foregroundMap];
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
        return toGlyphResult(bitmap).encodedImage;
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
                    // Put together the image.
                    EncodingResult result = encoder.toGlyphResult(image);
                    System.out.println(result.encodedImage);
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
