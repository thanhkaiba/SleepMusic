package com.example.tienthanh.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TimerFragment extends Fragment implements View.OnClickListener {


    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);
        initEvent(v);
        return v;
    }

    private void initEvent(View v) {
        TextView customTimer = v.findViewById(R.id.custom_timer);
        TextView timer10Minutes = v.findViewById(R.id.timer_10_minutes);
        TextView timer20Minutes = v.findViewById(R.id.timer_20_minutes);
        TextView timer30Minutes = v.findViewById(R.id.timer_30_minutes);
        TextView timer1Hour = v.findViewById(R.id.timer_1hour);
        TextView timer2hours = v.findViewById(R.id.timer_2hour);

        timer10Minutes.setOnClickListener(this);
        timer20Minutes.setOnClickListener(this);
        timer30Minutes.setOnClickListener(this);
        timer1Hour.setOnClickListener(this);
        timer2hours.setOnClickListener(this);


        customTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = null;
                if (fm != null) {
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new CustomTimerFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        long millisecond = 0;
        switch (v.getId()) {
            case R.id.timer_10_minutes:
                millisecond = 60 * 1000 * 10;
                break;
            case R.id.timer_20_minutes:
                millisecond = 60 * 1000 * 2 * 10;
                break;
            case R.id.timer_30_minutes:
                millisecond = 60 * 1000 * 3 * 10;
                break;
            case R.id.timer_1hour:
                millisecond = 60 * 1000 * 60 ;
                break;
            case R.id.timer_2hour:
                millisecond = 60 * 1000 * 120 ;
                break;
        }


        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setTimer(millisecond);
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
}
