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

package com.arcdatum.android.core.ui.other;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class SeekBarStrict extends AppCompatSeekBar {

    // private static final String TAG = HlprDbg.TAG_PREFIX + SeekBarStrict.class.getSimpleName();

    private Drawable mThumb;
    private boolean mValidTouch = false;

    private OnSeekBarChangeListener mListener;

    public SeekBarStrict(Context context) {
        super(context);
    }

    public SeekBarStrict(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SeekBarStrict(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Rect rect = new Rect(mThumb.getBounds());
                rect.inset((int) (-rect.width() * 0.2), (int) (-rect.height() * 0.2));
                if (rect.contains((int) x, (int) y)) {
                    if (mListener != null) {
                        mListener.onStartTrackingTouch(this);
                    }
                    mValidTouch = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mValidTouch) {
                    return super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mListener != null) {
                    mListener.onStopTrackingTouch(this);
                }
                if (mValidTouch) {
                    mValidTouch = false;
                    return super.onTouchEvent(event);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mListener != null) {
                    mListener.onStopTrackingTouch(this);
                }
                if (mValidTouch) {
                    mValidTouch = false;
                    return super.onTouchEvent(event);
                }
                break;
        }

        return false;
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mListener = l;
        super.setOnSeekBarChangeListener(l);
    }

}
