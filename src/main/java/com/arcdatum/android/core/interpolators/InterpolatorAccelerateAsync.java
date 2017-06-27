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

package com.arcdatum.android.core.interpolators;

import android.view.animation.AccelerateInterpolator;

import com.arcdatum.android.core.asyncTasks.AsyncTaskListening;

public class InterpolatorAccelerateAsync extends AsyncTaskListening<Float, Void> {

    public InterpolatorAccelerateAsync(OnProgressListener<Float> listenerProgress, OnCompletedListener<Void> listenerCompletion) {
        super(null, listenerProgress, listenerCompletion);
    }

    @Override
    protected Void doInBackground(Void... params) {

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        AccelerateInterpolator interpolator = new AccelerateInterpolator(5f);

        long start = System.currentTimeMillis();
        long current = System.currentTimeMillis();
        long interval = 0;
        long intervalLast = 0;

        float interpolationValue = 0;
        float interpolationValueLast = -1;

        while (interpolationValue < 0.999 && interpolationValueLast <= interpolationValue) {

            if (isCancelled()) {
                return null;
            }

            current = System.currentTimeMillis();
            interval = current - start;

            if (interval - intervalLast > 16) {
                intervalLast = interval;
                interpolationValueLast = interpolationValue;
                interpolationValue = interpolator.getInterpolation(interval / 1000f) * 400f;

                publishProgress(interpolationValue);
            }

            Thread.yield();

        }

        return null;
    }

}
