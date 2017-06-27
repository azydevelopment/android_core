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
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.arcdatum.android.core.helpers.HlprMath;
import com.arcdatum.android.core.renderscript.RenderScriptResource;

public class FilterGaussianBlur extends FilterBase {
    // private static final String TAG = HlprDbg.TAG_PREFIX + FilterGaussianBlur.class.getSimpleName();

    private static final float RADIUS_BLUR_NORM_MIN = 0.001f;
    private static final float RADIUS_BLUR_NORM_MAX = 1f;

    private ScriptIntrinsicBlur mScriptGaussianBlur;

    private float mScaleFactorMin;
    private float mRadiusBlurNorm;

    private float mScaleFactor = 1;

    private RenderScriptResource mRsResScaledIn;
    private RenderScriptResource mRsResScaledOut;

    public FilterGaussianBlur(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        mScriptGaussianBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        mScaleFactor = 1;

        mRsResScaledIn = null;
        mRsResScaledOut = null;

        setScaleFactorMin(1);
        setRadiusBlur(0);
    }

    public void setDependencies(FilterBase filterCoreInput) {
        setDependency(0, filterCoreInput);
    }

    public void setDependencies(FilterBase filterCoreInput, FilterBase[] filtersPassthrough) {
        setDependencies(filterCoreInput);
        setDependenciesPassthrough(filtersPassthrough);
    }

    @Override
    public void execute() {
        super.execute();

        if (mRadiusBlurNorm > RADIUS_BLUR_NORM_MIN) {
            RenderScriptResource input = mFilterDependencies[0].getOutput();

            float blurRadiusNorm = HlprMath.clamp(mRadiusBlurNorm, RADIUS_BLUR_NORM_MIN, RADIUS_BLUR_NORM_MAX);

            float scaleFactor = mScaleFactorMin;
            float blurRadiusScaled = blurRadiusNorm * input.getWidth() / mScaleFactorMin;

            // Loop until radius is below 25 pixels. Unfortunate RenderScript gaussian blur limitation (although probably for performance).
            while (blurRadiusScaled > 24) {
                scaleFactor *= 2;
                blurRadiusScaled /= 2;
            }

            if (scaleFactor != 1) {

                boolean scaleFactorChanged = (mScaleFactor != scaleFactor);

                int widthScaled = (int) (input.getWidth() / scaleFactor);
                int heightScaled = (int) (input.getHeight() / scaleFactor);

                // TODO PERF: A little bit crappy that in order to do a blur kernel larger than 25, need to create scaled bitmaps at runtime.

                if (mRsResScaledIn == null || scaleFactorChanged) {
                    mRsResScaledIn = new RenderScriptResource();
                    Bitmap bmpScaledInput = Bitmap.createScaledBitmap(input.getBmp(), widthScaled, heightScaled, true);
                    mRsResScaledIn.initFromBmp(mRenderScript, bmpScaledInput);
                }

                if (mRsResScaledOut == null || scaleFactorChanged) {
                    mRsResScaledOut = new RenderScriptResource();
                    Bitmap bmpScaledOutput = Bitmap.createBitmap(widthScaled, heightScaled, Config.ARGB_8888);
                    mRsResScaledOut.initFromBmp(mRenderScript, bmpScaledOutput);
                }

                mScaleFactor = scaleFactor;

                mScriptGaussianBlur.setInput(mRsResScaledIn.getAllocation());
                mScriptGaussianBlur.setRadius(blurRadiusScaled);
                mScriptGaussianBlur.forEach(mRsResScaledOut.getAllocation());

                Bitmap bmpFinal = Bitmap.createScaledBitmap(mRsResScaledOut.getBmp(), input.getWidth(), input.getHeight(), true);
                mOutput = new RenderScriptResource();
                mOutput.initFromBmp(mRenderScript, bmpFinal);

            } else {

                mScaleFactor = 1;

                if (mOutput == null) {
                    Bitmap bmp = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Config.ARGB_8888);
                    mOutput = new RenderScriptResource();
                    mOutput.initFromBmp(mRenderScript, bmp);
                }
                mScriptGaussianBlur.setInput(input.getAllocation());
                mScriptGaussianBlur.setRadius(blurRadiusScaled);
                mScriptGaussianBlur.forEach(mOutput.getAllocation());

            }

            // HlprDbg.log(TAG, Float.toString(blurRadiusNorm * input.getWidth()) + " | " + Float.toString(mScaleFactor) + " | "
            // + Float.toString(blurRadiusScaled));

            setOutput(mOutput);
        } else {
            passthrough();
        }

    }

    @Override
    public void passthrough() {
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

    public void setScaleFactorMin(float ratio) {
        mScaleFactorMin = ratio;
        invalidate(true);
    }

    public void setRadiusBlur(float radiusBlurNorm) {
        mRadiusBlurNorm = radiusBlurNorm;
        invalidate(true);
    }
}
