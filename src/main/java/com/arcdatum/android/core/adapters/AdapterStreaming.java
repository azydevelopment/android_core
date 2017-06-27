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
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;

import com.arcdatum.android.core.enhanced.other.AdapterEnhanced;

import java.util.List;

public abstract class AdapterStreaming<T, U extends View> extends AdapterEnhanced<T, U> {

    private int mNumItems = 0;

    private LruCache<Integer, List<T>> mCacheChunks;

    private int mRangeActiveChunksHalf = 0;

    public int mChunkSize = 0;

    public AdapterStreaming(Context context, int maxNumChunks, int rangeActiveChunksHalf, int chunkSize) {
        super(context);
        mCacheChunks = new LruCache<>(maxNumChunks);
        mRangeActiveChunksHalf = rangeActiveChunksHalf;
        mChunkSize = chunkSize;
    }

    public void clearCache() {
        // Basically start from scratch
        mCacheChunks.evictAll();
    }

    public void setNumItems(int numItems) {
        mNumItems = numItems;
        clearCache();
        notifyDataSetChanged();
    }

    @Override
    public final int getCount() {
        return mNumItems;
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final T getItem(int position) {
        // Load more chunks for a given position if necessary
        updateChunks(position);

        int indexChunk = position / mChunkSize;

        List<T> chunk = mCacheChunks.get(indexChunk);

        int chunkPos = position % mChunkSize;

        if (chunk != null && chunkPos < chunk.size()) {
            return chunk.get(chunkPos);
        }

        return null;
    }

    private final void updateChunks(int position) {
        // Get the truncated range target center by allowing pos/chunksize to truncate.
        int chunkTargetCenter = (position / mChunkSize);

        int chunkTargetTop = Math.max(0, chunkTargetCenter - mRangeActiveChunksHalf);
        int chunkTargetBottom = chunkTargetCenter + mRangeActiveChunksHalf;

        int i = chunkTargetTop;

        while (i <= chunkTargetBottom) {
            if (mCacheChunks.get(i) == null) {
                loadChunk(i);
            }
            i++;
        }
    }

    protected void addChunk(int index, List<T> chunk) {
        mCacheChunks.put(index, chunk);
    }

    public abstract void loadChunk(int index);

    public abstract U acquireView();

    public abstract View populateView(U view, T data, int position, ViewGroup parent);

}
