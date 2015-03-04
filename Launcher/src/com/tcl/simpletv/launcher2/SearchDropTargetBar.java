/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tcl.simpletv.launcher2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.tcl.simpletv.launcher2.R;

/*
 * Ths bar will manage the transition between the QSB search bar and the delete drop
 * targets so that each of the individual IconDropTargets don't have to.
 */
public class SearchDropTargetBar extends FrameLayout implements DragController.DragListener {

    private static final int sTransitionInDuration = 200;
    private static final int sTransitionOutDuration = 175;

    private ObjectAnimator mDropTargetBarAnim;
    private ObjectAnimator mQSBSearchBarAnim;
    private ObjectAnimator mEditHeloInfoAnim;
    private static final AccelerateInterpolator sAccelerateInterpolator =
            new AccelerateInterpolator();

    private boolean mIsSearchBarHidden;
    private boolean mIsEditHeloInfoHidden;
    private View mQSBSearchBar;
    private View mDropTargetBar;
    private ButtonDropTarget mInfoDropTarget;
    private ButtonDropTarget mDeleteDropTarget;
    private View mEdit_help_info;
    private int mBarHeight;
    private boolean mDeferOnDragEnd = false;
    private Launcher mLauncher;

    private Drawable mPreviousBackground;
    private boolean mEnableDropDownDropTargets;
    
    public DeleteDropTarget getDeleteDropTarget(){
    	return (DeleteDropTarget) mDeleteDropTarget;
    }

    public SearchDropTargetBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchDropTargetBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(Launcher launcher, DragController dragController) {
        dragController.addDragListener(this);
        dragController.addDragListener(mInfoDropTarget);
        dragController.addDragListener(mDeleteDropTarget);
        dragController.addDropTarget(mInfoDropTarget);
        dragController.addDropTarget(mDeleteDropTarget);
        dragController.setFlingToDeleteDropTarget(mDeleteDropTarget);
        mLauncher = launcher;
        mInfoDropTarget.setLauncher(launcher);
        mDeleteDropTarget.setLauncher(launcher);
    }

    private void prepareStartAnimation(View v) {
        // Enable the hw layers before the animation starts (will be disabled in the onAnimationEnd
        // callback below)
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        v.buildLayer();
    }

    private void setupAnimation(ObjectAnimator anim, final View v) {
        anim.setInterpolator(sAccelerateInterpolator);
        anim.setDuration(sTransitionInDuration);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Get the individual components
        mQSBSearchBar = findViewById(R.id.qsb_search_bar);
        mEdit_help_info = findViewById(R.id.edif_help_info);
        mDropTargetBar = findViewById(R.id.drag_target_bar);
        mInfoDropTarget = (ButtonDropTarget) mDropTargetBar.findViewById(R.id.info_target_text);
        mDeleteDropTarget = (ButtonDropTarget) mDropTargetBar.findViewById(R.id.delete_target_text);
        mBarHeight = getResources().getDimensionPixelSize(R.dimen.qsb_bar_height);

        mInfoDropTarget.setSearchDropTargetBar(this);
        mDeleteDropTarget.setSearchDropTargetBar(this);

        mEnableDropDownDropTargets =
            getResources().getBoolean(R.bool.config_useDropTargetDownTransition);

        // Create the various fade animations
        if (mEnableDropDownDropTargets) {
            mDropTargetBar.setTranslationY(-mBarHeight);
            mEdit_help_info.setTranslationY(-mBarHeight);
            mDropTargetBarAnim = LauncherAnimUtils.ofFloat(mDropTargetBar, "translationY",
                    -mBarHeight, 0f);
            mQSBSearchBarAnim = LauncherAnimUtils.ofFloat(mQSBSearchBar, "translationY", 0,
                    -mBarHeight);
            mEditHeloInfoAnim =  LauncherAnimUtils.ofFloat(mEdit_help_info, "translationY", 0,
            		-mBarHeight);
        } else {
            mDropTargetBar.setAlpha(0f);
            mEdit_help_info.setAlpha(0f);
            mDropTargetBarAnim = LauncherAnimUtils.ofFloat(mDropTargetBar, "alpha", 0f, 1f);
            mQSBSearchBarAnim = LauncherAnimUtils.ofFloat(mQSBSearchBar, "alpha", 1f, 0f);
            mEditHeloInfoAnim = LauncherAnimUtils.ofFloat(mEdit_help_info, "alpha", 1f, 0f);
        }
        mIsEditHeloInfoHidden = true;
        setupAnimation(mDropTargetBarAnim, mDropTargetBar);
        setupAnimation(mQSBSearchBarAnim, mQSBSearchBar);
        setupAnimation(mEditHeloInfoAnim, mEdit_help_info);
    }

    public void finishAnimations() {
        prepareStartAnimation(mDropTargetBar);
        mDropTargetBarAnim.reverse();
        prepareStartAnimation(mQSBSearchBar);
        mQSBSearchBarAnim.reverse();
        prepareStartAnimation(mEdit_help_info);
        mEditHeloInfoAnim.reverse();
    }

    /*
     * Shows and hides the search bar.
     */
    public void showSearchBar(boolean animated) {
        if (!mIsSearchBarHidden) return;
        if (animated) {
            prepareStartAnimation(mQSBSearchBar);
            mQSBSearchBarAnim.reverse();
        } else {
            mQSBSearchBarAnim.cancel();
            if (mEnableDropDownDropTargets) {
                mQSBSearchBar.setTranslationY(0);
            } else {
                mQSBSearchBar.setAlpha(1f);
            }
        }
        mIsSearchBarHidden = false;
    }
    public void hideSearchBar(boolean animated) {
        if (mIsSearchBarHidden) return;
        if (animated) {
            prepareStartAnimation(mQSBSearchBar);
            mQSBSearchBarAnim.start();
        } else {
            mQSBSearchBarAnim.cancel();
            if (mEnableDropDownDropTargets) {
                mQSBSearchBar.setTranslationY(-mBarHeight);
            } else {
                mQSBSearchBar.setAlpha(0f);
            }
        }
        mIsSearchBarHidden = true;
    }
    
    /*
     * Shows and hides the edit_help_info view.
     */
    public void showEditHelpInfo(boolean animated) {
        if (!mIsEditHeloInfoHidden) return;
        if (animated) {
            prepareStartAnimation(mEdit_help_info);
            mEditHeloInfoAnim.reverse();
        } else {
        	mEditHeloInfoAnim.cancel();
            if (mEnableDropDownDropTargets) {
            	mEdit_help_info.setTranslationY(0);
            } else {
            	mEdit_help_info.setAlpha(1f);
            }
        }
        mIsEditHeloInfoHidden = false;
    }
    public void hideEditHelpInfo(boolean animated) {
        if (mIsEditHeloInfoHidden) return;
        if (animated) {
            prepareStartAnimation(mEdit_help_info);
            mEditHeloInfoAnim.start();
        } else {
        	mEditHeloInfoAnim.cancel();
            if (mEnableDropDownDropTargets) {
            	mEdit_help_info.setTranslationY(-mBarHeight);
            } else {
            	mEdit_help_info.setAlpha(0f);
            }
        }
        mIsEditHeloInfoHidden = true;
    }

    /*
     * Gets various transition durations.
     */
    public int getTransitionInDuration() {
        return sTransitionInDuration;
    }
    public int getTransitionOutDuration() {
        return sTransitionOutDuration;
    }

    /*
     * DragController.DragListener implementation
     */
    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        // Animate out the QSB search bar, and animate in the drop target bar
        prepareStartAnimation(mDropTargetBar);
        mDropTargetBarAnim.start();
        if (!mIsSearchBarHidden) {
            prepareStartAnimation(mQSBSearchBar);
            mQSBSearchBarAnim.start();
        }
        if(mLauncher != null && mLauncher.isEditState()){
            if (!mIsEditHeloInfoHidden) {
                   prepareStartAnimation(mEdit_help_info);
                   mEditHeloInfoAnim.start();
            }
        }
    }

    public void deferOnDragEnd() {
        mDeferOnDragEnd = true;
    }

    @Override
    public void onDragEnd() {
        if (!mDeferOnDragEnd) {
            // Restore the QSB search bar, and animate out the drop target bar
            prepareStartAnimation(mDropTargetBar);
            mDropTargetBarAnim.reverse();
            if (!mIsSearchBarHidden) {
                prepareStartAnimation(mQSBSearchBar);
                mQSBSearchBarAnim.reverse();
            }
            if(mLauncher != null && mLauncher.isEditState()){
                if (!mIsEditHeloInfoHidden) {
                    prepareStartAnimation(mEdit_help_info);
                    mEditHeloInfoAnim.reverse();
                }
            }
        } else {
            mDeferOnDragEnd = false;
        }
    }

    public void onSearchPackagesChanged(boolean searchVisible, boolean voiceVisible) {
        if (mQSBSearchBar != null) {
            Drawable bg = mQSBSearchBar.getBackground();
            if (bg != null && (!searchVisible && !voiceVisible)) {
                // Save the background and disable it
                mPreviousBackground = bg;
                mQSBSearchBar.setBackgroundResource(0);
            } else if (mPreviousBackground != null && (searchVisible || voiceVisible)) {
                // Restore the background
                mQSBSearchBar.setBackground(mPreviousBackground);
            }
        }
    }

    public Rect getSearchBarBounds() {
        if (mQSBSearchBar != null) {
            final int[] pos = new int[2];
            mQSBSearchBar.getLocationOnScreen(pos);

            final Rect rect = new Rect();
            rect.left = pos[0];
            rect.top = pos[1];
            rect.right = pos[0] + mQSBSearchBar.getWidth();
            rect.bottom = pos[1] + mQSBSearchBar.getHeight();
            return rect;
        } else {
            return null;
        }
    }
}
