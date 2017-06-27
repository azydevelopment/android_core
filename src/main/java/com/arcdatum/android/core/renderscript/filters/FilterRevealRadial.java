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
import android.graphics.Bitmap.Config;
import android.support.v8.renderscript.RenderScript;

import com.arcdatum.android.core.renderscript.RenderScriptResource;
import com.noodlesandnaan.renderscript.scripts.RevealRadial.ScriptC_RevealRadial;

public class FilterRevealRadial extends FilterBase {

    // private static final String TAG = HlprDbg.TAG_PREFIX + FilterRevealRadial.class.getSimpleName();

    private ScriptC_RevealRadial mScriptRevealRadial;

    private float mRadiusNorm = 0;

    public FilterRevealRadial(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        mScriptRevealRadial = new ScriptC_RevealRadial(mRenderScript);
        setRadius(0);
    }

    public void setDependencies(FilterBase filterCoreInput, FilterBase filterBackgroundInput) {
        setDependency(0, filterCoreInput);
        setDependency(1, filterBackgroundInput);
    }

    public void setDependencies(FilterBase filterCoreInput, FilterBase filterBackgroundInput, FilterBase[] filtersPassthrough) {
        setDependency(0, filterCoreInput);
        setDependency(1, filterBackgroundInput);
        setDependenciesPassthrough(filtersPassthrough);
    }

    @Override
    protected void execute() {
        super.execute();

        if (mRadiusNorm > 0f) {

            RenderScriptResource inputFront = mFilterDependencies[0].getOutput();
            RenderScriptResource inputBack = mFilterDependencies[1].getOutput();

            if (mOutput == null) {
                Bitmap bmp = Bitmap.createBitmap(inputFront.getWidth(), inputFront.getHeight(), Config.ARGB_8888);
                mOutput = new RenderScriptResource();
                mOutput.initFromBmp(mRenderScript, bmp);
            }

            mScriptRevealRadial.invoke_setIORevealRadial(mScriptRevealRadial, inputFront.getAllocation(), inputBack.getAllocation(),
                    mOutput.getAllocation());
            mScriptRevealRadial.invoke_setParamsRevealRadial(mRadiusNorm);
            mScriptRevealRadial.invoke_execute();

            setOutput(mOutput);

        } else {
            passthrough();
        }
    }

    @Override
    protected void passthrough() {
        // Log.e(TAG, HlprDbg.getMethod());
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

    public void setRadius(float radiusNorm) {
        mRadiusNorm = radiusNorm;
        invalidate(true);
    }

}
