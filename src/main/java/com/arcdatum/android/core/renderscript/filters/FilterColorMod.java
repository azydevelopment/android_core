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
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

import com.arcdatum.android.core.helpers.HlprMath;
import com.arcdatum.android.core.renderscript.RenderScriptResource;
import com.noodlesandnaan.renderscript.scripts.ColorMod.ScriptC_ColorMod;

public class FilterColorMod extends FilterBase {

    private ScriptC_ColorMod mScriptColorMod;

    public float mHueNorm;
    public float mSaturationNorm;
    public float mContrastNorm;
    public float mBrightnessNorm;
    public float mTemperatureNorm;

    private ColorMatrix mMatColorMod;

    private ColorMatrix mMatHue;
    private ColorMatrix mMatSaturation;
    private ColorMatrix mMatContrast;
    private ColorMatrix mMatBrightness;
    private ColorMatrix mMatTemperature;

    private ColorMatrixColorFilter mColorFilter;

    private Paint mPaintColorMat;

    private Allocation mAllocationMatColor;

    public FilterColorMod(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();

        mScriptColorMod = new ScriptC_ColorMod(mRenderScript);

        mHueNorm = 0;
        mSaturationNorm = 0;
        mContrastNorm = 0;
        mBrightnessNorm = 0;
        mTemperatureNorm = 0;

        mMatColorMod = new ColorMatrix();

        mMatHue = new ColorMatrix();
        mMatSaturation = new ColorMatrix();
        mMatContrast = new ColorMatrix();
        mMatBrightness = new ColorMatrix();
        mMatTemperature = new ColorMatrix();

        mColorFilter = new ColorMatrixColorFilter(new ColorMatrix());

        mPaintColorMat = new Paint();
        mPaintColorMat.setColorFilter(mColorFilter);

        setHue(0);
        setSaturation(0);
        setContrast(0);
        setBrightness(0);
        setTemperature(0);
    }

    public void setDependencies(FilterBase filterCoreInput) {
        setDependency(0, filterCoreInput);
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

        mMatColorMod.reset();
        mMatColorMod.postConcat(mMatHue);
        mMatColorMod.postConcat(mMatSaturation);
        mMatColorMod.postConcat(mMatTemperature);
        mMatColorMod.postConcat(mMatContrast);
        mMatColorMod.postConcat(mMatBrightness);

        if (mAllocationMatColor == null) {
            mAllocationMatColor = Allocation.createSized(mRenderScript, Element.F32(mRenderScript), 20);
        }

        mAllocationMatColor.copyFrom(mMatColorMod.getArray());

        mScriptColorMod.invoke_setIOColorMod(mScriptColorMod, input.getAllocation(), mOutput.getAllocation());
        mScriptColorMod.bind_gMatColor(mAllocationMatColor);
        mScriptColorMod.invoke_execute();

        setOutput(mOutput);
    }

    @Override
    protected void passthrough() {
        super.passthrough();
        setOutputPassthrough(mFilterDependencies[0].getOutput());
    }

    public void setHue(float hueNorm) {
        mHueNorm = HlprMath.clamp(hueNorm, -1, 1);

        float hue = mHueNorm * (float) Math.PI;

        float cosVal = (float) Math.cos(hue);
        float sinVal = (float) Math.sin(hue);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        float[] mat = new float[]{
                lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG),
                lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0, lumR + cosVal * (-lumR) + sinVal * (0.143f),
                lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
                lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG),
                lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f
        };
        mMatHue.set(mat);

        invalidate(true);
    }

    public void setSaturation(float saturationNorm) {
        mMatSaturation.reset();
        mSaturationNorm = HlprMath.clamp(saturationNorm, -1f, 1f);
        mMatSaturation.setSaturation(mSaturationNorm + 1);
        invalidate(true);
    }

    public void setContrast(float contrastNorm) {
        mContrastNorm = HlprMath.clamp(contrastNorm, -1f, 1f);

        float scale = mContrastNorm + 1.f;
        float translate = (-.5f * scale + .5f);

        float[] mat = new float[]{
                scale, 0, 0, 0, translate, 0, scale, 0, 0, translate, 0, 0, scale, 0, translate, 0, 0, 0, 1, 0
        };

        mMatContrast.set(mat);
        invalidate(true);
    }

    public void setBrightness(float brightnessNorm) {
        mBrightnessNorm = HlprMath.clamp(brightnessNorm, -1f, 1f);
        float brightness = mBrightnessNorm;
        float[] mat = new float[]{
                1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0
        };

        mMatBrightness.set(mat);
        invalidate(true);
    }

    public void setTemperature(float temperatureNorm) {
        mTemperatureNorm = HlprMath.clamp(temperatureNorm, -1f, 1f);

        float r = 1f;
        float g = 1f;
        float b = 1f;

        if (mTemperatureNorm < 0) {
            r = 1f + mTemperatureNorm / 20f;
            b = 1f - mTemperatureNorm / 2f;
            g = 1f + mTemperatureNorm / 20f;
        } else {
            r = 1f + mTemperatureNorm / 3f;
            g = 1f + mTemperatureNorm / 7f;
            b = 1f - mTemperatureNorm / 6f;
        }

        float[] mat = new float[]{
                r, 0, 0, 0, 0, 0, g, 0, 0, 0, 0, 0, b, 0, 0, 0, 0, 0, 1, 0
        };

        mMatTemperature.set(mat);
        invalidate(true);
    }
}
