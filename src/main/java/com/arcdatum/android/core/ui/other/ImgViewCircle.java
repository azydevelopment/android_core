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

package com.arcdatum.android.core.ui.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;

public class ImgViewCircle extends ImageView {

    private static final String TAG = HlprDbg.TAG_PREFIX + ImgViewCircle.class.getSimpleName();

    private Paint mPaint = new Paint();

    public ImgViewCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {

        try {

            Drawable drawable = getDrawable();

            if (drawable == null || getWidth() == 0 || getHeight() == 0) {
                return;
            }

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            BitmapShader shader;
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            mPaint.setAntiAlias(true);
            mPaint.setShader(shader);

            float widthBmp = bitmap.getWidth();
            float heightBmp = bitmap.getHeight();

            float widthCanvas = canvas.getWidth();
            float heightCanvas = canvas.getHeight();

            float scaleWidth = widthCanvas / widthBmp;
            float scaleHeight = heightCanvas / heightBmp;

            float scale = Math.max(scaleWidth, scaleHeight);

            float radius = Math.min(widthBmp, heightBmp) / 2f;

            canvas.save();
            canvas.scale(scale, scale);
            canvas.drawCircle(widthBmp / 2f, heightBmp / 2f, radius, mPaint);
            canvas.restore();

        } catch (Exception e) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, e.getMessage());
            super.draw(canvas);
        }
    }
}
