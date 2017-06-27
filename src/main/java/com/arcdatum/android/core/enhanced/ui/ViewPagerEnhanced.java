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
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class ViewPagerEnhanced extends ViewPager {

    // private static final String TAG = HlprDebug.TAG_PREFIX + ViewPagerBase.class.getSimpleName();

    private PageListener mPageListener;

    public interface InterfaceViewPager {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageChanged(ViewPagerEnhanced view, int pageNew, int pageOld);

        public void onPageLanded(ViewPagerEnhanced view, int pageCur);
    }

    public ViewPagerEnhanced(Context context) {
        super(context);
        initCommon();
    }

    public ViewPagerEnhanced(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCommon();
    }

    public void initCommon() {
        mPageListener = new PageListener(this);
        addOnPageChangeListener(mPageListener);
    }

    public void setDelegate(InterfaceViewPager del) {
        mPageListener.setDelegate(del);
    }

    class PageListener extends SimpleOnPageChangeListener {

        private ViewPagerEnhanced mParent;
        private InterfaceViewPager mDel;

        private int mPageCur = 0;

        public PageListener(ViewPagerEnhanced viewPager) {
            mParent = viewPager;
        }

        public void setDelegate(InterfaceViewPager del) {
            mDel = del;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case SCROLL_STATE_IDLE:
                    if (mDel != null) {
                        mDel.onPageLanded(mParent, getCurrentItem());
                    }
                    break;
                case SCROLL_STATE_DRAGGING:
                    break;
                case SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mDel != null) {
                mDel.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            int pageNew = position + Math.round(positionOffset);
            if (mPageCur != pageNew) {
                onPageChanged(pageNew, mPageCur);
                mPageCur = pageNew;
            }
        }

        @Override
        public void onPageSelected(int position) {
        }

        public void onPageChanged(int pageNew, int pageOld) {
            if (mDel != null) {
                mDel.onPageChanged(mParent, pageNew, pageOld);
            }
        }

    }

}
