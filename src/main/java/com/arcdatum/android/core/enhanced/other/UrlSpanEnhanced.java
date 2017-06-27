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

package com.arcdatum.android.core.enhanced.other;

import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.lang.ref.WeakReference;

public class UrlSpanEnhanced extends ClickableSpan {

    public interface IDelUrlSpanEnhanced {
        void onClickHyperlink(UrlSpanEnhanced span, View view, String uriText, Uri uri);
    }

    private WeakReference<IDelUrlSpanEnhanced> mDel = new WeakReference<IDelUrlSpanEnhanced>(null);

    private String mText;
    private Uri mUri;

    public UrlSpanEnhanced(String text, Uri uri) {
        mText = text;
        mUri = uri;
    }

    public void setDelegate(IDelUrlSpanEnhanced del) {
        mDel = new WeakReference<>(del);
    }

    public Uri getUri() {
        return mUri;
    }

    @Override
    public void onClick(View view) {
        IDelUrlSpanEnhanced del = mDel.get();
        if (del != null) {
            del.onClickHyperlink(this, view, mText, mUri);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
//        super.updateDrawState(ds);
//        ds.setTypeface(Typeface.create(ds.getTypeface(), Typeface.NORMAL));
    }

}
