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

package com.arcdatum.android.core.managers;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.helpers.HlprMath;

import java.util.ArrayList;
import java.util.List;

public class MngrCamParams {
    private static final String TAG = HlprDbg.TAG_PREFIX + MngrCamParams.class.getSimpleName();

    private Parameters mParamsCam;

    private boolean mSaved = false;

    private List<Camera.Size> mSupportedPreviewSizes;
    private List<Camera.Size> mSupportedPictureSizes;
    private List<String> mSupportedFocusModes;
    private List<String> mSupportedFlashModes;
    private List<String> mSupportedWhiteBalance;

    private int mExposureMin = 0;
    private int mExposureMax = 0;
    private float mExposureNorm = 0;

    public void initParamsCam(Parameters paramsCam) {
        mParamsCam = paramsCam;

        // get supported camera features
        mSupportedPreviewSizes = mParamsCam.getSupportedPreviewSizes();
        mSupportedPictureSizes = mParamsCam.getSupportedPictureSizes();
        mSupportedFocusModes = mParamsCam.getSupportedFocusModes();
        mSupportedFlashModes = mParamsCam.getSupportedFlashModes();
        mSupportedWhiteBalance = mParamsCam.getSupportedWhiteBalance();
        mExposureMin = mParamsCam.getMinExposureCompensation();
        mExposureMax = mParamsCam.getMaxExposureCompensation();

        // remove unwanted flash modes
        if (mSupportedFlashModes != null) {
            mSupportedFlashModes.remove(Parameters.FLASH_MODE_RED_EYE);
            mSupportedFlashModes.remove(Parameters.FLASH_MODE_TORCH);
        }
    }

    public void saveParamsCam(Parameters paramsCam) {
        mParamsCam = paramsCam;
        mSaved = true;
    }

    public void resetSave() {
        mSaved = false;
    }

    public boolean isSaved() {
        return mSaved;
    }

    public Parameters getParams() {
        return mParamsCam;
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        return mSupportedPreviewSizes;
    }

    public List<Camera.Size> getSupportedPictureSizes() {
        return mSupportedPictureSizes;
    }

    public List<String> getSupportedFocusModes() {
        return mSupportedFocusModes;
    }

    public List<String> getSupportedFlashModes() {
        return mSupportedFlashModes;
    }

    public List<String> getSupportedWhiteBalance() {
        return mSupportedWhiteBalance;
    }

    /******** FEATURE METHODS ********/

    public boolean isSupportedFocusMode(String focusMode) {
        HlprDbg.log(DBG_LVL.DEBUG, TAG);
        return mSupportedFocusModes.contains(focusMode);
    }

    public void setRecordingHint(boolean recordingHint) {
        mParamsCam.setRecordingHint(recordingHint);
    }

    public void setPreviewSize(int width, int height) {
        mParamsCam.setPreviewSize(width, height);
    }

    public void setPictureSize(int width, int height) {
        mParamsCam.setPictureSize(width, height);
    }

    public void setRotation(int rotation) {
        mParamsCam.setRotation(rotation);
    }

    public void setExposure(float exposureNorm) {
        mExposureNorm = HlprMath.clamp(exposureNorm, -1f, 1f);

        float exposure = 0;

        if (mExposureNorm > 0f) {
            exposure = mExposureNorm * mExposureMax;
        } else if (mExposureNorm < 0f) {
            exposure = mExposureNorm * -mExposureMin;
        }

        mParamsCam.setExposureCompensation((int) exposure);
    }

    public void setFocusMode(String focusMode) {
        if (mSupportedFocusModes != null && mSupportedFocusModes.contains(focusMode)) {
            HlprDbg.log(DBG_LVL.INFO, TAG, focusMode);
            mParamsCam.setFocusMode(focusMode);
        }
    }

    public String setFlashMode(String flashMode) {
        if (mSupportedFlashModes != null && mSupportedFlashModes.contains(flashMode)) {
            HlprDbg.log(DBG_LVL.INFO, TAG, flashMode);
            mParamsCam.setFlashMode(flashMode);
        }
        return mParamsCam.getFlashMode();
    }

    public String toggleFlashMode() {
        HlprDbg.log(DBG_LVL.DEBUG, TAG);
        if (mSupportedFlashModes != null && mSupportedFlashModes.size() > 0) {
            String flashMode = mParamsCam.getFlashMode();
            flashMode = mSupportedFlashModes.get((mSupportedFlashModes.indexOf(flashMode) + 1) % mSupportedFlashModes.size());
            setFlashMode(flashMode);
            return flashMode;
        } else {
            return null;
        }
    }

    public void setWhiteBalance(String whiteBalance) {
        if (mSupportedWhiteBalance != null && mSupportedWhiteBalance.contains(whiteBalance)) {
            HlprDbg.log(DBG_LVL.DEBUG, TAG);
            mParamsCam.setWhiteBalance(whiteBalance);
        }
    }

    public void setAutoFocus(float xNorm, float yNorm, float radiusNorm, float viewRatio) {
        HlprDbg.log(DBG_LVL.INFO, TAG, "Focus point: " + Float.toString(xNorm) + "x" + Float.toString(yNorm));

        int xNormAdj = -(int) ((float) xNorm * 2000f - 1000f);
        int yNormAdj = (int) ((float) yNorm * 2000f - 1000f);
        int radiusNormAdj = (int) (radiusNorm * 2000f);

        // Log.e(TAG,
        // HlprDbg.getMethod("Focus point: " + Integer.toString(xNormAdj) + "x" + Integer.toString(yNormAdj) + " | "
        // + Integer.toString(radiusNormAdj)));

        int left = (int) ((yNormAdj - radiusNormAdj));
        int top = (int) ((xNormAdj - radiusNormAdj));
        int right = (int) ((yNormAdj + radiusNormAdj));
        int bottom = (int) (top + (right - left));

        // Log.e(TAG,
        // HlprDbg.getMethod(Integer.toString(left) + "x" + Integer.toString(top) + " | " + Integer.toString(right) + "x"
        // + Integer.toString(bottom)));

        setMeteringArea(left, top, right, bottom);
        setFocusArea(left, top, right, bottom);
    }

    public void setMeteringArea(int left, int top, int right, int bottom) {
        if (mParamsCam.getMaxNumMeteringAreas() > 0) {
            left = (int) HlprMath.clamp(left, -1000, 1000);
            top = (int) HlprMath.clamp(top, -1000, 1000);
            right = (int) HlprMath.clamp(right, -1000, 1000);
            bottom = (int) HlprMath.clamp(bottom, -1000, 1000);

            HlprDbg.log(DBG_LVL.INFO, TAG, left + "x" + top + " - " + right + "x" + bottom);

            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            Rect rect = new Rect(left, top, right, bottom);
            meteringAreas.add(new Camera.Area(rect, 800));
            mParamsCam.setMeteringAreas(meteringAreas);
        }
    }

    public void setFocusArea(int left, int top, int right, int bottom) {
        if (mParamsCam.getMaxNumFocusAreas() > 0) {
            left = (int) HlprMath.clamp(left, -1000, 1000);
            top = (int) HlprMath.clamp(top, -1000, 1000);
            right = (int) HlprMath.clamp(right, -1000, 1000);
            bottom = (int) HlprMath.clamp(bottom, -1000, 1000);

            HlprDbg.log(DBG_LVL.INFO, TAG, left + "x" + top + " - " + right + "x" + bottom);

            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            Rect rect = new Rect(left, top, right, bottom);
            focusAreas.add(new Camera.Area(rect, 1000));
            mParamsCam.setFocusAreas(focusAreas);
        }
    }

    public String getFlashMode() {
        return mParamsCam.getFlashMode();
    }

    public float getExposureMin() {
        return mExposureMin;
    }

    public float getExposureMax() {
        return mExposureMax;
    }

    public float getExposureNorm() {
        return mExposureNorm;
    }

}
