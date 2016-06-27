package com.lb.cannonrobot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentTab2 extends Fragment {
   static public TextView mDataSend;
    static public TextView mDataDisplay;
    static public TextView mDataP1;
    static public TextView mDataP2;
    static public TextView mDataP3;
    static public TextView mDataP4;
    static public TextView mDataP5;
    static public TextView mDataP6;
    static public TextView mDataP7;
    static public TextView mDataP8;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2,
                container, false);
        mDataSend = (EditText) view.findViewById(R.id.SendText);
        mDataDisplay =(TextView)view.findViewById(R.id.textView);
        mDataP1 = (EditText) view.findViewById(R.id.P1);
        mDataP2 =(TextView)view.findViewById(R.id.P2);
        mDataP3 = (EditText) view.findViewById(R.id.P3);
        mDataP4 =(TextView)view.findViewById(R.id.P4);
        mDataP5 = (EditText) view.findViewById(R.id.P5);
        mDataP6 =(TextView)view.findViewById(R.id.P6);
        mDataP7 = (EditText) view.findViewById(R.id.P7);
        mDataP8 =(TextView)view.findViewById(R.id.P8);
        return view;
    }

}

