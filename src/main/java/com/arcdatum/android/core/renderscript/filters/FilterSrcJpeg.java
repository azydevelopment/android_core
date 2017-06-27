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
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v8.renderscript.RenderScript;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.renderscript.RenderScriptResource;

public class FilterSrcJpeg extends FilterBase {

    private static final String TAG = HlprDbg.TAG_PREFIX + FilterSrcJpeg.class.getSimpleName();

    private byte[] mDataJpeg;

    private Rect mRectDecode;

    private int mWidthTar = 0;
    private int mHeightTar = 0;

    private int mRotation = 0;

    public FilterSrcJpeg(RenderScript renderScript) {
        super(renderScript);
    }

    @Override
    public void reset() {
        super.reset();
        setImgSrcJpeg(null);
        setRectDecode(null);
        setResTar(0, 0);
        setRotation(0);
    }

    @Override
    protected void execute() {
        super.execute();

        Bitmap bmpOut = null;
        try {
            Options meta = getImgMeta();

            Rect rectImg = new Rect(0, 0, meta.outWidth, meta.outHeight);

            if (rectImg.contains(mRectDecode)) {

                float ratioWidth = mRectDecode.width() / 2f / mWidthTar;
                float ratioHeight = mRectDecode.height() / 2f / mHeightTar;

                // Used to limit the amount of memory used during initial decoding
                int sampleSize = 1;
                float ratio = Math.min(ratioWidth, ratioHeight);
                while (ratio > 1) {
                    ratio /= 2;
                    sampleSize *= 2;
                }

                Options options = new Options();
                options.inSampleSize = sampleSize;

                // Only decode the square region that we want
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(mDataJpeg, 0, mDataJpeg.length, true);
                Bitmap bmpDecoded = decoder.decodeRegion(mRectDecode, options);
                decoder.recycle();

                // Rotate the image based on how the camera was being held during capture
                Matrix mat = new Matrix();
                mat.postRotate(mRotation);

                // Scale up to the desired preview size
                float scaleWidth = (float) mWidthTar / ((float) mRectDecode.width() / sampleSize);
                float scaleHeight = (float) mHeightTar / ((float) mRectDecode.height() / sampleSize);
                float scale = Math.max(scaleWidth, scaleHeight);
                mat.postScale(scale, scale);

                bmpOut = Bitmap.createBitmap(bmpDecoded, 0, 0, bmpDecoded.getWidth(), bmpDecoded.getHeight(), mat, true);

                bmpDecoded.recycle();
            }
        } catch (Exception e) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, e.getMessage());
            int[] colors = {
                    0xff00ff00
            };
            bmpOut = Bitmap.createBitmap(colors, 1, 1, Config.ARGB_8888);
        }

        mOutput = new RenderScriptResource();
        mOutput.initFromBmp(mRenderScript, bmpOut);
        setOutput(mOutput);
    }

    public Options getImgMeta() {
        if (mDataJpeg != null) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(mDataJpeg, 0, mDataJpeg.length, options);
            return options;
        } else {
            return null;
        }
    }

    public void setImgSrcJpeg(byte[] data) {
        mDataJpeg = data;
        invalidate(true);
    }

    public void setRectDecode(Rect rect) {
        // TODO BUG: This doesn't take rotation into account properly
        mRectDecode = rect;
        invalidate(true);
    }

    public void setResTar(int width, int height) {
        mWidthTar = width;
        mHeightTar = height;
        invalidate(true);
    }

    public void setRotation(int rotation) {
        mRotation = rotation;
        invalidate(true);
    }

}
