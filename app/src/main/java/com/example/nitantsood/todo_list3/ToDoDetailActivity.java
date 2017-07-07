package com.example.nitantsood.todo_list3;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by NITANT SOOD on 03-07-2017.
 */
public class ToDoDetailActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    public int year=0,month=0,dom=0;
    public int hours=0,mins=0;
    Timestamp timeStamp;
    int decider,color=4;
    String id;
    EditText title;
    TextView date,time;
    EditText detail;
    MenuItem col_button;
    ImageButton calendar,clock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);
        Intent i=getIntent();
        decider=i.getIntExtra("requestCode",ListActivity.NEW_ITEM);
        title=(EditText) findViewById(R.id.detail_title);
        date=(TextView) findViewById(R.id.detail_date);
        time=(TextView) findViewById(R.id.detail_time);
        detail=(EditText) findViewById(R.id.Detail_detail);
        clock=(ImageButton) findViewById(R.id.time_selector);
        calendar=(ImageButton) findViewById(R.id.date_selector);
        if(decider==ListActivity.MODIFY_ITEM){
           id=i.getStringExtra(FirstOpenHelper.TODO_ID);
            FirstOpenHelper firstOpenHelper=new FirstOpenHelper(this);
            SQLiteDatabase database=firstOpenHelper.getWritableDatabase();
            String[] S={""+id};
            Cursor cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ID+"=?",S,null,null,null);
            cursor.moveToNext();
            color=cursor.getInt(cursor.getColumnIndex(FirstOpenHelper.TODO_COLOR));
            String title1=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TITLE));
            String detail1=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_DETAIL));
            String date1=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_DATE));
            String time1=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TIME));
            String timeStamp1=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TIMESTAMP));
            if(!timeStamp1.equals("")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = null;
                try {
                    parsedDate = dateFormat.parse(timeStamp1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeStamp = new java.sql.Timestamp(parsedDate.getTime());
            }
            else{
                timeStamp=new Timestamp(0);
            }
            if(date1.equals("")){
                date1="Date";
            }
            else{
                dom=(Integer.parseInt(String.valueOf(date1.charAt(0)))*10)+(Integer.parseInt(String.valueOf(date1.charAt(1))));
                month=(Integer.parseInt(String.valueOf(date1.charAt(3)))*10)+(Integer.parseInt(String.valueOf(date1.charAt(4))))-1;
                year=(Integer.parseInt(String.valueOf(date1.charAt(6)))*1000)+(Integer.parseInt(String.valueOf(date1.charAt(7)))*100)+(Integer.parseInt(String.valueOf(date1.charAt(8)))*10)+(Integer.parseInt(String.valueOf(date1.charAt(9))));
            }
            if(time1.equals("")){
                time1="Time";
            }else{
                hours=(Integer.parseInt(String.valueOf(time1.charAt(0)))*10)+(Integer.parseInt(String.valueOf(time1.charAt(1))));
                mins=(Integer.parseInt(String.valueOf(time1.charAt(3)))*10)+(Integer.parseInt(String.valueOf(time1.charAt(4))));
            }
            title.setText(title1);
            detail.setText(detail1);
            date.setText(date1);
            time.setText(time1);
            setTimeStamp(year,month,dom,hours,mins);
        }
        setColor(color);

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                hours=c.get(Calendar.HOUR_OF_DAY);
                mins=c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(ToDoDetailActivity.this,ToDoDetailActivity.this,hours,mins,true);
                timePickerDialog.show();
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                year=c.get(Calendar.YEAR);
                month=c.get(Calendar.MONTH);
                dom=c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(ToDoDetailActivity.this,ToDoDetailActivity.this,year,month,dom);
                datePickerDialog.show();
            }
        });
        clock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                time.setText("Time");
                hours=0;
                mins=0;
                setTimeStamp(year,month,dom,hours,mins);
                return true;
            }
        });
        calendar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                date.setText("Date");
                year=0;
                month=0;
                dom=0;
                setTimeStamp(year,month,dom,hours,mins);
                return true;
            }
        });
    }
    public void setColor(int color){
        int main_col,base_col;
        LinearLayout header_Layout=(LinearLayout)  findViewById(R.id.header_layout);
        LinearLayout base_Layout=(LinearLayout) findViewById(R.id.base_layout);
        if(color==1){
            main_col= Color.parseColor(ToDo_Adapter.red_main);
            base_col=Color.parseColor(ToDo_Adapter.red_base);

        }
        else if(color==2){
            main_col= Color.parseColor(ToDo_Adapter.green_main);
            base_col=Color.parseColor(ToDo_Adapter.green_base);
        }
        else if(color==3){
            main_col= Color.parseColor(ToDo_Adapter.blue_main);
            base_col=Color.parseColor(ToDo_Adapter.blue_base);
        }
        else{
            main_col= Color.parseColor(ToDo_Adapter.yellow_main);
            base_col=Color.parseColor(ToDo_Adapter.yellow_base);
        }
        header_Layout.setBackgroundColor(main_col);
        base_Layout.setBackgroundColor(base_col);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        col_button=menu.getItem(0);
        col_button.setIcon(R.drawable.colors);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int item_id=item.getItemId();
        if(item_id==R.id.save_button){
            Date date1=new Date();
            Timestamp checker=new Timestamp(date1.getTime());
            FirstOpenHelper firstOpenHelper=new FirstOpenHelper(this);
            SQLiteDatabase database=firstOpenHelper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            if(title.getText().toString().equals("")){
                Toast.makeText(this,"Enter a Title for Reminder",Toast.LENGTH_SHORT).show();
            }
            else if(date.getText().toString().equals("Date") && !time.getText().toString().equals("Time")){
                    Toast.makeText(this,"Enter a Date for Reminder",Toast.LENGTH_SHORT).show();
            }
            else if (!date.getText().toString().equals("Date") || !time.getText().toString().equals("Time")){
                if(timeStamp.before(checker)){
                    Toast.makeText(this,"Enter a valid Date and Time",Toast.LENGTH_SHORT).show();
                }
                else{
                    transfer(cv,database);
                }
            }
            else {
                transfer(cv,database);
            }
        }
        else if(item_id==R.id.discard_button){
            Intent i=new Intent();
            setResult(RESULT_CANCELED,i);
            finish();
        }
        else if(item_id==R.id.color_button){
            final Dialog dialog=new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(480,480);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=getLayoutInflater().inflate(R.layout.color_dialog,null);
            dialog.setContentView(v,params);

//            AlertDialog.Builder builder=new AlertDialog.Builder(this);
//            View v=getLayoutInflater().inflate(R.layout.color_dialog,null);
//            builder.setView(v);
//            final AlertDialog dialog=builder.create();
//            Window window = dialog.getWindow();
//            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           dialog.show();
            Button red_button=(Button) v.findViewById(R.id.red_button);
            Button green_button=(Button) v.findViewById(R.id.green_button);
            Button blue_button=(Button) v.findViewById(R.id.blue_button);
            Button yello_button=(Button) v.findViewById(R.id.yellow_button);
            red_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color=1;
                    setColor(color);
                    dialog.dismiss();
                }
            });
            green_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color=2;
                    setColor(color);
                    dialog.dismiss();
                }
            });
            blue_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color=3;
                    setColor(color);
                    dialog.dismiss();
                }
            });
            yello_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color=4;
                    setColor(color);
                    dialog.dismiss();
                }
            });

        }
        return true;
    }
    void transfer(ContentValues cv,SQLiteDatabase database){
        cv.put(FirstOpenHelper.TODO_TITLE, title.getText().toString());
        cv.put(FirstOpenHelper.TODO_DETAIL, detail.getText().toString());
        if(date.getText().toString().equals("Date")){
            date.setText("");
        }
        cv.put(FirstOpenHelper.TODO_DATE, date.getText().toString());
        if(time.getText().toString().equals("Time")){
            time.setText("");
        }
        cv.put(FirstOpenHelper.TODO_TIME,time.getText().toString());
        if(date.getText().toString().equals("") && time.getText().toString().equals("")){
            cv.put(FirstOpenHelper.TODO_TIMESTAMP,"");
        }else {
            cv.put(FirstOpenHelper.TODO_TIMESTAMP, timeStamp.toString());
        }
        cv.put(FirstOpenHelper.TODO_COLOR,color);
        if (decider == ListActivity.NEW_ITEM) {
            database.insert(FirstOpenHelper.TODO_TABLE_NAME, null, cv);
            if(!time.getText().toString().equals("")){
                String[]string1={timeStamp.toString()};
                Cursor cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_TIMESTAMP+"=?",string1,null,null,null);
                cursor.moveToNext();
                String idq=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_ID));
                setAlarm(idq);
            }
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        } else if (decider == ListActivity.MODIFY_ITEM) {
            String[] S = {"" + id};
            database.update(FirstOpenHelper.TODO_TABLE_NAME, cv, FirstOpenHelper.TODO_ID + "=?", S);
            if(!time.getText().toString().equals("")){
                setAlarm(id);
            }
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
    }
    void setTimeStamp(int year1,int month1,int dom1,int hours1,int mins1){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.YEAR,year1);
        cal.set(Calendar.MONTH,month1);
        cal.set(Calendar.DAY_OF_MONTH,dom1);
        cal.set(Calendar.HOUR_OF_DAY,hours1);
        cal.set(Calendar.MINUTE,mins1);
        if(hours==0 && mins==0){
            if(year==0 && month==0 && dom==0){
                timeStamp=new Timestamp(0);
            }
            else{
                timeStamp=new Timestamp(cal.getTimeInMillis());
            }
        }
        else{
            cal.set(Calendar.HOUR_OF_DAY,hours1);
            cal.set(Calendar.MINUTE,mins1);
            timeStamp=new Timestamp(cal.getTimeInMillis());
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year=year;
        this.month=month;
        this.dom=dayOfMonth;
        setTimeStamp(year,month,dom,hours,mins);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        date.setText(simpleDateFormat.format(timeStamp));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hours=hourOfDay;
        mins=minute;
        setTimeStamp(year,month,dom,hourOfDay,minute);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
        time.setText(simpleDateFormat.format(timeStamp));
    }

    public void setAlarm(String id_req){
        Toast.makeText(this,"Reminder Set",Toast.LENGTH_SHORT).show();
        AlarmManager am=(AlarmManager) ToDoDetailActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(ToDoDetailActivity.this,AlarmReceiver.class);
        i.putExtra(FirstOpenHelper.TODO_ID,id_req);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(ToDoDetailActivity.this,Integer.parseInt(id_req),i,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(am.RTC_WAKEUP,timeStamp.getTime(),pendingIntent);

    }
}
