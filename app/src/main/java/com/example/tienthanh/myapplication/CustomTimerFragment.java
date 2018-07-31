package com.example.tienthanh.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class CustomTimerFragment extends Fragment {

    private int timerHour = 05;
    private int timerMinute = 00;
    private static final int MAX_TIMER_HOUR = 24;
    private static final int MAX_TIMER_MINUTE = 59;

    public CustomTimerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View v = inflater.inflate(R.layout.fragment_custom_timer, container, false);
       addEvent(v);
       return v;
    }

    private void addEvent(View v) {

        final TextView hour = v.findViewById(R.id.timer_hour);
        final TextView minute = v.findViewById(R.id.timer_minute);

        TextView startCustomTimer = v.findViewById(R.id.tv_start_custom_timer);
        TextView cancelCustomTimer = v.findViewById(R.id.tv_cancle_custom_timer);

        startCustomTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int millisecond = (timerHour*60 + timerMinute) * 60 * 1000;

                if (millisecond != 0) {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) {
                        activity.setTimer(millisecond);
                    }
                }

                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = null;
                if (fm != null) {
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new RunningTimerFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                }
            }
        });

        cancelCustomTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = null;
                if (fm != null) {
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new TimerFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                }
            }
        });

        ImageView btnAddHour = v.findViewById(R.id.btn_add_hour);
        ImageView btnMinusHour = v.findViewById(R.id.btn_minus_hour);
        ImageView btnAddMinute = v.findViewById(R.id.btn_add_minute);
        ImageView btnMinusMinute = v.findViewById(R.id.btn_minus_minute);

        btnAddHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerHour < MAX_TIMER_HOUR) {
                    timerHour++;

                } else {
                    timerHour = 0;

                }
                hour.setText(String.format(Locale.getDefault(), "%02d", timerHour));
            }

        });

        btnMinusHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerHour > 0) {
                    timerHour--;
                } else {
                    timerHour = MAX_TIMER_HOUR;
                }
                hour.setText(String.format(Locale.getDefault(), "%02d", timerHour));
            }
        });

        btnAddMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerMinute < MAX_TIMER_MINUTE) {
                    timerMinute++;

                } else {
                    timerMinute = 0;
                }

                minute.setText(String.format(Locale.getDefault(), "%02d", timerMinute));

            }
        });

        btnMinusMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerMinute > 0) {
                    timerMinute--;

                } else {
                    timerMinute = MAX_TIMER_MINUTE;

                }
                minute.setText(String.format(Locale.getDefault(), "%02d", timerMinute));
            }
        });
    }



}
