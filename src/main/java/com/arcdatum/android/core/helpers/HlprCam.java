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

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;

import java.util.List;

public class HlprCam {

    private static final String TAG = HlprDbg.TAG_PREFIX + "HlprCam";

    public static Size getOptimalPreviewSize(Size picSize, List<Size> supportedPreviewSizes) {
        // final double ASPECT_TOLERANCE = 0.1;
        // double targetRatio = (double) surfaceHeight / surfaceWidth;
        //
        // if (sizes == null)
        // return null;
        //
        // Camera.Size optimalSize = null;
        // double minDiff = Double.MAX_VALUE;
        //
        // int targetHeight = surfaceHeight;
        //
        // int width = 0;
        // for (Camera.Size size : sizes) {
        // int previewWidth = size.width;
        // int previewHeight = size.height;
        // float previewRatio = (float) previewWidth / (float) previewHeight;
        // if (previewHeight < surfaceHeight && previewWidth > width && previewRatio < targetRatio) {
        // optimalSize = size;
        // width = size.width;
        // }
        // }
        //
        // return optimalSize;

        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) picSize.height / picSize.width;

        Size previewSize = null;
        for (Size supportedSize : supportedPreviewSizes) {

            double previewSizeRatio = (double) supportedSize.height / supportedSize.width;

            if (Math.abs(previewSizeRatio - targetRatio) < ASPECT_TOLERANCE) {
                if (previewSize == null || previewSize.width < supportedSize.width)
                    previewSize = supportedSize;
            }
        }

        HlprDbg.log(DBG_LVL.DEBUG, TAG, Integer.toString(previewSize.width) + "x" + Integer.toString(previewSize.height));

        return previewSize;
    }

    public static Size getOptimalPictureSize(List<Size> supportedPicSizes) {

        Size picSize = supportedPicSizes.get(0);
        for (Size supportedSize : supportedPicSizes) {
            if (supportedSize.width > picSize.width) {
                picSize = supportedSize;
            }
        }

        HlprDbg.log(DBG_LVL.DEBUG, TAG, Integer.toString(picSize.width) + "x" + Integer.toString(picSize.height));
        return picSize;
    }

    public static boolean checkCameraHardware(Context context) {
        HlprDbg.log(DBG_LVL.DEBUG, TAG);
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
}
