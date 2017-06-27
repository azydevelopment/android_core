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

package com.arcdatum.android.core.touch;

import android.graphics.PointF;

import java.util.ArrayList;

public class TouchTracker {

    private int mNumTouches = 0;
    private ArrayList<Integer> mTouchIds = new ArrayList<Integer>();
    private ArrayList<PointF> mTouchPosStarts = new ArrayList<PointF>();

    public void addTouch(int id, PointF posStart) {
        mTouchIds.add(id);
        mTouchPosStarts.add(posStart);
        mNumTouches++;
    }

    public boolean removeTouch(int index) {
        if (index >= 0 && index < mNumTouches) {
            mTouchIds.remove(index);
            mTouchPosStarts.remove(index);
            mNumTouches--;
            return true;
        }
        return false;
    }

    public void clearTouches() {
        mTouchIds.clear();
        mTouchPosStarts.clear();
        mNumTouches = 0;
    }

    public int getNumTouches() {
        return mNumTouches;
    }

    public int getIndexOf(int id) {
        for (int i = 0; i < mTouchIds.size(); i++) {
            if (mTouchIds.get(i) == id) {
                return i;
            }
        }
        return -1;
    }

    public PointF getTouchPosStart(int index) {
        if (index >= 0 && index < mNumTouches) {
            return mTouchPosStarts.get(index);
        }
        return null;
    }

    public int getTouchId(int index) {
        if (index >= 0 && index < mNumTouches) {
            return mTouchIds.get(index);
        }
        return -1;
    }

}
