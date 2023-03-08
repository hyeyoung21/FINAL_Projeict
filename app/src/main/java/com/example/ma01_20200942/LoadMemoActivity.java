package com.example.ma01_20200942;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class LoadMemoActivity extends AppCompatActivity {
    SimpleCursorAdapter memoAdapter;
    Cursor cursor;
    MemoDBHelper helper;
    List<String> list = new ArrayList<>();

    ListView lvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_memo);

        helper = new MemoDBHelper(this);

//        어댑터에 SimpleCursorAdapter 연결
        memoAdapter = new SimpleCursorAdapter ( this, android.R.layout.simple_list_item_2, null,
                new String[] { MemoDBHelper.MEMO },
                new int[] { android.R.id.text1 },
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

        lvMemo = (ListView)findViewById(R.id.lv_memo);

        lvMemo.setAdapter(memoAdapter);
        readAllContacts();

        lvMemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String vo = "";

                Cursor cursor = db.rawQuery( "select * from " + MemoDBHelper.TABLE_NAME + " where " + MemoDBHelper.ID + "=?", new String[] { String.valueOf(i+1) });
                while (cursor.moveToNext()) {
                    vo = cursor.getString( cursor.getColumnIndex(MemoDBHelper.MEMO) );
                }
                cursor.close();
                helper.close();

                shareContent(vo);
            }
        });
        lvMemo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long targetId = id;	// id 값을 다이얼로그 객체 내부에서 사용하기 위하여 상수로 선언

                new AlertDialog.Builder(LoadMemoActivity.this).setTitle("삭제")
                        .setMessage("메모를 삭제합니다")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {

                            //							삭제 수행
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = helper.getWritableDatabase();

                                String whereClause = MemoDBHelper.ID + "=?";
                                String[] whereArgs = new String[] { String.valueOf(targetId) };

                                db.delete(MemoDBHelper.TABLE_NAME, whereClause, whereArgs);
                                helper.close();
                                readAllContacts();		// 삭제 상태를 반영하기 위하여 전체 목록을 다시 읽음
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }

    private void readAllContacts() {
//        DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + MemoDBHelper.TABLE_NAME, null);
        memoAdapter.changeCursor(cursor);
        helper.close();
    }

    private void shareContent(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

}