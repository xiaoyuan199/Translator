package com.example.mytranslation.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class DButil {

    //查找方法
    public static Boolean quearyIfItemExit(NotebookDatabaseHelper dbHelper,String queryString){

        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query("Notebook",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {

                String s=cursor.getString(cursor.getColumnIndex("input"));
                if(queryString.equals(s)){
                    return true;
                }

            }while (cursor.moveToNext());
        }

        cursor.close();

        return false;
    }

    //插入值
    public  static void insertValue(NotebookDatabaseHelper dbHelper,ContentValues values){

        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        sqLiteDatabase.insert("Notebook",null,values);

    }

    //删除值
    public  static  void deleteValue(NotebookDatabaseHelper dbHelper,String deleteString){

        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        sqLiteDatabase.delete("Notebook","input = ?",new String[]{deleteString});
    }



}
