package lu.uni.timetable;

import java.util.Date;
import java.util.List;

public interface ITimetableView {
    void onDatabaseUpdated(Date startOfUpdatedPeriod, Date endOfUpdatedPeriod);
    void onEventsReady(List<Event> events);
}
