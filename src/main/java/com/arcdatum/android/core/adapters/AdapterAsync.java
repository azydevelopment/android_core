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

package com.arcdatum.android.core.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.arcdatum.android.core.enhanced.other.AdapterEnhanced;
import com.arcdatum.android.core.asyncTasks.AsyncTaskListening.OnProgressListener;
import com.arcdatum.android.core.asyncTasks.AsyncTaskListening.OnCompletedListener;

import java.util.List;

public abstract class AdapterAsync<T, U, V extends View> extends AdapterEnhanced<U, V> implements
        OnProgressListener<T>,
        OnCompletedListener<List<U>> {

    public AdapterAsync(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        if (isValid()) {
            return getCountLoaded();
        } else {
            return 0;
        }
    }

    public abstract int getCountLoaded();

    @Override
    public abstract U getItem(int position);

    @Override
    public abstract V acquireView();

    @Override
    public abstract View populateView(V view, U data, int position, ViewGroup parent);

    // OnProgressListener

    @Override
    public void onProgress(T[] progress) {
    }

    // OnCompletedListener

    @Override
    public void onCompleted(List<U> result) {
        setValid(true);
    }
}
