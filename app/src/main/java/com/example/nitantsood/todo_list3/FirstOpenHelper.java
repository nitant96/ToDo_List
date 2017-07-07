package com.example.nitantsood.todo_list3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NITANT SOOD on 03-07-2017.
 */

public class FirstOpenHelper extends SQLiteOpenHelper {
    public FirstOpenHelper(Context context) {
        super(context,"ToDo.db",null,1);
    }
    public final static String TODO_TABLE_NAME  = "ToDoList";
    public final static String TODO_TITLE  = "title";
    public final static String TODO_ID  = "_id";
    public final static String TODO_DETAIL = "detail" ;
    public final static String TODO_COLOR = "color" ;
    public final static String TODO_DATE="Date";
    public final static String TODO_TIMESTAMP="TimeStamp";
    public final static String TODO_TIME="Time";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableCreate="Create table "+TODO_TABLE_NAME+"( "+TODO_ID+" integer primary key autoincrement, "+TODO_TITLE+" text, "+
                TODO_DETAIL+" text, "+TODO_COLOR+" integer, "+TODO_DATE+" text, "+TODO_TIME+" text, "+
                TODO_TIMESTAMP+" text);";
        db.execSQL(tableCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

