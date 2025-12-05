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

import jexer.TApplication;
import jexer.TLabel;
import jexer.TTimer;

/**
 * InfoBoxDialog displays a message without buttons (auto-close after display).
 */
public class InfoBoxDialog extends BaseDialog {

    /**
     * Construct a new info box dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public InfoBoxDialog(final TApplication application,
                         final DialogOptions options,
                         final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int contentHeight = getHeight() - 4;
        int y = 1;
        for (String line : lines) {
            if (y <= contentHeight) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Auto-close after a short delay (like dialog's infobox)
        // The infobox is meant to be displayed briefly
        application.addTimer(500, false, new jexer.TAction() {
            public void DO() {
                closeOk("");
            }
        });
    }
}
