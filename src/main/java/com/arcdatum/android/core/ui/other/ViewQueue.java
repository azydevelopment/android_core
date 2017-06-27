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
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.arcdatum.android.core.enhanced.other.AdapterEnhanced;
import com.arcdatum.android.core.helpers.HlprDbg;

public class ViewQueue extends FrameLayout {

    private static final String TAG = HlprDbg.TAG_PREFIX + ViewQueue.class.getSimpleName();

    private static final int DEPTH = 3;

    private AdapterEnhanced<?, ?> mAdapter;

    private int mPosCur = 0;

    private final DataSetObserver mObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            reset();
        }

        @Override
        public void onInvalidated() {
            removeAllViews();
        }
    };

    public ViewQueue(Context context) {
        super(context);
    }

    public ViewQueue(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewQueue(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewQueue(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        HlprDbg.log(TAG, Integer.toString(w) + "x" + Integer.toString(h));
        if (w > 0 && h > 0) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setAdapter(AdapterEnhanced<?, ?> adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mObserver);
    }

    public void reset() {
        HlprDbg.log(TAG);

        if (mAdapter != null) {
            removeAllViews();

            int count = mAdapter.getCount();

            int depth = Math.min(DEPTH, count);

            // TODO HACK: A little weird pre-populating the views and then using getView after the fact to load them
            // This is done because 'onsnapblurred' sometimes completes before 'addview' happens.

            // Generate and add dummy views
            for (int i = 0; i < depth; i++) {
                View view = mAdapter.acquireView();
                addView(view, 0);
            }

            // Actually populate the views
            for (int i = 0; i < depth; i++) {
                int pos = mPosCur + i;
                mAdapter.getView(pos, getChildAt(depth - 1 - i), this);
            }
        }
    }

    public int getFrontIndex() {
        return getChildCount() - 1;
    }

    public View getFront() {
        return getChildAt(getChildCount() - 1);
    }

    public View jumpTo(int pos) {
        mPosCur = pos;
        reset();
        return getFront();
    }

    public View rotate() {
        // Remove the top item
        int posFront = getChildCount() - 1;
        View viewFront = getChildAt(posFront);
        removeViewAt(posFront);

        int posToLoad = mPosCur + DEPTH;
        if (posToLoad < mAdapter.getCount()) {
            View view = mAdapter.getView(mPosCur + DEPTH, viewFront, this);
            addView(view, 0);
        } else {
            addView(viewFront, 0);
        }

        mPosCur++;

        // Return the new view at the front of the queue
        return getFront();
    }
}
