/*
 * JxDialog - A dialog command clone using Jexer
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2025
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
 */
package jxdialog;

import java.util.ArrayList;
import java.util.List;

import jexer.TAction;
import jexer.TApplication;
import jexer.TButton;
import jexer.TLabel;
import jexer.TList;

/**
 * MenuDialog displays a menu with selectable items.
 */
public class MenuDialog extends BaseDialog {

    /**
     * The list widget.
     */
    private TList menuList;

    /**
     * The tags for menu items.
     */
    private List<String> tags;

    /**
     * Construct a new menu dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public MenuDialog(final TApplication application,
                      final DialogOptions options,
                      final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int y = 1;
        for (String line : lines) {
            if (y < 3) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Build list items from menu items
        tags = new ArrayList<>();
        List<String> displayItems = new ArrayList<>();

        for (String[] item : options.getMenuItems()) {
            tags.add(item[0]);
            // Format: tag - description
            displayItems.add(item[0] + "  " + item[1]);
        }

        // Calculate list dimensions
        int listY = y + 1;
        int listHeight = options.getListHeight();
        if (listHeight <= 0) {
            listHeight = getHeight() - listY - 5;
        }
        if (listHeight < 3) {
            listHeight = 3;
        }
        int listWidth = getWidth() - 4;

        // Add the menu list
        menuList = addList(displayItems, 1, listY, listWidth, listHeight,
                new TAction() {
                    public void DO() {
                        // Enter pressed on item
                        int idx = menuList.getSelectedIndex();
                        if (idx >= 0 && idx < tags.size()) {
                            closeOk(tags.get(idx));
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        // Move action (optional)
                    }
                });

        // Set default item if specified
        if (options.getDefaultItem() != null) {
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).equals(options.getDefaultItem())) {
                    menuList.setSelectedIndex(i);
                    break;
                }
            }
        } else if (!tags.isEmpty()) {
            menuList.setSelectedIndex(0);
        }

        // Add OK and Cancel buttons at the bottom
        String okLabel = options.getOkLabel();
        String cancelLabel = options.getCancelLabel();

        int okWidth = okLabel.length() + 4;
        int cancelWidth = cancelLabel.length() + 4;
        int buttonY = getHeight() - 4;

        if (!options.isNoOk()) {
            int totalWidth = okWidth;
            if (!options.isNoCancel()) {
                totalWidth += cancelWidth + 2;
            }
            int startX = (getWidth() - totalWidth) / 2;

            addButton(okLabel, startX, buttonY, new TAction() {
                public void DO() {
                    int idx = menuList.getSelectedIndex();
                    if (idx >= 0 && idx < tags.size()) {
                        closeOk(tags.get(idx));
                    } else {
                        closeCancel();
                    }
                }
            });

            if (!options.isNoCancel()) {
                addButton(cancelLabel, startX + okWidth + 2, buttonY, new TAction() {
                    public void DO() {
                        closeCancel();
                    }
                });
            }
        } else if (!options.isNoCancel()) {
            int startX = (getWidth() - cancelWidth) / 2;
            addButton(cancelLabel, startX, buttonY, new TAction() {
                public void DO() {
                    closeCancel();
                }
            });
        }

        // Focus on the list
        activate(menuList);
    }
}
