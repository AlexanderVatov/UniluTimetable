package lu.uni.timetable;

import android.os.AsyncTask;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.avatov.guichetetudiant.OkHttpBackend;

public class Updater {

    public static void update(Date start, Date end) throws GEError {

        //Date timeStarted = Calendar.getInstance().getTime();
        GuichetEtudiant g = new GuichetEtudiant(new OkHttpBackend());
        g.authenticate(Settings.username(), Settings.password());
        List<Event> events = Event.convertGEEventList(
                g.getEvents(
                    start,
                    end,
                    Settings.studyProgramIds()
                )
        );
        EventDAO dao = Database.instance().getEventDAO();
        for (Event e: events) {
            dao.upsert(e); //If an event with the same id already exists, it will be updated
        }
        System.err.println("Updater: Inserted " + events.size() + " events!");


    }

    public static void asyncUpdate(UpdateListener listener, Date start, Date end) {
        new AsyncUpdate(listener, start,end).execute();
    }

    public static void asyncUpdate(UpdateListener listener) {
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
        asyncUpdate(listener, start, end);
    }

    public static class AsyncUpdate extends AsyncTask<Void, Void, Boolean> {
        UpdateListener listener;
        private Date start, end;
        private Exception error;

        public AsyncUpdate(UpdateListener listener, Date startDate, Date endDate) {
            this.listener = listener;
            start = startDate;
            end = endDate;
        }


        @Override
        protected Boolean doInBackground(Void... emptiness) {
            try {
                System.err.println("Updater.ASyncUpdate: Running now...");
                update(start, end);
                return true;
            }
            catch (GEError e) {
                System.err.println("Error while updating: " + e.getMessage());
                error = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            //The boolean parameter seems to be necessary so as to execute onPostExecute
            if(successful) Presenter.getInstance().updatePerformed(start, end);
            if(listener != null) {
                if(successful) listener.onUpdateFinished(start, end);
                else listener.onUpdateError(error);
            }
        }
    }

    public interface UpdateListener {
        void onUpdateFinished(Date startDate, Date endDate);
        void onUpdateError(Exception error);
    }
}
