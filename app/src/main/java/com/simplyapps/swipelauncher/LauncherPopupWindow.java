package com.simplyapps.swipelauncher;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;

public class LauncherPopupWindow extends Dialog implements View.OnTouchListener{
    private ArrayList<View> views = new ArrayList<View>();
    private LinearLayout container;
    private SharedPreferences prefs;
    private Boolean toggleInterruptionFilter;

    public LauncherPopupWindow(Context context, int themeResId) {
        super(context, themeResId);
    }

    //TODO: while connected to wear?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_launcher);

        prefs = getContext().getSharedPreferences("com.simplyapps.swipelauncher", Context.MODE_PRIVATE);
        toggleInterruptionFilter = prefs.getBoolean("toggleInterruptionFilter", false);

        LinearLayout screenBackground = (LinearLayout) findViewById(R.id.launcher_background);
        screenBackground.setOnTouchListener(this);

        LauncherScrollView launcher = (LauncherScrollView) findViewById(R.id.launcher);
        Drawable launcherBackground = ContextCompat.getDrawable(getContext(), R.drawable.launcher_background);
        launcherBackground.setTint(prefs.getInt("launcherColorPrimary", ContextCompat.getColor(getContext(), R.color.launcherColorPrimary)));
        launcher.setBackground(launcherBackground);
        launcher.setOnTouchListener(this);

        container = (LinearLayout) findViewById(R.id.container);

        initVolumeSeekBar(AudioManager.STREAM_RING,
                R.drawable.ic_ring_off,
                R.drawable.ic_ring_vibrate,
                R.drawable.ic_ring_priority,
                R.drawable.ic_ring_volume);
        initVolumeSeekBar(AudioManager.STREAM_MUSIC,
                R.drawable.ic_media_mute,
                R.drawable.ic_media_volume);

        syncOrder();
    }

    private void syncOrder() {
        for(View v: views) {
            container.addView(v);
        }
    }

    private void initVolumeSeekBar(final int STREAM, final int mute, final int volume) {
        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout seekBarLayout = (LinearLayout) inflater.inflate(R.layout.seekbar, null);
        views.add(seekBarLayout);

        final SeekBar seekBar = (SeekBar) seekBarLayout.findViewById(R.id.seekbar);
        seekBar.setMax(audioManager.getStreamMaxVolume(STREAM));
        seekBar.setProgress(audioManager.getStreamVolume(STREAM));
        seekBar.getProgressDrawable().setTint(prefs.getInt("launcherColorAccent", ContextCompat.getColor(getContext(), R.color.launcherColorAccent)));
        seekBar.getThumb().setTint(prefs.getInt("launcherColorAccent", ContextCompat.getColor(getContext(), R.color.launcherColorAccent)));

        final Button button = (Button) seekBarLayout.findViewById(R.id.button);

        if (seekBar.getProgress() == 0) {
            button.setBackground(ContextCompat.getDrawable(getContext(), mute));
        } else {
            button.setBackground(ContextCompat.getDrawable(getContext(), volume));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar s, int progress, boolean b) {
                audioManager.setStreamVolume(STREAM, progress, 0);
                if (s.getProgress() == 0) {
                    button.setBackground(ContextCompat.getDrawable(getContext(), mute));
                } else {
                    button.setBackground(ContextCompat.getDrawable(getContext(), volume));
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            int previousValue = seekBar.getMax() / 3;

            @Override
            public void onClick(View v) {
                if (seekBar.getProgress() == 0) {
                    button.setBackground(ContextCompat.getDrawable(getContext(), volume));
                    seekBar.setProgress(previousValue);
                } else {
                    previousValue = seekBar.getProgress();
                    button.setBackground(ContextCompat.getDrawable(getContext(), mute));
                    seekBar.setProgress(0);
                }
            }
        });
    }

    private void initVolumeSeekBar(final int STREAM, final int none, final int vibrate, final int priority, final int volume) {
        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout seekBarLayout = (LinearLayout) inflater.inflate(R.layout.seekbar, null);
        views.add(seekBarLayout);

        final SeekBar seekBar = (SeekBar) seekBarLayout.findViewById(R.id.seekbar);
        seekBar.setMax(audioManager.getStreamMaxVolume(STREAM));
        seekBar.setProgress(audioManager.getStreamVolume(STREAM));
        seekBar.getProgressDrawable().setTint(prefs.getInt("launcherColorAccent", ContextCompat.getColor(getContext(), R.color.launcherColorAccent)));
        seekBar.getThumb().setTint(prefs.getInt("launcherColorAccent", ContextCompat.getColor(getContext(), R.color.launcherColorAccent)));

        final Button button = (Button) seekBarLayout.findViewById(R.id.button);

        if(LauncherTriggerService.getFilter() == NotificationService.INTERRUPTION_FILTER_NONE){
            //set none
            seekBar.setEnabled(false);
            button.setBackground(ContextCompat.getDrawable(getContext(), none));
        } else if(LauncherTriggerService.getFilter() == NotificationService.INTERRUPTION_FILTER_PRIORITY) {
            //set priority
            seekBar.setEnabled(true);
            button.setBackground(ContextCompat.getDrawable(getContext(), priority));
        } else if(seekBar.getProgress() == 0) {
            //set vibrate
            seekBar.setEnabled(true);
            button.setBackground(ContextCompat.getDrawable(getContext(), vibrate));
        }else {
            //set volume
            seekBar.setEnabled(true);
            button.setBackground(ContextCompat.getDrawable(getContext(), volume));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar s, int progress, boolean b) {
                audioManager.setStreamVolume(STREAM, progress, 0);
                if (s.getProgress() == 0) {
                    button.setBackground(ContextCompat.getDrawable(getContext(), vibrate));
                } else {
                    button.setBackground(ContextCompat.getDrawable(getContext(), (LauncherTriggerService.getFilter() == NotificationService.INTERRUPTION_FILTER_PRIORITY) ? priority : volume));
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            int previousValue = seekBar.getMax() / 3;

            @Override
            public void onClick(View v) {
                //all vol -> priority -> none -> all (vol or vibr( -> all vol)) ->priority
                int filter = LauncherTriggerService.getFilter();

                if (toggleInterruptionFilter) {
                    if (filter == NotificationService.INTERRUPTION_FILTER_NONE) {
                        //set all
                        seekBar.setEnabled(true);
                        button.setBackground(ContextCompat.getDrawable(getContext(), (seekBar.getProgress() == 0) ? vibrate : volume));
                        LauncherTriggerService.setFilter(NotificationService.INTERRUPTION_FILTER_ALL);
                    } else if (filter == NotificationService.INTERRUPTION_FILTER_PRIORITY) {
                        //set none
                        seekBar.setEnabled(false);
                        button.setBackground(ContextCompat.getDrawable(getContext(), none));
                        LauncherTriggerService.setFilter(NotificationService.INTERRUPTION_FILTER_NONE);;
                    } else {
                        //set priority
                        seekBar.setEnabled(true);
                        button.setBackground(ContextCompat.getDrawable(getContext(), priority));
                        LauncherTriggerService.setFilter(NotificationService.INTERRUPTION_FILTER_PRIORITY);
                    }
                } else {
                    if (filter == NotificationService.INTERRUPTION_FILTER_NONE) {

                    } else if (seekBar.getProgress() == 0) {
                        //set volume
                        seekBar.setProgress(previousValue);
                        button.setBackground(ContextCompat.getDrawable(getContext(), (filter == NotificationService.INTERRUPTION_FILTER_PRIORITY) ? priority : volume));
                    } else {
                        //set vibrate
                        previousValue = seekBar.getProgress();
                        button.setBackground(ContextCompat.getDrawable(getContext(), vibrate));
                        seekBar.setProgress(0);
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.launcher_background) {
            dismiss();
            return true;
        }
        return false;
    }
}
