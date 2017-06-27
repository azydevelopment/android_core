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

package com.arcdatum.android.core.enhanced.other;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.animation.DecelerateInterpolator;

public class ItemTouchCallbackEnhanced extends ItemTouchHelper.Callback {

    private static final DecelerateInterpolator INTERPOLATOR_DECELERATE = new DecelerateInterpolator(2);
    private static final int DURATION_ANIM = 150;
    private static final float ELEVATION = 5f;

    private int mFlagsSwipe;
    private int mFlagsDrag;

    private int mColor;

    private boolean mIsItemElevated = false;

    public interface IDelItemTouchCallbackEnhanced {
        boolean onItemMove(ItemTouchCallbackEnhanced itemTouchCallback, int from, int to);

        void onItemRemove(ItemTouchCallbackEnhanced itemTouchCallback, int position);
    }

    private IDelItemTouchCallbackEnhanced mDel;

    public ItemTouchCallbackEnhanced(IDelItemTouchCallbackEnhanced del, int flagsSwipe, int flagsDrag, int color) {
        mDel = del;
        mFlagsSwipe = flagsSwipe;
        mFlagsDrag = flagsDrag;
        mColor = color;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(mFlagsDrag, mFlagsSwipe);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mFlagsSwipe != 0;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (Build.VERSION.SDK_INT >= 21 && isCurrentlyActive && !mIsItemElevated) {
            final float newElevation = ELEVATION + ViewCompat.getElevation(viewHolder.itemView);

            ObjectAnimator animElevation = ObjectAnimator.ofFloat(viewHolder.itemView, "elevation", 0, newElevation);
            animElevation.setInterpolator(INTERPOLATOR_DECELERATE);
            animElevation.setDuration(DURATION_ANIM);

            ObjectAnimator animColor = ObjectAnimator.ofArgb(viewHolder.itemView, "backgroundColor", 0x00000000, mColor);
            animColor.setDuration(0);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animElevation, animColor);
            animSet.start();

            mIsItemElevated = true;
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (Build.VERSION.SDK_INT >= 21 && mIsItemElevated) {
            ObjectAnimator animElevation = ObjectAnimator.ofFloat(viewHolder.itemView, "elevation", ViewCompat.getElevation(viewHolder.itemView), 0f);
            animElevation.setInterpolator(INTERPOLATOR_DECELERATE);
            animElevation.setDuration(DURATION_ANIM);

            ObjectAnimator animColor = ObjectAnimator.ofArgb(viewHolder.itemView, "backgroundColor", mColor, 0x00000000);
            animColor.setStartDelay(DURATION_ANIM);
            animColor.setDuration(0);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animElevation, animColor);
            animSet.start();
        }

        mIsItemElevated = false;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return mDel.onItemMove(this, viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mDel.onItemRemove(this, viewHolder.getAdapterPosition());
    }

}
