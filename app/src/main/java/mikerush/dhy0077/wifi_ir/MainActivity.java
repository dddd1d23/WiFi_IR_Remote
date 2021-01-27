package mikerush.dhy0077.wifi_ir;

import androidx.appcompat.app.AppCompatActivity;
import mikerush.dhy0077.wifi_ir.ui.login.LoginActivity;
import mikerush.dhy0077.wifi_ir.v1.EspTouchActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;


public class MainActivity extends AppCompatActivity {

    private Button start_sc_btn;
    private Button device_info_btn;
    private iotx_dev_meta_info_t idev;
    private iotx_sign_mqtt_t isign;
    private final String TAG="MainActivity";
    private MenuItem mLogoutItem;
    private static boolean isExit = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mLogoutItem=menu.add("登出");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mLogoutItem) {
            MyApp.getInstance().AutoLogin=false;
            MyApp.getInstance().ClientLogout();
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_sc_btn = findViewById(R.id.start_sc);
        start_sc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EspTouchActivity.class);  //从IntentActivity跳转到SubActivity
                startActivity(intent);
            }
        });
        device_info_btn = findViewById(R.id.device_info_btn);
        device_info_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    MyApp.getInstance().MQTT_Send();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息，2000==2秒
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void IOT_Sign_MQTT(iotx_dev_meta_info_t iotx_dev_meta_info,iotx_sign_mqtt_t iotx_sign_mqtt);
}
