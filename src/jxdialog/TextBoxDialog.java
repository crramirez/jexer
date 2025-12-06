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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import jexer.TAction;
import jexer.TApplication;
import jexer.TButton;
import jexer.TText;

/**
 * TextBoxDialog displays the contents of a text file.
 */
public class TextBoxDialog extends BaseDialog {

    /**
     * The text widget.
     */
    private TText textWidget;

    /**
     * Construct a new text box dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public TextBoxDialog(final TApplication application,
                         final DialogOptions options,
                         final DialogRunner runner) {
        super(application, options, runner);

        // Read the file content
        String content = "";
        String filePath = options.getFilePath();
        if (filePath != null && !filePath.isEmpty()) {
            try {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    content = new String(Files.readAllBytes(file.toPath()));
                } else {
                    content = "Error: File not found: " + filePath;
                }
            } catch (IOException e) {
                content = "Error reading file: " + e.getMessage();
            }
        }

        // Add text widget
        int textWidth = getWidth() - 2;
        int textHeight = getHeight() - 6;

        textWidget = addText(content, 1, 1, textWidth, textHeight);

        // Add Exit button at the bottom
        String exitLabel = "Exit";
        int buttonWidth = exitLabel.length() + 4;
        int buttonX = (getWidth() - buttonWidth) / 2 - 1;
        int buttonY = getHeight() - 4;

        addButton(exitLabel, buttonX, buttonY, new TAction() {
            public void DO() {
                closeOk("");
            }
        });
    }
}
