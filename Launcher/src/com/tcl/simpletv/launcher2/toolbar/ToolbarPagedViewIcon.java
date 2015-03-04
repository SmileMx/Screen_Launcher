/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.tcl.simpletv.launcher2.toolbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An icon on a PagedView, specifically for items in the launcher's paged view (with compound
 * drawables on the top).
 */
public class ToolbarPagedViewIcon extends TextView {
    /** A simple callback interface to allow a PagedViewIcon to notify when it has been pressed */
    public static interface PressedCallback {
        void iconPressed(ToolbarPagedViewIcon icon);
    }

    @SuppressWarnings("unused")
    private static final String TAG = "PagedViewIcon";
    private static final float PRESS_ALPHA = 0.4f;

    private ToolbarPagedViewIcon.PressedCallback mPressedCallback;
    private boolean mLockDrawableState = false;

    private Bitmap mIcon;
    public ImageView delIcon;
    public ToolbarApplicationsInfo mInfo;
    
//    public ApplicationInfo mInfo;
    
    
    public void setDelIcon(ImageView icon){
    	this.delIcon = icon;
    }
    
    public ToolbarPagedViewIcon(Context context) {
        this(context, null);
    }

    public ToolbarPagedViewIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolbarPagedViewIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void applyFromApplicationInfo(ToolbarApplicationsInfo info, boolean scaleUp,
            ToolbarPagedViewIcon.PressedCallback cb) {
    	this.mInfo = info;
    	this.mIcon = info.iconBitmap;
    	this.mPressedCallback = cb;
        
        setCompoundDrawablesWithIntrinsicBounds(null, new ToolbarFastBitmapDrawable(mIcon), null, null);
//        setCompoundDrawablesWithIntrinsicBounds(null, info.iconBitmap, null, null);
        setText(info.title);
        setTag(info);
    }

    public void lockDrawableState() {
        mLockDrawableState = true;
    }

    public void resetDrawableState() {
        mLockDrawableState = false;
        post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();

        // We keep in the pressed state until resetDrawableState() is called to reset the press
        // feedback
        if (isPressed()) {
            setAlpha(PRESS_ALPHA);
            if (mPressedCallback != null) {
                mPressedCallback.iconPressed(this);
            }
        } else if (!mLockDrawableState) {
            setAlpha(1f);
        }
    }
}
