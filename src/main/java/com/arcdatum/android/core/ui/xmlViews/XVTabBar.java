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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arcdatum.android.core.R;

import java.util.ArrayList;

public class XVTabBar extends XmlViewBase {

    public interface InterfaceTabBar {
        public void onClickTabBarItem(XmlViewBase xmlView, int item);
    }

    private InterfaceTabBar mDel;

    private ArrayList<View> mTabs = new ArrayList<View>();

    private LinearLayout mRoot;
    private View mViewHintBar;

    public XVTabBar(Context context) {
        super(context);
    }

    public XVTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initCommon(Context context) {
        super.initCommon(context);
        mRoot = (LinearLayout) findViewById(R.id.Xv_TabBar_Root);
        mViewHintBar = findViewById(R.id.Xv_TabView_HintBar);
    }

    @Override
    public int getXmlIdentifier() {
        return R.layout.xv_tabbar;
    }

    public void setDelegate(InterfaceTabBar del) {
        mDel = del;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateHintBarWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewHintBar.requestLayout();
    }

    public void addTab(Context context, Drawable drawable) {
        ImageView imgView = new ImageView(context);
        imgView.setImageDrawable(drawable);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        imgView.setLayoutParams(params);
        imgView.setClickable(true);
        imgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTab(v);
            }
        });

        mTabs.add(imgView);

        mRoot.addView(imgView);
        mRoot.invalidate();

        updateHintBarWidth();
    }

    public int getNumTabs() {
        return mTabs.size();
    }

    private void updateHintBarWidth() {
        int numTabs = getNumTabs();
        int width = getWidth();
        if (numTabs > 0 && width > 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mViewHintBar.getLayoutParams();
            params.width = width / numTabs;
            mViewHintBar.setLayoutParams(params);
        }
    }

    public void setHintPos(float position, float positionOffset) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mViewHintBar.getLayoutParams();
        float pos = params.width * (position + positionOffset);
        params.leftMargin = (int) pos;
        mViewHintBar.setLayoutParams(params);
    }

    public void onClickTab(View view) {
        if (mDel != null) {
            mDel.onClickTabBarItem(this, mTabs.indexOf(view));
        }
    }

}
