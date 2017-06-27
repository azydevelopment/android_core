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

package com.arcdatum.android.core.frags;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.interfaces.IDelFrag;

import java.util.UUID;

public class FragBase extends Fragment implements
        IDelFrag {

    private static final String TAG = HlprDbg.TAG_PREFIX + FragBase.class.getSimpleName();

    public void injectDependencies(ServicesCommon servicesCommon) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Notify the parent frag if there is one; otherwise, notify the activity
        Fragment fragParent = getParentFragment();
        if (fragParent != null) {
            // TODO HACK: More instanceof usage :(
            if (fragParent instanceof IDelFrag) {
                ((IDelFrag) fragParent).onFragAttach(this);
            }
        } else {
            // TODO HACK: More instanceof usage :(
            if (context instanceof IDelFrag) {
                ((IDelFrag) context).onFragAttach(this);
            }
        }
    }

    @Override
    public void onDetach() {
        Context context = getContext();

        super.onDetach();

        // Notify the parent frag if there is one; otherwise, notify the activity
        Fragment fragParent = getParentFragment();
        if (fragParent != null) {
            // TODO HACK: More instanceof usage :(
            if (fragParent instanceof IDelFrag) {
                ((IDelFrag) fragParent).onFragDetach(this);
            }
        } else {
            // TODO HACK: More instanceof usage :(
            if (context instanceof IDelFrag) {
                ((IDelFrag) context).onFragDetach(this);
            }
        }
    }

    // IDelFrag

    @Override
    public void onFragAttach(FragBase frag) {
        // Child class can override this
    }

    @Override
    public void onFragDetach(FragBase frag) {
        // Child class can override this
    }

    /**
     * @return True for handled; false for not handled
     */
    @Override
    public boolean popFrag() {
        FragmentManager manager = getChildFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public void removeFragById(int idContainer) {
        FragmentManager fragMngr = getChildFragmentManager();
        Fragment frag = fragMngr.findFragmentById(idContainer);
        if (frag != null) {
            fragMngr.beginTransaction().remove(frag).commit();
        }
    }

    @Override
    public void pushFrag(Fragment frag, int idContainer) {
        pushFrag(frag, idContainer, true);
    }

    @Override
    public void pushFrag(Fragment frag, int idContainer, boolean addToBackStack) {
        pushFrag(frag, idContainer, addToBackStack, 0, 0, 0, 0);
    }

    @Override
    public void pushFrag(Fragment frag, int idContainer, boolean addToBackStack, int animEnter, int animExit, int animPopEnter, int animPopExit) {
        if (frag != null) {
            // Begin the fragment transition
            FragmentManager fragMngr = getChildFragmentManager();
            FragmentTransaction fragTransaction = fragMngr.beginTransaction();

            if (addToBackStack) {
                fragTransaction.addToBackStack(null);
            }

            // Generate a unique fragment tag
            String tag = UUID.randomUUID().toString();

            if (animEnter > 0 || animExit > 0) {
                if (animPopEnter > 0 || animPopExit > 0) {
                    fragTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
                } else {
                    fragTransaction.setCustomAnimations(animEnter, animExit);
                }
            }
            fragTransaction.replace(idContainer, frag, tag);
            fragTransaction.commit();
        } else {
            HlprDbg.log(DBG_LVL.ERROR, TAG, "Frag to push is null");
        }
    }

    // Aggregate services

    public static class ServicesCommon {
        public ServicesCommon() {
        }
    }

}
