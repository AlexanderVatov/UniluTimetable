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

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CalendarSync implements Presenter.Observer {
    private static CalendarSync _instance;
    public CalendarSync() {
        _instance=this;
//        Presenter.getInstance().register(new WeakReference<Presenter.Observer>(this));
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

    public static void updateEvent(Event event) {
        if (ContextCompat.checkSelfPermission(App.getInstance(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            System.err.println("No permission to write to calendar!");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.start.getTime());
        values.put(CalendarContract.Events.DTEND, event.end.getTime());
        values.put(CalendarContract.Events.TITLE, event.title);
        values.put(CalendarContract.Events.DESCRIPTION, event.lecturer);
        values.put(CalendarContract.Events.CALENDAR_ID, 11);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Luxembourg");
        values.put(CalendarContract.Events.EVENT_LOCATION, event.room);

        ContentResolver cr = App.getInstance().getContentResolver();
        Long eventCalendarId = event.calendarId;
        if(eventCalendarId == null) {
            //If the event has not yet been added to the calendar
            System.err.println("Adding event to calendar...");
            Uri insertUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            event.calendarId = Long.parseLong(insertUri.getLastPathSegment());
        }
        else {
            System.err.println("Updating event in calendar...");
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.calendarId);
            cr.update(updateUri, values, null, null);
        }
    }

    public static void deleteEvent(Event event) {
        if(event.calendarId != null) {
            System.err.println("Deleting event from calendar...");
            ContentResolver cr = App.getInstance().getContentResolver();
            Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.calendarId);
            cr.delete(deleteUri, null, null);
            event.calendarId=null;
        }
    }



    @Override
    public void onDatabaseUpdated(Date startOfUpdatedPeriod, Date endOfUpdatedPeriod) {
        List<Event>  events = Presenter.synchronousRequestEvents(startOfUpdatedPeriod, endOfUpdatedPeriod);
        for(Event e: events) updateEvent(e);
    }

    public static void update(Date startOfUpdatedPeriod,
                       Date endOfUpdatedPeriod,
                       Collection<Event> createdEvents,
                       Collection<Event> modifiedEvents,
                       Collection<Event> removedEvents) {

        for(Event e: createdEvents)  updateEvent(e);
        for(Event e: modifiedEvents) updateEvent(e);
        for(Event e: removedEvents)  deleteEvent(e);
    }

    @Override
    public void onEventsReady(List<Event> events) {

    }
}
