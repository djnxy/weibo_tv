package com.example.xinyu10.newir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Calendar;

/**
 * Created by xinyu10 on 2016/10/17.
 */
public class MyCalender {

    static private Calendar mCalendar = null;
    static private Cursor userCursor= null;
    static private Context maincentext = null;
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";

    private MyCalender(){

    }

    static private void initCalendar(Context context){
        maincentext = context;
        mCalendar = Calendar.getInstance();
        userCursor = context.getContentResolver().query(Uri.parse(calanderURL), null,
                null, null, null);
        if(userCursor.getCount() > 0){
            userCursor.moveToFirst();
        }
    }


    public static void setInstance(Context context){
        if (mCalendar == null) {
            initCalendar(context);
        }
    }

    public static void testset(){
        String calId = userCursor.getString(userCursor.getColumnIndex("_id"));

        ContentValues event = new ContentValues();
        event.put("title", "test");
        event.put("description", "test");
        event.put("calendar_id",calId);

        mCalendar.set(Calendar.HOUR_OF_DAY,20);
        long start = mCalendar.getTime().getTime();
        mCalendar.set(Calendar.HOUR_OF_DAY,21);
        long end = mCalendar.getTime().getTime();

        event.put("dtstart", start);
        event.put("dtend", end);
        event.put("hasAlarm",1);

        Uri newEvent = maincentext.getContentResolver().insert(Uri.parse(calanderEventURL), event);
        long id = Long.parseLong( newEvent.getLastPathSegment() );
        ContentValues values = new ContentValues();
        values.put( "event_id", id );
        values.put( "minutes", 10 );
        maincentext.getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
    }
    public static String test(){
        return userCursor.getString(userCursor.getColumnIndex("name"));
    }
}
