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

/**
 * JxDialog provides a clone of the Linux 'dialog' command using Jexer
 * as the TUI backend. This enables bash scripts to display Turbo Vision-like
 * dialog boxes for user interaction.
 *
 * <p>Example usage:</p>
 * <pre>
 * jxdialog --title "Hello" --msgbox "Welcome to JxDialog!" 10 40
 * jxdialog --yesno "Do you want to continue?" 10 40
 * result=$(jxdialog --inputbox "Enter your name:" 10 40 2>&amp;1)
 * </pre>
 *
 * <p>Supported dialog types:</p>
 * <ul>
 * <li>msgbox - Display a message</li>
 * <li>yesno - Yes/No confirmation</li>
 * <li>infobox - Brief information display</li>
 * <li>inputbox - Text input</li>
 * <li>passwordbox - Password input</li>
 * <li>menu - Menu selection</li>
 * <li>checklist - Multiple selection with checkboxes</li>
 * <li>radiolist - Single selection with radio buttons</li>
 * <li>gauge - Progress bar</li>
 * <li>fselect - File selection</li>
 * <li>dselect - Directory selection</li>
 * <li>calendar - Date selection</li>
 * <li>textbox - Display text file contents</li>
 * </ul>
 */
package jxdialog;
