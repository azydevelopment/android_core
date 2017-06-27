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

package com.arcdatum.android.core.enhanced.other;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.arcdatum.android.core.interfaces.IDataObservable;
import com.arcdatum.android.core.interfaces.ISaveable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public abstract class AdapterEnhanced<T, U extends View> extends BaseAdapter implements
        IDataObservable,
        ISaveable {

    private WeakReference<IDelDataObservable> mDel = new WeakReference<>(null);

    private Context mContext;

    private boolean mValid = true;

    public AdapterEnhanced(Context context) {
        mContext = context;
    }

    public void setDelegateObserver(IDelDataObservable observer) {
        mDel = new WeakReference<>(observer);
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = acquireView();
        }

        return populateView((U) view, getItem(position), position, parent);
    }

    public abstract U acquireView();

    public abstract View populateView(U view, T data, int position, ViewGroup parent);

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        IDelDataObservable del = mDel.get();
        if (del != null) {
            del.onChanged(this);
        }
    }

    // IDataObservable

    @Override
    public boolean isValid() {
        return mValid;
    }

    @Override
    public void setValid(boolean valid) {
        mValid = valid;
    }

    // ISaveable

    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }

}
