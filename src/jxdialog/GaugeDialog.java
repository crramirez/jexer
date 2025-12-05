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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import jexer.TAction;
import jexer.TApplication;
import jexer.TLabel;
import jexer.TProgressBar;

/**
 * GaugeDialog displays a progress bar that can be updated from stdin.
 */
public class GaugeDialog extends BaseDialog {

    /**
     * The progress bar widget.
     */
    private TProgressBar progressBar;

    /**
     * The label showing the text.
     */
    private TLabel textLabel;

    /**
     * Current percentage.
     */
    private int percent;

    /**
     * Whether to keep reading from stdin.
     */
    private volatile boolean running = true;

    /**
     * Construct a new gauge dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public GaugeDialog(final TApplication application,
                       final DialogOptions options,
                       final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        textLabel = addLabel(text, 1, 1);

        // Add progress bar
        int barY = getHeight() - 5;
        int barWidth = getWidth() - 4;
        percent = options.getPercentValue();

        progressBar = addProgressBar(1, barY, barWidth, percent);

        // Add percentage label
        addLabel(percent + "%", (getWidth() - 4) / 2, barY + 1);

        // Start a thread to read from stdin for updates
        Thread readerThread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in));
                String line;
                while (running && (line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("XXX")) {
                        // Start of new text block
                        StringBuilder newText = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            if (line.trim().equals("XXX")) {
                                break;
                            }
                            if (newText.length() > 0) {
                                newText.append("\n");
                            }
                            newText.append(line);
                        }
                        // Update text label (would need to be on UI thread)
                        // For simplicity, we just ignore text updates
                    } else {
                        try {
                            int newPercent = Integer.parseInt(line);
                            if (newPercent >= 0 && newPercent <= 100) {
                                percent = newPercent;
                                progressBar.setValue(percent);
                                if (percent >= 100) {
                                    // Auto-close when complete
                                    application.invokeLater(new Runnable() {
                                        public void run() {
                                            closeOk("");
                                        }
                                    });
                                    break;
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Ignore non-numeric input
                        }
                    }
                }
            } catch (Exception e) {
                // Reader closed or error
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Called when the window is closed.
     */
    @Override
    public void close() {
        running = false;
        super.close();
    }
}
