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
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class AdapterStreamingAsync<T, U extends View> extends AdapterStreaming<T, U> {

    private SparseArray<AsyncTask<Void, Void, ?>> mTasksLoadChunk;

    public AdapterStreamingAsync(Context context, int maxNumChunks, int rangeActiveChunksHalf, int chunkSize) {
        super(context, maxNumChunks, rangeActiveChunksHalf, chunkSize);
        mTasksLoadChunk = new SparseArray<>();
    }

    @Override
    public void clearCache() {
        super.clearCache();
        for (int i = 0; i < mTasksLoadChunk.size(); i++) {
            int key = mTasksLoadChunk.keyAt(i);
            mTasksLoadChunk.get(key).cancel(true);
        }
    }

    protected void addTaskLoadChunk(int index, AsyncTask<Void, Void, ?> task) {
        mTasksLoadChunk.put(index, task);
    }

    protected void removeTaskLoadChunk(int index) {
        mTasksLoadChunk.remove(index);
    }

    public void loadChunk(int index) {
        if (mTasksLoadChunk.get(index) == null) {
            loadChunkAsync(index);
        }
    }

    public abstract void loadChunkAsync(int index);

    public abstract U acquireView();

    public abstract View populateView(U view, T data, int position, ViewGroup parent);

}
