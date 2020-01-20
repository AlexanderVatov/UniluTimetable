package lu.uni.timetable;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lu.uni.avatov.guichetetudiant.GEAuthenticationError;
import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GEEvent;
import lu.uni.avatov.guichetetudiant.GEStudyProgram;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;

/**
 * Performs a database update. This can be done either synchronously using {@link #update(Date, Date)}
 * (this should of course never be done in the UI thread) or asynchronously using
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

        GuichetEtudiant g = App.guichetEtudiant();
        if(!g.isAuthenticated()) {

            //Date timeStarted = Calendar.getInstance().getTime();
            SharedPreferences encryptedPreferences = Settings.encryptedPreferences();
            String username = encryptedPreferences.getString(Settings.USERNAME, "");
            String password = encryptedPreferences.getString(Settings.PASSWORD, "");

            if(username.isEmpty() || password.isEmpty()) {
                throw new GEAuthenticationError("Blank credentials returned by EncryptedSharedPreferences!");
            }
            g.authenticate(username, password);
        }


        SharedPreferences preferences = Settings.preferences();
        Set<String> studyProgramIds = preferences.getStringSet(Settings.STUDY_PROGRAMS, null);
        if(studyProgramIds == null) {
            System.err.println("Study programs not recorded. Fetching them...");
            List<GEStudyProgram> list = g.getStudyPrograms();
            studyProgramIds = new HashSet<>();
            for(GEStudyProgram p: list) {
                if (p.isMain) {
                    studyProgramIds.add(p.id);
                }
            }
            preferences.edit().putStringSet(Settings.STUDY_PROGRAMS, studyProgramIds).apply();
        }

        List<GEEvent> geEvents = g.getEvents(start, end, new ArrayList<>(studyProgramIds));
        List<Event> dbEvents = Presenter.synchronousRequestEvents(start, end);
        HashMap<String,Event> dbEventsMap = new HashMap<>();
        for(Event e: dbEvents)
            dbEventsMap.put(e.id, e);

        Collection<Event> createdEvents = new ArrayList<>(), modifiedEvents = new ArrayList<>(), cancelledEvents;
        for(GEEvent geEvent: geEvents) {

            //Fetch event from the hashmap, and remove it from there. Whatever remains at the end of
            //the loop are events which are no longer there on the Guichet Étudiant.
            Event dbEvent = dbEventsMap.remove(geEvent.id);
            if(dbEvent == null) //Event not present in the database; it is newly created
                createdEvents.add(new Event(geEvent));
            else {
                Object modified = null; //Used to tell whether any modification was made

                if(!dbEvent.start.equals(geEvent.start))
                    modified = (dbEvent.start = geEvent.start);
                if(!dbEvent.end.equals(geEvent.end))
                    modified = (dbEvent.end = geEvent.end);
                if(!dbEvent.title.equals(geEvent.title))
                    modified = (dbEvent.title = geEvent.title);
                if(!dbEvent.subject.equals(geEvent.subject))
                    modified = (dbEvent.subject = geEvent.subject);
                if(!dbEvent.subjectId.equals(geEvent.subjectId))
                    modified = (dbEvent.subjectId = geEvent.subjectId);
                if(!dbEvent.lecturer.equals(geEvent.lecturer))
                    modified = (dbEvent.lecturer = geEvent.lecturer);
                if(!dbEvent.eventType.equals(geEvent.eventType))
                    modified = (dbEvent.eventType = geEvent.eventType);
                if(!dbEvent.room.equals(geEvent.room))
                    modified = (dbEvent.room = geEvent.room);
                if(!dbEvent.mainStudyProgramId.equals(geEvent.mainStudyProgramId))
                    modified = (dbEvent.mainStudyProgramId = geEvent.mainStudyProgramId);

                //If any modification was made, add event to the list of modified events
                if(modified != null) modifiedEvents.add(dbEvent);
            }
        }
        cancelledEvents = dbEventsMap.values();
        for(Event e: cancelledEvents)
            e.isCanceled = true;

        CalendarSync.update(start,end,createdEvents, modifiedEvents, cancelledEvents);

        System.err.println("Updater: " + createdEvents.size() + " new, "
                        + modifiedEvents.size() + " modified, "
                        + cancelledEvents.size()+ " cancelled.");

        EventDAO dao = Database.instance().getEventDAO();
        modifiedEvents.addAll(cancelledEvents);
        dao.insertAndUpdate(createdEvents,modifiedEvents);
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
