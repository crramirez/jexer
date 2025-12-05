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
import jexer.TDesktop;
import jexer.TWindow;
import jexer.bits.CellAttributes;
import jexer.bits.Color;

/**
 * DialogApplication is the TApplication that hosts the dialog windows.
 */
public class DialogApplication extends TApplication {

    /**
     * The dialog options.
     */
    private final DialogOptions options;

    /**
     * The dialog runner to report results to.
     */
    private final DialogRunner runner;

    /**
     * Whether the application has finished.
     */
    private volatile boolean finished = false;

    /**
     * Construct with options.
     *
     * @param options the dialog options
     * @param runner the dialog runner
     * @throws Exception if there's an error creating the application
     */
    @SuppressWarnings("this-escape")
    public DialogApplication(final DialogOptions options, final DialogRunner runner)
            throws Exception {
        super(BackendType.XTERM);

        this.options = options;
        this.runner = runner;

        // Remove default desktop for cleaner look
        setDesktop(null);

        // Hide menu bar and status bar for dialog mode
        setHideMenuBar(true);
        setHideStatusBar(true);

        // Set backtitle if provided
        if (options.getBacktitle() != null && !options.getBacktitle().isEmpty()) {
            getBackend().setTitle(options.getBacktitle());
        }

        // Create the appropriate dialog based on type
        createDialog();
    }

    /**
     * Create the dialog window based on the dialog type.
     *
     * @throws Exception if there's an error creating the dialog
     */
    private void createDialog() throws Exception {
        switch (options.getDialogType()) {
        case MSGBOX:
            new MsgBoxDialog(this, options, runner);
            break;

        case YESNO:
            new YesNoDialog(this, options, runner);
            break;

        case INFOBOX:
            new InfoBoxDialog(this, options, runner);
            break;

        case INPUTBOX:
            new InputBoxDialog(this, options, runner, false);
            break;

        case PASSWORDBOX:
            new InputBoxDialog(this, options, runner, true);
            break;

        case MENU:
            new MenuDialog(this, options, runner);
            break;

        case CHECKLIST:
            new ChecklistDialog(this, options, runner);
            break;

        case RADIOLIST:
            new RadiolistDialog(this, options, runner);
            break;

        case GAUGE:
            new GaugeDialog(this, options, runner);
            break;

        case FSELECT:
            new FileSelectDialog(this, options, runner, false);
            break;

        case DSELECT:
            new FileSelectDialog(this, options, runner, true);
            break;

        case CALENDAR:
            new CalendarDialog(this, options, runner);
            break;

        case TEXTBOX:
            new TextBoxDialog(this, options, runner);
            break;

        default:
            throw new DialogException("Unsupported dialog type: " + options.getDialogType());
        }
    }

    /**
     * Check if the application has finished.
     *
     * @return true if finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Called when we are done with the application.
     */
    @Override
    public void onExit() {
        finished = true;
        synchronized (runner) {
            runner.notifyAll();
        }
    }

    /**
     * Draw the background with the backtitle if set.
     */
    @Override
    protected void onPreDraw() {
        super.onPreDraw();

        // Draw backtitle at the top if specified
        if (options.getBacktitle() != null && !options.getBacktitle().isEmpty()) {
            CellAttributes attr = new CellAttributes();
            attr.setForeColor(Color.WHITE);
            attr.setBackColor(Color.BLUE);
            attr.setBold(true);

            String bt = options.getBacktitle();
            int x = (getScreen().getWidth() - bt.length()) / 2;
            if (x < 0) {
                x = 0;
            }
            getScreen().putStringXY(x, 0, bt, attr);
        }
    }

    /**
     * Get the dialog options.
     *
     * @return the options
     */
    public DialogOptions getDialogOptions() {
        return options;
    }

    /**
     * Get the dialog runner.
     *
     * @return the runner
     */
    public DialogRunner getDialogRunner() {
        return runner;
    }
}
