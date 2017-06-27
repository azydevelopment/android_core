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

import android.support.v8.renderscript.RenderScript;

public class FilterMux extends FilterBase {

    // private static final String TAG = HlprDbg.TAG_PREFIX + FilterMux.class.getSimpleName();

    private int mIndex;

    public FilterMux(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        setMuxIndex(0);
    }

    @Override
    public void cleanupOutput(boolean upwardRecursive, boolean force) {
        // do not clean anything; this is a routing class only but pass up the recursion
        for (int i = 0; i < mFilterDependencies.length; i++) {
            FilterBase dependency = mFilterDependencies[i];
            if (dependency != null) {
                dependency.cleanupOutput(upwardRecursive, force);
            }
        }
    }

    @Override
    protected void execute() {
        super.execute();
        setOutput(mFilterDependencies[mIndex].getOutput());
    }

    public void setDependencies(FilterBase[] filterInputs) {
        for (int i = 0; i < filterInputs.length; i++) {
            setDependency(i, filterInputs[i]);
        }
    }

    @Override
    protected void passthrough() {
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

    public void setMuxIndex(int muxIndex) {
        mIndex = muxIndex;
        invalidate(true);
    }

}
