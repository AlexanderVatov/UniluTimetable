package lu.uni.timetable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;
import java.util.Date;

import lu.uni.avatov.guichetetudiant.GEEvent;

@Entity (tableName = "events")
public class Event implements WeekViewDisplayable<Event> {

    public Event() {

    }

    @PrimaryKey @NonNull                  public String id;
    @ColumnInfo(name = "start")           public Date start;
    @ColumnInfo(name = "end")             public Date end;
    @ColumnInfo(name = "title")           public String title;
    @ColumnInfo(name = "subject")         public String subject;
    @ColumnInfo(name = "subject_id")      public String subjectId;
    @ColumnInfo(name = "lecturer")        public String lecturer;
    @ColumnInfo(name = "event_type")      public String eventType;
    @ColumnInfo(name = "is_canceled")     public boolean isCanceled;
    @ColumnInfo(name = "room")            public String room;
    @ColumnInfo(name = "main_program_id") public String mainStudyProgramId;
    @ColumnInfo(name = "time_added")      public Date timeAdded;
    @ColumnInfo(name = "calendar_id")     public Long calendarId;


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
    public WeekViewEvent<Event> toWeekViewEvent() {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);


//        WeekViewEvent.Style style = new WeekViewEvent.Style.Builder()
//                .setBackgroundColor(ContextCompat.getColor(App.getInstance(),R.color.colorDefaultEvent))
//                .setTextStrikeThrough(isCanceled)
//                .build();

        return new WeekViewEvent.Builder<>(this)
                .setId(Long.parseLong('1' + id.replaceAll("[^0-9]", "")))
                .setTitle(title)
                .setStartTime(startCalendar)
                .setEndTime(endCalendar)
                .setLocation(room)
                //.setStyle(style)
                .build();

    }

}