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

import android.widget.Filter;

import java.lang.ref.WeakReference;
import java.util.List;

public class FilterEnhanced<T> extends Filter {

    public interface IDelFilterEnhanced<U> {
        List<U> onExecuteFilter(FilterEnhanced filter, CharSequence constraint);

        void onDoneFilter(FilterEnhanced filter, CharSequence constraint, List<U> listFiltered);
    }

    private WeakReference<IDelFilterEnhanced<T>> mDel = new WeakReference<>(null);

    public FilterEnhanced(IDelFilterEnhanced<T> del) {
        mDel = new WeakReference<>(del);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = null;

        IDelFilterEnhanced del = mDel.get();
        if (del != null) {
            results = new FilterResults();
            List<T> listFiltered = del.onExecuteFilter(this, constraint);
            results.count = listFiltered.size();
            results.values = listFiltered;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        IDelFilterEnhanced del = mDel.get();
        if (del != null && results != null) {
            del.onDoneFilter(this, constraint, (List<T>) results.values);
        }
    }
}
