package com.example.ma01_20200942;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class MemoDBHelper extends SQLiteOpenHelper {

    private final static String TAG = "MemoDBHelper";

    private final static String DB_NAME ="memo_db";
    public final static String TABLE_NAME = "memo_table";
    public final static String ID = "_id";
    public final static String MEMO = "memo";

    public MemoDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_NAME + " ( " + ID + " integer primary key autoincrement, " + MEMO + " TEXT);";
        sqLiteDatabase.execSQL(sql);

        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, 'test');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
