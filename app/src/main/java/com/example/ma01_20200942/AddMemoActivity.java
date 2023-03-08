package com.example.ma01_20200942;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddMemoActivity extends AppCompatActivity {
    Intent intent;
    EditText editText;
    MemoDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        intent = getIntent();
        editText = findViewById(R.id.etMemo);
        String memo = intent.getStringExtra("memo");
        editText.setText(memo);

        helper = new MemoDBHelper(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSave:
                insertMemo();
                Toast.makeText(this, "Save!", Toast.LENGTH_SHORT).show();
            case R.id.btnCancel:
                finish();
                break;
        }
    }


    public void insertMemo() {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(MemoDBHelper.MEMO, editText.getText().toString());

        long result = db.insert(MemoDBHelper.TABLE_NAME, null, row);
        String msg = result > 0 ? "추가 성공!" : "추가 실패!";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
        helper.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.close();
    }
}