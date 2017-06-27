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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.arcdatum.android.core.asyncTasks.AsyncTaskListening.OnCompletedListener;
import com.arcdatum.android.core.renderscript.RenderScriptResource;

public class ViewRenderScriptResource extends View {

    private RenderScriptResource mResource;

    private RectF mRectCropNorm;

    private Rect mRectFullscreen = new Rect();
    private Rect mRectBmp = new Rect();

    public ViewRenderScriptResource(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void fade(float alphaBefore, final float alphaAfter, long durationMillis, final OnCompletedListener<?> completed) {
        AlphaAnimation anim = new AlphaAnimation(alphaBefore, alphaAfter);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);

        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setAlpha(1f);
                // setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (completed != null) {
                    completed.onCompleted(null);
                }
                setAlpha(alphaAfter);
            }
        });

        startAnimation(anim);
    }

    public void setResource(RenderScriptResource resource) {
        mResource = resource;
    }

    public void setRectCrop(RectF rectCropNorm) {
        mRectCropNorm = rectCropNorm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResource != null) {

            Bitmap bmp = mResource.getBmp();

            if (bmp != null && !bmp.isRecycled()) {

                mRectFullscreen.set(0, 0, canvas.getWidth(), canvas.getWidth());
                mRectBmp.set(0, 0, bmp.getWidth(), bmp.getHeight());

                if (mRectCropNorm != null) {
                    canvas.scale(1 / mRectCropNorm.width(), 1 / mRectCropNorm.height());
                    canvas.translate(-mRectCropNorm.left * mRectFullscreen.width(), -mRectCropNorm.top * mRectFullscreen.height());
                }

                canvas.drawBitmap(bmp, mRectBmp, mRectFullscreen, null);
            }

        }
    }

}
