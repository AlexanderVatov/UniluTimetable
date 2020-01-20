package lu.uni.timetable;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        Date now = Calendar.getInstance().getTime();

        //For testing purposes
//        Calendar c = Calendar.getInstance();
//        c.roll(Calendar.DAY_OF_YEAR,1);
//        c.set(Calendar.HOUR_OF_DAY,0);
//        c.set(Calendar.MINUTE,0);
//        Date now = c.getTime();

        List<Event> events = Presenter.getInstance().synchronousRequestOngoingEvents(
                now, Utils.endOfDay(now));
        if (events.size() == 0) {
            views.setTextViewText(R.id.roomCell, "No more classes today");
            views.setViewVisibility(R.id.subjectCell, View.GONE);
        }
        else {
            Event e = events.get(0);
            StringBuilder b = new StringBuilder();
            b.append(DateUtils.formatDateTime(App.getInstance(),
                    e.start.getTime(),
                    DateUtils.FORMAT_SHOW_TIME));
            b.append(" · ");
            b.append(e.room);

            views.setTextViewText(R.id.roomCell, b.toString());
            views.setTextViewText(R.id.subjectCell, e.title);
            views.setViewVisibility(R.id.subjectCell, View.VISIBLE);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            // Create a pending intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.roomCell, pendingIntent);
            views.setOnClickPendingIntent(R.id.subjectCell, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

