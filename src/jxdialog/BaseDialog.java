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
import jexer.TWindow;
import jexer.event.TKeypressEvent;
import static jexer.TKeypress.*;

/**
 * BaseDialog is the base class for all jxdialog dialog windows.
 */
public abstract class BaseDialog extends TWindow {

    /**
     * The dialog options.
     */
    protected final DialogOptions options;

    /**
     * The dialog runner to report results to.
     */
    protected final DialogRunner runner;

    /**
     * Construct a new dialog window.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    protected BaseDialog(final TApplication application,
                         final DialogOptions options,
                         final DialogRunner runner) {
        super(application, options.getTitle(),
              0, 0,
              calculateWidth(options, application),
              calculateHeight(options, application),
              CENTERED | MODAL);

        this.options = options;
        this.runner = runner;
    }

    /**
     * Calculate the dialog width.
     *
     * @param options the dialog options
     * @param app the application
     * @return the width
     */
    protected static int calculateWidth(final DialogOptions options,
                                        final TApplication app) {
        int width = options.getWidth();
        if (width <= 0) {
            // Auto width: use 80% of screen width
            width = (app.getScreen().getWidth() * 80) / 100;
        }
        // Ensure minimum width
        if (width < 20) {
            width = 20;
        }
        // Cap at screen width
        if (width > app.getScreen().getWidth() - 2) {
            width = app.getScreen().getWidth() - 2;
        }
        return width;
    }

    /**
     * Calculate the dialog height.
     *
     * @param options the dialog options
     * @param app the application
     * @return the height
     */
    protected static int calculateHeight(final DialogOptions options,
                                         final TApplication app) {
        int height = options.getHeight();
        if (height <= 0) {
            // Auto height: use 80% of screen height
            height = (app.getScreen().getHeight() * 80) / 100;
        }
        // Ensure minimum height
        if (height < 8) {
            height = 8;
        }
        // Cap at screen height
        if (height > app.getScreen().getHeight() - 2) {
            height = app.getScreen().getHeight() - 2;
        }
        return height;
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbEsc)) {
            // ESC pressed - cancel
            runner.setExitCode(DialogRunner.EXIT_ESC);
            getApplication().closeWindow(this);
            getApplication().exit();
            return;
        }
        super.onKeypress(keypress);
    }

    /**
     * Close with OK result.
     *
     * @param result the result string
     */
    protected void closeOk(final String result) {
        runner.setResult(result);
        runner.setExitCode(DialogRunner.EXIT_OK);
        getApplication().closeWindow(this);
        getApplication().exit();
    }

    /**
     * Close with Cancel result.
     */
    protected void closeCancel() {
        runner.setExitCode(DialogRunner.EXIT_CANCEL);
        getApplication().closeWindow(this);
        getApplication().exit();
    }

    /**
     * Close with ESC/error result.
     */
    protected void closeEsc() {
        runner.setExitCode(DialogRunner.EXIT_ESC);
        getApplication().closeWindow(this);
        getApplication().exit();
    }
}
