package lu.uni.timetable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lu.uni.avatov.guichetetudiant.GEEvent;

@Entity (tableName = "events")
public class Event implements WeekViewDisplayable<Event> {
    public Event() {

    }

    @PrimaryKey @NonNull                 private String id;
    @ColumnInfo(name="start")            private Date start;
    @ColumnInfo(name="end")              private Date end;
    @ColumnInfo(name="title")            private String title;
    @ColumnInfo(name="subject")          private String subject;
    @ColumnInfo(name="subject_id")       private String subjectId;
    @ColumnInfo(name="lecturer")         private String lecturer;
    @ColumnInfo(name="event_type")       private String eventType;
    @ColumnInfo(name="is_canceled")      private boolean isCanceled;
    @ColumnInfo(name="room")             private String room;
    @ColumnInfo(name="main_program_id")  private String mainStudyProgramId;
    @ColumnInfo(name="time_added")       private Date timeAdded;


    public Event(GEEvent e) {
        start = e.start;
        end = e.end;
        id = e.id;
        title = e.title;
        subject = e.subject;
        subjectId = e.subjectId;
        lecturer = e.lecturer;
        eventType = e.eventType;
        isCanceled = e.isCanceled;
        room = e.room;
        mainStudyProgramId = e.mainStudyProgramId;
    }

    @Override
    public WeekViewEvent toWeekViewEvent() {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);


//        WeekViewEvent.Style style = new WeekViewEvent.Style.Builder()
//                .setBackgroundColor(ContextCompat.getColor(App.getInstance(),R.color.colorDefaultEvent))
//                .setTextStrikeThrough(isCanceled)
//                .build();

        return new WeekViewEvent.Builder<Event>(this)
                .setId(Long.parseLong('1' + id.replaceAll("[^0-9]","")))
                .setTitle(title)
                .setStartTime(startCalendar)
                .setEndTime(endCalendar)
                .setLocation(room)
                //.setStyle(style)
                .build();

    }

    public static ArrayList<Event> convertGEEventList(List<GEEvent> in) {
        ArrayList<Event> out = new ArrayList<Event>(in.size());
        for(GEEvent g: in) out.add(new Event(g));
        return out;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getMainStudyProgramId() {
        return mainStudyProgramId;
    }

    public void setMainStudyProgramId(String mainStudyProgramId) {
        this.mainStudyProgramId = mainStudyProgramId;
    }

    public Date getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Date timeAdded) {
        this.timeAdded = timeAdded;
    }
}
