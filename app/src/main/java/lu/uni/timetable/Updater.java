package lu.uni.timetable;

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
                dao.insert(e); //If an event with the same id already exists, it will be updated
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
        Date start = c.getTime(); //Today
        c.roll(Calendar.WEEK_OF_YEAR, 2);
        Date end = c.getTime();
        asyncUpdate(start, end);
    }

}
