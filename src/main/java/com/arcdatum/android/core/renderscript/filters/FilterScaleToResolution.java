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

public class FilterScaleToResolution extends FilterBase {

    private int mScaleResolutionX;
    private int mScaleResolutionY;

    public FilterScaleToResolution(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        setScaleResolution(0, 0);
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

        if (mScaleResolutionX > 0 && mScaleResolutionY > 0) {
            RenderScriptResource input = mFilterDependencies[0].getOutput();

            if (mOutput == null) {
                Bitmap bmpInputScaled = Bitmap.createScaledBitmap(input.getBmp(), mScaleResolutionX, mScaleResolutionY, true);
                mOutput = new RenderScriptResource();
                mOutput.initFromBmp(mRenderScript, bmpInputScaled);
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

    public void setScaleResolution(int x, int y) {
        mScaleResolutionX = x;
        mScaleResolutionY = y;
        invalidate(true);
    }

    @Override
    public void invalidate(boolean recursive) {
        // clean every invalidation since Bitmap.createScaledBitmap creates new bitmap every time
        // potential for canvas drawing based optimization to recreate only when bmp size changes
        cleanupOutput(false, false);
        super.invalidate(recursive);
    }

}
