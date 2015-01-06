package com.honkimi.swipepalletview;

import android.view.MotionEvent;

/**
 * Created by honkimi on 15/01/06.
 */
public interface PalletListener {
    void onPalletTouched(MotionEvent event, boolean isFromBase);
    void onAnimationStart(boolean isFromBase);
    void onAnimationEnd(boolean isFromBase);
}
