package mikerush.dhy0077.wifi_ir;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import mikerush.dhy0077.wifi_ir.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;


public class MyApp extends Application {
    private static MyApp app;
    private final String TAG="MyApp";
    private MutableLiveData<String> mBroadcastData;
    private final Object Login_Sig = new Object();
    private LoginActivity CurLoginActivity;
    private String Send_Topic;
    private String Recv_Topic;
    private MyTask mTask;
    public String LoginRetStr;
    public boolean AutoLogin;

    private Map<String, Object> mCacheMap;
    private iotx_dev_meta_info_t idevinfo;
    private iotx_sign_mqtt_t isign;

    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;

    static {
        System.loadLibrary("native-lib");
    }
    public void setCurLoginActivity(LoginActivity t)
    {
        CurLoginActivity=t;
    }

    public native void IOT_Sign_MQTT(iotx_dev_meta_info_t iotx_dev_meta_info,iotx_sign_mqtt_t iotx_sign_mqtt);
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String netname = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + netname);
            return true;
        } else {
            Log.i(TAG, "MQTT没有可用网络");
            return false;
        }
    }
    public void ClientLogout()
    {
        Log.i(TAG,"MQTT Client Logout");
        client.unregisterResources();
        client.close();
        client=null;
    }

    public void Make_New_Client(String un,String pwd)
    {
        idevinfo=new iotx_dev_meta_info_t();
        isign=new iotx_sign_mqtt_t();
        idevinfo.device_name=un;
        idevinfo.device_secret=pwd;
        Recv_Topic="/a1UXTORPgGu/"+idevinfo.device_name+"/user/cmd_recv";
        Send_Topic="/a1UXTORPgGu/"+idevinfo.device_name+"/user/cmd_send";
        IOT_Sign_MQTT(idevinfo,isign);
        Log.i(TAG,"hostname="+isign.hostname);
        Log.i(TAG,"cliendid="+isign.clientid);
        Log.i(TAG,"port="+String.valueOf(isign.port));
        Log.i(TAG,"username="+isign.username);
        Log.i(TAG,"password="+isign.password);
        String uri="tcp://"+isign.hostname+":"+String.valueOf(isign.port);
        Log.i(TAG,"uri="+uri);
        LoginRetStr="N";
        client = new MqttAndroidClient(getApplicationContext(),uri,isign.clientid);
        conOpt = new MqttConnectOptions();
        conOpt.setMqttVersion(4);
        conOpt.setAutomaticReconnect(true);
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(60);
        // 用户名
        conOpt.setUserName(isign.username);
        // 密码
        conOpt.setPassword(isign.password.toCharArray());     //将字符串转换为字符串数组

    }
    private class MyTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.i(TAG,"收到设备的回复");
                return 1;
            }
            Log.i(TAG,"设备无回复");
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {

        }

        @Override
        protected void onPostExecute(Integer result) {

        }

        @Override
        protected void onCancelled() {
            //Log.i(TAG,"Message Received!");
        }
    }
    public void MQTT_Send() throws MqttException {
        if (mTask.getStatus()== AsyncTask.Status.RUNNING)
        {
            Log.i(TAG,"A Task is Running!");
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(("123").getBytes());
        client.publish(Send_Topic,message);
        Log.i(TAG,"信息已发出");
        mTask=new MyTask();
        mTask.execute("Go");
    }
    public void doClientConnection() {
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    // Because Clean Session is true, we need to re-subscribe
                    try {
                        client.subscribe(Recv_Topic,1);
                        Log.i(TAG,"Subscribed Topic="+Recv_Topic);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG,"Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG,"The Connection was lost.");

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG,"Incoming message: " + new String(message.getPayload()));
                if (mTask.getStatus()== AsyncTask.Status.RUNNING){
                    mTask.cancel(true);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        if (!client.isConnected() && isConnectIsNormal()) {
            try {
                client.connect(conOpt, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken arg0) {
                        Log.i(TAG, "MQTT连接成功");
                        try {
                            client.subscribe("/a1UXTORPgGu/"+idevinfo.device_name+"/user/cmd_recv",0);
                            Log.i(TAG,"Subscribed Topic="+"/a1UXTORPgGu/"+idevinfo.device_name+"/user/cmd_recv");
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        Toast t=Toast.makeText(getApplicationContext(),"登录成功:"+idevinfo.device_name,Toast.LENGTH_SHORT);
                        t.show();
                        Intent intent=new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(CurLoginActivity,MainActivity.class);
                        startActivity(intent);
                        CurLoginActivity.finish();
                    }

                    @Override
                    public void onFailure(IMqttToken arg0, Throwable arg1) {
                        Log.i(TAG, "登录失败 "+arg1.getMessage());
                        Toast t=Toast.makeText(getApplicationContext(),"登录失败 "+arg1.getMessage(),Toast.LENGTH_SHORT);
                        t.show();
                        client.unregisterResources();

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    //private IMqttActionListener iMqttActionListener =


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    mBroadcastData.setValue(action);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        mTask=new MyTask();
        AutoLogin=true;

        mCacheMap = new HashMap<>();

        mBroadcastData = new MutableLiveData<>();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(mReceiver);
    }

    public static MyApp getInstance() {
        return app;
    }

    public void observeBroadcast(LifecycleOwner owner, Observer<String> observer) {
        mBroadcastData.observe(owner, observer);
    }

    public void observeBroadcastForever(Observer<String> observer) {
        mBroadcastData.observeForever(observer);
    }

    public void removeBroadcastObserver(Observer<String> observer) {
        mBroadcastData.removeObserver(observer);
    }

    public void putCache(String key, Object value) {
        mCacheMap.put(key, value);
    }

    public Object takeCache(String key) {
        return mCacheMap.remove(key);
    }
}
