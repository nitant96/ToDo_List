package com.example.nitantsood.todo_list3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        long[] vibrate={123,123,123,246,123,123,123};
        String id=intent.getStringExtra(FirstOpenHelper.TODO_ID);
        FirstOpenHelper firstOpenHelper=new FirstOpenHelper(context);
        SQLiteDatabase databse=firstOpenHelper.getReadableDatabase();
        Cursor cursor=databse.query(FirstOpenHelper.TODO_TABLE_NAME,null,FirstOpenHelper.TODO_ID+"="+id,null,null,null,null);
        cursor.moveToNext();
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(cursor.getString(cursor.getColumnIndex(FirstOpenHelper.TODO_TITLE)))
                .setAutoCancel(true)
                .setContentText("Reminder").setVibrate(vibrate);
        Intent resultIntent=new Intent(context,ToDoDetailActivity.class);
        resultIntent.putExtra(FirstOpenHelper.TODO_ID,id);
        resultIntent.putExtra("requestCode",ListActivity.MODIFY_ITEM);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(context,Integer.parseInt(id),resultIntent,PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Integer.parseInt(id),mBuilder.build());
    }
}
