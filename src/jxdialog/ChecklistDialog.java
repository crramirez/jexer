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
import jexer.TCheckBox;
import jexer.TLabel;

/**
 * ChecklistDialog displays a list with checkboxes.
 */
public class ChecklistDialog extends BaseDialog {

    /**
     * The checkboxes.
     */
    private List<TCheckBox> checkboxes;

    /**
     * The tags for items.
     */
    private List<String> tags;

    /**
     * Construct a new checklist dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public ChecklistDialog(final TApplication application,
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

        // Build checkbox items
        tags = new ArrayList<>();
        checkboxes = new ArrayList<>();

        int listY = y + 1;
        int maxItems = getHeight() - listY - 5;

        int itemY = listY;
        for (String[] item : options.getMenuItems()) {
            if (itemY - listY >= maxItems) {
                break;
            }
            tags.add(item[0]);
            String label = item[0] + "  " + item[1];
            boolean checked = "on".equalsIgnoreCase(item[2]);
            TCheckBox checkbox = addCheckBox(1, itemY, label, checked);
            checkboxes.add(checkbox);
            itemY++;
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
                    StringBuilder result = new StringBuilder();
                    String sep = options.getSeparator();
                    boolean first = true;
                    for (int i = 0; i < checkboxes.size(); i++) {
                        if (checkboxes.get(i).isChecked()) {
                            if (!first) {
                                result.append(sep);
                            }
                            result.append(tags.get(i));
                            first = false;
                        }
                    }
                    closeOk(result.toString());
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

        // Focus on the first checkbox
        if (!checkboxes.isEmpty()) {
            activate(checkboxes.get(0));
        }
    }
}
