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

import java.util.Calendar;

import jexer.TAction;
import jexer.TApplication;
import jexer.TButton;
import jexer.TCalendar;
import jexer.TLabel;

/**
 * CalendarDialog displays a calendar for date selection.
 */
public class CalendarDialog extends BaseDialog {

    /**
     * The calendar widget.
     */
    private TCalendar calendar;

    /**
     * Construct a new calendar dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public CalendarDialog(final TApplication application,
                          final DialogOptions options,
                          final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        if (!text.isEmpty()) {
            addLabel(text, 1, 1);
        }

        // Calculate calendar position
        int calX = (getWidth() - 28) / 2;
        if (calX < 1) {
            calX = 1;
        }
        int calY = 3;

        // Add calendar widget
        calendar = addCalendar(calX, calY, new TAction() {
            public void DO() {
                // Date selected - close dialog
                Calendar cal = calendar.getValue();
                String result = String.format("%02d/%02d/%04d",
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR));
                closeOk(result);
            }
        });

        // Set initial date if specified
        if (options.getYear() > 0) {
            Calendar cal = Calendar.getInstance();
            int year = options.getYear();
            int month = options.getMonth() > 0 ? options.getMonth() - 1 : cal.get(Calendar.MONTH);
            int day = options.getDay() > 0 ? options.getDay() : cal.get(Calendar.DAY_OF_MONTH);
            cal.set(year, month, day);
            calendar.setValue(cal);
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
                    Calendar cal = calendar.getValue();
                    String result = String.format("%02d/%02d/%04d",
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.YEAR));
                    closeOk(result);
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

        // Focus on the calendar
        activate(calendar);
    }
}
