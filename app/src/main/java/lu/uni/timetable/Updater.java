package lu.uni.timetable;

import android.os.AsyncTask;

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
        System.err.println("Updater: update requested between " + start + " and " + end);
        new AsyncUpdate(listener, start,end).execute();
    }

    public static void asyncUpdate(UpdateListener listener) {
        Date start = Utils.firstDayOfMonth(Utils.Month.CURRENT_MONTH);
        Date end = Utils.lastDayOfMonth(Utils.Month.NEXT_MONTH);
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
