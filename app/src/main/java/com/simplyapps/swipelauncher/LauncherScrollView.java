package com.simplyapps.swipelauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class LauncherScrollView extends ScrollView {
    public LauncherScrollView(Context context) {
        super(context);
    }

    public LauncherScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LauncherScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LauncherScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        SharedPreferences prefs = getContext().getSharedPreferences("com.simplyapps.swipelauncher", Context.MODE_PRIVATE);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec((prefs.getInt("launcherMaxHeight", 5) * 20) + 200, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
