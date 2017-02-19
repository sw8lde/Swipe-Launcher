package com.simplyapps.swipelauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ColorPickerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> colorList;
    private int checkedItemPosition;

    public ColorPickerAdapter(Context context, ArrayList<Integer> colorList) {
        this.context = context;
        this.colorList = colorList;

        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        int color = prefs.getInt("triggerColor", ContextCompat.getColor(context, R.color.triggerColor));

        checkedItemPosition = -1;
        for(int i = 0; i < colorList.size(); i++) {
            if(colorList.get(i) == color) {
                checkedItemPosition = i;
            }
        }

    }

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return colorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView button;
        if(convertView == null) {
            button = new ImageView(context);
            button.setLayoutParams(new GridView.LayoutParams(
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            48,
                            context.getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            48,
                            context.getResources().getDisplayMetrics())));
        } else {
            button = (ImageView) convertView;
        }

        button.setBackground(SettingsActivity.getBackground(colorList.get(position), position == checkedItemPosition, context));

        return button;
    }

    public void setCheckedItem(View v, int position) {
        if(checkedItemPosition != -1) {
            getView(checkedItemPosition, null, null).setBackground(SettingsActivity.getBackground(colorList.get(checkedItemPosition), false, context));
        }

        v.setBackground(SettingsActivity.getBackground(colorList.get(position), true, context));
        //v.refreshDrawableState();
        checkedItemPosition = position;
        notifyDataSetChanged();
    }

    public int getCheckedItemPosition() {
        return checkedItemPosition;
    }

    public void clearCheckedItem() {
        checkedItemPosition = -1;
    }
}
