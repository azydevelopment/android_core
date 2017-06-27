/*
* Copyright (c) 2017 Andrew Yeung
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.arcdatum.android.core.enhanced.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager;

public class AlertDialogEnhanced extends AlertDialog {

    public AlertDialogEnhanced(Context context) {
        super(context);
    }

    public AlertDialogEnhanced(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public AlertDialogEnhanced(Context context, int theme) {
        super(context, theme);
    }

    public void setSoftInputMode(int mode) {
        getWindow().setSoftInputMode(mode);
    }

    public void setBackgroundResource(int resId) {
        getWindow().setBackgroundDrawableResource(resId);
    }

    public void setSize(int width, int height) {
        getWindow().setLayout(width, height);
    }

    public void setDimAmount(float dimNorm) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = dimNorm; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        getWindow().setAttributes(lp);
    }

}
