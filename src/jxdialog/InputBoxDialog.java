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
import jexer.TField;
import jexer.TLabel;
import jexer.TPasswordField;

/**
 * InputBoxDialog displays a text input field with OK and Cancel buttons.
 */
public class InputBoxDialog extends BaseDialog {

    /**
     * The input field.
     */
    private TField inputField;

    /**
     * Construct a new input box dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     * @param isPassword true for password input (hidden characters)
     */
    @SuppressWarnings("this-escape")
    public InputBoxDialog(final TApplication application,
                          final DialogOptions options,
                          final DialogRunner runner,
                          final boolean isPassword) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int y = 1;
        for (String line : lines) {
            if (y < getHeight() - 6) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Add input field
        int fieldY = getHeight() - 6;
        int fieldWidth = getWidth() - 4;

        if (isPassword && !options.isInsecure()) {
            inputField = addPasswordField(1, fieldY, fieldWidth, false,
                    options.getInitialValue(), new TAction() {
                        public void DO() {
                            closeOk(inputField.getText());
                        }
                    }, null);
        } else {
            inputField = addField(1, fieldY, fieldWidth, false,
                    options.getInitialValue(), new TAction() {
                        public void DO() {
                            closeOk(inputField.getText());
                        }
                    }, null);
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
                    closeOk(inputField.getText());
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

        // Focus on the input field
        activate(inputField);
    }
}
