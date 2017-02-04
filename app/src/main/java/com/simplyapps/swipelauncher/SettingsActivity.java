package com.simplyapps.swipelauncher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.rarepebble.colorpicker.ColorPickerView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
    private DrawerLayout drawerLayout;
    private View triggerColor;
    private Button triggerColorButton;
    private View triggerWidth;
    private TextView triggerWidthButton;
    private View triggerHeight;
    private TextView triggerHeightButton;
    private View triggerOffset;
    private TextView triggerOffsetButton;
    private View launcherColorPrimary;
    private Button launcherColorPrimaryButton;
    private View launcherColorAccent;
    private Button launcherColorAccentButton;
    private View launcherMaxHeight;
    private TextView launcherMaxHeightButton;
    private View iconPack;
    private TextView iconPackButton;
    private View iconTint;
    private Button iconTintButton;
    private View longPressRemove;
    private Switch longPressRemoveSwitch;
    private View vibrateOnLongPress;
    private TextView vibrateOnLongPressText;
    private Switch vibrateOnLongPressSwitch;
    private View hoverTrigger;
    private Switch hoverTriggerSwitch;
    private View autostart;
    private Switch autostartSwitch;
    private View toggleInterruptionFilter;
    private Switch toggleInterruptionFilterSwitch;
    private ArrayList<Integer> colorPrimaryList = new ArrayList<>();
    private ArrayList<Integer> colorAccentList = new ArrayList<>();

    //TODO: add share and donate buttons to nav drawer
    //TODO: implement autostart, icon pack

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.TextToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        NavigationView drawer = (NavigationView) findViewById(R.id.navigation_view);
        drawer.setNavigationItemSelectedListener(this);
        drawer.setCheckedItem(R.id.settings);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        initColors();
        initSettings();
    }

    public static int getResId(Class c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            return f.getInt(f);
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void initColors() {
        String[] colorNames = {"red", "pink", "purple", "dark_purple", "indigo", "blue", "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber", "orange", "deep_orange", "brown", "grey", "blue_grey"};
        String[] colorPrimaryInts = {"50", "100", "200", "300", "400", "500", "600", "700", "800", "900"};
        String[] colorAccentInts = {"A100", "A200", "A400", "A700"};

        for(String colorName: colorNames) {
            for(String colorPrimaryInt: colorPrimaryInts) {
                colorPrimaryList.add(ContextCompat.getColor(this, getResId(
                                        R.color.class,
                                        colorName + "_" + colorPrimaryInt)));
            }

            if(!colorName.equals("brown") && !colorName.equals("grey") && !colorName.equals("blue_grey")) {
                for(String colorAccentInt: colorAccentInts) {
                    colorAccentList.add(ContextCompat.getColor(this, getResId(
                            R.color.class,
                            colorName + "_" + colorAccentInt)));
                }
            }
        }

        colorPrimaryList.add(ContextCompat.getColor(this, R.color.white));
        colorPrimaryList.add(ContextCompat.getColor(this, R.color.black));
        colorAccentList.add(ContextCompat.getColor(this, R.color.white));
        colorAccentList.add(ContextCompat.getColor(this, R.color.black));
    }

    public void initSettings() {
        SharedPreferences prefs = getSharedPreferences("com.simplyapps.swipelauncher", MODE_PRIVATE);

        triggerColor = findViewById(R.id.trigger_color);
        triggerColor.setOnClickListener(this);
        triggerColorButton = (Button) findViewById(R.id.trigger_color_button);
        triggerColorButton.setBackground(getBackground(prefs.getInt("triggerColor", ContextCompat.getColor(this, R.color.triggerColor)), false, this));
        triggerWidth = findViewById(R.id.trigger_width);
        triggerWidth.setOnClickListener(this);
        triggerWidthButton = (TextView) findViewById(R.id.trigger_width_button);
        triggerWidthButton.setText(String.format("%d", ((prefs.getInt("triggerWidth", 4) + 1) * 5)));
        triggerHeight = findViewById(R.id.trigger_height);
        triggerHeight.setOnClickListener(this);
        triggerHeightButton = (TextView) findViewById(R.id.trigger_height_button);
        triggerHeightButton.setText(String.format("%d", prefs.getInt("triggerHeight", 4) + 1));
        triggerOffset = findViewById(R.id.trigger_offset);
        triggerOffset.setOnClickListener(this);
        triggerOffsetButton = (TextView) findViewById(R.id.trigger_offset_button);
        triggerOffsetButton.setText(String.format("%d", prefs.getInt("triggerOffset", 0) * 10 - 50));
        launcherColorPrimary = findViewById(R.id.launcher_color_primary);
        launcherColorPrimary.setOnClickListener(this);
        launcherColorPrimaryButton = (Button) findViewById(R.id.launcher_color_primary_button);
        launcherColorPrimaryButton.setBackground(getBackground(prefs.getInt("launcherColorPrimary", ContextCompat.getColor(this, R.color.launcherColorPrimary)), false, this));
        launcherColorAccent = findViewById(R.id.launcher_color_accent);
        launcherColorAccent.setOnClickListener(this);
        launcherColorAccentButton = (Button) findViewById(R.id.launcher_color_accent_button);
        launcherColorAccentButton.setBackground(getBackground(prefs.getInt("launcherColorAccent", ContextCompat.getColor(this, R.color.launcherColorAccent)), false, this));
        launcherMaxHeight = findViewById(R.id.launcher_max_height);
        launcherMaxHeight.setOnClickListener(this);
        launcherMaxHeightButton = (TextView) findViewById(R.id.launcher_max_height_button);
        launcherMaxHeightButton.setText(String.format("%d", prefs.getInt("launcherMaxHeight", 5) * 20 + 200));
        iconPack = findViewById(R.id.icon_pack);
        iconPack.setOnClickListener(this);
        iconPackButton = (TextView) findViewById(R.id.icon_pack_button);
        iconPackButton.setText(prefs.getString("iconPack", "System"));
        iconTint = findViewById(R.id.icon_tint);
        iconTint.setOnClickListener(this);
        iconTintButton = (Button) findViewById(R.id.icon_tint_button);
        iconTintButton.setBackground(getBackground(prefs.getInt("iconTint", ContextCompat.getColor(this, R.color.iconTint)), false, this));
        longPressRemove = findViewById(R.id.long_press_remove);
        longPressRemove.setOnClickListener(this);
        longPressRemoveSwitch = (Switch) findViewById(R.id.long_press_remove_switch);
        longPressRemoveSwitch.setChecked(prefs.getBoolean("longPressRemove", true));
        longPressRemoveSwitch.setOnCheckedChangeListener(this);
        vibrateOnLongPress = findViewById(R.id.vibrate_on_long_press);
        vibrateOnLongPress.setOnClickListener(this);
        vibrateOnLongPressSwitch = (Switch) findViewById(R.id.vibrate_on_long_press_switch);
        vibrateOnLongPressSwitch.setChecked(prefs.getBoolean("vibrateOnLongPress", false));
        vibrateOnLongPressSwitch.setOnCheckedChangeListener(this);
        vibrateOnLongPressText = (TextView) findViewById(R.id.vibrate_on_long_press_text);
        if(!longPressRemoveSwitch.isChecked()) {
            vibrateOnLongPressSwitch.setEnabled(false);
            vibrateOnLongPressText.setTextColor(ContextCompat.getColor(this, R.color.textDisabled));
        }
        hoverTrigger = findViewById(R.id.hover_trigger);
        hoverTrigger.setOnClickListener(this);
        hoverTriggerSwitch = (Switch) findViewById(R.id.hover_trigger_switch);
        hoverTriggerSwitch.setChecked(prefs.getBoolean("hoverTrigger", false));
        hoverTriggerSwitch.setOnCheckedChangeListener(this);
        autostart = findViewById(R.id.autostart);
        autostart.setOnClickListener(this);
        autostartSwitch = (Switch) findViewById(R.id.autostart_switch);
        autostartSwitch.setChecked(prefs.getBoolean("autostart", true));
        autostartSwitch.setOnCheckedChangeListener(this);
        toggleInterruptionFilter = findViewById(R.id.toggle_interruption_filter);
        toggleInterruptionFilter.setOnClickListener(this);
        toggleInterruptionFilterSwitch = (Switch) findViewById(R.id.toggle_interruption_filter_switch);
        toggleInterruptionFilterSwitch.setChecked(prefs.getBoolean("toggleInterruptionFilter", false));
        toggleInterruptionFilterSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == triggerColor.getId()) {
            initColorPicker("triggerColor");
        } else if(id == triggerWidth.getId()) {
            initNumberPicker("triggerWidth");
        } else if(id == triggerHeight.getId()) {
            initNumberPicker("triggerHeight");
        } else if(id == triggerOffset.getId()) {
            initNumberPicker("triggerOffset");
        } else if(id == launcherColorPrimary.getId()) {
            initColorPicker("launcherColorPrimary");
        } else if(id == launcherColorAccent.getId()) {
            initColorPicker("launcherColorAccent");
        } else if(id == launcherMaxHeight.getId()) {
            initNumberPicker("launcherMaxHeight");
        } else if(id == iconPack.getId()) {
            //show icon pack picker
            //save icon pack
        } else if(id == iconTint.getId()) {
            initColorPicker("iconTint");
        } else if(id == longPressRemove.getId()) {
            longPressRemoveSwitch.toggle();
            if(LauncherTriggerService.isRunning) {
                stopService(new Intent(this, LauncherTriggerService.class));
                startService(new Intent(this, LauncherTriggerService.class));
            }
        } else if(id == vibrateOnLongPress.getId()) {
            if(longPressRemoveSwitch.isChecked()){
                vibrateOnLongPressSwitch.toggle();
                if(LauncherTriggerService.isRunning) {
                    stopService(new Intent(this, LauncherTriggerService.class));
                    startService(new Intent(this, LauncherTriggerService.class));
                }
            }
        } else if(id == hoverTrigger.getId()) {
            hoverTriggerSwitch.toggle();
            if(LauncherTriggerService.isRunning) {
                stopService(new Intent(this, LauncherTriggerService.class));
                startService(new Intent(this, LauncherTriggerService.class));
            }
        } else if(id == autostart.getId()) {
            autostartSwitch.toggle();
            if(LauncherTriggerService.isRunning) {
                stopService(new Intent(this, LauncherTriggerService.class));
                startService(new Intent(this, LauncherTriggerService.class));
            }
        } else if(id == toggleInterruptionFilter.getId()) {
            toggleInterruptionFilterSwitch.toggle();
            if(LauncherTriggerService.isRunning) {
                stopService(new Intent(this, LauncherTriggerService.class));
                startService(new Intent(this, LauncherTriggerService.class));
            }
        }
    }

    private void setDividerColor(NumberPicker picker, int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for(java.lang.reflect.Field field: pickerFields) {
            if(field.getName().equals("mSelectionDivider")) {
                field.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    field.set(picker, colorDrawable);
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static LayerDrawable getBackground(int tint, boolean isChecked, Context context) {
        Drawable[] layers;
        if(isChecked) {
            layers = new Drawable[4];
            layers[0] = ContextCompat.getDrawable(context, R.drawable.image_transparent_background);
            layers[1] = ContextCompat.getDrawable(context, R.drawable.circle_button);
            layers[2] = ContextCompat.getDrawable(context, R.drawable.image_check_overlay);
            layers[3] = ContextCompat.getDrawable(context, R.drawable.circle_border);
        } else {
            layers = new Drawable[3];
            layers[0] = ContextCompat.getDrawable(context, R.drawable.image_transparent_background);
            layers[1] = ContextCompat.getDrawable(context, R.drawable.circle_button);
            layers[2] = ContextCompat.getDrawable(context, R.drawable.circle_border);
        }
        layers[1].setTint(tint);
        return new LayerDrawable(layers);
    }

    private void initColorPicker(final String setting) {
        final SharedPreferences prefs = getSharedPreferences(
                "com.simplyapps.swipelauncher",
                MODE_PRIVATE);
        final SharedPreferences.Editor editor = getSharedPreferences(
                "com.simplyapps.swipelauncher",
                MODE_PRIVATE).edit();
        final ColorPickerView picker;
        final AlertDialog.Builder builder;
        final GridView simpleColorPicker;
        final ColorPickerAdapter adapter;
        AlertDialog.Builder simpleColorPickerBuilder;
        final Button settingButton;
        final ArrayList<Integer> colorList;
        final int stringId;
        int color;

        if(setting.equals("triggerColor")) {
            settingButton = triggerColorButton;
            colorList = colorPrimaryList;
            stringId = R.string.trigger_color;
        } else if(setting.equals("launcherColorPrimary")) {
            settingButton = launcherColorPrimaryButton;
            colorList = colorPrimaryList;
            stringId = R.string.launcher_color_primary;
        } else if(setting.equals("launcherColorAccent")){
            settingButton = launcherColorAccentButton;
            colorList = colorAccentList;
            stringId = R.string.launcher_color_accent;
        } else {
            settingButton = iconTintButton;
            colorList = colorPrimaryList;
            stringId = R.string.default_icon_tint;
        }

        picker = new ColorPickerView(this);

        //Override hex edit text field's lon press listener to fix crash
        Field[] fields = ColorPickerView.class.getDeclaredFields();
        for(Field f: fields) {
            if(f.getName().equals("hexEdit")) {
                f.setAccessible(true);
                try {
                    EditText hexEdit = (EditText) picker.findViewById(R.id.hexEdit);
                    hexEdit.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    });
                    f.set(picker, hexEdit);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        adapter = new ColorPickerAdapter(this, colorList);

        color = prefs.getInt(
                setting,
                ContextCompat.getColor(this, getResources().getIdentifier(
                        setting,
                        "color",
                        "com.simplyapps.swipelauncher"))
        );
        picker.setColor(color);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                switch (button) {
                    case DialogInterface.BUTTON_POSITIVE:
                        adapter.clearCheckedItem();
                        settingButton.setBackground(getBackground(picker.getColor(), false, getApplicationContext()));
                        editor.putInt(setting, picker.getColor());
                        editor.apply();
                        if(LauncherTriggerService.isRunning) {
                            stopService(new Intent(getApplicationContext(), LauncherTriggerService.class));
                            startService(new Intent(getApplicationContext(), LauncherTriggerService.class));
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        };

        builder = new AlertDialog.Builder(this);
        builder
                .setView(picker)
                .setTitle(stringId)
                .setPositiveButton(getResources().getString(R.string.confirm), listener)
                .setNegativeButton(getResources().getString(R.string.cancel), listener);

        simpleColorPicker = new GridView(this);
        simpleColorPicker.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        simpleColorPicker.setColumnWidth(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        48,
                        getResources().getDisplayMetrics()));
        simpleColorPicker.setVerticalSpacing(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8,
                        getResources().getDisplayMetrics()));
        simpleColorPicker.setHorizontalSpacing(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8,
                        getResources().getDisplayMetrics()));
        int p = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                getResources().getDisplayMetrics());
        simpleColorPicker.setPadding(p, p, p, p);
        simpleColorPicker.setNumColumns(GridView.AUTO_FIT);
        simpleColorPicker.setGravity(Gravity.CENTER);
        simpleColorPicker.setAdapter(adapter);
        simpleColorPicker.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        simpleColorPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCheckedItem(view, position);
            }
        });

        DialogInterface.OnClickListener simpleColorPickerListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                switch (button) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int pos = adapter.getCheckedItemPosition();
                        if(pos != -1) {
                            settingButton.setBackground(getBackground(colorList.get(pos), false, getApplicationContext()));
                            editor.putInt(setting, colorList.get(pos));
                            editor.apply();
                            if(LauncherTriggerService.isRunning) {
                                stopService(new Intent(getApplicationContext(), LauncherTriggerService.class));
                                startService(new Intent(getApplicationContext(), LauncherTriggerService.class));
                            }
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        builder.show();
                        dialog.dismiss();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        };

        simpleColorPickerBuilder = new AlertDialog.Builder(this);
        simpleColorPickerBuilder
                .setView(simpleColorPicker)
                .setTitle(stringId)
                .setPositiveButton(
                        getResources().getString(R.string.confirm),
                        simpleColorPickerListener)
                .setNegativeButton(
                        getResources().getString(R.string.cancel),
                        simpleColorPickerListener)
                .setNeutralButton(
                        getResources().getString(R.string.custom_color),
                        simpleColorPickerListener)
                .show();
    }

    private void initNumberPicker(final String setting) {
        /**
         * triggerWidth is stored as array index values and measured in percentage of orientation width
         * triggerHeight is stored as array index values and measured in dp
         * triggerOffset is stored as array index values and measure in percentage of screen width
         * launcherMaxHeight is stored as array index values and measured in dp
         */

        final SharedPreferences prefs = getSharedPreferences(
                "com.simplyapps.swipelauncher",
                MODE_PRIVATE);
        final SharedPreferences.Editor editor = getSharedPreferences(
                "com.simplyapps.swipelauncher",
                MODE_PRIVATE).edit();
        final NumberPicker picker;
        AlertDialog.Builder builder;
        final TextView settingButton;
        final int stringId;
        String[] values;

        if(setting.equals("triggerWidth")) {
            settingButton = triggerWidthButton;
            stringId = R.string.trigger_width;
            values = new String[20];
            for(int i = 0; i < values.length; i++) {
                values[i] = String.format("%d", (i + 1) * 5);
            }
        } else if(setting.equals("triggerHeight")) {
            settingButton = triggerHeightButton;
            stringId = R.string.trigger_height;
            values = new String[25];
            for(int i = 0; i < values.length; i++) {
                values[i] = String.format("%d", i + 1);
            }
        } else if(setting.equals("triggerOffset")) {
            settingButton = triggerOffsetButton;
            stringId = R.string.trigger_offset;
            values = new String[11];
            for(int i = 0; i < values.length; i++) {
                values[i] = String.format("%d", 10 * i - 50);
            }
        } else {
            settingButton = launcherMaxHeightButton;
            stringId = R.string.launcher_max_height;
            values = new String[36];
            for(int i = 0; i < values.length; i++) {
                values[i] = String.format("%d", 20 * i + 200);
            }
        }

        picker = new NumberPicker(this);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setValue(prefs.getInt(setting, 4));
        picker.setWrapSelectorWheel(false);
        setDividerColor(picker, ContextCompat.getColor(this, R.color.colorPrimary));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                switch (button) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(setting.equals("triggerWidth")) {
                            settingButton.setText(String.format("%d", ((picker.getValue()) + 1) * 5));
                        } else if(setting.equals("triggerHeight")) {
                            settingButton.setText(String.format("%d", picker.getValue() + 1));
                        } else if(setting.equals("triggerOffset")) {
                            settingButton.setText(String.format("%d", ((picker.getValue()) * 10) - 50));
                        } else {
                            settingButton.setText(String.format("%d", picker.getValue() * 20 + 200));
                        }
                        editor.putInt(setting, picker.getValue());
                        editor.apply();
                        if(LauncherTriggerService.isRunning) {
                            stopService(new Intent(getApplicationContext(), LauncherTriggerService.class));
                            startService(new Intent(getApplicationContext(), LauncherTriggerService.class));
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        };

        builder = new AlertDialog.Builder(this);
        builder
                .setView(picker)
                .setTitle(stringId)
                .setPositiveButton(getResources().getString(R.string.confirm), listener)
                .setNegativeButton(getResources().getString(R.string.cancel), listener)
                .show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        String name;

        if(id == longPressRemoveSwitch.getId()) {
            name = "longPressRemove";
            if(isChecked) {
                vibrateOnLongPressSwitch.setEnabled(true);
                vibrateOnLongPressText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
            } else {
                vibrateOnLongPressSwitch.setEnabled(false);
                vibrateOnLongPressText.setTextColor(ContextCompat.getColor(this, R.color.textDisabled));
            }
        } else if(id == hoverTriggerSwitch.getId()) {
            name = "hoverTrigger";
        } else if(id == autostartSwitch.getId()) {
            name = "autostart";
        } else if(id == toggleInterruptionFilterSwitch.getId()) {
            name = "toggleInteruptionFilter";
        } else {
            name = "vibrateOnLongPress";
        }

        SharedPreferences.Editor editor = getSharedPreferences(
                "com.simplyapps.swipelauncher",
                MODE_PRIVATE).edit();
        editor.putBoolean(name, isChecked);
        editor.apply();

        //Log.d("BUTTON", buttonView.getId() + "");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {
            case R.id.home:
                getApplicationContext().startActivity(new Intent(
                        getApplicationContext(),
                        MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            case R.id.edit:
                getApplicationContext().startActivity(new Intent(
                        getApplicationContext(),
                        EditActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            case R.id.settings:
                //do nothing
                return true;
            case R.id.rate:
                //url to googly play
                return true;
            case R.id.contact:
                //compose email
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
