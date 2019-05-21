package com.xhd.base.adapter.anim;

import android.animation.Animator;
import android.view.View;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public interface BaseAnimation {
    /**
     *
     * @param view targetView
     * @return
     */
    Animator[] getAnimators(View view);
}
