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

package com.arcdatum.android.core.renderscript.filters;

import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;

import com.arcdatum.android.core.renderscript.RenderScriptResource;

public class FilterSrcBmp extends FilterBase {

    public FilterSrcBmp(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        setImgSrc(null);
    }

    @Override
    public void cleanupOutput(boolean upwardRecursive, boolean force) {
        // this is a filter source, don't clean it up
        // destroying the resource can only be done by explicitly calling setImgSrc with null
    }

    public void setImgSrc(Bitmap bmp) {
        if (bmp != null) {
            if (mOutput == null) {
                mOutput = new RenderScriptResource();
            }
            mOutput.initFromBmp(mRenderScript, bmp);
            setOutput(mOutput);
            setOutputPassthrough(mOutput);
        } else {
            setOutput(null);
            setOutputPassthrough(null);
        }
        invalidate(true);
    }
}
