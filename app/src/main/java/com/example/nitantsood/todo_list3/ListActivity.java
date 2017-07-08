package com.example.nitantsood.todo_list3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {

    public static final int NEW_ITEM = 20;
    public static final int MODIFY_ITEM=10;
    ListView listView;
    ArrayList<View> viewOfItemSelected=new ArrayList<>();
    ArrayList<String> listOfItemSelected=new ArrayList<>();
    ArrayList<OneEntry> ToDo_List = new ArrayList<>();
    ToDo_Adapter adapter;
    int ListOrderNo=1;
    boolean itemSelected=false;
    MenuItem add_item,del_item;
    ImageButton ListOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.item_list);
        adapter = new ToDo_Adapter(this, ToDo_List);
        listView.setAdapter(adapter);
        ListOrder = (ImageButton) findViewById(R.id.button);
        ListOrder.setImageResource(android.R.drawable.arrow_down_float);
        updateList();
        ListOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ListOrderNo==1) {
                    ListOrderNo = 2;
                    ListOrder.setImageResource(android.R.drawable.arrow_up_float);
                }
                else if(ListOrderNo==2) {
                    ListOrderNo = 3;
                    ListOrder.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                }
                else if(ListOrderNo==3){
                    ListOrderNo=4;
                    ListOrder.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
                }
                else if(ListOrderNo==4){
                    ListOrderNo=1;
                    ListOrder.setImageResource(android.R.drawable.arrow_down_float);
                }
                updateList();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView im=(ImageView) view.findViewById(R.id.selection_symbol);
                if(itemSelected==false) {
                    Intent i = new Intent(ListActivity.this, ToDoDetailActivity.class);
                    i.putExtra(FirstOpenHelper.TODO_ID, ToDo_List.get(position).id);
                    i.putExtra("requestCode", MODIFY_ITEM);
                    startActivityForResult(i, MODIFY_ITEM);
                }
                else{
                    if(listOfItemSelected.remove(ToDo_List.get(position).id)){
                        if(listOfItemSelected.size()==0){
                            itemSelected=false;
                            add_item.setVisible(true);
                            del_item.setVisible(false);
                            listOfItemSelected.clear();
                        }
                        im.setImageResource(android.R.drawable.btn_radio);
                        viewOfItemSelected.remove(view);
                    }
                    else {
                        listOfItemSelected.add(ToDo_List.get(position).id);
                        im.setImageResource(android.R.drawable.checkbox_on_background);
                        viewOfItemSelected.add(view);
                    }
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(itemSelected==false) {
                    itemSelected=true;
                    add_item.setVisible(false);
                    del_item.setVisible(true);
                    listOfItemSelected.add(ToDo_List.get(position).id);
                    ImageView im=(ImageView) view.findViewById(R.id.selection_symbol);
                    im.setImageResource(android.R.drawable.checkbox_on_background);
                    viewOfItemSelected.add(view);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.list_menu,menu);
        add_item=menu.getItem(0);
        del_item=menu.getItem(1);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.add_item){
            Intent i=new Intent(this,ToDoDetailActivity.class);
            i.putExtra("requestCode",NEW_ITEM);
            startActivityForResult(i,NEW_ITEM);
        }
        else if(id==R.id.delete_item){
            for(int index=0;index<viewOfItemSelected.size();index++){
                ImageView im=(ImageView) viewOfItemSelected.get(index).findViewById(R.id.selection_symbol);
                im.setImageResource(android.R.drawable.btn_radio);
            }
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("DELETE");
            builder.setMessage("Are you sure you want to DELETE the selected Reminder/s");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(int index=0;index<listOfItemSelected.size();index++) {
                        FirstOpenHelper firstOpenHelper = new FirstOpenHelper(ListActivity.this);
                        SQLiteDatabase database = firstOpenHelper.getWritableDatabase();
                        String[] s = {listOfItemSelected.get(index)};
                        database.delete(FirstOpenHelper.TODO_TABLE_NAME, FirstOpenHelper.TODO_ID + "=?", s);
                    }
                    listOfItemSelected.clear();
                    updateList();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listOfItemSelected.clear();
                }
            });
            Dialog dialog=builder.create();
            dialog.show();
            viewOfItemSelected.clear();
            itemSelected=false;
            add_item.setVisible(true);
            del_item.setVisible(false);
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            updateList();
        }
    }
    public void updateList(){
        FirstOpenHelper firstOpenHelper=new FirstOpenHelper(this);
        SQLiteDatabase database=firstOpenHelper.getReadableDatabase();
        ToDo_List.clear();
        Cursor cursor = null;
        if(ListOrderNo==1){
            cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,null,null,null,null,FirstOpenHelper.TODO_TIMESTAMP);
        }
        else if(ListOrderNo==2){
            cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,null,null,null,null,FirstOpenHelper.TODO_TIMESTAMP+" DESC");
        }
        else if(ListOrderNo==3){
            cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,null,null,null,null,FirstOpenHelper.TODO_COLOR);
        }
        else if(ListOrderNo==4){
            cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,null,null,null,null,FirstOpenHelper.TODO_TITLE);
        }
        while(cursor.moveToNext()){
            String title=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TITLE));
            String date=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_DATE));
            String detail=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_DETAIL));
            String id=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_ID));
            int color=cursor.getInt(cursor.getColumnIndex(FirstOpenHelper.TODO_COLOR));
            String time=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TIME));
            OneEntry entry=new OneEntry(title,detail,date,id,color,time);
            ToDo_List.add(entry);
        }
        adapter.notifyDataSetChanged();
    }
}

