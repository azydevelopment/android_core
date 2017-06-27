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

package com.arcdatum.android.core.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.arcdatum.android.core.frags.FragBase;
import com.arcdatum.android.core.interfaces.IDelFrag;

import java.util.UUID;

public class ActivityBase extends AppCompatActivity implements
        IDelFrag {

    // IDelFrag

    @Override
    public void onFragAttach(FragBase frag) {
        // Child class can override this
    }

    @Override
    public void onFragDetach(FragBase frag) {
        // Child class can override this
    }

    @Override
    public boolean popFrag() {
        // Child classes need to keep track of their own child frags and the order which they should be popped
        return false;
    }

    @Override
    public void removeFragById(int idContainer) {
        FragmentManager fragMngr = getSupportFragmentManager();
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
            FragmentManager fragMngr = getSupportFragmentManager();
            FragmentTransaction fragTransaction = fragMngr.beginTransaction();

            if (addToBackStack) {
                fragTransaction.addToBackStack(null);
            }

            // Generate a unique fragment tag
            String tag = UUID.randomUUID().toString();

            if (animEnter + animExit > 0) {
                if (animPopEnter + animPopExit > 0) {
                    fragTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
                } else {
                    fragTransaction.setCustomAnimations(animEnter, animExit);
                }
            }
            fragTransaction.replace(idContainer, frag, tag);
            fragTransaction.commit();
        }
    }
}
