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

package com.arcdatum.android.core.asyncTasks;

import android.os.AsyncTask;

/**
 * AsyncTask convenience class with injected listeners
 *
 * @param <U> Progress value type
 * @param <V> Completion return type
 */
public class AsyncTaskListening<U, V> extends AsyncTask<Void, U, V> {

    public interface OnStartListener {
        void onStart();
    }

    public interface OnProgressListener<U> {
        void onProgress(U[] progress);
    }

    public interface OnCompletedListener<V> {
        void onCompleted(V result);
    }

    public OnStartListener mListenerStart;
    public OnProgressListener<U> mListenerProgress;
    public OnCompletedListener<V> mListenerCompletion;

    public AsyncTaskListening() {
    }

    public AsyncTaskListening(OnStartListener listenerStart, OnProgressListener<U> listenerProgress, OnCompletedListener<V> listenerCompleted) {
        setListenerStart(listenerStart);
        setListenerProgress(listenerProgress);
        setListenerCompleted(listenerCompleted);
    }

    public void setListenerStart(OnStartListener listenerStart) {
        mListenerStart = listenerStart;
    }

    public void setListenerProgress(OnProgressListener<U> listenerProgress) {
        mListenerProgress = listenerProgress;
    }

    public void setListenerCompleted(OnCompletedListener<V> listenerCompleted) {
        mListenerCompletion = listenerCompleted;
    }

    public boolean isPending() {
        return getStatus() == Status.PENDING;
    }

    public boolean isRunning() {
        return getStatus() == Status.RUNNING;
    }

    public boolean isFinished() {
        return getStatus() == Status.FINISHED;
    }

    @Override
    protected V doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListenerStart != null) {
            mListenerStart.onStart();
        }
    }

    @Override
    protected void onProgressUpdate(U... values) {
        super.onProgressUpdate(values);
        if (mListenerProgress != null && !isCancelled()) {
            mListenerProgress.onProgress(values);
        }
    }

    @Override
    protected void onPostExecute(V result) {
        super.onPostExecute(result);
        if (mListenerCompletion != null && !isCancelled()) {
            mListenerCompletion.onCompleted(result);
        }
    }

}