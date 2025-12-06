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

import jexer.TAction;
import jexer.TApplication;
import jexer.TButton;
import jexer.TLabel;

/**
 * YesNoDialog displays a question with Yes and No buttons.
 */
public class YesNoDialog extends BaseDialog {

    /**
     * Construct a new yes/no dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public YesNoDialog(final TApplication application,
                       final DialogOptions options,
                       final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int contentHeight = getHeight() - 6;
        int y = 1;
        for (String line : lines) {
            if (y <= contentHeight) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Add Yes and No buttons at the bottom
        String yesLabel = options.getYesLabel();
        String noLabel = options.getNoLabel();

        int yesWidth = yesLabel.length() + 4;
        int noWidth = noLabel.length() + 4;
        int totalWidth = yesWidth + noWidth + 4;
        int startX = (getWidth() - totalWidth) / 2;
        int buttonY = getHeight() - 4;

        TButton yesButton = addButton(yesLabel, startX, buttonY, new TAction() {
            public void DO() {
                closeOk("");
            }
        });

        addButton(noLabel, startX + yesWidth + 2, buttonY, new TAction() {
            public void DO() {
                closeCancel();
            }
        });

        // Set default button focus
        if ("no".equalsIgnoreCase(options.getDefaultButton())) {
            // Focus will be on No button by default tab order
        } else {
            activate(yesButton);
        }
    }
}
