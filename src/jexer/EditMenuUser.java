/*
 * Jexer - Java Text User Interface
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2019 Kevin Lamonte
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
 *
 * @author Kevin Lamonte [kevin.lamonte@gmail.com]
 * @version 1
 */
package jexer;

/**
 * EditMenuUser is used by TApplication to enable/disable edit menu items.  A
 * widget that supports these functions should define an onCommand method
 * that operates on cmCut, cmCopy, cmPaste, and cmClear.
 */
public interface EditMenuUser {

    /**
     * Check if cut to clipboard is supported.
     *
     * @return true if cut to clipboard is supported
     */
    public boolean isEditMenuCut();

    /**
     * Check if copy to clipboard is supported.
     *
     * @return true if copy to clipboard is supported
     */
    public boolean isEditMenuCopy();

    /**
     * Check if paste from clipboard is supported.
     *
     * @return true if paste from clipboard is supported
     */
    public boolean isEditMenuPaste();

    /**
     * Check if clear selection is supported.
     *
     * @return true if clear selection is supported
     */
    public boolean isEditMenuClear();

}