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

package com.arcdatum.android.core.enhanced.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.touch.TouchTracker;

public class ImgViewEnhanced extends ImageView {

    private static final String TAG = HlprDbg.TAG_PREFIX + ImgViewEnhanced.class.getSimpleName();

    private boolean mEnableTransform = false;

    protected TouchTracker mTouchTracker = new TouchTracker();

    protected Matrix mMat = new Matrix();

    protected TransformState mTransformState = new TransformState();
    protected TransformState mTransformStateIncrement = new TransformState();

    private PointF mCenterStart = new PointF();
    private PointF mCenterCur = new PointF();
    private float mDistStart = 0;
    private float mDegreesStart = 0;

    protected int mImgSrcWidth = 0;
    protected int mImgSrcHeight = 0;

    public ImgViewEnhanced(Context context) {
        super(context);
    }

    public ImgViewEnhanced(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImgViewEnhanced(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImgViewEnhanced(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getImgSrcWidth() {
        return mImgSrcWidth;
    }

    public int getImgSrcHeight() {
        return mImgSrcHeight;
    }

    public void setEnableTransform(boolean enable) {
        mEnableTransform = enable;

        if (enable) {
            setScaleType(ScaleType.MATRIX);
        }
    }

    public void setImgSrcSize(int imgSrcWidth, int imgSrcHeight) {
        mImgSrcWidth = imgSrcWidth;
        mImgSrcHeight = imgSrcHeight;
    }

    public void setFitAndCenter() {
        if (mImgSrcWidth <= 0 || mImgSrcHeight <= 0) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, "Width or height both must be greater than 0.  Forget to call setImgSrcSize()?");
        } else {
            mTransformState.reset();
            mTransformStateIncrement.reset();

            float scaleX = (float) getWidth() / mImgSrcWidth;
            float scaleY = (float) getHeight() / mImgSrcHeight;

            float scale = Math.max(scaleX, scaleY);
            mTransformState.mScaleX = scale;
            mTransformState.mScaleY = scale;

            int widthFinal = Math.round(mImgSrcWidth * scale);
            int heightFinal = Math.round(mImgSrcHeight * scale);

            mTransformState.mTransX = (getWidth() - widthFinal) / 2;
            mTransformState.mTransY = (getHeight() - heightFinal) / 2;

            updateMat();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnableTransform) {
            // HlprDbg.log(TAG);
            updateTransformsOnTouch(event);
            updateMat();
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    protected void updateTransformsOnTouch(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        int numPointers = MotionEventCompat.getPointerCount(event);
        int pointerEventIndex = MotionEventCompat.getActionIndex(event);
        int pointerIdActive = MotionEventCompat.getPointerId(event, pointerEventIndex);

        float posX = MotionEventCompat.getX(event, pointerEventIndex);
        float posY = MotionEventCompat.getY(event, pointerEventIndex);

        int numTouches = mTouchTracker.getNumTouches();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchTracker.clearTouches();
                commitTransformIncrement();
                mTouchTracker.addTouch(pointerIdActive, new PointF(posX, posY));
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchTracker.addTouch(pointerIdActive, new PointF(posX, posY));

                // Need to update how many touches we're tracking
                numTouches = mTouchTracker.getNumTouches();

                if (numPointers >= numTouches && numTouches == 2) {
                    commitTransformIncrement();
                    updateStartState(event, MotionEventCompat.findPointerIndex(event, mTouchTracker.getTouchId(0)),
                            MotionEventCompat.findPointerIndex(event, mTouchTracker.getTouchId(1)));
                }
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                int touchIndex = mTouchTracker.getIndexOf(pointerIdActive);

                mTouchTracker.removeTouch(touchIndex);

                // Need to update how many touches we're tracking
                numTouches = mTouchTracker.getNumTouches();

                if (numTouches == 1) {
                    // If there's only one touch left, reset and pretend a new finger was put down

                    int idRemainingPointer = mTouchTracker.getTouchId(0);
                    int indexRemainingPointer = MotionEventCompat.findPointerIndex(event, idRemainingPointer);

                    mTouchTracker.clearTouches();

                    commitTransformIncrement();

                    mTouchTracker.addTouch(idRemainingPointer,
                            new PointF(MotionEventCompat.getX(event, indexRemainingPointer), MotionEventCompat.getY(event, indexRemainingPointer)));

                } else if (numTouches > 1) {
                    // If we have two or more touches left
                    // Only do something if this is one of our first two tracked touches
                    if (touchIndex < 2) {
                        commitTransformIncrement();
                        updateStartState(event, MotionEventCompat.findPointerIndex(event, mTouchTracker.getTouchId(0)),
                                MotionEventCompat.findPointerIndex(event, mTouchTracker.getTouchId(1)));
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (numPointers >= mTouchTracker.getNumTouches()) {
                    if (numTouches == 1) {
                        // There's only one touch being tracked at the moment
                        PointF posStart = mTouchTracker.getTouchPosStart(0);

                        mTransformStateIncrement.mTransX = posX - posStart.x;
                        mTransformStateIncrement.mTransY = posY - posStart.y;
                    } else if (numTouches >= 2) {
                        int indexPointer0 = event.findPointerIndex(mTouchTracker.getTouchId(0));
                        int indexPointer1 = event.findPointerIndex(mTouchTracker.getTouchId(1));

                        float x0 = MotionEventCompat.getX(event, indexPointer0);
                        float y0 = MotionEventCompat.getY(event, indexPointer0);
                        float x1 = MotionEventCompat.getX(event, indexPointer1);
                        float y1 = MotionEventCompat.getY(event, indexPointer1);

                        float scale = computeDist(x0, y0, x1, y1) / mDistStart;
                        mTransformStateIncrement.mScaleX = scale;
                        mTransformStateIncrement.mScaleY = scale;

                        float degrees = computeDegrees(x0, y0, x1, y1);
                        mTransformStateIncrement.mDegrees = degrees - mDegreesStart;

                        computeCenter(mCenterCur, x0, y0, x1, y1);
                        mTransformStateIncrement.mTransX = mCenterCur.x - mCenterStart.x;
                        mTransformStateIncrement.mTransY = mCenterCur.y - mCenterStart.y;
                    }
                }

                break;
        }
    }

    public void updateMat() {
        mMat.reset();
        transformMat(mMat, mTransformState);
        transformMat(mMat, mTransformStateIncrement);
        setImageMatrix(mMat);
        invalidate();
    }

    private void commitTransformIncrement() {
        extractTransform(mTransformState, mMat);
        mTransformStateIncrement.reset();
    }

    protected void updateStartState(MotionEvent event, int indexPointer0, int indexPointer1) {
        float x0 = MotionEventCompat.getX(event, indexPointer0);
        float y0 = MotionEventCompat.getY(event, indexPointer0);
        float x1 = MotionEventCompat.getX(event, indexPointer1);
        float y1 = MotionEventCompat.getY(event, indexPointer1);

        computeCenter(mCenterStart, x0, y0, x1, y1);
        mDistStart = computeDist(x0, y0, x1, y1);
        mDegreesStart = computeDegrees(x0, y0, x1, y1);

        // HlprDbg.log(TAG, "Start center: " + Float.toString(mCenterStart.x) + "x" + Float.toString(mCenterStart.y));
        // HlprDbg.log(TAG, "Start distance: " + Float.toString(mDistStart));
        // HlprDbg.log(TAG, "Start degrees: " + Float.toString(mDegreesStart));

        mTransformStateIncrement.mScaleCenterX = mCenterStart.x;
        mTransformStateIncrement.mScaleCenterY = mCenterStart.y;
        mTransformStateIncrement.mRotCenterX = mCenterStart.x;
        mTransformStateIncrement.mRotCenterY = mCenterStart.y;
    }

    protected static void transformMat(Matrix matToModify, TransformState state) {
        matToModify.postScale(state.mScaleX, state.mScaleY, state.mScaleCenterX, state.mScaleCenterY);
        matToModify.postRotate(state.mDegrees, state.mRotCenterX, state.mRotCenterY);
        matToModify.postTranslate(state.mTransX, state.mTransY);
    }

    protected static void extractTransform(TransformState stateToModify, Matrix matSource) {
        float[] values = new float[9];
        matSource.getValues(values);

        float scaleX = values[Matrix.MSCALE_X];
        // float scaleY = values[Matrix.MSCALE_Y];
        float skewX = values[Matrix.MSKEW_X];
        float skewY = values[Matrix.MSKEW_Y];
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];

        float scale = (float) Math.sqrt(scaleX * scaleX + skewY * skewY);
        float degrees = (float) Math.toDegrees(Math.atan2(skewX, scaleX));

        stateToModify.reset();

        stateToModify.mScaleX = scale;
        stateToModify.mScaleY = scale;

        // This has to be inverted for some reason
        stateToModify.mDegrees = -degrees;

        stateToModify.mTransX = transX;
        stateToModify.mTransY = transY;
    }

    private static float computeDist(float x0, float y0, float x1, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float) Math.sqrt(x * x + y * y);
    }

    private static void computeCenter(PointF pointToModify, float x0, float y0, float x1, float y1) {
        float x = (x0 + x1) / 2;
        float y = (y0 + y1) / 2;
        pointToModify.set(x, y);
    }

    private static float computeDegrees(float x0, float y0, float x1, float y1) {
        double delta_x = (x0 - x1);
        double delta_y = (y0 - y1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    protected static class TransformState {
        public float mTransX = 0;
        public float mTransY = 0;

        public float mScaleX = 1;
        public float mScaleY = 1;
        public float mScaleCenterX = 0;
        public float mScaleCenterY = 0;

        public float mDegrees = 0;
        public float mRotCenterX = 0;
        public float mRotCenterY = 0;

        public TransformState() {
        }

        public void reset() {
            mTransX = 0;
            mTransY = 0;
            mScaleX = 1;
            mScaleY = 1;
            mScaleCenterX = 0;
            mScaleCenterY = 0;
            mDegrees = 0;
            mRotCenterX = 0;
            mRotCenterY = 0;
        }

        public void set(TransformState state) {
            mTransX = state.mTransX;
            mTransY = state.mTransY;
            mScaleX = state.mScaleX;
            mScaleY = state.mScaleY;
            mScaleCenterX = state.mScaleCenterX;
            mScaleCenterY = state.mScaleCenterY;
            mDegrees = state.mDegrees;
            mRotCenterX = state.mRotCenterX;
            mRotCenterY = state.mRotCenterY;
        }
    }

}
