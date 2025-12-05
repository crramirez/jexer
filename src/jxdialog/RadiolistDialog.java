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
import jexer.TRadioButton;
import jexer.TRadioGroup;

/**
 * RadiolistDialog displays a list with radio buttons.
 */
public class RadiolistDialog extends BaseDialog {

    /**
     * The radio group.
     */
    private TRadioGroup radioGroup;

    /**
     * The tags for items.
     */
    private List<String> tags;

    /**
     * Construct a new radiolist dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public RadiolistDialog(final TApplication application,
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

        // Build radio button items
        tags = new ArrayList<>();

        int listY = y + 1;
        int maxItems = getHeight() - listY - 5;
        int listHeight = Math.min(options.getMenuItems().size(), maxItems);

        // Create radio group
        radioGroup = addRadioGroup(1, listY, getWidth() - 4, "");

        int defaultSelection = -1;
        int idx = 0;
        for (String[] item : options.getMenuItems()) {
            if (idx >= maxItems) {
                break;
            }
            tags.add(item[0]);
            String label = item[0] + "  " + item[1];
            radioGroup.addRadioButton(label);
            if ("on".equalsIgnoreCase(item[2]) && defaultSelection < 0) {
                defaultSelection = idx + 1; // Radio buttons are 1-indexed
            }
            idx++;
        }

        // Set default selection
        if (defaultSelection > 0) {
            radioGroup.setSelected(defaultSelection);
        } else if (!tags.isEmpty()) {
            radioGroup.setSelected(1);
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
                    int selected = radioGroup.getSelected();
                    if (selected > 0 && selected <= tags.size()) {
                        closeOk(tags.get(selected - 1));
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

        // Focus on the radio group
        activate(radioGroup);
    }
}
