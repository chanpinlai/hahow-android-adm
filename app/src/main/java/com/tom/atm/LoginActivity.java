package com.tom.atm;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText edUserid;
    private EditText edPasswd;
    private CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edUserid = findViewById(R.id.userid);
        edPasswd = findViewById(R.id.passwd);
        cbRemember = findViewById(R.id.cb_rem_userid);
        cbRemember.setChecked(getPreferences(MODE_PRIVATE).getBoolean("REMEMBER_USERID",false));
        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getPreferences(MODE_PRIVATE).edit().putBoolean("REMEMBER_USERID",isChecked).commit();
            }
        });
        String userid = getPreferences(MODE_PRIVATE).getString("USERID", "");
        edUserid.setText(userid);
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
                                boolean remember = getPreferences(MODE_PRIVATE).getBoolean("REMEMBER_USERID",false);
                                if(remember) {
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
}