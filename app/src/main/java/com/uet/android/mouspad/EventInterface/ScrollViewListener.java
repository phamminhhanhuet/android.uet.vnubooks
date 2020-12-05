package com.uet.android.mouspad.EventInterface;

import com.uet.android.mouspad.Utils.ScrollViewExt;

public interface ScrollViewListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}
