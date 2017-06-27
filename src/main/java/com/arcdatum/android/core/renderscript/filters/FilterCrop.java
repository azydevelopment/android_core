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
import android.graphics.RectF;
import android.support.v8.renderscript.RenderScript;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprMath;
import com.arcdatum.android.core.renderscript.RenderScriptResource;

public class FilterCrop extends FilterBase {

    private static final String TAG = HlprDbg.TAG_PREFIX + FilterCrop.class.getSimpleName();

    private RectF mRectCrop;

    public FilterCrop(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        setRectCrop(null);
    }

    public void setDependencies(FilterBase filterCoreInput) {
        setDependency(0, filterCoreInput);
    }

    public void setDependencies(FilterBase filterCoreInput, FilterBase[] filtersPassthrough) {
        setDependencies(filterCoreInput);
        setDependenciesPassthrough(filtersPassthrough);
    }

    @Override
    protected void execute() {
        super.execute();

        if (mRectCrop != null) {

            RenderScriptResource input = mFilterDependencies[0].getOutput();

            int inputWidth = input.getWidth();
            int inputHeight = input.getHeight();

            if (mOutput == null) {
                int x = (int) HlprMath.clamp(inputWidth * mRectCrop.left, 0, inputWidth);
                int y = (int) HlprMath.clamp(inputHeight * mRectCrop.top, 0, inputHeight);
                int width = (int) HlprMath.clamp(inputWidth * mRectCrop.width(), 0, inputWidth - x);
                int height = (int) HlprMath.clamp(inputHeight * mRectCrop.height(), 0, inputHeight - y);

                // HlprDbg.log(TAG, "Crop | Pos: " + Integer.toString(x) + "x" + Integer.toString(y) + " | Size: " + Integer.toString(width) + "x"
                // + Integer.toString(height));

                Bitmap bmpInputCropped = Bitmap.createBitmap(input.getBmp(), x, y, width, height);
                mOutput = new RenderScriptResource();
                mOutput.initFromBmp(mRenderScript, bmpInputCropped);
            }

            setOutput(mOutput);
        } else {
            passthrough();
        }
    }

    @Override
    protected void passthrough() {
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

    public void setRectCrop(RectF rectCropNorm) {
        mRectCrop = rectCropNorm;
        invalidate(true);
    }

    @Override
    public void invalidate(boolean recursive) {
        // clean every invalidation since Bitmap.createBitmap creates a new bitmap every time
        // potential for canvas drawing based optimization to recreate only when bmp size changes
        cleanupOutput(false, false);
        super.invalidate(recursive);
    }

}
