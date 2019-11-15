package lu.uni.timetable;

import android.os.AsyncTask;

import androidx.collection.ArraySet;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Presenter {
    private static Presenter _inst = null;
    private Set<WeakReference<ITimetableView>> views = new ArraySet<WeakReference<ITimetableView>>();

    private Presenter() {

    }

    public void register(WeakReference<ITimetableView> view) {
        this.views.add(view);
    }

    public void updatePerformed(Date start, Date end) {
        System.err.println("Presenter: An update was performed! Informing " + views.size() + " view(s)...");

        for (WeakReference<ITimetableView> viewWeakRef : views) {
            ITimetableView view = viewWeakRef.get();
            if (view != null)
                view.onDatabaseUpdated(start, end);
        }
    }

    public void getEvents(WeakReference<ITimetableView> view, Date start, Date end) {
        new DatabaseQuery(view, start, end).execute();
    }

    public void getEventsToday (WeakReference<ITimetableView> view) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime(); //Today
        c.roll(Calendar.DAY_OF_YEAR, 1);
        c.roll(Calendar.SECOND, -1);
        Date end = c.getTime();
        getEvents(view, start, end);
    }

    public void getEventsThisWeek (WeakReference<ITimetableView> view) {
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
        getEvents(view, start, end);
    }

    public static Presenter getInstance() {
        if (_inst == null)
            synchronized (Presenter.class) {
                if (_inst == null)
                    _inst = new Presenter();
            }

        return _inst;
    }

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
            EventDAO dao = Database.instance().getEventDAO();
            if(start == null || end == null)
                return dao.getAllEvents();
            return dao.getEventsBetweenDates(start,end);
        }

        @Override
        protected void onPostExecute(List<Event> result) {
            ITimetableView v = view.get();
            if (v != null) {
                v.onQueryFinished(result);
            }

        }
    }

}
