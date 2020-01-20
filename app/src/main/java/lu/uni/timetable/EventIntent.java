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
        i.putExtra(start, event.start.getTime());
        i.putExtra(end, event.end.getTime());
        i.putExtra(title, event.title);
        i.putExtra(subject, event.subject);
        i.putExtra(subject_id, event.subjectId);
        i.putExtra(lecturer, event.lecturer);
        i.putExtra(event_type, event.eventType);
        i.putExtra(is_canceled, event.isCanceled);
        i.putExtra(room, event.room);
        i.putExtra(main_program_id, event.mainStudyProgramId);
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

        e.start = new Date(intent.getLongExtra(start, 0));
        e.end = new Date(intent.getLongExtra(end, 0));
        e.title = intent.getStringExtra(title);
        e.subject = intent.getStringExtra(subject);
        e.subjectId = intent.getStringExtra(subject_id);
        e.lecturer = intent.getStringExtra(lecturer);
        e.eventType = intent.getStringExtra(event_type);
        e.isCanceled = intent.getBooleanExtra(is_canceled, false);
        e.room = intent.getStringExtra(room);
        e.mainStudyProgramId = intent.getStringExtra(main_program_id);
        //e.setTimeAdded(new Date(getLongExtra(time_added, 0)));

        return e;
    }
}
