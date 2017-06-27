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

package com.arcdatum.android.core.helpers;

import android.net.Uri;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

import com.arcdatum.android.core.enhanced.other.UrlSpanEnhanced;
import com.arcdatum.android.core.enhanced.other.UrlSpanEnhanced.IDelUrlSpanEnhanced;

public class HlprSpans {

    public static Spannable enhanceUrlSpans(Spannable text, IDelUrlSpanEnhanced del) {
        URLSpan[] urlSpans = text.getSpans(0, text.length() - 1, URLSpan.class);
        for (int i = 0; i < urlSpans.length; i++) {
            URLSpan urlSpan = urlSpans[i];

            int start = text.getSpanStart(urlSpan);
            int end = text.getSpanEnd(urlSpan);

            UrlSpanEnhanced spanNew = new UrlSpanEnhanced(text.subSequence(start, end).toString(), Uri.parse(urlSpan.getURL()));

            // TODO HACK: Hardcoded color
            ForegroundColorSpan spanColor = new ForegroundColorSpan(0xff0088ff);

            text.removeSpan(urlSpan);
            text.setSpan(spanNew, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(spanColor, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanNew.setDelegate(del);
        }

        // Also reset the enhanced url spans in case the delegate has changed
        UrlSpanEnhanced[] urlSpansEnhanced = text.getSpans(0, text.length() - 1, UrlSpanEnhanced.class);
        for (int i = 0; i < urlSpansEnhanced.length; i++) {
            UrlSpanEnhanced urlSpanEnhanced = urlSpansEnhanced[i];

            int start = text.getSpanStart(urlSpanEnhanced);
            int end = text.getSpanEnd(urlSpanEnhanced);

            UrlSpanEnhanced spanNew = new UrlSpanEnhanced(text.subSequence(start, end).toString(), urlSpanEnhanced.getUri());

            // TODO HACK: Hardcoded color
            ForegroundColorSpan spanColor = new ForegroundColorSpan(0xff0088ff);

            text.removeSpan(urlSpanEnhanced);
            text.setSpan(spanNew, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(spanColor, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanNew.setDelegate(del);
        }

        return text;
    }

    public static Spannable deenhanceUrlSpans(Spannable text) {
        UrlSpanEnhanced[] enhancedSpans = text.getSpans(0, text.length() - 1, UrlSpanEnhanced.class);

        for (int i = 0; i < enhancedSpans.length; i++) {
            UrlSpanEnhanced enhancedSpan = enhancedSpans[i];

            int start = text.getSpanStart(enhancedSpan);
            int end = text.getSpanEnd(enhancedSpan);

            // Remove all other spans on top of the url link
            ParcelableSpan[] otherSpans = text.getSpans(start, end, ParcelableSpan.class);
            for (int j = 0; j < otherSpans.length; j++) {
                ParcelableSpan otherSpan = otherSpans[j];
                text.removeSpan(otherSpan);
            }

            URLSpan urlSpan = new URLSpan(enhancedSpan.getUri().toString());
            text.setSpan(urlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return text;
    }

    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if (source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i + 1);
    }

}
