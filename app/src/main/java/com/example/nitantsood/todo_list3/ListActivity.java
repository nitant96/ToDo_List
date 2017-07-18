package com.example.nitantsood.todo_list3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.StringTokenizer;

import static android.R.attr.width;
import static java.security.AccessController.getContext;

public class ListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, ToDo_Adapter.ReminderClickListener {

    public static final int NEW_ITEM = 20;
    public static final int MODIFY_ITEM=10;
    public static final int SEARCHED_BY_DATE=1;
    public static final int SEARCHED_BY_NAME=2;
    public static final int SEARCHED_BY_ARCHIVED=3;
    public static final int SEARCHED_BY_PRIORITY=4;
    int searchedBy=SEARCHED_BY_DATE;
    RecyclerView recyclerView;
    int searched_month,searched_year,searched_dom;
    public static ArrayList<String> listOfItemSelected=new ArrayList<>();
    ArrayList<OneEntry> ToDo_List = new ArrayList<>();
    ToDo_Adapter adapter;
    TextView listType;
    FloatingActionButton fab;
    String searched_date=null,searchedName;
    int ListOrderNo=1,color;
    boolean itemSelected=false,searched=false;
    MenuItem del_item,search_item,col_item,show_archive,search_priority,search_name;
    ImageButton ListOrder;
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        adapter = new ToDo_Adapter(this, ToDo_List,this);
        listType= (TextView) findViewById(R.id.listType);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                int priority1;
                int priority2;
                String id1,id2;
                id1=ToDo_List.get(from).id;
                id2=ToDo_List.get(to).id;
                FirstOpenHelper firstOpenHelper=new FirstOpenHelper(ListActivity.this);
                SQLiteDatabase database=firstOpenHelper.getWritableDatabase();
                ContentValues cv1=new ContentValues();
                ContentValues cv2=new ContentValues();
                Cursor c1=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ID+"="+id1,null,null,null,null);
                c1.moveToNext();
                priority1=c1.getInt(c1.getColumnIndex(FirstOpenHelper.TODO_PRIORITY));
                Cursor c2=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ID+"="+id2,null,null,null,null);
                c2.moveToNext();
                priority2=c2.getInt(c2.getColumnIndex(FirstOpenHelper.TODO_PRIORITY));
                cv1.put(FirstOpenHelper.TODO_PRIORITY,priority1);
                cv2.put(FirstOpenHelper.TODO_PRIORITY,priority2);
                String[] S1={id1};
                String[] S2={id2};
                database.update(FirstOpenHelper.TODO_TABLE_NAME,cv2,FirstOpenHelper.TODO_ID+"=?",S1);
                database.update(FirstOpenHelper.TODO_TABLE_NAME,cv1,FirstOpenHelper.TODO_ID+"=?",S2);
                Collections.swap(ToDo_List,from,to);
                adapter.notifyItemMoved(from,to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position=viewHolder.getAdapterPosition();
                final  String id=ToDo_List.get(position).id;
                final OneEntry entry=ToDo_List.get(position);
                FirstOpenHelper firstOpenHelper=new FirstOpenHelper(ListActivity.this);
                final SQLiteDatabase database=firstOpenHelper.getWritableDatabase();
                final ContentValues cv=new ContentValues();
                if(searched && searchedBy==SEARCHED_BY_ARCHIVED){
                    cv.put(FirstOpenHelper.TODO_ARCHIVE,0);
                    Snackbar.make(recyclerView,"Reminder UnArchived. Press to Undo", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cv.put(FirstOpenHelper.TODO_ARCHIVE,1);
                            database.update(FirstOpenHelper.TODO_TABLE_NAME,cv,FirstOpenHelper.TODO_ID+"="+id,null);
                            ToDo_List.add(position,entry);
                            adapter.notifyItemInserted(position);
                        }
                    }).show();
                }else {
                    cv.put(FirstOpenHelper.TODO_ARCHIVE, 1);
                    Snackbar.make(recyclerView,"Reminder Archived. Press to Undo", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cv.put(FirstOpenHelper.TODO_ARCHIVE,0);
                            database.update(FirstOpenHelper.TODO_TABLE_NAME,cv,FirstOpenHelper.TODO_ID+"="+id,null);
                            ToDo_List.add(position,entry);
                            adapter.notifyItemInserted(position);
                        }
                    }).show();
                }
                database.update(FirstOpenHelper.TODO_TABLE_NAME,cv,FirstOpenHelper.TODO_ID+"="+id,null);
                ToDo_List.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                if(itemSelected) {
                    return false;
                }else if(searched && searchedBy==SEARCHED_BY_PRIORITY){
                    return true;
                }else{
                    return false;
                }
            }
@Override
public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
        View itemView = viewHolder.itemView;

        Paint p = new Paint();
//        Bitmap icon;
        p.setColor(Color.parseColor("#00695C"));
        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                (float) itemView.getBottom(), p);
        Paint p1=new Paint();
        p1.setColor(Color.RED);
        p1.setTextSize(50);
        c.drawText("Archiving...",itemView.getLeft()+dpToPx(10),(itemView.getBottom()+itemView.getTop())/2,p1);
//        icon = BitmapFactory.decodeResource(
//                getApplicationContext().getResources(),R.drawable.export_icon);
//        RectF icon_dest = new RectF((float) itemView.getRight() + 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
////        c.drawBitmap(icon, null, icon_dest, p);
//        c.drawBitmap(icon,itemView.getLeft(),itemView.getTop(),p);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ListActivity.this,ToDoDetailActivity.class);
                i.putExtra("requestCode",NEW_ITEM);
                startActivityForResult(i,NEW_ITEM);
            }
        });
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
                setupMenuOptions();
                updateList();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.list_menu,menu);
        col_item=menu.getItem(0);
        del_item=menu.getItem(1);
        search_item=menu.getItem(2);
        show_archive=menu.getItem(3);
        search_priority=menu.getItem(4);
        search_name=menu.getItem(5);
        del_item.setIcon(R.drawable.del_but);
        col_item.setIcon(R.drawable.color_but_1);
        col_item.setVisible(false);
        search_item.setVisible(true);
        del_item.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.List_color_button){
            final Dialog dialog=new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(480,480);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=getLayoutInflater().inflate(R.layout.color_dialog,null);
            dialog.setContentView(v,params);

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
        else if(id==R.id.delete_item){
            itemSelected=false;
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
                    setupMenuOptions();
                    updateList();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listOfItemSelected.clear();
                    setupMenuOptions();
                    updateList();
                }
            });
            Dialog dialog=builder.create();
            dialog.show();
        }
        else if(id==R.id.search_item){
            Calendar calendar=Calendar.getInstance();
            searched_year=calendar.get(Calendar.YEAR);
            searched_month=calendar.get(Calendar.MONTH);
            searched_dom=calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog=new DatePickerDialog(ListActivity.this,ListActivity.this,searched_year,searched_month,searched_dom);
            datePickerDialog.show();

        }
        else if(id==R.id.show_archive){
            searched=true;
            searchedBy=SEARCHED_BY_ARCHIVED;
            setupMenuOptions();
            updateList();
        }
        else if(id==R.id.search_by_priority){
            searched=true;
            searchedBy=SEARCHED_BY_PRIORITY;
            ListOrder.setEnabled(false);
            ListOrderNo=1;
            ListOrder.setImageResource(android.R.drawable.arrow_down_float);
            setupMenuOptions();
            updateList();
        }
        else if(id==R.id.search_by_title){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Search By Name");
            final View v=getLayoutInflater().inflate(R.layout.search_by_name_dialog,null);
            builder.setView(v);
            final Dialog dialog=builder.create();
            dialog.show();
            Button search=(Button) v.findViewById(R.id.search_name_ok_button);
            Button cancel=(Button) v.findViewById(R.id.search_name_cancel_button);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searched=true;
                    searchedBy=SEARCHED_BY_NAME;
                    searchedName="";
                    EditText searched_title=(EditText) v.findViewById(R.id.searched_title);
                    if(!searched_title.getText().toString().equals("")) {
                        searchedName = searched_title.getText().toString();
                    }
                    updateList();
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        return true;
    }
    void setColor(int color){
        itemSelected=false;
        ContentValues cv=new ContentValues();
        cv.put(FirstOpenHelper.TODO_COLOR,color);
        for(int index=0;index<listOfItemSelected.size();index++) {
            FirstOpenHelper firstOpenHelper = new FirstOpenHelper(ListActivity.this);
            SQLiteDatabase database = firstOpenHelper.getWritableDatabase();
            String[] s = {listOfItemSelected.get(index)};
            database.update(FirstOpenHelper.TODO_TABLE_NAME,cv,FirstOpenHelper.TODO_ID + "=?", s);
        }
        listOfItemSelected.clear();
        setupMenuOptions();
        updateList();
    }
    @Override
    public void onBackPressed(){
        if(!searched) {
            if (itemSelected == true) {
                itemSelected = false;
                listOfItemSelected.clear();
                setupMenuOptions();
                updateList();
            } else {
                finish();
            }
        }
        else
        {
            searched=false;
            ListOrder.setEnabled(true);
            setupMenuOptions();
            updateList();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            setupMenuOptions();
            updateList();
        }
    }
    public void setupMenuOptions(){
        search_name.setVisible(false);
        search_priority.setVisible(false);
        show_archive.setVisible(false);
        del_item.setVisible(false);
        col_item.setVisible(false);
        fab.setVisibility(View.INVISIBLE);
        search_item.setVisible(false);
        if(searched){
            if(itemSelected){
                del_item.setVisible(true);
                col_item.setVisible(true);
            }else{
                search_item.setVisible(true);
                search_name.setVisible(true);
                search_priority.setVisible(true);
                show_archive.setVisible(true);
            }
        }else{
            if(itemSelected){
                del_item.setVisible(true);
                col_item.setVisible(true);
            }else{
                search_item.setVisible(true);
                search_name.setVisible(true);
                search_priority.setVisible(true);
                show_archive.setVisible(true);
                fab.setVisibility(View.VISIBLE);
            }
        }
    }
    public void updateList(){
        ToDo_List.clear();
        Cursor cursor;
        cursor=getCursor();
        while(cursor.moveToNext()){
            int size=ToDo_List.size();
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
    public Cursor getCursor(){
        FirstOpenHelper firstOpenHelper=new FirstOpenHelper(this);
        SQLiteDatabase database=firstOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        if(!searched){
            listType.setText("ToDo List");
            if(ListOrderNo==1){
                cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ARCHIVE+"= 0",null,null,null,FirstOpenHelper.TODO_TIMESTAMP);
            }
            else if(ListOrderNo==2){
                cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ARCHIVE+"= 0",null,null,null,FirstOpenHelper.TODO_TIMESTAMP+" DESC");
            }
            else if(ListOrderNo==3){
                cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ARCHIVE+"= 0",null,null,null,FirstOpenHelper.TODO_COLOR);
            }
            else if(ListOrderNo==4){
                cursor=database.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ARCHIVE+"= 0",null,null,null,FirstOpenHelper.TODO_TITLE);
            }
            if(!cursor.moveToNext()){
            Snackbar.make(recyclerView,"No Reminders to show, Add a New Reminder.", Snackbar.LENGTH_LONG).show();
            }else{
                cursor.moveToPrevious();
            }
        }else{
            if(searchedBy==SEARCHED_BY_DATE) {
                listType.setText(searched_date);
                String[] search = {searched_date};
                if (ListOrderNo == 1) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null, FirstOpenHelper.TODO_DATE + " LIKE ? AND " + FirstOpenHelper.TODO_ARCHIVE + "= 0", search, null, null, FirstOpenHelper.TODO_TIMESTAMP);
                } else if (ListOrderNo == 2) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null, FirstOpenHelper.TODO_DATE + " LIKE ? AND " + FirstOpenHelper.TODO_ARCHIVE + "= 0", search, null, null, FirstOpenHelper.TODO_TIMESTAMP + " DESC");
                } else if (ListOrderNo == 3) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null, FirstOpenHelper.TODO_DATE + " LIKE ? AND " + FirstOpenHelper.TODO_ARCHIVE + "= 0", search, null, null, FirstOpenHelper.TODO_COLOR);
                } else if (ListOrderNo == 4) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null, FirstOpenHelper.TODO_DATE + " LIKE ? AND " + FirstOpenHelper.TODO_ARCHIVE + "= 0", search, null, null, FirstOpenHelper.TODO_TITLE);
                }
                if (!cursor.moveToNext()) {
                    Snackbar.make(recyclerView, "Sorry, No Reminder on " + searched_date, Snackbar.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    cursor.moveToPrevious();
                }
            }
            else if(searchedBy==SEARCHED_BY_NAME){
                listType.setText(searchedName);
                String[] S={searchedName};
                if (ListOrderNo == 1) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_TITLE+" LIKE ?",S, null, null, FirstOpenHelper.TODO_TIMESTAMP);
                } else if (ListOrderNo == 2) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_TITLE+" LIKE ?",S, null, null, FirstOpenHelper.TODO_TIMESTAMP + " DESC");
                } else if (ListOrderNo == 3) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_TITLE+" LIKE ?",S, null, null, FirstOpenHelper.TODO_COLOR);
                } else if (ListOrderNo == 4) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_TITLE+" LIKE ?",S, null, null, FirstOpenHelper.TODO_TITLE);
                }
                if (!cursor.moveToNext()) {
                    Snackbar.make(recyclerView, "Sorry, No Reminder with Title "+searchedName+" Found !!", Snackbar.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    cursor.moveToPrevious();
                }
            }
            else if(searchedBy==SEARCHED_BY_ARCHIVED){
                listType.setText("ARCHIVED");
                if (ListOrderNo == 1) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_ARCHIVE + "= 1",null, null, null, FirstOpenHelper.TODO_TIMESTAMP);
                } else if (ListOrderNo == 2) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_ARCHIVE + "= 1",null, null, null, FirstOpenHelper.TODO_TIMESTAMP + " DESC");
                } else if (ListOrderNo == 3) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_ARCHIVE + "= 1",null, null, null, FirstOpenHelper.TODO_COLOR);
                } else if (ListOrderNo == 4) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_ARCHIVE + "= 1",null, null, null, FirstOpenHelper.TODO_TITLE);
                }
                if (!cursor.moveToNext()) {
                    Snackbar.make(recyclerView, "Sorry, No Archived Reminder Found !!", Snackbar.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    cursor.moveToPrevious();
                }
            }
            else if(searchedBy==SEARCHED_BY_PRIORITY){
                listType.setText("PRIORITY LIST");
                if (ListOrderNo == 1) {
                    cursor = database.query(FirstOpenHelper.TODO_TABLE_NAME, null,FirstOpenHelper.TODO_ARCHIVE + "= 0",null, null, null, FirstOpenHelper.TODO_PRIORITY);
                }
                if (!cursor.moveToNext()) {
                    Snackbar.make(recyclerView, "No Reminders to show, Add a New Reminder.", Snackbar.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    cursor.moveToPrevious();
                }
            }
        }
        return cursor;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        searched=true;
        searchedBy=SEARCHED_BY_DATE;
        Calendar c=Calendar.getInstance();
        c.set(year,month,dayOfMonth);
        Timestamp timestamp=new Timestamp(c.getTimeInMillis());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        searched_date=simpleDateFormat.format(timestamp);
        setupMenuOptions();
        updateList();
    }

    @Override
    public void onItemClick(View view, int position) {
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
                    listOfItemSelected.clear();
                }
                setupMenuOptions();
                updateList();
            }
            else {
                listOfItemSelected.add(ToDo_List.get(position).id);
                setupMenuOptions();
                updateList();
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
            if (itemSelected == false) {
                itemSelected = true;
                listOfItemSelected.add(ToDo_List.get(position).id);
                setupMenuOptions();
                updateList();
                return true;
        }
        return false;
    }
}