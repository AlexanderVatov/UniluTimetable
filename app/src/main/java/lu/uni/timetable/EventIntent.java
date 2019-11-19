package lu.uni.timetable;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

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
    
    public EventIntent(Event event, Context context, Class<?> cls) {
        super(context, cls);
        putExtra(start, event.getStart().getTime());
        putExtra(end, event.getEnd().getTime());
        putExtra(title, event.getTitle());
        putExtra(subject, event.getSubject());
        putExtra(subject_id, event.getSubjectId());
        putExtra(lecturer, event.getLecturer());
        putExtra(event_type, event.getEventType());
        putExtra(is_canceled, event.isCanceled());
        putExtra(room, event.getRoom());
        putExtra(main_program_id, event.getMainStudyProgramId());
        //putExtra(time_added, event.getTimeAdded().getTime());
    }

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
