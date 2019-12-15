package lu.uni.timetable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface EventDAO {
    @Insert
    public void insert(Event... events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void upsert(Event... events);

    @Update
    public void update(Event... events);

    @Delete
    public void delete(Event... events);

    @Query("SELECT * FROM events WHERE id = :id")
    public Event getEventById(String id);

    @Query("SELECT * FROM events WHERE (start>= :start AND `end` <= :end )")
    public List<Event> getEventsBetweenDates(Date start, Date end);

    @Query("SELECT * FROM events WHERE (start <= :moment AND `end` >= :moment)")
    public List<Event> getOngoingEventsAt(Date moment);

    @Query("SELECT * FROM events WHERE ((`end` >= :start AND `end` <= :end) OR (start >= :start AND start <= :start))")
    public List<Event> getOngoingEventsBetween(Date start, Date end);

    @Query("SELECT * FROM events")
    public List<Event> getAllEvents();
}
