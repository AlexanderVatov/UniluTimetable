package lu.uni.timetable;

import android.os.AsyncTask;

import androidx.collection.ArraySet;

import java.lang.ref.WeakReference;
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
        System.err.println("Update performed! Number of views: " + views.size());

        for (WeakReference<ITimetableView> viewWeakRef : views) {
            ITimetableView view = viewWeakRef.get();
            if (view != null) {
                view.onDatabaseUpdate(start, end);
                System.err.println("Notifying " + view);
            }
            else System.err.println("View is null!");


        }
    }

    public void getEvents(Date start, Date end) {
        new DatabaseQuery(start, end).execute();
    }

    public static Presenter getInstance() {
//        if (_inst == null)
//            synchronized (Presenter.class) {
                if (_inst == null)
                    _inst = new Presenter();
            //}

        return _inst;
    }

    public class DatabaseQuery extends AsyncTask<Void, Void, List<Event>> {
        private Date start, end;

        public DatabaseQuery(){

        };

        public DatabaseQuery(Date start, Date end) {
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
            for (WeakReference<ITimetableView> viewWeakRef : views) {
                ITimetableView view = viewWeakRef.get();
                if (view != null) {
                    //TODO: Notify each view of its own query
                    view.queryFinished(result);
                }
            }
        }
    }

}
