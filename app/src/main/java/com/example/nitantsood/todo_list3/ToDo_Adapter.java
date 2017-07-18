package com.example.nitantsood.todo_list3;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

public class ToDo_Adapter extends RecyclerView.Adapter<ToDo_Adapter.OneEntryViewHolder> {
    public final static String red_main="#e91e63";
    public final static String red_base="#ff6090";
    public final static String green_main="#7ecb20";
    public final static String green_base="#b2ff59";
    public final static String blue_main="#4d82cb";
    public final static String blue_base="#82b1ff";
    public final static String yellow_main="#FFFFE628";
    public final static String yellow_base="#FFFFF07C";

    private Context mContext;
    private ArrayList<OneEntry> mReminder;
    private ReminderClickListener mListener;

    public interface ReminderClickListener {
        void onItemClick(View view,int position);
        boolean onItemLongClick(View view,int position);
    }


    public ToDo_Adapter(Context context, ArrayList<OneEntry> reminder,ReminderClickListener listener){
        mContext = context;
        mReminder=reminder;
        mListener = listener;
    }

    @Override
    public OneEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return  new OneEntryViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(OneEntryViewHolder holder, int position) {
        OneEntry entry=mReminder.get(position);
        holder.title.setText(entry.title);
        holder.detail.setText(entry.detail);
        holder.date.setText(entry.date);
        setSelectioSymbol(mReminder.get(position).id,holder.selection_symbol);
        if(!mReminder.get(position).time.equals("")){
            holder.clock.setImageResource(android.R.drawable.ic_lock_idle_alarm);
        }
        else{
            holder.clock.setImageResource(0);
        }
        int main_col=getMainColor(position);
        int base_col=getBaseColor(position);
        holder.itemView.setBackgroundColor(base_col);
        holder.selection_symbol.setBackgroundColor(main_col);
    }

    @Override
    public int getItemCount() {
        return mReminder.size();
    }

    public  static class OneEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView title;
        TextView detail ;
        TextView date ;
        ImageView clock;
        ImageView selection_symbol;
        ReminderClickListener mReminderClickListener;

        public OneEntryViewHolder(View itemView,ReminderClickListener listener) {
            super(itemView);
            itemView.findViewById(R.id.selection_symbol).setOnClickListener(this);
            itemView.findViewById(R.id.list_item_viewer).setOnClickListener(this);
            itemView.findViewById(R.id.list_item_viewer).setOnLongClickListener(this);
            mReminderClickListener=listener;
            title=(TextView) itemView.findViewById(R.id.item_title);
            detail=(TextView) itemView.findViewById(R.id.item_detail);
            date=(TextView) itemView.findViewById(R.id.item_date);
            selection_symbol=(ImageView) itemView.findViewById(R.id.selection_symbol);
            clock=(ImageView) itemView.findViewById(R.id.alarm_notifier);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            mReminderClickListener.onItemClick(v,position);
        }

        @Override
        public boolean onLongClick(View v) {
            int position=getAdapterPosition();
            return mReminderClickListener.onItemLongClick(v,position);

        }
    }
    public void setSelectioSymbol(String id,ImageView im){
        if(ListActivity.listOfItemSelected.contains(id)){
            im.setImageResource(android.R.drawable.checkbox_on_background);
        }
        else{
            im.setImageResource(android.R.drawable.btn_radio);
        }
    }
    public int getMainColor(int position){
        int main_col;
        if(mReminder.get(position).color==1)
        {
            main_col=Color.parseColor(red_main);
        }
        else if(mReminder.get(position).color==2)
        {
            main_col=Color.parseColor(green_main);
        }
        else if(mReminder.get(position).color==3)
        {
            main_col=Color.parseColor(blue_main);
        }
        else
        {
            main_col=Color.parseColor(yellow_main);
        }
        return main_col;
    }
    public int getBaseColor(int position){
        int base_col;
        if(mReminder.get(position).color==1)
        {
            base_col=Color.parseColor(red_base);
        }
        else if(mReminder.get(position).color==2)
        {
            base_col=Color.parseColor(green_base);
        }
        else if(mReminder.get(position).color==3)
        {
            base_col=Color.parseColor(blue_base);
        }
        else
        {
            base_col=Color.parseColor(yellow_base);
        }
        return base_col;
    }
}

