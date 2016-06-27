package com.lb.cannonrobot;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  implements OnClickListener {

    public static final String ACTION_MUSIC_MODE         =    "com.lb.cannonrobot.ACTION_MUSIC_MODE";
    public static final String ACTION_MUSIC_VOLUME         =    "com.lb.cannonrobot.ACTION_MUSIC_VOLUME";
    public static final String ACTION_PARAMETER_MODIFY         =    "com.lb.cannonrobot.ACTION_PARAMETER_MODIFY";
    public static final String ACTION_PARAMETER_MODIFY_ACK         =    "com.lb.cannonrobot.ACTION_PARAMETER_MODIFY_ACK";
    public static final String ACTION_PARAMETER_ORIGIN        =    "com.lb.cannonrobot.ACTION_PARAMETER_ORIGIN";
    public static final String ACTION_REMOTE_CONTROL         =    "com.lb.cannonrobot.ACTION_REMOTE_CONTROL";
    public static final String ACTION_REMOTE_MODE        =    "com.lb.cannonrobot.ACTION_REMOTE_MODE";
    public static final String ACTION_REMOTE_SEND       =    "com.lb.cannonrobot.ACTION_REMOTE_SEND";


    private FragmentTab1 messageFragment;
    private FragmentTab2 contactsFragment;
    private FragmentTab3 newsFragment;
    private FragmentTab4 settingFragment;
    private View messageLayout;
    private View contactsLayout;
    private View newsLayout;
    private View settingLayout;
    private ImageView messageImage;
    private ImageView contactsImage;
    private ImageView newsImage;
    private ImageView settingImage;
    private TextView messageText;
    private TextView contactsText;
    private TextView newsText;
    private TextView settingText;
    private FragmentManager fragmentManager;
    private ScanHelper scanner;
    static public JumaDevice myDevice;

    private HashMap<UUID, JumaDevice> deviceList =  new HashMap<UUID, JumaDevice>();
    public static final String ACTION_DEVICE_DISCOVERED = "com.example.temperaturegatheringdemo.ACTION_DEVICE_DISCOVERED";
    public Toolbar toolbar;
    public static TextView topdisplay;
    private TextView topdisplay2;
    private TextView topdisplay3;
    private TextView topdisplay4;
    private int temp=0;
    private RockerView rockerView;
    private byte[] rcsend= new byte[2];
    static public Handler handler = new Handler();
    byte[] sendbytesplay= new byte[4];
    boolean isPlayMusic=false;
    public static  boolean Remote_Flag=false;
   static byte[] sendbytesremote= new byte[13];
    static byte Car_cmd2;
    static boolean Car_cmd2_New_Flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//透明化导航栏
        }
      //  View decorView=getWindow().getDecorView();//隐藏导航栏
      //  int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
      //  decorView.setSystemUiVisibility(uiOptions);

        IntentFilter sendFilter = new IntentFilter();
        sendFilter.addAction(ACTION_MUSIC_MODE);
        sendFilter.addAction(ACTION_MUSIC_VOLUME);
        sendFilter.addAction(ACTION_PARAMETER_MODIFY);
        sendFilter.addAction(ACTION_PARAMETER_MODIFY_ACK);
        sendFilter.addAction(ACTION_PARAMETER_ORIGIN);
        sendFilter.addAction(ACTION_REMOTE_CONTROL);
        sendFilter.addAction(ACTION_REMOTE_MODE);
        sendFilter.addAction(ACTION_REMOTE_SEND);
        registerReceiver(sendReceiver, sendFilter);


        topdisplay=(TextView)findViewById(R.id.top_display);
        topdisplay2=(TextView)findViewById(R.id.top_display2);
        topdisplay3=(TextView)findViewById(R.id.top_display3);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_signal_0_bar_24dp);
    //    toolbar.setTitle("My Title");
     //   toolbar.setSubtitle("Sub title");

        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
       // FragmentTab3.mseekBarVolume=(SeekBar)findViewById(R.id.seekBarVolume);

        initViews();
        fragmentManager = getFragmentManager();
        setTabSelection(0);
        scanDevice();
      //  scanDevice_my();


        deviceList.clear();
        scanner.startScan(null);
        final CustomDialog scanDialog = new CustomDialog(MainActivity.this,R.style.NobackDialog);

        scanDialog.setScanCallback(new CustomDialog.Callback() {
            @Override
            public void onDevice(final UUID uuid, final String name) {
                scanner.stopScan();
                myDevice = deviceList.get(uuid);
                //    bConnect.setText("TO DISCONNECT");
                myDevice.connect(callback);
            }

            @Override
            public void onDismiss() {
                scanner.stopScan();
            }
        });
        scanDialog.setNegativeButton(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                scanDialog.dismiss();
            }
        });
        scanDialog.show();

    }


    private BroadcastReceiver sendReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action =intent.getAction();
            String tempString;
            short tempshort;
            byte[] sendbytesBroadcast= new byte[18];

            Log.e("lb","play2");
            if(ACTION_REMOTE_CONTROL.equals(action)){

            }
            else if(ACTION_MUSIC_VOLUME.equals(action)){

                byte msg=intent.getByteExtra("msg", (byte) 0);
                sendbytesBroadcast[0] = 0x03;//ID

                sendbytesBroadcast[1] =(byte)((10-msg)*6);
                 System.out.println(msg);
                sendbytesBroadcast[2] =(byte)( sendbytesBroadcast[1]+sendbytesBroadcast[0] );
                if (myDevice != null && myDevice.isConnected()) {
                    myDevice.send((byte) 0x01, sendbytesBroadcast);
                }
            }
            else if(ACTION_MUSIC_MODE.equals(action)){

              //  Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            else if(ACTION_REMOTE_CONTROL.equals(action)){

            }
        }
    };

    static public Runnable runnable = new Runnable() {
       // byte[] sendbytesremote= new byte[13];
        short[] shorts=new short[1];
        int temp;
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 110);
                if (myDevice != null && myDevice.isConnected())
                myDevice.getRemoteRssi();//获取RSSI在回调函数中
                sendbytesremote[0]='s';
                sendbytesremote[1]='t';
                if(FragmentTab1.isRemoteSwitch){//自动
                    sendbytesremote[2]=0x01;
                }else{//手动
                    sendbytesremote[2]=0x00;
                }
                if(FragmentTab1.isTurnmodeSwitch) {//舵机
                    sendbytesremote[5]=(byte)-(int)FragmentTab1.RockerValue[1];
                    sendbytesremote[6]=FragmentTab1.RockerValue[0];
                }else {//电机
                     sendbytesremote[3] = FragmentTab1.RockerValue[0];
                    if (FragmentTab1.isOriTurnmodeSwitch) {//摇杆
                        sendbytesremote[4] = (byte)(FragmentTab1.Yaw/2);
                    }else{//朝向
                        sendbytesremote[4] = FragmentTab1.RockerValue[1];
                    }
                }
                if(Car_cmd2!='t'&&!Car_cmd2_New_Flag){
                    Car_cmd2_New_Flag=true;
                }
                else if(Car_cmd2_New_Flag){
                    Car_cmd2='t';
                    Car_cmd2_New_Flag=false;
                }
                sendbytesremote[7]=Car_cmd2;
                if(Remote_Flag) {
                    if (myDevice != null && myDevice.isConnected()) {
                        myDevice.send((byte) 0x01, sendbytesremote);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    private void scanDevice_my(){

        scanner = new ScanHelper(getApplicationContext(), new ScanHelper.ScanCallback(){
            @Override
            public void onDiscover(JumaDevice device, int rssi) {
                Log.e("lb", "scandevice");
                scanner.stopScan();

                device.connect(callback);


            }
            @Override
            public void onScanStateChange(int arg0) {
            }
        });
    }
    private void scanDevice(){
        scanner = new ScanHelper(getApplicationContext(), new ScanHelper.ScanCallback(){
            @Override
            public void onDiscover(JumaDevice device, int rssi) {
                if(!deviceList.containsKey(device.getUuid())){
                    deviceList.put(device.getUuid(), device);
                    Log.e("lb", "scandevice2");
                    Intent intent = new Intent(MainActivity.ACTION_DEVICE_DISCOVERED);
                    intent.putExtra("name", device.getName());
                    intent.putExtra("uuid", device.getUuid().toString());
                    intent.putExtra("rssi", rssi);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            }
            @Override
            public void onScanStateChange(int arg0) {
            }
        });
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_scan:
                    deviceList.clear();
                    scanner.startScan(null);
                    final CustomDialog scanDialog = new CustomDialog(MainActivity.this,R.style.NobackDialog);

                    scanDialog.setScanCallback(new CustomDialog.Callback() {
                        @Override
                        public void onDevice(final UUID uuid, final String name) {
                            scanner.stopScan();
                            myDevice = deviceList.get(uuid);
                            //    bConnect.setText("TO DISCONNECT");
                            myDevice.connect(callback);
                        }

                        @Override
                        public void onDismiss() {
                            scanner.stopScan();
                        }
                    });
                    scanDialog.setNegativeButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            scanDialog.dismiss();
                        }
                    });
                    scanDialog.show();
                    break;
                case R.id.action_connect:
                    if (myDevice != null && myDevice.isConnected()) {
                        myDevice.disconnect();
                    } else if(myDevice != null) {
                        myDevice.connect(callback);
                    }
                    break;
                case R.id.action_id:
                    Car_cmd2='i';
                    break;
                case R.id.action_laugh:
                    Car_cmd2='L';
                    break;
                case R.id.action_temperature:
                    Car_cmd2='T';
                    break;
                case R.id.action_humidity:
                    Car_cmd2='H';
                    break;
                case R.id.action_Pressure:
                    Car_cmd2='P';
                    break;
            }

            return true;
        }
    };

    private JumaDeviceCallback callback = new JumaDeviceCallback() {
        @Override
        public void onConnectionStateChange(int status, int newState) {
            super.onConnectionStateChange(status, newState);
            if (newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        sendbytesremote[0]='s';
                        sendbytesremote[1]='t';
                        sendbytesremote[2]=0;
                        sendbytesremote[3]=0;
                        sendbytesremote[4]=0;
                        sendbytesremote[5]=0;
                        sendbytesremote[6]=0;
                        sendbytesremote[7]='c';
                        if (myDevice != null && myDevice.isConnected()) {
                            myDevice.send((byte) 0x01, sendbytesremote);
                        }
                    //    bConnect.setText("TO DISCONNECT");
                      //  getSupportActionBar().setTitle(myDevice.getName() + " is connect");
                        toolbar.setLogo(R.drawable.ic_signal_5_bar_24dp);
                        topdisplay.setText("连接成功");
                        handler.postDelayed(runnable, 110);//开启周期性的定时
                    Remote_Flag=true;
                       // FragmentTab1.remoteSwitch.setChecked(true);
                    }

                });
            } else if (newState == JumaDevice.STATE_DISCONNECTED) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                   //     bConnect.setText("TO CONNECT");
                      //  getSupportActionBar().setTitle(myDevice.getName() + " is disconnect");
                        toolbar.setLogo(R.drawable.ic_signal_0_bar_24dp);
                        topdisplay.setText("断开连接");
                        handler.removeCallbacks(runnable);//关闭定时
                        Remote_Flag=false;
                      //  FragmentTab1.remoteSwitch.setChecked(false);
                    }

                });
            }
        }
        @Override
        public void onSend(int status) {
            super.onSend(status);
           // String s3;
            /*
           temp=temp+1;
            if(myDevice != null && myDevice.isConnected()) {
                String sendString =  FragmentTab2.mDataSend.getText().toString();
                byte[] srtbyte = sendString.getBytes();
                if(sendString.length() > 0) {
                    myDevice.send((byte) 9, srtbyte);
                }
            }
            System.out.println(temp);
            */
        }
        String s1;
        byte s2;
        float Voltage;
        int RSSI;
        int[] inttemp =new int[2];
        int[] ints=new int[8];
         boolean flag=false;
        @Override
        public void onReceive(byte type, byte[] message) {
            super.onReceive(type, message);
            s1=new String(message);
            s2=type;

            switch (message[0]){
                case 0x07:
                for(int i=0;i<8;i++){
                    inttemp[0] = ((message[2*i+1]))<<8;
                    inttemp[1] =(message[2*i+2]);
                    if(inttemp[0]<0)inttemp[0]+=256;
                    if(inttemp[1]<0)inttemp[1]+=256;
                    ints[i]= inttemp[0]+ inttemp[1];

                   //System.out.println(((message[2*i+1]))<<8);
                    //System.out.println(message[2*i+2]);
                }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("onReceive", s2 + " " + s1);
                            FragmentTab2.mDataDisplay.append(s1);
                                FragmentTab2.mDataP1.setText(String.valueOf(ints[0]));
                                FragmentTab2.mDataP2.setText(String.valueOf(ints[1]));
                                FragmentTab2.mDataP3.setText(String.valueOf(ints[2]));
                                FragmentTab2.mDataP4.setText(String.valueOf(ints[3]));
                                FragmentTab2.mDataP5.setText(String.valueOf(ints[4]));
                                FragmentTab2.mDataP6.setText(String.valueOf(ints[5]));
                                FragmentTab2.mDataP7.setText(String.valueOf(ints[6]));
                                FragmentTab2.mDataP8.setText(String.valueOf(ints[7]));
                        }
                    });
                    break;
                case 'Y':
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("onReceive", s2 + " " + s1);
                            FragmentTab2.mDataDisplay.append(s1);
                            Toast.makeText(MainActivity.this, "Modefy OK", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 0x04://电压ID
                    for(int i=0;i<1;i++){
                        inttemp[0] = ((message[2*i+1]))<<8;
                        inttemp[1] =(message[2*i+2]);
                        if(inttemp[0]<0)inttemp[0]+=256;
                        if(inttemp[1]<0)inttemp[1]+=256;
                        ints[i]= inttemp[0]+ inttemp[1];
                        //System.out.println(((message[2*i+1]))<<8);
                        //System.out.println(message[2*i+2]);
                    }
                    Voltage= (float)ints[0]/100;
                    System.out.println(Voltage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topdisplay2.setText(" 电压：" +(double)ints[0]/100+"V");
                            if(Voltage<11){
                                topdisplay.setText("电压过低");

                            }
                            else{
                                topdisplay.setText(" ");
                            }
                        }
                    });
                    break;
            }
        }
        @Override
        public void onRemoteRssi(int status, int rssi){
            if(status==0){
                RSSI=rssi;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (RSSI != 127) {//不明白为何RSSI会总是出现127这个数字
                         //   System.out.println(RSSI);
                        topdisplay3.setText(" RSSI：" + String.valueOf(RSSI));
                        if (RSSI < -100) {
                            toolbar.setLogo(R.drawable.ic_signal_1_bar_24dp);
                        } else if (RSSI < -90) {
                            toolbar.setLogo(R.drawable.ic_signal_2_bar_24dp);
                        } else if (RSSI < -80) {
                            toolbar.setLogo(R.drawable.ic_signal_3_bar_24dp);
                        } else if (RSSI < -70) {
                            toolbar.setLogo(R.drawable.ic_signal_4_bar_24dp);
                        } else {
                            toolbar.setLogo(R.drawable.ic_signal_5_bar_24dp);
                        }
                    }
                }
            });
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bClear:
                FragmentTab2.mDataDisplay.setText("");
                //Toast.makeText(MainActivity.this, "Clear", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bSend:
                if(myDevice != null && myDevice.isConnected()) {
                    String sendString =  FragmentTab2.mDataSend.getText().toString();
                    byte[] srtbyte = sendString.getBytes();
                    if(sendString.length() > 0) {
                        myDevice.send((byte) 9, srtbyte);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bReceiveOrigin:
                byte[] sendbytesroigin= new byte[2];
                sendbytesroigin[0]=0x08;//获取原始数据的ID
                sendbytesroigin[1]=0x08;//checksum
                myDevice.send((byte)0x01 , sendbytesroigin);
                break;
            case R.id.bModify:
                byte[] sendbytesmodify= new byte[18];
                int temp=0;
                sendbytesmodify[0]=0x09;//修改参数的ID
                byte[] bytes=new byte[8];
                short[] shorts=new short[8];
                if(FragmentTab2.mDataP1.getText().toString().length()>0)
                 shorts[0]=   (short)Integer.parseInt(FragmentTab2.mDataP1.getText().toString());
                else shorts[0]=0;

                if(FragmentTab2.mDataP2.getText().toString().length()>0)
                shorts[1]=  (short)Integer.parseInt(FragmentTab2.mDataP2.getText().toString());
                else shorts[1]=0;

                if(FragmentTab2.mDataP3.getText().toString().length()>0)
                shorts[2]=  (short)Integer.parseInt(FragmentTab2.mDataP3.getText().toString());
                else shorts[2]=0;

                if(FragmentTab2.mDataP4.getText().toString().length()>0)
                shorts[3]=  (short)Integer.parseInt(FragmentTab2.mDataP4.getText().toString());
                else shorts[3]=0;

                if(FragmentTab2.mDataP5.getText().toString().length()>0)
                    shorts[4]=  (short)Integer.parseInt(FragmentTab2.mDataP5.getText().toString());
                else shorts[4]=0;

                if(FragmentTab2.mDataP6.getText().toString().length()>0)
                    shorts[5]=  (short)Integer.parseInt(FragmentTab2.mDataP6.getText().toString());
                else shorts[5]=0;
                if(FragmentTab2.mDataP7.getText().toString().length()>0)
                    shorts[6]=  (short)Integer.parseInt(FragmentTab2.mDataP7.getText().toString());
                else shorts[6]=0;

                if(FragmentTab2.mDataP8.getText().toString().length()>0)
                    shorts[7]=  (short)Integer.parseInt(FragmentTab2.mDataP8.getText().toString());
                else shorts[7]=0;
               // for(int i=0;i<2;i++){
               //     bytes[i]=(byte)(shorts[0]>>(8-(i)*8));
               // }
                for(int i=0;i<16;i++){
                    sendbytesmodify[i+1]=(byte)(shorts[i/2]>>(8-(i%2)*8));
                    temp+=sendbytesmodify[i+1];
               }
                sendbytesmodify[1+16]=(byte)(temp+sendbytesmodify[0]);
                if(myDevice != null && myDevice.isConnected()) {
                    myDevice.send((byte) 0x01, sendbytesmodify);
                }
                else {
                    Toast.makeText(MainActivity.this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnPlayPrevious:
                sendbytesplay[0] = 0x02;//ID
                sendbytesplay[1] = 5;//上一首
                sendbytesplay[2] =(byte)( sendbytesplay[1]+sendbytesplay[0] );
                if (myDevice != null && myDevice.isConnected()) {
                    myDevice.send((byte) 0x01, sendbytesplay);
                }

                break;

            case R.id.btnPlayPlay:

                if (isPlayMusic) {
                    sendbytesplay[1] = 2;//停止
                    isPlayMusic = false;
                    FragmentTab3.mbtnPlayPlay.setImageResource(R.drawable.btn_playback_play);
                } else {
                    sendbytesplay[1] = 1;//播放
                    isPlayMusic = true;
                    FragmentTab3.mbtnPlayPlay.setImageResource(R.drawable.btn_playback_pause);
                }
                sendbytesplay[0] = 0x02;//ID
                sendbytesplay[2] =(byte)( sendbytesplay[1]+sendbytesplay[0] );
                if (myDevice != null && myDevice.isConnected()) {
                    myDevice.send((byte) 0x01, sendbytesplay);
                }
                break;
            case R.id.btnPlayNext:
                sendbytesplay[0] = 0x02;//ID
                sendbytesplay[1] = 6;//下一首
                sendbytesplay[2] =(byte)( sendbytesplay[1]+sendbytesplay[0] );
                if (myDevice != null && myDevice.isConnected()) {
                    myDevice.send((byte) 0x01, sendbytesplay);
                }
                break;
            case R.id.btnPlayRepeat:
                sendbytesplay[0] = 0x02;//ID
                sendbytesplay[1] = 7;
                sendbytesplay[2] =(byte)( sendbytesplay[1]+sendbytesplay[0] );
                if (myDevice != null && myDevice.isConnected()) {
                    myDevice.send((byte) 0x01, sendbytesplay);
                }
                break;

            case R.id.message_layout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.contacts_layout:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.news_layout:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.setting_layout:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }
    private void initViews() {
        messageLayout = findViewById(R.id.message_layout);
        contactsLayout = findViewById(R.id.contacts_layout);
        newsLayout = findViewById(R.id.news_layout);
        settingLayout = findViewById(R.id.setting_layout);
        messageImage = (ImageView) findViewById(R.id.message_image);
        contactsImage = (ImageView) findViewById(R.id.contacts_image);
        newsImage = (ImageView) findViewById(R.id.news_image);
        settingImage = (ImageView) findViewById(R.id.setting_image);
        messageText = (TextView) findViewById(R.id.message_text);
        contactsText = (TextView) findViewById(R.id.contacts_text);
        newsText = (TextView) findViewById(R.id.news_text);
        settingText = (TextView) findViewById(R.id.setting_text);
        messageLayout.setOnClickListener(this);
        contactsLayout.setOnClickListener(this);
        newsLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
    }
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:

                messageImage.setImageResource(R.drawable.message_selected);
                messageText.setTextColor(Color.WHITE);
                if (messageFragment == null) {

                    messageFragment = new FragmentTab1();
                    transaction.add(R.id.content, messageFragment);
                } else {

                    transaction.show(messageFragment);
                }
                break;
            case 1:
                FragmentTab1.remoteSwitch.setChecked(false);

                contactsImage.setImageResource(R.drawable.contacts_selected);
                contactsText.setTextColor(Color.WHITE);
                if (contactsFragment == null) {

                    contactsFragment = new FragmentTab2();
                    transaction.add(R.id.content, contactsFragment);
                } else {

                    transaction.show(contactsFragment);
                }
                break;
            case 2:
                FragmentTab1.remoteSwitch.setChecked(false);
                newsImage.setImageResource(R.drawable.news_selected);
                newsText.setTextColor(Color.WHITE);
                if (newsFragment == null) {

                    newsFragment = new FragmentTab3();
                    transaction.add(R.id.content, newsFragment);
                } else {

                    transaction.show(newsFragment);
                }
                break;
            case 3:
            default:

                settingImage.setImageResource(R.drawable.setting_selected);
                settingText.setTextColor(Color.WHITE);
                if (settingFragment == null) {

                    settingFragment = new FragmentTab4();
                    transaction.add(R.id.content, settingFragment);
                } else {

                    transaction.show(settingFragment);
                }
                break;
        }
        transaction.commit();
    }
    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        messageImage.setImageResource(R.drawable.message_unselected);
        messageText.setTextColor(Color.parseColor("#82858b"));
        contactsImage.setImageResource(R.drawable.contacts_unselected);
        contactsText.setTextColor(Color.parseColor("#82858b"));
        newsImage.setImageResource(R.drawable.news_unselected);
        newsText.setTextColor(Color.parseColor("#82858b"));
        settingImage.setImageResource(R.drawable.setting_unselected);
        settingText.setTextColor(Color.parseColor("#82858b"));
    }
    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (contactsFragment != null) {
            transaction.hide(contactsFragment);
        }
        if (newsFragment != null) {
            transaction.hide(newsFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
    }
}
