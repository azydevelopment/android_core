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
import com.noodlesandnaan.renderscript.scripts.Vignette.ScriptC_Vignette;

public class FilterVignette extends FilterBase {
    // private static final String TAG = HlprDebug.TAG_PREFIX + FilterVignette.class.getSimpleName();

    private ScriptC_Vignette mScriptVignette;

    private float mIntensityNorm;
    private float mRadiusInnerNorm;
    private float mRadiusOuterNorm;

    public FilterVignette(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        mScriptVignette = new ScriptC_Vignette(mRenderScript);
        setIntensity(0);
        setRadiusInner(0.15f);
        setRadiusOuter(0.73f);
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

        if (mIntensityNorm > 0.02f) {
            RenderScriptResource input = mFilterDependencies[0].getOutput();

            if (mOutput == null) {
                Bitmap bmp = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Config.ARGB_8888);
                mOutput = new RenderScriptResource();
                mOutput.initFromBmp(mRenderScript, bmp);
            }

            mScriptVignette.invoke_setIOVignette(mScriptVignette, input.getAllocation(), mOutput.getAllocation());
            mScriptVignette.invoke_setParamsVignette(mIntensityNorm, mRadiusInnerNorm, mRadiusOuterNorm);
            mScriptVignette.invoke_execute();

            setOutput(mOutput);
        } else {
            passthrough();
        }
    }

    @Override
    public void cleanupOutput(boolean upwardRecursive, boolean force) {
        super.cleanupOutput(upwardRecursive, force);
    }

    @Override
    protected void passthrough() {
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

    public void setIntensity(float intensityNorm) {
        mIntensityNorm = intensityNorm;
        invalidate(true);
    }

    public void setRadiusInner(float radiusInnerNorm) {
        mRadiusInnerNorm = radiusInnerNorm;
        invalidate(true);
    }

    public void setRadiusOuter(float radiusOuterNorm) {
        mRadiusOuterNorm = radiusOuterNorm;
        invalidate(true);
    }

}
