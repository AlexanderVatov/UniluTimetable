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
interface EventDAO {
    @Insert
    void insert(Event... events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Event... events);

    @Update
    void update(Event... events);

    @Delete
    void delete(Event... events);

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(String id);

    @Query("SELECT * FROM events WHERE (start>= :start AND `end` <= :end )")
    List<Event> getEventsBetweenDates(Date start, Date end);

    @Query("SELECT * FROM events WHERE (start <= :moment AND `end` >= :moment)")
    List<Event> getOngoingEventsAt(Date moment);

    @Query("SELECT * FROM events WHERE ((`end` >= :start AND `end` <= :end) OR (start >= :start AND start <= :start))")
    List<Event> getOngoingEventsBetween(Date start, Date end);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();
    
    @Query("DELETE FROM events")
    void deleteAllEvents();
}
