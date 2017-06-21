package com.example.mytranslation.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class NotebookDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public  static final String CREATE_NOTEBOOK="create table Notebook("
            +"id integer primary key autoincrement,"
            +"input text,"
            +"output text)";


                                                    //数据库名
    public NotebookDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTEBOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
