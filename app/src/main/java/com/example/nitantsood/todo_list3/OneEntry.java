package com.example.nitantsood.todo_list3;

/**
 * Created by NITANT SOOD on 03-07-2017.
 */

public class OneEntry {
    String title;
    String detail;
    String date;
    int color=4;
    String timestamp;
    String id;
    String time;
    int archive=0;


    public OneEntry(String title,String detail,String date,String id,int color,String time) {
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.id=id;
        this.color=color;
        this.time=time;
    }
}
