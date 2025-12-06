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

/**
 * DialogRunner executes the dialog based on the parsed options.
 */
public class DialogRunner {

    /**
     * Exit code for OK/Yes.
     */
    public static final int EXIT_OK = 0;

    /**
     * Exit code for Cancel/No.
     */
    public static final int EXIT_CANCEL = 1;

    /**
     * Exit code for Help.
     */
    public static final int EXIT_HELP = 2;

    /**
     * Exit code for Extra button.
     */
    public static final int EXIT_EXTRA = 3;

    /**
     * Exit code for ESC or error.
     */
    public static final int EXIT_ESC = 255;

    /**
     * The dialog options.
     */
    private final DialogOptions options;

    /**
     * The result from the dialog.
     */
    private String result = "";

    /**
     * The exit code.
     */
    private int exitCode = EXIT_ESC;

    /**
     * Construct with options.
     *
     * @param options the dialog options
     */
    public DialogRunner(final DialogOptions options) {
        this.options = options;
    }

    /**
     * Run the dialog.
     *
     * @return the exit code
     * @throws Exception if there's an error running the dialog
     */
    public int run() throws Exception {
        try {
            DialogApplication app = new DialogApplication(options, this);
            (new Thread(app)).start();

            // Wait for the application to finish using proper wait/notify
            synchronized (this) {
                while (!app.isFinished()) {
                    wait();
                }
            }

            app.restoreConsole();

            // Output the result to the configured output stream
            if (!result.isEmpty()) {
                options.getOutput().print(result);
            }

            return exitCode;

        } catch (Exception e) {
            throw new DialogException("Error running dialog: " + e.getMessage(), e);
        }
    }

    /**
     * Set the result.
     *
     * @param result the result string
     */
    public void setResult(final String result) {
        this.result = result;
    }

    /**
     * Get the result.
     *
     * @return the result string
     */
    public String getResult() {
        return result;
    }

    /**
     * Set the exit code.
     *
     * @param exitCode the exit code
     */
    public void setExitCode(final int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Get the exit code.
     *
     * @return the exit code
     */
    public int getExitCode() {
        return exitCode;
    }
}
