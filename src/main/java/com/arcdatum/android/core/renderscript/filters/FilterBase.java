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

package com.arcdatum.android.core.renderscript.filters;

import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.renderscript.RenderScriptResource;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class FilterBase {

    private static final String TAG = HlprDbg.TAG_PREFIX + FilterBase.class.getSimpleName();
    private static final boolean ENABLE_DEBUG = false;

    private static final int NUM_FILTER_DEPENDENCIES_MAX = 8;

    protected RenderScript mRenderScript;

    protected FilterBase[] mFilterDependencies = new FilterBase[NUM_FILTER_DEPENDENCIES_MAX];
    protected ArrayList<FilterBase> mFilterDependenciesPassthrough = new ArrayList<FilterBase>();
    protected ArrayList<FilterBase> mFilterDependents = new ArrayList<FilterBase>();

    protected RenderScriptResource mOutput;
    protected RenderScriptResource mOutputPassthrough;

    private boolean mInvalid = true;
    private boolean mEnabled = true;
    private boolean mOutputLocked = false;
    private boolean mForcePassThrough = false;
    private boolean mPassedThrough = true;

    private boolean mStreamlined = false;

    public FilterBase(RenderScript renderScript) {
        mRenderScript = renderScript;
    }

    public void reset() {
        if (ENABLE_DEBUG) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
        }
        invalidate(true);
    }

    public void reset(boolean recursive) {
        if (recursive) {
            for (int i = 0; i < mFilterDependents.size(); i++) {
                mFilterDependents.get(i).reset(true);
            }
        }
        reset();
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        invalidate(true);
    }

    public void setOutputLocked(boolean locked) {
        if (ENABLE_DEBUG) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
        }

        if (mOutputLocked != locked) {
            mOutputLocked = locked;

            if (mPassedThrough && locked) {
                mOutputPassthrough = new RenderScriptResource(mRenderScript, mOutputPassthrough);
            }
        }
    }

    public void invalidate(boolean recursive) {
        if (ENABLE_DEBUG) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
        }

        if (recursive) {
            // recursively mark all dependents as invalid
            for (int i = 0; i < mFilterDependents.size(); i++) {
                mFilterDependents.get(i).invalidate(true);
            }
        }
        invalidate();
    }

    public void invalidate() {
        mInvalid = true;
    }

    public void validate() {
        mInvalid = false;
    }

    protected void setDependency(int slot, FilterBase filterDependency) {
        if (slot < NUM_FILTER_DEPENDENCIES_MAX && slot >= 0) {
            // register the two way dependent/dependency relationship
            mFilterDependencies[slot] = filterDependency;
            filterDependency.addDependent(this);
        } else {
            Log.e(TAG, "Filter dependency slot out of expected range.");
        }
    }

    protected void setDependenciesPassthrough(FilterBase[] filterDependencies) {
        if (filterDependencies != null) {
            mFilterDependenciesPassthrough.addAll(Arrays.asList(filterDependencies));
        }
    }

    private void addDependent(FilterBase filterDependent) {
        mFilterDependents.add(filterDependent);
    }

    public void update() {
        if (mInvalid && !mOutputLocked) {

            if (ENABLE_DEBUG) {
                HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
            }

            // recursively update dependencies
            for (int i = 0; i < mFilterDependencies.length; i++) {
                FilterBase dependency = mFilterDependencies[i];
                if (dependency != null && dependency.mInvalid) {
                    dependency.update();
                }
            }

            boolean passthrough;

            // if all passthrough dependencies have passed through, pass through this filter too
            if (mFilterDependenciesPassthrough.size() == 0) {
                passthrough = false;
            } else {
                passthrough = true;
                for (int i = 0; i < mFilterDependenciesPassthrough.size(); i++) {
                    FilterBase dependencyPassthrough = mFilterDependenciesPassthrough.get(i);
                    if (!dependencyPassthrough.mPassedThrough) {
                        passthrough = false;
                        break;
                    }
                }
            }

            if (mEnabled && !passthrough && !mForcePassThrough) {
                execute();
            } else {
                passthrough();
            }

        }
        validate();
    }

    protected void execute() {
        mPassedThrough = false;
        if (ENABLE_DEBUG) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
        }
    }

    protected void passthrough() {
        mPassedThrough = true;
        if (ENABLE_DEBUG) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
        }
    }

    protected void setOutput(RenderScriptResource resource) {
        mOutput = resource;
    }

    protected void setOutputPassthrough(RenderScriptResource resource) {
        mOutputPassthrough = resource;
    }

    public void setForcePassthrough(boolean forcecPassthrough) {
        mForcePassThrough = forcecPassthrough;
    }

    public void setStreamlined(boolean streamlined, boolean recursive) {
        mStreamlined = streamlined;

        if (recursive) {
            for (int i = 0; i < mFilterDependents.size(); i++) {
                FilterBase dependency = mFilterDependents.get(i);
                if (dependency != null) {
                    dependency.setStreamlined(streamlined, true);
                }
            }
        }

        cleanupOutput(false, false);
    }

    // update and get the final output
    public RenderScriptResource getOutput() {
        // ensure an upward recursive update before returning the output
        update();

        RenderScriptResource output;
        if (!mPassedThrough) {
            output = mOutput;
        } else {
            output = mOutputPassthrough;
        }

        if (mStreamlined) {
            cleanupOutput(false, false);
        }

        return output;
    }

    // destroy existing resource
    public void cleanupOutput(boolean upwardRecursive, boolean force) {
        // only clean if we haven't locked the output
        if (force || ((mOutput != null || mOutput != mOutputPassthrough) && !mOutputLocked)) {
            if (ENABLE_DEBUG) {
                HlprDbg.log(DBG_LVL.ERROR, TAG, getClass().getSimpleName(), "");
            }

            // recursively cleanup dependencies
            if (upwardRecursive) {
                for (int i = 0; i < mFilterDependencies.length; i++) {
                    FilterBase dependency = mFilterDependencies[i];
                    if (dependency != null) {
                        dependency.cleanupOutput(upwardRecursive, force);
                    }
                }
            }

            HlprDbg.log(TAG);
            mOutput = null;
            mOutputPassthrough = null;
            invalidate();
        }
    }

}
