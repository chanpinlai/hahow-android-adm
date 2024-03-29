package com.tom.atm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CAMERA = 5;
    private EditText edUserid;
    private EditText edPasswd;
    private CheckBox cbRemember;
    private Intent helloService1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.container_news, NewsFragment.getInstance());
        fragmentTransaction.commit();
        //Service
        helloService1 = new Intent(this, HelloService.class);
        helloService1.putExtra("NAME", "T1");
        startService(helloService1);
        helloService1.putExtra("NAME", "T2");
        startService(helloService1);
        helloService1.putExtra("NAME", "T3");
        startService(helloService1);


        //第 1 章，第 27 單元 - Android 6.0 危險權限設計機制實作
        //取得是0，沒取得是-1
//        camera();
        findViews();
        //第 1 章，第 32 單元 - 執行緒觀念與耗時工作設計(AsyncTask類別)
//        new TestTask().execute("http://tw.yahoo.com");


    }

    //接收機制
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Hello");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(HelloService.ACTION_HELLO_DONE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopService(helloService1);
        unregisterReceiver(receiver);
    }

    //1.傳入型態，不傳Void
    //2.中途要不回報資料
    //3.結果
    public class TestTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Toast.makeText(LoginActivity.this, "onPostExecute:" + integer, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Integer doInBackground(String... strings) {

            int data;

            try {
                URL url = new URL(strings[0]);
                data = url.openStream().read();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data;
        }
    }

    private void findViews() {
        edUserid = findViewById(R.id.userid);
        edPasswd = findViewById(R.id.passwd);
        cbRemember = findViewById(R.id.cb_rem_userid);
        boolean remember = getSharedPreferences("adm", MODE_PRIVATE).getBoolean("REMEMBER_USERID", true);
        cbRemember.setChecked(remember);
        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("atm", MODE_PRIVATE).edit().putBoolean("REMEMBER_USERID", isChecked).commit();
            }
        });
        getSharedPreferences("atm", MODE_PRIVATE).edit().putString("USERID", "jack").commit();
        String userid = getSharedPreferences("atm", MODE_PRIVATE).getString("USERID", "");
        edUserid.setText(userid);
    }

    private void camera() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }
    }

    private void takePhoto() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

    public void login(View view) {
        String userid = edUserid.getText().toString();
        String passwd = edPasswd.getText().toString();
        FirebaseDatabase.getInstance().getReference("users").child(userid).child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String pw = null;
                        if (null == snapshot) {
                            Log.d(TAG, "Get Value " + snapshot.getValue());
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("訊息")
                                    .setMessage("null == snapshot")
                                    .setPositiveButton("確認", null)
                                    .show();
                        } else if (null == snapshot.getValue()) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("訊息")
                                    .setMessage("null == snapshot.getValue()")
                                    .setPositiveButton("確認", null)
                                    .show();
                        } else {
                            pw = snapshot.getValue().toString();

                            if (pw.equals(passwd)) {
                                boolean remember = getPreferences(MODE_PRIVATE).getBoolean("REMEMBER_USERID", false);
                                if (remember) {
                                    getPreferences(MODE_PRIVATE)
                                            .edit()
                                            .putString("USERID", userid)
                                            .commit();
                                }
                                setResult(RESULT_OK);
                                finish();
                                Log.d(TAG, "Login success:");
                            } else {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("訊息")
                                        .setMessage("登入失敗")
                                        .setPositiveButton("確認", null)
                                        .show();
                                Log.d(TAG, "Login failed: ");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
//        if ("jack".equals(userid) && "1234".equals(passwd)) {
//
//        }


    }

    public void quit(View view) {

    }
    public void map(View view){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);

    }
    public void web(View view){
        Intent intent = new Intent(this,WebViewActivity.class);
        startActivity(intent);

    }
}