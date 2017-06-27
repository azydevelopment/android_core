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

package com.arcdatum.android.core.renderscript;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Allocation.MipmapControl;
import android.support.v8.renderscript.RenderScript;

public class RenderScriptResource {

    private Bitmap mBmp;
    private Allocation mAllocation;

    public RenderScriptResource() {
    };

    public RenderScriptResource(RenderScript renderScript, RenderScriptResource cloneSrc) {
        Bitmap bmpSrc = cloneSrc.getBmp();
        initFromBmp(renderScript, bmpSrc.copy(bmpSrc.getConfig(), true));
    }

    public void initFromBmp(RenderScript renderScript, Bitmap bmp) {
        mBmp = bmp;

        if (mAllocation != null) {
            mAllocation.destroy();
        }
        mAllocation = Allocation.createFromBitmap(renderScript, bmp, MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED | Allocation.USAGE_SCRIPT);
    }

    public void commitBmp() {
        mAllocation.copyTo(mBmp);
    }

    public Allocation getAllocation() {
        return mAllocation;
    }

    public Bitmap getBmp() {
        commitBmp();
        return mBmp;
    }

    public int getWidth() {
        return mBmp.getWidth();
    }

    public int getHeight() {
        return mBmp.getHeight();
    }

}
