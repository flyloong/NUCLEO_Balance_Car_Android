package com.lb.cannonrobot;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View.OnClickListener;
public class FragmentTab3 extends Fragment implements OnClickListener {
    static public ImageView mbtnPlayPrevious;
    static public ImageView mbtnPlayPlay;
    static public ImageView mbtnPlayNext;
    static public ImageView mbtnPlayRepeat;
    static public SeekBar mseekBarVolume;
    static public byte mVolume;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        mbtnPlayPrevious=(ImageView)view.findViewById(R.id.btnPlayPrevious);
        mbtnPlayPlay=(ImageView)view.findViewById(R.id.btnPlayPlay);
        mbtnPlayNext=(ImageView)view.findViewById(R.id.btnPlayNext);
        mbtnPlayRepeat=(ImageView)view.findViewById(R.id.btnPlayRepeat);
        mseekBarVolume=(SeekBar)view.findViewById(R.id.seekBarVolume);
        mseekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //      mDataDisplay.setText(String.valueOf(progress));
                //  MYSendValue[1]=progress;
                mVolume=(byte)progress;
                Intent intent =new Intent();
                intent.setAction(MainActivity.ACTION_MUSIC_VOLUME);
                intent.putExtra("msg", mVolume);
                getActivity().sendBroadcast(intent);
            }
        });
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

/*

            */
        }
    }
}

