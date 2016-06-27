package com.lb.cannonrobot;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;

public class FragmentTab1 extends Fragment {

    private RockerView Rocker;
    static public Switch remoteSwitch;
    static public Switch turnmodeSwitch;
    static public Switch oriturnmodeSwitch;
    static public SeekBar mseekBarHead;
    static public SeekBar mseekBarLeftHand;
    static public SeekBar mseekBarRightHand;
    static public byte RockerValue[]={1,0,0,0,0,1};
    static public byte SteerValue[]={0,0,0,0,0};
    static public boolean isRemoteSwitch;
    static public boolean isTurnmodeSwitch;
    static public boolean isOriTurnmodeSwitch;
    private SensorManager sm=null;
   private Sensor aSensor=null;
    private Sensor mSensor=null;

    float[] accelerometerValues=new float[3];
    float[] magneticFieldValues=new float[3];
   public static float[] EylerValues=new float[3];
    public static float Yaw;
    private float Yaw_first;
    private boolean isYaw_first=false;
    float[] rotation=new float[9];




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1,
                container, false);

        WindowManager wm1 = getActivity().getWindowManager();
        int width = wm1.getDefaultDisplay().getWidth();
        Rocker=  (RockerView)view.findViewById(R.id.rockerView1);
        remoteSwitch=(Switch)view.findViewById(R.id.switch_Remote);
        turnmodeSwitch=(Switch)view.findViewById(R.id.switch_Turnmode);
        oriturnmodeSwitch=(Switch)view.findViewById(R.id.switch_OriTurnmode);
        mseekBarHead=(SeekBar)view.findViewById(R.id.seekBarHead);
        mseekBarLeftHand=(SeekBar)view.findViewById(R.id.seekBarlefttHand);
        mseekBarRightHand=(SeekBar)view.findViewById(R.id.seekBarRighttHand);
        mseekBarHead.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
               @Override
               public void onStopTrackingTouch(SeekBar seekBar) {}
              @Override
             public void onStartTrackingTouch(SeekBar seekBar) {}
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 SteerValue[0]=(byte)(progress-100);
             }
         });
        mseekBarLeftHand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SteerValue[1]=(byte)(progress-100);
            }
        });
        mseekBarRightHand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SteerValue[2]=(byte)(progress-100);
            }
        });
        sm=(SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        aSensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        turnmodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   isTurnmodeSwitch=true;
                //    oriturnmodeSwitch.setEnabled(true);


                }
                else {
                  isTurnmodeSwitch=false;
                   // oriturnmodeSwitch.setEnabled(false);

                }
            }
        });

        remoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isRemoteSwitch=true;
                   // MainActivity.handler.postDelayed(MainActivity.runnable, 110);//开启周期性的发送遥控数据
                 //   MainActivity.Remote_Flag=true;

                }
                else {
                    isRemoteSwitch=false;
                   // MainActivity.handler.removeCallbacks(MainActivity.runnable);//关闭遥控数据发送
                 //   MainActivity.Remote_Flag=false;
                }
            }
        });
        oriturnmodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isOriTurnmodeSwitch=true;
                    isYaw_first=true;
                   // sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
                   // sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
                }
                else {
                    isOriTurnmodeSwitch=false;
                 //   sm.unregisterListener(myListener);
                    isYaw_first=false;
                }
            }
        });
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) Rocker.getLayoutParams();
        linearParams.width = width;
        linearParams.height = width;
        Rocker.setLayoutParams(linearParams);


        Rocker.setRockerChangeListener(new RockerView.RockerChangeListener() {
            @Override
            public void report(float x, float y) {

                RockerValue[0] = (byte) (y / Rocker.getR() * 30);
                RockerValue[1] = (byte) (x / Rocker.getR() * 30);
                // System.out.println(RockerValue[0]);

            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
       // sm.unregisterListener(myListener);
    }
    public final SensorEventListener myListener=new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                accelerometerValues=event.values;
            }
            if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                magneticFieldValues=event.values;
            }
            //调用getRotaionMatrix获得变换矩阵R[]
            SensorManager.getRotationMatrix(rotation, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(rotation, EylerValues);
            //经过SensorManager.getOrientation(R, values);得到的values值为弧度
            //转换为角度

            EylerValues[0]=(float)Math.toDegrees(EylerValues[0]);
            EylerValues[1]=(float)Math.toDegrees(EylerValues[1]);
            EylerValues[2]=(float)Math.toDegrees(EylerValues[2]);
           // MainActivity.topdisplay.setText("x=" + EylerValues[0]);
            if(isYaw_first){
                isYaw_first=false;
                Yaw_first=EylerValues[0];
            }
            Yaw=EylerValues[0]-Yaw_first;
            if(Yaw>180)Yaw-=360;
            else if(Yaw<-180)Yaw+=360;
           // topdisplay2.setText("x="+values[1]);
           // topdisplay3.setText("x="+values[2]);
        }};
}

