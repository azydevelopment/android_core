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
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class ViewPagerFading extends FrameLayout {

    private ArrayList<View> mPages = new ArrayList<View>();

    private int mPageCur = 0;

    private Animation mAnimIn;
    private Animation mAnimOut;

    public ViewPagerFading(Context context) {
        super(context);
        initCommon(context);
    }

    public ViewPagerFading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCommon(context);
    }

    public void initCommon(Context context) {
        initAnimations(500);
    }

    public void initAnimations(long durationMillis) {
        mAnimIn = new AlphaAnimation(0f, 1f);
        mAnimIn.setInterpolator(new DecelerateInterpolator());
        mAnimIn.setDuration(durationMillis);
        mAnimOut = new AlphaAnimation(1f, 0f);
        mAnimOut.setInterpolator(new DecelerateInterpolator());
        mAnimOut.setDuration(durationMillis);
    }

    public int getCurrentItem() {
        return mPageCur;
    }

    public void addPage(View page) {
        mPages.add(page);
        addView(page);
        page.setVisibility(View.GONE);
    }

    public int pageNext(boolean animate) {
        if (setCurrentItem(mPageCur + 1, animate)) {
            return mPageCur;
        } else {
            return -1;
        }
    }

    public int pagePrev(boolean animate) {
        if (setCurrentItem(mPageCur - 1, animate)) {
            return mPageCur;
        } else {
            return -1;
        }
    }

    public boolean setCurrentItem(int index, boolean animate) {
        if (index >= 0 && index < mPages.size()) {
            pageDisable(mPageCur, animate);
            pageEnable(index, animate);
            mPageCur = index;
            return true;
        } else {
            return false;
        }
    }

    private void pageDisable(int index, boolean animate) {
        final View page = mPages.get(index);
        if (animate) {
            mAnimOut.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    page.setVisibility(View.GONE);
                }
            });
            page.startAnimation(mAnimOut);
        }
        page.setVisibility(View.GONE);
    }

    private void pageEnable(int index, boolean animate) {
        View page = mPages.get(index);
        if (animate) {
            page.startAnimation(mAnimIn);
        }
        page.setVisibility(View.VISIBLE);
    }
}
