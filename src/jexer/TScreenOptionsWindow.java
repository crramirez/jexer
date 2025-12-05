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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import jexer.backend.ECMA48Terminal;
import jexer.bits.BorderStyle;
import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import static jexer.TKeypress.*;

/**
 * TScreenOptionsWindow provides an easy UI for users to alter the running
 * screen options such as fonts and images.
 */
public class TScreenOptionsWindow extends TWindow {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Font style constant for plain style (equivalent to java.awt.Font.PLAIN).
     */
    private static final int FONT_STYLE_PLAIN = 0;

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Translated strings.
     */
    private ResourceBundle i18n = null;

    /**
     * The Swing screen (stored as Object to avoid dependency on SwingTerminal class).
     */
    private Object terminal = null;

    /**
     * The ECMA48 screen.
     */
    private ECMA48Terminal ecmaTerminal = null;

    /**
     * The font name.
     */
    private TComboBox fontName;

    /**
     * The font size.
     */
    private TField fontSize;

    /**
     * The X text adjustment.
     */
    private TField textAdjustX;

    /**
     * The Y text adjustment.
     */
    private TField textAdjustY;

    /**
     * The height text adjustment.
     */
    private TField textAdjustHeight;

    /**
     * The width text adjustment.
     */
    private TField textAdjustWidth;

    /**
     * The sixel palette size.
     */
    private TComboBox sixelPaletteSize;

    /**
     * The wideCharImages option.
     */
    private TCheckBox wideCharImages;

    /**
     * Triple-buffer support.
     */
    private TCheckBox tripleBuffer;

    /**
     * Cursor style.
     */
    private TComboBox cursorStyle;

    /**
     * Mouse style.
     */
    private TComboBox mouseStyle;

    /**
     * Sixel support.
     */
    private TCheckBox sixel;

    /**
     * Whether or not sixel uses a single shared palette.
     */
    private TCheckBox sixelSharedPalette;

    /**
     * 24-bit RGB color for normal system colors.
     */
    private TCheckBox rgbColor;

    /**
     * The window opacity.
     */
    private TField windowOpacity;

    /**
     * The original font size.
     */
    private int oldFontSize = 20;

    /**
     * The original font (stored as Object to avoid java.awt.Font import).
     */
    private Object oldFont = null;

    /**
     * The original text adjust X value.
     */
    private int oldTextAdjustX = 0;

    /**
     * The original text adjust Y value.
     */
    private int oldTextAdjustY = 0;

    /**
     * The original text adjust height value.
     */
    private int oldTextAdjustHeight = 0;

    /**
     * The original text adjust width value.
     */
    private int oldTextAdjustWidth = 0;

    /**
     * The original sixel palette (number of colors) value.
     */
    private int oldSixelPaletteSize = 1024;

    /**
     * The original wideCharImages value.
     */
    private boolean oldWideCharImages = true;

    /**
     * The original triple-buffer support.
     */
    private boolean oldTripleBuffer = true;

    /**
     * The original cursor style (stored as Object to avoid dependency on SwingTerminal.CursorStyle).
     */
    private Object oldCursorStyle;

    /**
     * The original mouse style.
     */
    private String oldMouseStyle = "default";

    /**
     * The original sixel support.
     */
    private boolean oldSixel = true;

    /**
     * The original sixelSharedPalette value.
     */
    private boolean oldSixelSharedPalette = true;

    /**
     * The original 24-bit RGB color for normal system colors.
     */
    private boolean oldRgbColor = false;

    /**
     * The original window opacity.
     */
    private int oldWindowOpacity = 100;

    // ------------------------------------------------------------------------
    // SwingTerminal reflection helpers ---------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the font from SwingTerminal via reflection.
     * @return the font Object or null
     */
    private Object terminalGetFont() {
        if (terminal == null) return null;
        try {
            Method method = terminal.getClass().getMethod("getFont");
            return method.invoke(terminal);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the font size from SwingTerminal via reflection.
     * @return the font size or 0
     */
    private int terminalGetFontSize() {
        if (terminal == null) return 0;
        try {
            Method method = terminal.getClass().getMethod("getFontSize");
            return (Integer) method.invoke(terminal);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Set the font on SwingTerminal via reflection.
     * @param font the font Object to set
     */
    private void terminalSetFont(Object font) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setFont", Object.class);
            method.invoke(terminal, font);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Set the font size on SwingTerminal via reflection.
     * @param size the font size to set
     */
    private void terminalSetFontSize(int size) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setFontSize", int.class);
            method.invoke(terminal, size);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Set the default font on SwingTerminal via reflection.
     */
    private void terminalSetDefaultFont() {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setDefaultFont");
            method.invoke(terminal);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Get text adjust X from SwingTerminal via reflection.
     * @return the adjustment or 0
     */
    private int terminalGetTextAdjustX() {
        if (terminal == null) return 0;
        try {
            Method method = terminal.getClass().getMethod("getTextAdjustX");
            return (Integer) method.invoke(terminal);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get text adjust Y from SwingTerminal via reflection.
     * @return the adjustment or 0
     */
    private int terminalGetTextAdjustY() {
        if (terminal == null) return 0;
        try {
            Method method = terminal.getClass().getMethod("getTextAdjustY");
            return (Integer) method.invoke(terminal);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get text adjust height from SwingTerminal via reflection.
     * @return the adjustment or 0
     */
    private int terminalGetTextAdjustHeight() {
        if (terminal == null) return 0;
        try {
            Method method = terminal.getClass().getMethod("getTextAdjustHeight");
            return (Integer) method.invoke(terminal);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get text adjust width from SwingTerminal via reflection.
     * @return the adjustment or 0
     */
    private int terminalGetTextAdjustWidth() {
        if (terminal == null) return 0;
        try {
            Method method = terminal.getClass().getMethod("getTextAdjustWidth");
            return (Integer) method.invoke(terminal);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Set text adjust X on SwingTerminal via reflection.
     * @param adjust the adjustment value
     */
    private void terminalSetTextAdjustX(int adjust) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setTextAdjustX", int.class);
            method.invoke(terminal, adjust);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Set text adjust Y on SwingTerminal via reflection.
     * @param adjust the adjustment value
     */
    private void terminalSetTextAdjustY(int adjust) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setTextAdjustY", int.class);
            method.invoke(terminal, adjust);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Set text adjust height on SwingTerminal via reflection.
     * @param adjust the adjustment value
     */
    private void terminalSetTextAdjustHeight(int adjust) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setTextAdjustHeight", int.class);
            method.invoke(terminal, adjust);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Set text adjust width on SwingTerminal via reflection.
     * @param adjust the adjustment value
     */
    private void terminalSetTextAdjustWidth(int adjust) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setTextAdjustWidth", int.class);
            method.invoke(terminal, adjust);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Get cursor style from SwingTerminal via reflection.
     * @return the cursor style Object or null
     */
    private Object terminalGetCursorStyle() {
        if (terminal == null) return null;
        try {
            Method method = terminal.getClass().getMethod("getCursorStyle");
            return method.invoke(terminal);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set cursor style on SwingTerminal via reflection.
     * @param cursorStyle the cursor style Object to set
     */
    private void terminalSetCursorStyle(Object cursorStyle) {
        if (terminal == null) return;
        try {
            // Get the CursorStyle class
            Class<?> cursorStyleClass = cursorStyle.getClass();
            Method method = terminal.getClass().getMethod("setCursorStyle", cursorStyleClass);
            method.invoke(terminal, cursorStyle);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Get mouse style from SwingTerminal via reflection.
     * @return the mouse style string or "default"
     */
    private String terminalGetMouseStyle() {
        if (terminal == null) return "default";
        try {
            Method method = terminal.getClass().getMethod("getMouseStyle");
            return (String) method.invoke(terminal);
        } catch (Exception e) {
            return "default";
        }
    }

    /**
     * Set mouse style on SwingTerminal via reflection.
     * @param mouseStyle the mouse style string
     */
    private void terminalSetMouseStyle(String mouseStyle) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setMouseStyle", String.class);
            method.invoke(terminal, mouseStyle);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Check if triple buffer is enabled on SwingTerminal via reflection.
     * @return true if triple buffer enabled
     */
    private boolean terminalIsTripleBuffer() {
        if (terminal == null) return true;
        try {
            Method method = terminal.getClass().getMethod("isTripleBuffer");
            return (Boolean) method.invoke(terminal);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Set triple buffer on SwingTerminal via reflection.
     * @param tripleBuffer the value to set
     */
    private void terminalSetTripleBuffer(boolean tripleBuffer) {
        if (terminal == null) return;
        try {
            Method method = terminal.getClass().getMethod("setTripleBuffer", boolean.class);
            method.invoke(terminal, tripleBuffer);
        } catch (Exception e) {
            // ignore
        }
    }

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.  The window will be centered on screen.
     *
     * @param application the TApplication that manages this window
     */
    @SuppressWarnings("this-escape")
    public TScreenOptionsWindow(final TApplication application) {

        // Register with the TApplication
        super(application, "", 0, 0, 66, 24, MODAL);
        i18n = ResourceBundle.getBundle(TScreenOptionsWindow.class.getName(),
            getLocale());
        setTitle(i18n.getString("windowTitle"));

        // Add shortcut text
        newStatusBar(i18n.getString("statusBar"));

        if (TApplication.isSwingTerminal(getScreen())) {
            terminal = getScreen();  // Store as Object, methods called via reflection
        }
        if (getScreen() instanceof ECMA48Terminal) {
            ecmaTerminal = (ECMA48Terminal) getScreen();
        }

        addLabel(i18n.getString("windowOpacity"), 1, 0, "ttext", false,
            new TAction() {
                public void DO() {
                    if (windowOpacity != null) {
                        windowOpacity.activate();
                    }
                }
            });

        addLabel(i18n.getString("fontName"), 3, 2, "ttext", false,
            new TAction() {
                public void DO() {
                    if (fontName != null) {
                        fontName.activate();
                    }
                }
            });
        addLabel(i18n.getString("fontSize"), 3, 3, "ttext", false,
            new TAction() {
                public void DO() {
                    if (fontSize != null) {
                        fontSize.activate();
                    }
                }
            });
        addLabel(i18n.getString("textAdjustX"), 3, 4, "ttext", false,
            new TAction() {
                public void DO() {
                    if (textAdjustX != null) {
                        textAdjustX.activate();
                    }
                }
            });
        addLabel(i18n.getString("textAdjustY"), 3, 5, "ttext", false,
            new TAction() {
                public void DO() {
                    if (textAdjustY != null) {
                        textAdjustY.activate();
                    }
                }
            });
        addLabel(i18n.getString("textAdjustHeight"), 3, 6, "ttext", false,
            new TAction() {
                public void DO() {
                    if (textAdjustHeight != null) {
                        textAdjustHeight.activate();
                    }
                }
            });
        addLabel(i18n.getString("textAdjustWidth"), 3, 7, "ttext", false,
            new TAction() {
                public void DO() {
                    if (textAdjustWidth != null) {
                        textAdjustWidth.activate();
                    }
                }
            });
        addLabel(i18n.getString("cursorStyle"), 3, 10, "ttext", false,
            new TAction() {
                public void DO() {
                    if (cursorStyle != null) {
                        cursorStyle.activate();
                    }
                }
            });
        addLabel(i18n.getString("mouseStyle"), 3, 11, "ttext", false,
            new TAction() {
                public void DO() {
                    if (mouseStyle != null) {
                        mouseStyle.activate();
                    }
                }
            });

        // Window opacity
        int alpha = getAlpha();
        oldWindowOpacity = 95;
        try {
            oldWindowOpacity = Integer.parseInt(System.getProperty(
                "jexer.TWindow.opacity", "95"));
            alpha = oldWindowOpacity * 255 / 100;
        } catch (NumberFormatException e) {
            // SQUASH
        }
        windowOpacity = addField(41, 0, 4, true,
            Integer.toString(alpha * 100 / 255),
            new TAction() {
                public void DO() {
                    int currentOpacity = getAlpha() * 100 / 255;
                    int newOpacity = currentOpacity;
                    newOpacity = Math.max(newOpacity, 10);
                    newOpacity = Math.min(newOpacity, 100);
                    try {
                        newOpacity = Integer.parseInt(windowOpacity.getText());
                    } catch (NumberFormatException e) {
                        windowOpacity.setText(Integer.toString(currentOpacity));
                    }
                    if (newOpacity != currentOpacity) {
                        getApplication().setWindowOpacity(newOpacity);
                        System.setProperty("jexer.TWindow.opacity",
                            Integer.toString(newOpacity));
                    }
                }
            },
            null);

        addSpinner(45, 0,
            new TAction() {
                public void DO() {
                    int currentOpacity = getAlpha() * 100 / 255;
                    int newOpacity = currentOpacity;
                    try {
                        newOpacity = Integer.parseInt(windowOpacity.getText());
                        newOpacity++;
                        newOpacity = Math.min(newOpacity, 100);
                    } catch (NumberFormatException e) {
                        windowOpacity.setText(Integer.toString(currentOpacity));
                    }
                    windowOpacity.setText(Integer.toString(newOpacity));
                    if (newOpacity != currentOpacity) {
                        getApplication().setWindowOpacity(newOpacity);
                        System.setProperty("jexer.TWindow.opacity",
                            Integer.toString(newOpacity));
                    }
                }
            },
            new TAction() {
                public void DO() {
                    int currentOpacity = getAlpha() * 100 / 255;
                    int newOpacity = currentOpacity;
                    try {
                        newOpacity = Integer.parseInt(windowOpacity.getText());
                        newOpacity--;
                        newOpacity = Math.max(newOpacity, 10);
                    } catch (NumberFormatException e) {
                        windowOpacity.setText(Integer.toString(currentOpacity));
                    }
                    windowOpacity.setText(Integer.toString(newOpacity));
                    if (newOpacity != currentOpacity) {
                        getApplication().setWindowOpacity(newOpacity);
                        System.setProperty("jexer.TWindow.opacity",
                            Integer.toString(newOpacity));
                    }
                }
            }
        );

        sixel = addCheckBox(3, 15, i18n.getString("sixel"),
            (ecmaTerminal != null ? ecmaTerminal.hasSixel() :
                System.getProperty("jexer.ECMA48.sixel",
                    "true").equals("true")));
        oldSixel = sixel.isChecked();
        addLabel(i18n.getString("sixelPaletteSize"), 3, 16, "ttext", false,
            new TAction() {
                public void DO() {
                    if (sixelPaletteSize != null) {
                        sixelPaletteSize.activate();
                    }
                }
            });
        sixelSharedPalette = addCheckBox(3, 17,
            i18n.getString("sixelSharedPalette"),
            (ecmaTerminal != null ? ecmaTerminal.hasSixelSharedPalette() :
                System.getProperty("jexer.ECMA48.sixelSharedPalette",
                    "true").equals("true")));
        oldSixelSharedPalette = sixelSharedPalette.isChecked();
        wideCharImages = addCheckBox(3, 18, i18n.getString("wideCharImages"),
            (ecmaTerminal != null ? ecmaTerminal.isWideCharImages() :
                System.getProperty("jexer.ECMA48.wideCharImages",
                    "true").equals("true")));
        oldWideCharImages = wideCharImages.isChecked();
        rgbColor = addCheckBox(3, 19, i18n.getString("rgbColor"),
            (ecmaTerminal != null ? ecmaTerminal.isRgbColor() :
                System.getProperty("jexer.ECMA48.rgbColor",
                    "false").equals("true")));
        oldRgbColor = rgbColor.isChecked();

        int col = 23;
        if (terminal == null) {
            // Non-Swing case: turn off stuff we can't change
            addLabel(i18n.getString("unavailable"), col, 2);
            addLabel(i18n.getString("unavailable"), col, 3);
            addLabel(i18n.getString("unavailable"), col, 4);
            addLabel(i18n.getString("unavailable"), col, 5);
            addLabel(i18n.getString("unavailable"), col, 6);
            addLabel(i18n.getString("unavailable"), col, 7);
        }
        if (ecmaTerminal == null) {
            // Swing case: turn off stuff we can't change
            addLabel(i18n.getString("unavailable"), col, 16);
            sixel.setEnabled(false);
            sixelSharedPalette.setEnabled(false);
            wideCharImages.setEnabled(false);
            rgbColor.setEnabled(false);
        }

        if (ecmaTerminal != null) {
            oldSixelPaletteSize = ecmaTerminal.getSixelPaletteSize();

            String [] sixelSizes;
            if (System.getProperty("jexer.ECMA48.sixelEncoder",
                    "hq").equals("hq")
            ) {
                String [] sizes = { "     2 ", "    16 ", "    64 ", "   128 ", "   256 ", "   512 ", "  1024 ", "  2048 " };
                sixelSizes = sizes;
                sixelSharedPalette.setEnabled(false);
            } else {
                String [] sizes = { "     2 ", "   256 ", "   512 ", "  1024 ", "  2048 " };
                sixelSizes = sizes;
            }
            List<String> sizes = new ArrayList<String>();
            sizes.addAll(Arrays.asList(sixelSizes));
            sixelPaletteSize = addComboBox(col, 16, 10, sizes, 0, 4,
                new TAction() {
                    public void DO() {
                        try {
                            ecmaTerminal.setSixelPaletteSize(Integer.parseInt(
                                sixelPaletteSize.getText().trim()));
                        } catch (NumberFormatException e) {
                            // SQUASH
                        }
                    }
                }
            );
            sixelPaletteSize.setText(String.format("%6d ",
                    oldSixelPaletteSize));
        }

        if (terminal != null) {
            oldFont = terminalGetFont();
            oldFontSize = terminalGetFontSize();
            oldTextAdjustX = terminalGetTextAdjustX();
            oldTextAdjustY = terminalGetTextAdjustY();
            oldTextAdjustHeight = terminalGetTextAdjustHeight();
            oldTextAdjustWidth = terminalGetTextAdjustWidth();
            oldCursorStyle = terminalGetCursorStyle();
            oldMouseStyle = terminalGetMouseStyle();

            String [] fontNames = getFontFamilyNames();
            List<String> fonts = new ArrayList<String>();
            fonts.add(0, i18n.getString("builtInTerminus"));
            if (fontNames != null) {
                fonts.addAll(Arrays.asList(fontNames));
            }
            fontName = addComboBox(col, 2, 25, fonts, 0, 8,
                new TAction() {
                    public void DO() {
                        if (fontName.getText().equals(i18n.
                                getString("builtInTerminus"))) {

                            terminalSetDefaultFont();
                        } else {
                            Object newFont = createFont(fontName.getText(),
                                FONT_STYLE_PLAIN, terminalGetFontSize());
                            if (newFont != null) {
                                terminalSetFont(newFont);
                            }
                            fontSize.setText(Integer.toString(
                                terminalGetFontSize()));
                            textAdjustX.setText(Integer.toString(
                                terminalGetTextAdjustX()));
                            textAdjustY.setText(Integer.toString(
                                terminalGetTextAdjustY()));
                            textAdjustHeight.setText(Integer.toString(
                                terminalGetTextAdjustHeight()));
                            textAdjustWidth.setText(Integer.toString(
                                terminalGetTextAdjustWidth()));
                        }
                    }
                }
            );

            // Font size
            fontSize = addField(col, 3, 3, true,
                Integer.toString(terminalGetFontSize()),
                new TAction() {
                    public void DO() {
                        int currentSize = terminalGetFontSize();
                        int newSize = currentSize;
                        try {
                            newSize = Integer.parseInt(fontSize.getText());
                        } catch (NumberFormatException e) {
                            fontSize.setText(Integer.toString(currentSize));
                        }
                        if (newSize != currentSize) {
                            terminalSetFontSize(newSize);
                            textAdjustX.setText(Integer.toString(
                                terminalGetTextAdjustX()));
                            textAdjustY.setText(Integer.toString(
                                terminalGetTextAdjustY()));
                            textAdjustHeight.setText(Integer.toString(
                                terminalGetTextAdjustHeight()));
                            textAdjustWidth.setText(Integer.toString(
                                terminalGetTextAdjustWidth()));
                        }
                    }
                },
                null);

            addSpinner(col + 3, 3,
                new TAction() {
                    public void DO() {
                        int currentSize = terminalGetFontSize();
                        int newSize = currentSize;
                        try {
                            newSize = Integer.parseInt(fontSize.getText());
                            newSize++;
                        } catch (NumberFormatException e) {
                            fontSize.setText(Integer.toString(currentSize));
                        }
                        fontSize.setText(Integer.toString(newSize));
                        if (newSize != currentSize) {
                            terminalSetFontSize(newSize);
                            textAdjustX.setText(Integer.toString(
                                terminalGetTextAdjustX()));
                            textAdjustY.setText(Integer.toString(
                                terminalGetTextAdjustY()));
                            textAdjustHeight.setText(Integer.toString(
                                terminalGetTextAdjustHeight()));
                            textAdjustWidth.setText(Integer.toString(
                                terminalGetTextAdjustWidth()));
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        int currentSize = terminalGetFontSize();
                        int newSize = currentSize;
                        try {
                            newSize = Integer.parseInt(fontSize.getText());
                            newSize--;
                        } catch (NumberFormatException e) {
                            fontSize.setText(Integer.toString(currentSize));
                        }
                        fontSize.setText(Integer.toString(newSize));
                        if (newSize != currentSize) {
                            terminalSetFontSize(newSize);
                            textAdjustX.setText(Integer.toString(
                                terminalGetTextAdjustX()));
                            textAdjustY.setText(Integer.toString(
                                terminalGetTextAdjustY()));
                            textAdjustHeight.setText(Integer.toString(
                                terminalGetTextAdjustHeight()));
                            textAdjustWidth.setText(Integer.toString(
                                terminalGetTextAdjustWidth()));
                        }
                    }
                }
            );

            // textAdjustX
            textAdjustX = addField(col, 4, 3, true,
                Integer.toString(terminalGetTextAdjustX()),
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustX();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustX.getText());
                        } catch (NumberFormatException e) {
                            textAdjustX.setText(Integer.toString(currentAdjust));
                        }
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustX(newAdjust);
                        }
                    }
                },
                null);

            addSpinner(col + 3, 4,
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustX();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustX.getText());
                            newAdjust++;
                        } catch (NumberFormatException e) {
                            textAdjustX.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustX.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustX(newAdjust);
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustX();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustX.getText());
                            newAdjust--;
                        } catch (NumberFormatException e) {
                            textAdjustX.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustX.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustX(newAdjust);
                        }
                    }
                }
            );

            // textAdjustY
            textAdjustY = addField(col, 5, 3, true,
                Integer.toString(terminalGetTextAdjustY()),
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustY();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustY.getText());
                        } catch (NumberFormatException e) {
                            textAdjustY.setText(Integer.toString(currentAdjust));
                        }
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustY(newAdjust);
                        }
                    }
                },
                null);

            addSpinner(col + 3, 5,
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustY();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustY.getText());
                            newAdjust++;
                        } catch (NumberFormatException e) {
                            textAdjustY.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustY.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustY(newAdjust);
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustY();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustY.getText());
                            newAdjust--;
                        } catch (NumberFormatException e) {
                            textAdjustY.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustY.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustY(newAdjust);
                        }
                    }
                }
            );

            // textAdjustHeight
            textAdjustHeight = addField(col, 6, 3, true,
                Integer.toString(terminalGetTextAdjustHeight()),
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustHeight();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustHeight.getText());
                        } catch (NumberFormatException e) {
                            textAdjustHeight.setText(Integer.toString(currentAdjust));
                        }
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustHeight(newAdjust);
                        }
                    }
                },
                null);

            addSpinner(col + 3, 6,
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustHeight();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustHeight.getText());
                            newAdjust++;
                        } catch (NumberFormatException e) {
                            textAdjustHeight.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustHeight.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustHeight(newAdjust);
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustHeight();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustHeight.getText());
                            newAdjust--;
                        } catch (NumberFormatException e) {
                            textAdjustHeight.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustHeight.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustHeight(newAdjust);
                        }
                    }
                }
            );

            // textAdjustWidth
            textAdjustWidth = addField(col, 7, 3, true,
                Integer.toString(terminalGetTextAdjustWidth()),
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustWidth();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustWidth.getText());
                        } catch (NumberFormatException e) {
                            textAdjustWidth.setText(Integer.toString(currentAdjust));
                        }
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustWidth(newAdjust);
                        }
                    }
                },
                null);

            addSpinner(col + 3, 7,
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustWidth();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustWidth.getText());
                            newAdjust++;
                        } catch (NumberFormatException e) {
                            textAdjustWidth.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustWidth.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustWidth(newAdjust);
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        int currentAdjust = terminalGetTextAdjustWidth();
                        int newAdjust = currentAdjust;
                        try {
                            newAdjust = Integer.parseInt(textAdjustWidth.getText());
                            newAdjust--;
                        } catch (NumberFormatException e) {
                            textAdjustWidth.setText(Integer.toString(currentAdjust));
                        }
                        textAdjustWidth.setText(Integer.toString(newAdjust));
                        if (newAdjust != currentAdjust) {
                            terminalSetTextAdjustWidth(newAdjust);
                        }
                    }
                }
            );

        } // if (terminal != null)

        tripleBuffer = addCheckBox(3, 9, i18n.getString("tripleBuffer"),
            (terminal != null ? terminalIsTripleBuffer() :
                System.getProperty("jexer.Swing.tripleBuffer",
                    "true").equals("true")));
        oldTripleBuffer = tripleBuffer.isChecked();

        ArrayList<String> cursorStyles = new ArrayList<String>();
        cursorStyles.add(i18n.getString("cursorStyleBlock").toLowerCase());
        cursorStyles.add(i18n.getString("cursorStyleOutline").toLowerCase());
        cursorStyles.add(i18n.getString("cursorStyleUnderline").toLowerCase());
        cursorStyle = addComboBox(22, 10, 25, cursorStyles, 0, 4,
            new TAction() {
                public void DO() {
                    terminalSetCursorStyle(cursorStyle.getText());
                }
            });
        cursorStyle.setText((terminal == null ?
                System.getProperty("jexer.Swing.cursorStyle", "underline") :
                terminalGetCursorStyle().toString().toLowerCase()));

        ArrayList<String> mouseStyles = new ArrayList<String>();
        mouseStyles.add("default");
        mouseStyles.add("crosshair");
        mouseStyles.add("hand");
        mouseStyles.add("move");
        mouseStyles.add("text");
        mouseStyles.add("none");
        mouseStyle = addComboBox(22, 11, 25, mouseStyles, 0, 7,
            new TAction() {
                public void DO() {
                    // TApplication.setMouseState() will override mouse style
                    // with the system property, so set that here.  If we
                    // cancel the screen, it will be put back.
                    String newMouseStyle = mouseStyle.getText();
                    System.setProperty("jexer.Swing.mouseStyle", newMouseStyle);
                    terminalSetMouseStyle(newMouseStyle);
                }
            });
        mouseStyle.setText((terminal == null ?
                System.getProperty("jexer.Swing.mouseStyle", "default") :
                terminalGetMouseStyle().toLowerCase()));

        if (terminal == null) {
            tripleBuffer.setEnabled(false);
            cursorStyle.setEnabled(false);
            mouseStyle.setEnabled(false);
        }

        addButton(i18n.getString("okButton"),
            getWidth() - 14, getHeight() - 7,
            new TAction() {
                public void DO() {
                    // Copy values out.
                    if (ecmaTerminal != null) {
                        ecmaTerminal.setHasSixel(sixel.isChecked());
                        ecmaTerminal.setSixelSharedPalette(sixelSharedPalette.
                            isChecked());
                        ecmaTerminal.setWideCharImages(wideCharImages.
                            isChecked());
                        ecmaTerminal.setRgbColor(rgbColor.isChecked());
                    }
                    if (terminal != null) {
                        terminalSetTripleBuffer(tripleBuffer.isChecked());
                        terminalSetFont(terminalGetFont());
                    }

                    // Close window.
                    TScreenOptionsWindow.this.close();
                }
            });

        TButton cancelButton = addButton(i18n.getString("cancelButton"),
            getWidth() - 14, getHeight() - 5,
            new TAction() {
                public void DO() {
                    // Restore old values, then close the window.
                    if (terminal != null) {
                        terminalSetFont(oldFont);
                        terminalSetFontSize(oldFontSize);
                        terminalSetTextAdjustX(oldTextAdjustX);
                        terminalSetTextAdjustY(oldTextAdjustY);
                        terminalSetTextAdjustHeight(oldTextAdjustHeight);
                        terminalSetTextAdjustWidth(oldTextAdjustWidth);
                        terminalSetTripleBuffer(oldTripleBuffer);
                        terminalSetCursorStyle(oldCursorStyle);
                        terminalSetMouseStyle(oldMouseStyle);
                        System.setProperty("jexer.Swing.mouseStyle",
                            oldMouseStyle);
                    }
                    if (ecmaTerminal != null) {
                        ecmaTerminal.setHasSixel(oldSixel);
                        ecmaTerminal.setSixelSharedPalette(oldSixelSharedPalette);
                        ecmaTerminal.setSixelPaletteSize(oldSixelPaletteSize);
                        ecmaTerminal.setWideCharImages(oldWideCharImages);
                        ecmaTerminal.setRgbColor(oldRgbColor);
                    }
                    getApplication().setWindowOpacity(oldWindowOpacity);
                    System.setProperty("jexer.TWindow.opacity",
                        Integer.toString(oldWindowOpacity));
                    TScreenOptionsWindow.this.close();
                }
            });

        // Save this for last: make the cancel button default action.
        activate(cancelButton);

    }

    // ------------------------------------------------------------------------
    // Event handlers ---------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        // Escape - behave like cancel
        if (keypress.equals(kbEsc)) {
            // Restore old values, then close the window.
            if (terminal != null) {
                terminalSetFont(oldFont);
                terminalSetFontSize(oldFontSize);
                terminalSetTextAdjustX(oldTextAdjustX);
                terminalSetTextAdjustY(oldTextAdjustY);
                terminalSetTextAdjustHeight(oldTextAdjustHeight);
                terminalSetTextAdjustWidth(oldTextAdjustWidth);
                terminalSetTripleBuffer(oldTripleBuffer);
                terminalSetCursorStyle(oldCursorStyle);
                terminalSetMouseStyle(oldMouseStyle);
                System.setProperty("jexer.Swing.mouseStyle", oldMouseStyle);
            }
            if (ecmaTerminal != null) {
                ecmaTerminal.setHasSixel(oldSixel);
                ecmaTerminal.setSixelSharedPalette(oldSixelSharedPalette);
                ecmaTerminal.setSixelPaletteSize(oldSixelPaletteSize);
                ecmaTerminal.setWideCharImages(oldWideCharImages);
                ecmaTerminal.setRgbColor(oldRgbColor);
            }
            getApplication().setWindowOpacity(oldWindowOpacity);
            System.setProperty("jexer.TWindow.opacity",
                Integer.toString(oldWindowOpacity));
            getApplication().closeWindow(this);
            return;
        }

        // Pass to my parent
        super.onKeypress(keypress);
    }

    // ------------------------------------------------------------------------
    // TWindow ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Draw me on screen.
     */
    @Override
    public void draw() {
        super.draw();

        int left = 34;

        BorderStyle borderStyle;
        borderStyle = BorderStyle.getStyle(System.getProperty(
            "jexer.TScreenOptions.options.borderStyle", "single"));

        CellAttributes color = getTheme().getColor("ttext");
        drawBox(2, 2, left + 30, 14, color, color, borderStyle, false);
        if (borderStyle.equals(BorderStyle.NONE)) {
            putStringXY(3, 2, i18n.getString("swingOptions"), color);
        } else {
            putStringXY(4, 2, i18n.getString("swingOptions"), color);
        }


        drawBox(2, 15, left + 18, 22, color, color, borderStyle, false);
        if (borderStyle.equals(BorderStyle.NONE)) {
            putStringXY(3, 15, i18n.getString("xtermOptions"), color);
        } else {
            putStringXY(4, 15, i18n.getString("xtermOptions"), color);
        }

        borderStyle = BorderStyle.getStyle(System.getProperty(
            "jexer.TScreenOptions.grid.borderStyle", "singleVdoubleH"));

        drawBox(left + 4, 5, left + 28, 10, color, color, borderStyle, false);
        if (borderStyle.equals(BorderStyle.NONE)) {
            putStringXY(left + 4, 5, i18n.getString("sample"), color);
        } else {
            putStringXY(left + 6, 5, i18n.getString("sample"), color);
        }
        for (int i = 6; i < 9; i++) {
            hLineXY(left + 5, i, 22, GraphicsChars.HATCH, color);
        }

    }

    /**
     * This window will reset the physical screen, making closing effects not
     * always work right.  Let's just disable that for now.
     *
     * @return true to disable close effects
     */
    @Override
    public boolean disableCloseEffect() {
        return true;
    }

    // ------------------------------------------------------------------------
    // TScreenOptionsWindow ---------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Set the border style for the window when it is the foreground window.
     *
     * @param borderStyle the border style string, one of: "default", "none",
     * "single", "double", "singleVdoubleH", "singleHdoubleV", or "round"; or
     * null to use the value from jexer.TScreenOptions.borderStyle.
     */
    @Override
    public void setBorderStyleForeground(final String borderStyle) {
        if (borderStyle == null) {
            String style = System.getProperty("jexer.TScreenOptions.borderStyle",
                "double");
            super.setBorderStyleForeground(style);
        } else {
            super.setBorderStyleForeground(borderStyle);
        }
    }

    /**
     * Set the border style for the window when it is the modal window.
     *
     * @param borderStyle the border style string, one of: "default", "none",
     * "single", "double", "singleVdoubleH", "singleHdoubleV", or "round"; or
     * null to use the value from jexer.TScreenOptions.borderStyle.
     */
    @Override
    public void setBorderStyleModal(final String borderStyle) {
        if (borderStyle == null) {
            String style = System.getProperty("jexer.TScreenOptions.borderStyle",
                "double");
            super.setBorderStyleModal(style);
        } else {
            super.setBorderStyleModal(borderStyle);
        }
    }

    /**
     * Set the border style for the window when it is an inactive/background
     * window.
     *
     * @param borderStyle the border style string, one of: "default", "none",
     * "single", "double", "singleVdoubleH", "singleHdoubleV", or "round"; or
     * null to use the value from jexer.TScreenOptions.borderStyle.
     */
    @Override
    public void setBorderStyleInactive(final String borderStyle) {
        if (borderStyle == null) {
            String style = System.getProperty("jexer.TScreenOptions.borderStyle",
                "double");
            super.setBorderStyleInactive(style);
        } else {
            super.setBorderStyleInactive(borderStyle);
        }
    }

    /**
     * Set the border style for the window when it is being dragged/resize.
     *
     * @param borderStyle the border style string, one of: "default", "none",
     * "single", "double", "singleVdoubleH", "singleHdoubleV", or "round"; or
     * null to use the value from jexer.TScreenOptions.borderStyle.
     */
    @Override
    public void setBorderStyleMoving(final String borderStyle) {
        if (borderStyle == null) {
            String style = System.getProperty("jexer.TScreenOptions.borderStyle",
                "double");
            super.setBorderStyleMoving(style);
        } else {
            super.setBorderStyleMoving(borderStyle);
        }
    }

    // ------------------------------------------------------------------------
    // Font helper methods (use reflection to avoid java.awt imports) ---------
    // ------------------------------------------------------------------------

    /**
     * FontHelper class reference, loaded via reflection.
     */
    private static Class<?> fontHelperClass;

    /**
     * Whether FontHelper is available.
     */
    private static boolean fontHelperAvailable = true;

    static {
        try {
            fontHelperClass = Class.forName("jexer.desktop.FontHelper");
        } catch (ClassNotFoundException e) {
            fontHelperAvailable = false;
        }
    }

    /**
     * Get the list of available font family names via reflection.
     *
     * @return array of font family names, or null if not available
     */
    private static String[] getFontFamilyNames() {
        if (!fontHelperAvailable) {
            return null;
        }
        try {
            return (String[]) fontHelperClass.getMethod(
                "getAvailableFontFamilyNames").invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create a Font via reflection.
     *
     * @param fontName the font name
     * @param style the style (0 = PLAIN)
     * @param size the font size
     * @return the Font object, or null if not available
     */
    private static Object createFont(final String fontName, final int style,
        final int size) {

        if (!fontHelperAvailable) {
            return null;
        }
        try {
            return fontHelperClass.getMethod("createFont", String.class,
                int.class, int.class).invoke(null, fontName, style, size);
        } catch (Exception e) {
            return null;
        }
    }

}
