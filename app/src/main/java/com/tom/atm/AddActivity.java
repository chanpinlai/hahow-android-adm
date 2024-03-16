package com.tom.atm;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddActivity extends AppCompatActivity {

    private EditText edDate;
    private EditText edInfo;
    private EditText edAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        edDate = findViewById(R.id.ed_date);
        edInfo = findViewById(R.id.ed_info);
        edAmount = findViewById(R.id.ed_amuont);

    }

    public void add(View view) {
        String date = edDate.getText().toString();
        String info = edInfo.getText().toString();
        int amount = Integer.parseInt(edAmount.getText().toString());
        ExpenseHelper expenseHelper = new ExpenseHelper(this);
        ContentValues contentValues = new ContentValues();
        contentValues.put("cdate", date);
        contentValues.put("info", info);
        contentValues.put("amount", amount);
        long id = expenseHelper.getWritableDatabase().insert("expense", null, contentValues);
        if (id > -1) {
            Toast.makeText(this, "新增成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "新增失敗", Toast.LENGTH_LONG).show();
        }

    }
}