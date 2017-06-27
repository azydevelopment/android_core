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

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

public class FrameLayoutRevealCircular extends FrameLayout {

    private boolean mShowing = false;

    private AnimatorListener mListenerShow;
    private AnimatorListener mListenerHide;

    public FrameLayoutRevealCircular(Context context) {
        super(context);
        initCommon();
    }

    public FrameLayoutRevealCircular(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCommon();
    }

    public FrameLayoutRevealCircular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCommon();
    }

    public FrameLayoutRevealCircular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCommon();
    }

    private void initCommon() {
        setVisibility(View.INVISIBLE);

        mListenerShow = new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setShowing(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setShowing(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

        mListenerHide = new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
                setShowing(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setVisibility(View.INVISIBLE);
                setShowing(false);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    private void setShowing(boolean showing) {
        mShowing = showing;
    }

    public boolean isShowing() {
        return mShowing;
    }

    public Animator getShowAnim(int centerX, int centerY, float radiusStart, float radiusEnd) {
        Animator anim = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Create the animator for this view (the start radius is zero)
            anim = ViewAnimationUtils.createCircularReveal(this, centerX, centerY, radiusStart, radiusEnd);
        } else {
            // Fade-in fallback for anything below API 21
            float alphaStart = 0;
            float alphaEnd = 1;

            if (radiusStart > radiusEnd) {
                alphaStart = 1;
                alphaEnd = 0;
            }
            anim = ObjectAnimator.ofFloat(this, "alpha", alphaStart, alphaEnd);
        }

        if (radiusStart < radiusEnd) {
            anim.addListener(mListenerShow);
        } else {
            anim.addListener(mListenerHide);
        }

        return anim;
    }
}
