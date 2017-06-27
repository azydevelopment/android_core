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

import android.util.Log;

public class HlprDbg {

    public enum DBG_LVL {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT
    }

    // TODO HACK: Need to find a way to make this application specific
    public static final String TAG_PREFIX = "Wikipedia Lecturer: ";


    public static void assertFail(String tag, String suffix) {
        log(DBG_LVL.ASSERT, tag, suffix);
        throw new AssertionError(suffix);
    }

    public static void log(String tag) {
        log(DBG_LVL.VERBOSE, tag, "", "");
    }

    public static void log(String tag, String suffix) {
        log(DBG_LVL.VERBOSE, tag, "", suffix);
    }

    public static void log(DBG_LVL level, String tag) {
        log(level, tag, "", "");
    }

    public static void log(DBG_LVL level, String tag, String suffix) {
        log(level, tag, "", suffix);
    }

    public static void log(DBG_LVL level, String tag, String prefix, String suffix) {
        String msgDbg = genMsgDbg(prefix, suffix);

        switch (level) {
            case VERBOSE:
                Log.v(tag, msgDbg);
                break;
            case DEBUG:
                Log.d(tag, msgDbg);
                break;
            case INFO:
                Log.i(tag, msgDbg);
                break;
            case WARN:
                Log.w(tag, msgDbg);
                break;
            case ERROR:
                Log.e(tag, msgDbg);
                break;
            case ASSERT:
                Log.e(tag, msgDbg);
                break;

            default:
                break;
        }
    }

    private static String genMsgDbg(String prefix, String suffix) {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();

        if (prefix != null && prefix.length() > 0) {
            prefix += " | ";
        }

        if (suffix != null && suffix.length() > 0) {
            suffix = " | " + suffix;
        }

        return prefix + trace[3].getMethodName() + ": " + trace[3].getLineNumber() + suffix;
    }

}
