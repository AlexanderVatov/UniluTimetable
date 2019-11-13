package lu.uni.timetable;

import java.util.Date;
import java.util.List;

public interface ITimetableView {
    void onDatabaseUpdate(Date start, Date end);
    void queryFinished(List<Event> events);
}
