package lu.uni.timetable;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.collection.ArraySet;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Presenter class implements the Presenter in the MVP model. In a variation of MVP, a single
 * application-wide instance is used; it survives the destruction of fragments and activities. An
 * instance can be obtained using {@link #getInstance()}. As per the MVP model, views should
 * implement the {@link ITimetableView} interface. Views should register using {@link #register(WeakReference)}.
 * Views are stored using WeakReferences in order to avoid memory leaks. When a database update is
 * performed, all registered views will be notified using {@link ITimetableView#onDatabaseUpdated(Date, Date)}.
 * However, when a view queries events using {@link #requestEvents(WeakReference, Date, Date)}, only
 * the view passed as an argument will be modified.
 */

public class Presenter {
    private static Presenter _inst = null;
    private Set<WeakReference<ITimetableView>> views = new ArraySet<WeakReference<ITimetableView>>();

    private Presenter() {

    }

    /**
     * Register a view in order to receive notifications.
     * @param view View to be registered
     */
    public void register(WeakReference<ITimetableView> view) {
        this.views.add(view);
    }

    /**
     * This method is called by Updater when an update is performed.
     * @param start Start of the date range affected by the update.
     * @param end End of the date range affected by the update.
     */
    void updatePerformed(Date start, Date end) {
        System.err.println("Presenter: An update was performed! Informing " + views.size() + " view(s)...");

        for (WeakReference<ITimetableView> viewWeakRef : views) {
            ITimetableView view = viewWeakRef.get();
            if (view != null)
                view.onDatabaseUpdated(start, end);
        }

        //Update widget(s)
        Intent intent = new Intent(App.getInstance(), Widget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(App.getInstance())
                .getAppWidgetIds(new ComponentName(App.getInstance(),Widget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        App.getInstance().sendBroadcast(intent);
    }

    /**
     * This method is used to query events from the database. The query is performed asynchronously;
     * when it is finished, the view passed as an argument will be notified using
     * {@link ITimetableView#onDatabaseUpdated(Date, Date)}.
     *
     * @param view The view which should be notified
     * @param start The start of the date range for which events are requested
     * @param end The end of the date range for which events are requested
     */
    public void requestEvents(WeakReference<ITimetableView> view, Date start, Date end) {
        new DatabaseQuery(view, start, end).execute();
    }

    /**
     * Performs synchronous database queries of events fully comprised between two dates.
     *
     * @param start The start of the date range for which events are requested
     * @param end The end of the date range for which events are requested
     * @return A list of events fully comprised between start and end.
     */
    public List<Event> synchronousRequestEvents(Date start, Date end) {
        EventDAO dao = Database.instance().getEventDAO();
        if(start == null || end == null)
            return dao.getAllEvents();
        return dao.getEventsBetweenDates(start,end);
    }

    /**
     * Performs synchronous database queries of events partially comprised between two dates.
     *
     * @param start The start of the date range for which events are requested
     * @param end The end of the date range for which events are requested
     * @return A list of events fully comprised between start and end
     */
    public List<Event> synchronousRequestOngoingEvents(Date start, Date end) {
        return Database.instance().getEventDAO().getOngoingEventsBetween(start, end);
    }

    /**
     * Performs synchronous database queries of events ongoing at a moment in time.
     *
     * @param moment The particular moment in time for which events are requested
     * @return A list of events ongoing at the given moment
     */
    public List<Event> synchronousRequestOngoingEvents(Date moment) {
        return Database.instance().getEventDAO().getOngoingEventsAt(moment);
    }
    /**
     * Convenience method which requests events in the current day. Its behaviour is otherwise the
     * same as {@link #requestEvents(WeakReference, Date, Date)}.
     * @param view The view which should be notified
     */
    public void requestEventsToday(WeakReference<ITimetableView> view) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime(); //Today
        c.roll(Calendar.DAY_OF_YEAR, 1);
        c.roll(Calendar.SECOND, -1);
        Date end = c.getTime();
        requestEvents(view, start, end);
    }

    /**
     * Convenience method which requests events in the current week. Its behaviour is otherwise the
     * same as {@link #requestEvents(WeakReference, Date, Date)}.
     * @param view The view which should be notified
     */
    public void requestEventsThisWeek(WeakReference<ITimetableView> view) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.roll(Calendar.WEEK_OF_YEAR, -1);
        Date start = c.getTime(); //Last Monday
        c.roll(Calendar.WEEK_OF_YEAR, 2);
        c.roll(Calendar.SECOND, -1);
        Date end = c.getTime();
        requestEvents(view, start, end);
    }

    /**
     * Returns an instance of this class.
     * @return An instance of this class. It is never null.
     */
    public static Presenter getInstance() {
        if (_inst == null)
            synchronized (Presenter.class) {
                if (_inst == null)
                    _inst = new Presenter();
            }

        return _inst;
    }

    /**
     * This class is used for a database query. {@link Presenter#requestEvents(WeakReference, Date, Date)}
     * should normally be used instead.
     */

    public class DatabaseQuery extends AsyncTask<Void, Void, List<Event>> {
        private WeakReference<ITimetableView> view;
        private Date start, end;

        public DatabaseQuery(WeakReference<ITimetableView> view){
            this.view = view;
        };

        public DatabaseQuery(WeakReference<ITimetableView> view, Date start, Date end) {
            this.view = view;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {
            return synchronousRequestEvents(start,end);
        }

        @Override
        protected void onPostExecute(List<Event> result) {
            ITimetableView v = view.get();
            if (v != null) {
                v.onEventsReady(result);
            }

        }
    }

}
