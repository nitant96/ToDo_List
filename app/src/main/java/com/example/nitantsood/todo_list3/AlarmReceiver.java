package com.example.nitantsood.todo_list3;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import static android.R.attr.start;

public class AlarmReceiver extends BroadcastReceiver {
    public String id;
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        long[] vibrate_patter={0,500,300,800};
//        Vibrator vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(vibrate_patter,6);
        id=intent.getStringExtra(FirstOpenHelper.TODO_ID);
        FirstOpenHelper firstOpenHelper=new FirstOpenHelper(context);
        final SQLiteDatabase databse=firstOpenHelper.getReadableDatabase();
        final Cursor cursor=databse.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ID+"="+id,null,null,null,null);
        cursor.moveToNext();
        String title=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TITLE));
        String detail=cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_DETAIL));
        ContentValues cv=new ContentValues();
        cv.put(FirstOpenHelper.TODO_DATE,"");
        cv.put(FirstOpenHelper.TODO_TIME,"");
        cv.put(FirstOpenHelper.TODO_TIMESTAMP,"");
        databse.update(FirstOpenHelper.TODO_TABLE_NAME,cv,FirstOpenHelper.TODO_ID+"="+id,null);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("Reminder")
                .setContentText(title)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, android.R.color.holo_orange_light))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title).setSummaryText(detail))
                .setShowWhen(true)
                .setVibrate(vibrate_patter);
        ToDoDetailActivity.called_from=true;
        Intent resultIntent=new Intent(context,ToDoDetailActivity.class);
        resultIntent.putExtra(FirstOpenHelper.TODO_ID,id);
        resultIntent.putExtra("requestCode",ListActivity.MODIFY_ITEM);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(context,Integer.parseInt(id),resultIntent,PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Integer.parseInt(id),mBuilder.build());
    }
}
