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
import com.noodlesandnaan.renderscript.scripts.ScaleNoop.ScriptC_Noop;

public class FilterNoop extends FilterBase {

    // private static final String TAG = HlprDbg.TAG_PREFIX + FilterNoop.class.getSimpleName();

    private ScriptC_Noop mScriptNoop;

    public FilterNoop(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        mScriptNoop = new ScriptC_Noop(mRenderScript);
    }

    public void setDependencies(FilterBase filterCoreInput) {
        setDependency(0, filterCoreInput);
    }

    public void setDependencies(FilterBase filterCoreInput, FilterBase[] filtersPassthrough) {
        setDependency(0, filterCoreInput);
        setDependenciesPassthrough(filtersPassthrough);
    }

    @Override
    protected void execute() {
        super.execute();

        RenderScriptResource input = mFilterDependencies[0].getOutput();

        if (mOutput == null) {
            Bitmap bmp = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Config.ARGB_8888);
            mOutput = new RenderScriptResource();
            mOutput.initFromBmp(mRenderScript, bmp);
        }

        mScriptNoop.invoke_setIONoop(mScriptNoop, input.getAllocation(), mOutput.getAllocation());
        mScriptNoop.invoke_execute();

        setOutput(mOutput);
    }

    @Override
    protected void passthrough() {
        // Log.e(TAG, HlprDbg.getMethod());
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

}
