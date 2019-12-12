package lu.uni.timetable;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

/**
 * Constructs an Intent containing all the fields of an event, and extracts these fields from
 * an Intent constructed using this class.
 */
public class EventIntent extends Intent {
    public static final String start = "lu.uni.timetable.start";
    public static final String end = "lu.uni.timetable.end";
    public static final String title = "lu.uni.timetable.title";
    public static final String subject = "lu.uni.timetable.subject";
    public static final String subject_id = "lu.uni.timetable.subject_id";
    public static final String lecturer = "lu.uni.timetable.lecturer";
    public static final String event_type = "lu.uni.timetable.event_type";
    public static final String is_canceled = "lu.uni.timetable.is_canceled";
    public static final String room = "lu.uni.timetable.room";
    public static final String main_program_id = "lu.uni.timetable.main_program_id";
    public static final String time_added = "lu.uni.timetable.time_added";

    /**
     * Constructs an Intent containing all the fields of an event, and extracts these fields from
     * an Intent constructed using this class.
     * @param event The event to be represented
     * @param context The context of the caller
     * @param cls The class used for constructing the Intent
     * @return A non-null intent
     */
    public static Intent newIntent (Event event, Context context, Class<?> cls) {
        Intent i = new Intent(context, cls);
        i.putExtra(start, event.getStart().getTime());
        i.putExtra(end, event.getEnd().getTime());
        i.putExtra(title, event.getTitle());
        i.putExtra(subject, event.getSubject());
        i.putExtra(subject_id, event.getSubjectId());
        i.putExtra(lecturer, event.getLecturer());
        i.putExtra(event_type, event.getEventType());
        i.putExtra(is_canceled, event.isCanceled());
        i.putExtra(room, event.getRoom());
        i.putExtra(main_program_id, event.getMainStudyProgramId());
        //i.putExtra(time_added, event.getTimeAdded().getTime());

        return i;
    }

    /**
     * Extracts the fields of an event from an Intent constructed using this class.
     * @param intent The Intent from which the fields are to be extracted.
     * @return A non-null event
     */
    public static Event getEvent(Intent intent) {
        Event e = new Event();

        e.setStart(new Date(intent.getLongExtra(start, 0)));
        e.setEnd(new Date(intent.getLongExtra(end, 0)));
        e.setTitle(intent.getStringExtra(title));
        e.setSubject(intent.getStringExtra(subject));
        e.setSubjectId(intent.getStringExtra(subject_id));
        e.setLecturer(intent.getStringExtra(lecturer));
        e.setEventType(intent.getStringExtra(event_type));
        e.setCanceled(intent.getBooleanExtra(is_canceled, false));
        e.setRoom(intent.getStringExtra(room));
        e.setMainStudyProgramId(intent.getStringExtra(main_program_id));
        //e.setTimeAdded(new Date(getLongExtra(time_added, 0)));

        return e;
    }
}
