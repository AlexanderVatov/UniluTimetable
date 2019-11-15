package lu.uni.timetable;

import java.util.Date;
import java.util.List;

public interface ITimetableView {
    void onDatabaseUpdated(Date start, Date end);
    void onQueryFinished(List<Event> events);
}
