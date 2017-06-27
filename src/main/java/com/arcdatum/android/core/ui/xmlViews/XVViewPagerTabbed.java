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

package com.arcdatum.android.core.ui.xmlViews;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

import com.arcdatum.android.core.R;
import com.arcdatum.android.core.enhanced.ui.ViewPagerEnhanced;
import com.arcdatum.android.core.enhanced.ui.ViewPagerEnhanced.InterfaceViewPager;
import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.ui.xmlViews.XVTabBar.InterfaceTabBar;

public class XVViewPagerTabbed extends XmlViewBase implements InterfaceTabBar, InterfaceViewPager {
    private static final String TAG = HlprDbg.TAG_PREFIX + XVViewPagerTabbed.class.getSimpleName();

    public interface InterfaceViewPagerTabbed {
        public void onPageChanged(XVViewPagerTabbed view, int pageNew, int pageOld);

        public void onPageLanded(XVViewPagerTabbed view, int pageCur);
    }

    private InterfaceViewPagerTabbed mDel;

    private ViewPagerEnhanced mViewPager;
    private XVTabBar mTabBar;

    public XVViewPagerTabbed(Context context) {
        super(context);
    }

    public XVViewPagerTabbed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initCommon(Context context) {
        super.initCommon(context);
        mViewPager = (ViewPagerEnhanced) findViewById(R.id.ViewPager);
        mTabBar = (XVTabBar) findViewById(R.id.TabBar_SnapEdit);

        mViewPager.setDelegate(this);
        mTabBar.setDelegate(this);
    }

    @Override
    public int getXmlIdentifier() {
        return R.layout.xv_viewpager_tabbed;
    }

    public void setDelegate(InterfaceViewPagerTabbed del) {
        mDel = del;
    }

    public void reset() {
        HlprDbg.log(DBG_LVL.DEBUG, TAG);
    }

    @SuppressWarnings("deprecation")
    public void addTab(Context context, int drawableId) {
        Resources resources = context.getResources();
        mTabBar.addTab(context, resources.getDrawable(drawableId));
    }

    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    public void setOffscreenpageLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit);
    }

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    public void setCurrentItem(int index, boolean smoothScroll) {
        mViewPager.setCurrentItem(index, smoothScroll);
    }

    @Override
    public void onClickTabBarItem(XmlViewBase xmlView, int item) {
        mViewPager.setCurrentItem(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTabBar.setHintPos(position, positionOffset);
    }

    @Override
    public void onPageChanged(ViewPagerEnhanced view, int pageNew, int pageOld) {
        if (mDel != null) {
            mDel.onPageChanged(this, pageNew, pageOld);
        }
    }

    @Override
    public void onPageLanded(ViewPagerEnhanced view, int pageCur) {
        if (mDel != null) {
            mDel.onPageLanded(this, pageCur);
        }
    }
}
