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
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.arcdatum.android.core.R;

public class FloatingActionButtonEnhanced extends FloatingActionButton {

    public static interface AnimationListenerFab {
        public void onAnimationEnd();
    }

    private AnimationSet mAnimIn;
    private AnimationSet mAnimOut;

    private boolean mShowing = false;

    private int mColorCur = 0;
    private int mResIdIconCur = 0;

    public FloatingActionButtonEnhanced(Context context) {
        super(context);
        initCommon(context);
    }

    public FloatingActionButtonEnhanced(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCommon(context);
    }

    public void initCommon(Context context) {
        mAnimIn = new AnimationSet(false);
        mAnimIn.addAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_in_scale));
        mAnimIn.addAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_in_rotate));
        mAnimIn.setDuration(180);
        mAnimIn.setStartOffset(300);

        mAnimOut = new AnimationSet(false);
        mAnimOut.addAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_out_scale));
        mAnimOut.addAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_out_rotate));
        mAnimOut.setDuration(150);
        mAnimOut.setStartOffset(200);

        setVisibility(View.GONE);
    }

    public void setVisible(boolean visible, final AnimationListenerFab listener) {
        if (visible) {
            if (!mShowing) {

                mAnimIn.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (listener != null) {
                            listener.onAnimationEnd();
                        }
                    }
                });
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
                        if (listener != null) {
                            listener.onAnimationEnd();
                        }
                    }
                });
                mShowing = false;
                startAnimation(mAnimOut);

            }
        }
    }

    public void setColor(int color) {
        mColorCur = color;

        int[][] states = new int[][] {
                new int[] {}
        };
        int[] colors = new int[] {
                color
        };
        ColorStateList colorList = new ColorStateList(states, colors);
        setBackgroundTintList(colorList);
    }

    public void setIcon(int resId) {
        mResIdIconCur = resId;
        setImageResource(resId);
    }

    public void changeAppearance(final int color, final int resIdIcon) {
        if (mShowing) {
            if (resIdIcon != mResIdIconCur || color != mColorCur)
                setVisible(false, new AnimationListenerFab() {
                    @Override
                    public void onAnimationEnd() {

                        // TODO HACK: Animation end event is called too early.
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIcon(resIdIcon);
                                setColor(color);
                                setVisible(true, null);
                            }
                        }, 20);

                    }
                });
        } else {
            setIcon(resIdIcon);
            setColor(color);
            setVisible(true, null);
        }
    }

}
