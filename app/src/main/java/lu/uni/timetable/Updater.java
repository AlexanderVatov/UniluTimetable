package lu.uni.timetable;

import android.os.AsyncTask;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.avatov.guichetetudiant.OkHttpBackend;

public class Updater {

    public static void update(Date start, Date end) {
        try {
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
        catch (GEError e) {
            System.err.println("An error occurred during update: " + e.getMessage());
        }
    }

    public static void asyncUpdate(Date start, Date end) {
        new AsyncUpdate(start,end).execute();
    }

    public static void asyncUpdate() {
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
        asyncUpdate(start, end);
    }

    public static class AsyncUpdate extends AsyncTask<Void, Void, Boolean> {
        private Date start, end;

        public AsyncUpdate(Date startDate, Date endDate) {
            start = startDate;
            end = endDate;
        }


        @Override
        protected Boolean doInBackground(Void... emptiness) {
            System.err.println("Updater.ASyncUpdate: Running now...");
            update(start, end);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean params) {
            Presenter.getInstance().updatePerformed(start, end);
        }
    }
}
