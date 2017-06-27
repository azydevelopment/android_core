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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arcdatum.android.core.R;
import com.arcdatum.android.core.frags.FragBase;
import com.arcdatum.android.core.helpers.HlprDbg;

public class ActivityTwoPane extends ActivityBase {

    private static final String TAG = HlprDbg.TAG_PREFIX + ActivityTwoPane.class.getSimpleName();

    protected enum PANE {
        MAIN, SIDE
    }

    private FragBase mFragMain;
    private FragBase mFragSide;

    // Drawer: optional
    private DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_twopane);

        // Restore stored fragments if available
        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();

            mFragMain = (FragBase) manager.findFragmentByTag(savedInstanceState.getString(PANE.MAIN.name()));
            mFragSide = (FragBase) manager.findFragmentByTag(savedInstanceState.getString(PANE.SIDE.name()));
        }

        // Optional drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.Activity_TwoPane_DrawerLayout);

        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerShadow(R.drawable.shape_gradient_drawershadow, GravityCompat.START);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Store the main pane frag's tag
        if (mFragMain != null) {
            outState.putString(PANE.MAIN.name(), mFragMain.getTag());
        }

        // Store the side pane frag's tag
        if (mFragSide != null) {
            outState.putString(PANE.SIDE.name(), mFragSide.getTag());
        }
    }

    @Override
    public void onBackPressed() {
        // TODO FEATURE: Technically, the below is not robust UX.
        // It's possible we can push a top level frag after pushing a side or main frag.
        // Need to find a way to figure which order top level frags and child frags are pushed.

        // First check if there are side pane fragments to pop (and the side pane is visible)
        boolean isSidePaneVisible = !hasDrawer() || (hasDrawer() && mDrawerLayout.isDrawerOpen(GravityCompat.START));
        if (isSidePaneVisible && popFrag(PANE.SIDE)) {
            return;
        }

        // Barring the above, if the drawer is open, close it
        if (closeDrawer()) {
            return;
        }

        // Barring the above again, try popping the main frags
        if (popFrag(PANE.MAIN)) {
            return;
        }

        // If none of the above happens, then finally move this activity to the back
        moveTaskToBack(false);
    }

    // Frags

    protected boolean popFrag(PANE pane) {
        switch (pane) {
            case MAIN:
                if (mFragMain != null) {
                    return mFragMain.popFrag();
                }
                break;
            case SIDE:
                if (mFragSide != null) {
                    return mFragSide.popFrag();
                }
                break;
        }

        return false;
    }

    protected void pushFrag(FragBase frag, PANE pane, boolean addToBackStack) {
        if (frag != null) {
            HlprDbg.log(TAG);

            int idFragContainer;
            switch (pane) {
                case MAIN:
                    mFragMain = frag;
                    idFragContainer = R.id.Activity_TwoPane_Main;
                    break;
                case SIDE:
                    mFragSide = frag;
                    idFragContainer = R.id.Activity_TwoPane_Side;
                    break;
                default:
                    idFragContainer = R.id.Activity_TwoPane_Main;
                    break;
            }

            pushFrag(frag, idFragContainer, addToBackStack);
        }
    }

    // Optionals

    protected boolean hasDrawer() {
        return mDrawerLayout != null;
    }

    protected boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected boolean closeDrawer() {
        if (hasDrawer() && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    protected void addDrawerToggleToToolbar(Toolbar toolbar) {
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                HlprDbg.log(TAG, "Drawer closed");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                HlprDbg.log(TAG, "Drawer opened");
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
    }

}
