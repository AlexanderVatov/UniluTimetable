package lu.uni.timetable;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarSync implements Presenter.Observer {
    private static CalendarSync _instance;
    public CalendarSync() {
        _instance=this;
        Presenter.getInstance().register(new WeakReference<Presenter.Observer>(this));
    }

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static void test(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            System.err.println("Requesting calendar writing permission...");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        }
        ContentResolver cr = App.getInstance().getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String selection = "(" + Calendars.ACCOUNT_NAME + " = ?)";
        //String selection = "(" + Calendars.OWNER_ACCOUNT + " = ?)";
//        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                + Calendars.OWNER_ACCOUNT + " = ?))";
//        String[] selectionArgs = new String[] {"aleksander.vatov@gmail.com"};
        String[] selectionArgs = new String[] {"Uni"};
        Cursor cursor = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cursor.moveToNext()) {

            // Get the field values
            long calID = cursor.getLong(PROJECTION_ID_INDEX);
            String displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            String accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            String ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // Do something with the values...
            System.err.println(calID);
            System.err.println(displayName);
            System.err.println(accountName);
            System.err.println(ownerName);
        }
        cursor.close();

    }

    public static void testAdd(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            System.err.println("Requesting calendar writing permission...");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        }

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2020, 0, 10, 10, 30);

        Calendar endTime = Calendar.getInstance();
        endTime.set(2020, 0, 10, 11, 30);

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, "Test event 3");
        values.put(CalendarContract.Events.DESCRIPTION, "Test description");
        values.put(CalendarContract.Events.CALENDAR_ID, 11);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Luxembourg");
        values.put(CalendarContract.Events.EVENT_LOCATION, "Luxembourg");

        ContentResolver cr = App.getInstance().getContentResolver();
        System.err.println(cr.insert(CalendarContract.Events.CONTENT_URI, values));
    }

    public static void updateEvent(Event event) {
        if (ContextCompat.checkSelfPermission(App.getInstance(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            System.err.println("No permission to write to calendar!");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.getStart().getTime());
        values.put(CalendarContract.Events.DTEND, event.getEnd().getTime());
        values.put(CalendarContract.Events.TITLE, event.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, event.getLecturer());
        values.put(CalendarContract.Events.CALENDAR_ID, 11);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Luxembourg");
        values.put(CalendarContract.Events.EVENT_LOCATION, event.getRoom());

        ContentResolver cr = App.getInstance().getContentResolver();
        Long eventCalendarId = event.getCalendarId();
        if(eventCalendarId == null) {
            //If the event has not yet been added to the calendar
            System.err.println("Adding event to calendar...");
            Uri insertUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            event.setCalendarId(Long.parseLong(insertUri.getLastPathSegment()));
            Database.instance().getEventDAO().update(event);
        }
        else {
            System.err.println("Updating event...");
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getCalendarId());
            cr.update(updateUri, values, null, null);
        }
    }

    public static void testQuery(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            System.err.println("Requesting calendar writing permission...");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        }

        Cursor cur;
        ContentResolver cr = App.getInstance().getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = CalendarContract.Events.CALENDAR_ID + " = ? ";
        String[] selectionArgs = new String[]{"11"};
        //String[] selectionArgs = new String[]{"content://com.android.calendar/events/2615"};

        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        for (int i = 1; (i <= 15) && cur.moveToNext(); ++i) {
            //String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
            for (int j = 0; j < mProjection.length; ++j) {
                System.err.println(cur.getString(j));
            }
        }
    }

    @Override
    public void onDatabaseUpdated(Date startOfUpdatedPeriod, Date endOfUpdatedPeriod) {
        List<Event>  events = Presenter.synchronousRequestEvents(startOfUpdatedPeriod, endOfUpdatedPeriod);
        for(Event e: events) updateEvent(e);

    }

    @Override
    public void onEventsReady(List<Event> events) {

    }
}
