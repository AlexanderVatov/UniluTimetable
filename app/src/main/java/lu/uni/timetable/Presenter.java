package lu.uni.timetable;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The Presenter class implements the Presenter in the MVP model. In a variation of MVP, a single
 * application-wide instance is used; it survives the destruction of fragments and activities. An
 * instance can be obtained using {@link #getInstance()}. As per the MVP model, observers should
 * implement the {@link Observer} interface. Observers should register using {@link #register(WeakReference)}.
 * Observers are stored using WeakReferences in order to avoid memory leaks. When a database update is
 * performed, all registered observers will be notified using {@link Observer#onDatabaseUpdated(Date, Date)}.
 * However, when an observer queries events using {@link #requestEvents(WeakReference, Date, Date)}, only
 * the observer passed as an argument will be modified.
 */

public class Presenter {
    public interface Observer {
        void onDatabaseUpdated(Date startOfUpdatedPeriod, Date endOfUpdatedPeriod);
        void onEventsReady(List<Event> events);
    }
    private static Presenter _inst = null;
    private List<WeakReference<Observer>> observers = new ArrayList<>();

    private Presenter() {

    }

    /**
     * Register an observer in order to receive notifications. If the observer is already registered,
     * it will not be registered again.
     * @param newObserver Observer to be registered
     */
    public void register(WeakReference<Observer> newObserver) {
        Observer newRef = newObserver.get();

        //Check that the observer is not already in the list.
        for(WeakReference<Observer> ref: observers)
            if(ref.get() == newRef)
                return;

        this.observers.add(newObserver);
    }

    /**
     * This method is called by Updater when an update is performed.
     * @param start Start of the date range affected by the update.
     * @param end End of the date range affected by the update.
     */
    void updatePerformed(Date start, Date end) {
        System.err.println("Presenter: An update was performed! Informing " + observers.size() + " observer(s)...");

        for (Iterator<WeakReference<Observer>> it = observers.iterator(); it.hasNext();) {
            Observer observer = it.next().get();
            if (observer == null)
                it.remove();
            else
                observer.onDatabaseUpdated(start, end);
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
     * when it is finished, the observer passed as an argument will be notified using
     * {@link Observer#onDatabaseUpdated(Date, Date)}.
     *
     * @param observer The observer which should be notified
     * @param start The start of the date range for which events are requested
     * @param end The end of the date range for which events are requested
     */
    public void requestEvents(WeakReference<Observer> observer, Date start, Date end) {
        new DatabaseQuery(observer, start, end).execute();
    }

    /**
     * Performs synchronous database queries of events fully comprised between two dates.
     *
     * @param start The start of the date range for which events are requested
     * @param end The end of the date range for which events are requested
     * @return A list of events fully comprised between start and end.
     */
    public static List<Event> synchronousRequestEvents(Date start, Date end) {
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
     * @param observer The observer which should be notified
     */
    public void requestEventsToday(WeakReference<Observer> observer) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime(); //Today
        c.roll(Calendar.DAY_OF_YEAR, 1);
        c.roll(Calendar.SECOND, -1);
        Date end = c.getTime();
        requestEvents(observer, start, end);
    }

    /**
     * Convenience method which requests events in the current week. Its behaviour is otherwise the
     * same as {@link #requestEvents(WeakReference, Date, Date)}.
     * @param observer The observer which should be notified
     */
    public void requestEventsThisWeek(WeakReference<Observer> observer) {
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
        requestEvents(observer, start, end);
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

    public static class DatabaseQuery extends AsyncTask<Void, Void, List<Event>> {
        private WeakReference<Observer> observer;
        private Date start, end;

        public DatabaseQuery(WeakReference<Observer> observer){
            this.observer = observer;
        };

        public DatabaseQuery(WeakReference<Observer> observer, Date start, Date end) {
            this.observer = observer;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {
            return Presenter.synchronousRequestEvents(start,end);
        }

        @Override
        protected void onPostExecute(List<Event> result) {
            Observer o = observer.get();
            if (o != null) {
                o.onEventsReady(result);
            }

        }
    }


}
