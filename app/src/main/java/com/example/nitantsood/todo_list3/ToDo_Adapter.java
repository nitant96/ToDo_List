package com.example.nitantsood.todo_list3;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.YELLOW;
import static android.graphics.Color.red;

/**
 * Created by NITANT SOOD on 03-07-2017.
 */

public class ToDo_Adapter extends ArrayAdapter<OneEntry> {
    public final static String red_main="#e91e63";
    public final static String red_base="#ff6090";
    public final static String green_main="#7ecb20";
    public final static String green_base="#b2ff59";
    public final static String blue_main="#4d82cb";
    public final static String blue_base="#82b1ff";
    public final static String yellow_main="#FFFFE628";
    public final static String yellow_base="#FFFFF07C";
    Context context;
    ArrayList<OneEntry> arrayList;

    public ToDo_Adapter(@NonNull Context context, ArrayList<OneEntry> arrayList) {
        super(context,0,arrayList);
        this.context=context;
        this.arrayList=arrayList;
    }
    static class OneEntryViewHolder{

        TextView title;
        TextView detail ;
        TextView date ;
        ImageView clock;

        public OneEntryViewHolder(TextView title, TextView detail, TextView date,ImageView clock) {
            this.title = title;
            this.detail = detail;
            this.date = date;
            this.clock=clock;
        }
    }
    public int getColor(int position){
        int col;
        if(arrayList.get(position).color==1)
        {
            col=Color.parseColor(red_main);
        }
        else if(arrayList.get(position).color==2)
        {
            col=Color.parseColor(green_main);
        }
        else if(arrayList.get(position).color==3)
        {
            col=Color.parseColor(blue_main);
        }
        else
        {
            col=Color.parseColor(yellow_main);
        }
        return col;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.list_item,null);
            TextView title=(TextView) convertView.findViewById(R.id.item_title);
            TextView detail=(TextView) convertView.findViewById(R.id.item_detail);
            TextView date=(TextView) convertView.findViewById(R.id.item_date);
            ImageView clock=(ImageView) convertView.findViewById(R.id.alarm_notifier);
            OneEntryViewHolder OneEntryHolder=new OneEntryViewHolder(title,detail,date,clock);
            convertView.setTag(OneEntryHolder);
        }
        OneEntry entry=arrayList.get(position);
        OneEntryViewHolder OneEntryHolder=(OneEntryViewHolder)  convertView.getTag();
        OneEntryHolder.title.setText(entry.title);
        OneEntryHolder.detail.setText(entry.detail);
        OneEntryHolder.date.setText(entry.date);
        if(!arrayList.get(position).time.equals("")){
            OneEntryHolder.clock.setImageResource(android.R.drawable.ic_lock_idle_alarm);
        }
        else{
            OneEntryHolder.clock.setImageResource(0);
        }
        int col=getColor(position);
        convertView.setBackgroundColor(col);

        return convertView;
    }
}

