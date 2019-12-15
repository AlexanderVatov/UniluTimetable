package lu.uni.timetable;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;

/**
 * Performs a database update. This can be done either synchronously using {@link #update(Date, Date)}
 * (this should of corse never be done in the UI thread) or asynchronously using
 * {@link #asyncUpdate(UpdateListener, Date, Date)}. In the latter case, an UpdateListener is
 * required to be passed. This does not cause a memory leak as the reference is destroyed as soon as
 * the update is complete (or fails). When the update completes successfully,
 * {@link UpdateListener#onUpdateFinished(Date, Date)} will be called; in case of error,
 * {@link UpdateListener#onUpdateError(Exception)} will be called instead.
 */

public class Updater {

    /**
     * Perform a synchronous database update on a given date range. This should never be called from
     * the UI thread.
     * @param start The start of the date range.
     * @param end The end of the date range.
     * @throws GEError In case of any error in {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant}.
     */
    public static void update(Date start, Date end) throws GEError {

        //Date timeStarted = Calendar.getInstance().getTime();
        GuichetEtudiant g = App.guichetEtudiant();
        SharedPreferences prefs = Settings.encryptedPreferences();
        String username = prefs.getString(Settings.USERNAME, "");
        String password = prefs.getString(Settings.PASSWORD, "");
        if(username.isEmpty() || password.isEmpty()) {
            System.err.println("Blank credentials returned by EncryptedSharedPreferences!");
            return;
        }
        if(!g.isAuthenticated())
            g.authenticate(username, password);
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

    /**
     * Asynchronously performs the first update after a user has logged in.
     */
    public static void firstUpdate(UpdateListener listener) {
        asyncUpdate(listener,
                Utils.firstDayOfMonth(Utils.Month.LAST_MONTH),
                Utils.lastDayOfMonth(Utils.Month.NEXT_MONTH));
    }
    /**
     * Perform an asynchronous database update on a given date range.
     * @param start The start of the date range.
     * @param end The end of the date range.
     * @throws GEError In case of any error in {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant}.
     */
    public static void asyncUpdate(UpdateListener listener, Date start, Date end) {
        System.err.println("Updater: update requested between " + start + " and " + end);
        new AsyncUpdate(listener, start,end).execute();
    }

//    public static void asyncUpdate(UpdateListener listener) {
//        Date start = Utils.firstDayOfMonth(Utils.Month.CURRENT_MONTH);
//        Date end = Utils.lastDayOfMonth(Utils.Month.NEXT_MONTH);
//        asyncUpdate(listener, start, end);
//    }


    /**
     * Used by {@link UpdateListener#asyncUpdate(UpdateListener, Date, Date)}. Do not use directly.
     */
    private static class AsyncUpdate extends AsyncTask<Void, Void, Boolean> {
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
