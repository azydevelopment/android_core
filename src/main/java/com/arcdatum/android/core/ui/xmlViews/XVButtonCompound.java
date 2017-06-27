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
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.arcdatum.android.core.R;

public class XVButtonCompound extends XmlViewBase {

    private Button mButton;

    private TextView mTextViewText;
    private TextView mTextViewSubtext;

    public XVButtonCompound(Context context) {
        super(context);
    }

    public XVButtonCompound(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initCommon(Context context) {
        super.initCommon(context);
        mButton = (Button) findViewById(R.id.Button);
        mTextViewText = (TextView) findViewById(R.id.Text);
        mTextViewSubtext = (TextView) findViewById(R.id.Subtext);
    }

    @Override
    protected int getXmlIdentifier() {
        return R.layout.xv_button_compound;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mButton.setEnabled(enabled);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }

    public void setSelected(boolean selected) {
        Resources res = getContext().getResources();
        // TODO IMPLEMENT: Color change
//        if (selected) {
//            mTextViewText.setTextColor(res.getColor(R.color.common_graydark));
//        } else {
//            mTextViewText.setTextColor(res.getColor(R.color.common_graylight));
//        }
    }

    public String getText() {
        return mTextViewText.getText().toString();
    }

    public String getSubtext() {
        return mTextViewSubtext.getText().toString();
    }

    public void setContent(String text, String subtext) {
        mTextViewText.setText(text);
        mTextViewSubtext.setText(subtext);
    }

    public void setText(String text) {
        mTextViewText.setText(text);
    }

    public void setSubtext(String subtext) {
        mTextViewSubtext.setText(subtext);
    }

}
