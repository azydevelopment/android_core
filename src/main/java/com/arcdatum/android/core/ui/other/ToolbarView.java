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
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.arcdatum.android.core.R;

public class ToolbarView extends RelativeLayout {

    // private static final String TAG = HlprDbg.TAG_PREFIX + ToolbarView.class.getSimpleName();

    public enum TOOLBAR_VISIBILITY {
        NONE, COMPLEMENTARY_ONLY, ALL
    }

    private static final int ANIMATION_TOOLBAR_SHOW_DURATION = 120;
    private static final int ANIMATION_TOOLBAR_HIDE_DURATION = 120;

    private static final int ANIMATION_TOOLBAR_MAIN_SWAP_DURATION = 0;

    private static final int ANIMATION_SHADOW_SHOW_DURATION = 300;
    private static final int ANIMATION_SHADOW_HIDE_DURATION = 300;

    private View mBackground;

    private View mToolbarContainer;
    private Toolbar mToolbar;

    private FrameLayout mToolbarViewContainerMain;
    private FrameLayout mToolbarViewContainerComplementary;

    private View mToolbarViewMainCur;
    private View mToolbarViewComplementaryCur;

    private View mViewShadow;

    private int mResIdNavIcon;

    private boolean mBgHidden = false;
    private boolean mShadowHidden = false;

    private int mMarginTopCur = 0;

    private ViewPropertyAnimator mAnimatorBg;;
    private ViewPropertyAnimator mAnimatorShadow;

    public ToolbarView(Context context) {
        super(context);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackground = findViewById(R.id.Background);

        mToolbarContainer = findViewById(R.id.ToolbarContainer);
        mToolbar = (Toolbar) findViewById(R.id.Toolbar);
        mToolbarViewContainerMain = (FrameLayout) findViewById(R.id.ToolbarViewMain);
        mToolbarViewContainerComplementary = (FrameLayout) findViewById(R.id.ToolbarViewComplementary);

        mViewShadow = findViewById(R.id.View_Shadow);

        // TODO FEATURE: Is it really that impossible to get a nice translucent status bar working under API v21? The gradient status bar in 4.4 looks
        // bad with white icons.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarContainer.setPadding(0, getStatusBarHeight(), 0, 0);
        }

    }

    // Use this method to get the height without accessory views (eg. shadow view)
    public int getToolbarHeight() {
        // Need to check if the current complementary view is null otherwise fading out complementary view might still contribute.
        if (mToolbarViewComplementaryCur != null) {
            mToolbarContainer.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            return mToolbarContainer.getMeasuredHeight();
        } else {
            mToolbar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            return mToolbar.getMeasuredHeight() + mToolbarContainer.getPaddingTop();
        }
    }

    public void setNavigationIcon(int resId, boolean animated) {
        if (mResIdNavIcon != resId) {

            if (animated && mResIdNavIcon != 0) {
                Drawable backgrounds[] = new Drawable[2];
                Resources res = getResources();

                backgrounds[0] = res.getDrawable(mResIdNavIcon);
                backgrounds[1] = res.getDrawable(resId);

                TransitionDrawable transition = new TransitionDrawable(backgrounds);
                mToolbar.setNavigationIcon(transition);

                transition.startTransition(ANIMATION_SHADOW_SHOW_DURATION);
            } else {
                mToolbar.setNavigationIcon(resId);
            }

            mResIdNavIcon = resId;
        }
    }

    public void setNavigationOnClickListener(OnClickListener listener) {
        mToolbar.setNavigationOnClickListener(listener);
    }

    public void setViewMain(View view) {
        if (mToolbarViewMainCur != view) {
            // Animate old view out
            final View viewOld = mToolbarViewMainCur;
            if (viewOld != null) {
                viewOld.animate().alpha(0).setDuration(ANIMATION_TOOLBAR_MAIN_SWAP_DURATION).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewOld.setVisibility(View.GONE);
                        mToolbarViewContainerMain.removeView(viewOld);
                    }
                });
            }

            // Animate new view in
            if (view != null) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                view.setAlpha(0);
                view.setVisibility(View.VISIBLE);
                mToolbarViewContainerMain.addView(view);
                view.animate().alpha(1).setDuration(ANIMATION_TOOLBAR_MAIN_SWAP_DURATION).setListener(null);
            }

            // Keep record of what the current main view is for when we change it later.
            mToolbarViewMainCur = view;
        }
    }

    public void setViewComplementary(View view) {
        if (mToolbarViewComplementaryCur != view) {

            // // Animate old view out
            // final View viewOld = mToolbarViewComplementaryCur;
            // if (viewOld != null) {
            // viewOld.animate().alpha(0).setDuration(ANIMATION_TOOLBAR_MAIN_SWAP_DURATION).setListener(new AnimatorListenerAdapter() {
            // @Override
            // public void onAnimationEnd(Animator animation) {
            // viewOld.setVisibility(View.GONE);
            // mToolbarViewContainerComplementary.removeView(viewOld);
            // }
            // });
            // }

            mToolbarViewContainerComplementary.removeView(mToolbarViewComplementaryCur);

            // Animate new view in
            if (view != null) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                view.setAlpha(0);
                view.setVisibility(View.VISIBLE);
                mToolbarViewContainerComplementary.addView(view);
                view.animate().alpha(1).setDuration(ANIMATION_TOOLBAR_MAIN_SWAP_DURATION).setListener(null);
            }

            // Keep record of what the current complementary view is for when we change it later.
            mToolbarViewComplementaryCur = view;
        }
    }

    public void setVisibility(TOOLBAR_VISIBILITY visibility) {
        int marginFrom = mMarginTopCur;

        int marginTo;
        switch (visibility) {
            case NONE:
                marginTo = -getHeight();
                break;
            case COMPLEMENTARY_ONLY:
                marginTo = -mToolbar.getHeight();
                break;
            case ALL:
                marginTo = 0;
                break;
            default:
                marginTo = 0;
                break;
        }

        if (marginTo != marginFrom) {

            int duration;
            Interpolator interpolator;
            if (marginTo < marginFrom) {
                duration = ANIMATION_TOOLBAR_HIDE_DURATION;
                interpolator = new AccelerateInterpolator();
            } else {
                duration = ANIMATION_TOOLBAR_SHOW_DURATION;
                interpolator = new DecelerateInterpolator();
            }

            animateTopMargin(marginFrom, marginTo, duration, interpolator);
            mMarginTopCur = marginTo;

        }

    }

    public void hideBackground(boolean hide, boolean animated) {
        if (hide != mBgHidden) {
            // HlprDbg.log(DBG_LVL.ERROR, TAG, Boolean.toString(hide));

            float alphaNew = 0;

            if (hide) {
                alphaNew = 0f;
            } else {
                alphaNew = 1;
            }

            if (mAnimatorBg != null) {
                mAnimatorBg.cancel();
            }

            if (animated) {
                if (mBackground.getAlpha() != alphaNew) {
                    mAnimatorBg = mBackground.animate().alpha(alphaNew).setDuration(ANIMATION_SHADOW_SHOW_DURATION);
                    mAnimatorBg.start();
                }
            } else {
                mBackground.setAlpha(alphaNew);
            }

        }
        mBgHidden = hide;
        hideShadow(hide, true);
    }

    public void hideShadow(boolean hide, boolean animated) {
        if (hide != mShadowHidden) {
            float alphaNew = 0;
            int duration = 0;

            if (hide) {
                alphaNew = 0f;
                duration = ANIMATION_SHADOW_HIDE_DURATION;
            } else {
                alphaNew = 1;
                duration = ANIMATION_SHADOW_SHOW_DURATION;
            }

            if (mAnimatorShadow != null) {
                mAnimatorShadow.cancel();
            }

            if (animated) {
                if (mViewShadow.getAlpha() != alphaNew) {
                    mAnimatorShadow = mViewShadow.animate().alpha(alphaNew).setDuration(duration);
                    mAnimatorShadow.start();
                }
            } else {
                mViewShadow.setAlpha(alphaNew);
            }
        }

        mShadowHidden = hide;
    }

    public void animateTopMargin(final int oldMargin, final int newMargin, int duration, Interpolator interpolator) {

        // Start margin animation
        Animation animMargin = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // Can we assume that the layout params given to us will always subclass MarginLayoutParams?
                MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
                params.topMargin = (int) (oldMargin + (newMargin - oldMargin) * interpolatedTime);
                setLayoutParams(params);
            }
        };

        animMargin.setDuration(duration);
        animMargin.setInterpolator(interpolator);
        startAnimation(animMargin);

        // Start alpha animation for the Toolbar itself
        float alphaFrom = 1;
        float alphaTo = 0;

        if (oldMargin < newMargin) {
            alphaFrom = 0;
            alphaTo = 1;
        }

        AlphaAnimation animAlpha = new AlphaAnimation(alphaFrom, alphaTo);
        animAlpha.setDuration(duration);
        animAlpha.setInterpolator(interpolator);
        animAlpha.setFillAfter(true);
        mToolbar.startAnimation(animAlpha);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
