package lu.uni.timetable;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

public class Presenter {


    private Presenter _inst;

    public void register(WeakReference<ITimetableView> view) {
        this.view = view;
    }

    private WeakReference<ITimetableView> view;

    public void updatePerformed(Date start, Date end) {
        ITimetableView v = view.get();
        if(v != null) v.onDatabaseUpdate(start, end);
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
            ITimetableView v = view.get();
            if (v != null) v.queryFinished(result);
        }
    }

    public void query(Date start, Date end) {
        new DatabaseQuery(start, end).execute();
    }
}
