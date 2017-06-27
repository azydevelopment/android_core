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

package com.arcdatum.android.core.helpers;

public class HlprMath {

    // private static final String TAG = HlprDbg.TAG_PREFIX + HlprMath.class.getSimpleName();

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float magnitude(float[] vec) {
        float sumOfSquares = 0;
        for (int i = 0; i < vec.length; i++) {
            // Log.e(TAG, HlprDbg.getMethod(Float.toString(vec[i])));
            sumOfSquares += vec[i] * vec[i];
        }
        return (float) Math.sqrt(sumOfSquares);
    }

    public static float[] normalize(float[] vec) {
        float magnitude = magnitude(vec);
        for (int i = 0; i < vec.length; i++) {
            vec[i] /= magnitude;
        }
        return vec;
    }

    public static float dot(float[] vec1, float[] vec2) {
        float dot = 0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
        }
        // Log.e(TAG, HlprDbg.getMethod(Float.toString(dot)));
        return dot;
    }
}
