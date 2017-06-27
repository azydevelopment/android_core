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

package com.arcdatum.android.core.enhanced.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.arcdatum.android.core.R;

public class ImgButtonEnhanced extends ImageButton {

    private AnimationSet mAnimIn;
    private AnimationSet mAnimOut;

    private boolean mShowing = false;

    public ImgButtonEnhanced(Context context) {
        super(context);
        initCommon(context);
    }

    public ImgButtonEnhanced(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCommon(context);
    }

    public ImgButtonEnhanced(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCommon(context);
    }

    public ImgButtonEnhanced(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCommon(context);
    }

    public void initCommon(Context context) {
        mAnimIn = new AnimationSet(false);
        mAnimIn.addAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_in_scale));
        mAnimIn.setDuration(130);
        mAnimIn.setStartOffset(50);

        mAnimOut = new AnimationSet(false);
        mAnimOut.addAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_out_scale));
        mAnimOut.setDuration(80);
        mAnimOut.setStartOffset(0);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            if (!mShowing) {
                mAnimIn.setAnimationListener(null);
                mShowing = true;
                startAnimation(mAnimIn);
                setVisibility(View.VISIBLE);
            }
        } else {
            if (mShowing) {

                mAnimOut.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setVisibility(View.GONE);
                    }
                });
                mShowing = false;
                startAnimation(mAnimOut);
            }
        }
    }
}
