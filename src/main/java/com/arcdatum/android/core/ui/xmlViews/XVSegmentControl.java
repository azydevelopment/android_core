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
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.arcdatum.android.core.R;

public class XVSegmentControl extends XmlViewBase {

    // private static final String TAG = HlprDbg.TAG_PREFIX + XVSegmentControl.class.getSimpleName();

    public interface InterfaceSegmentControl {
        // Returns whether the item should be selected or not
        public boolean onClickItem(XVSegmentControl control, XVButtonCompound button, int index);
    }

    private InterfaceSegmentControl mDel;

    private LinearLayout mRoot;

    private XVButtonCompound mSegmentSelectedCur;

    public XVSegmentControl(Context context) {
        super(context);
    }

    public XVSegmentControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initCommon(Context context) {
        super.initCommon(context);
        mRoot = (LinearLayout) findViewById(R.id.SegmentControl_Root);

        // int padding = (int) (13f * Resources.getSystem().getDisplayMetrics().density);
        // mRoot.setPadding(0, padding, 0, padding);

        // mRoot.setBackgroundColor(0xff0088ff);
    }

    @Override
    protected int getXmlIdentifier() {
        return R.layout.xv_segmentcontrol;
    }

    public void setDelegate(InterfaceSegmentControl del) {
        mDel = del;
    }

    public void addSegment(XVButtonCompound button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
        button.setLayoutParams(params);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO HACK: Hmm...this seems pretty hacky...nested getParent calls.
                onClickItem((XVButtonCompound) v.getParent().getParent());
            }
        });

        mRoot.addView(button);
    }

    public void selectItem(int index) {
        onClickItem((XVButtonCompound) mRoot.getChildAt(index));
    }

    public void onClickItem(XVButtonCompound segmentButton) {
        if (segmentButton != mSegmentSelectedCur && mDel != null) {
            // if (mDel != null && view != mButtonSelectedCur) {

            int index = mRoot.indexOfChild(segmentButton);
            boolean shouldSelect = mDel.onClickItem(this, segmentButton, index);
            if (shouldSelect) {
                if (mSegmentSelectedCur != null) {
                    mSegmentSelectedCur.setSelected(false);
                }
                segmentButton.setSelected(shouldSelect);
                mSegmentSelectedCur = segmentButton;
            }
        }
    }

}
