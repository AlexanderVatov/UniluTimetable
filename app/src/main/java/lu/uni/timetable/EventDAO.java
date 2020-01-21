package lu.uni.timetable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Dao
public abstract class EventDAO {
    @Insert
    public abstract void insert(Event... events);

    @Insert
    public abstract void insertAll(Collection<Event> events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsert(Event... events);

    @Update
    public abstract void update(Event... events);

    @Update
    public abstract void updateAll(Collection<Event> events);

    @Transaction
    public void insertAndUpdate(Collection<Event> toInsert, Collection<Event> toUpdate) {
        insertAll(toInsert);
        updateAll(toUpdate);
    }

    @Delete
    public abstract void delete(Event... events);

    @Query("SELECT * FROM events WHERE id = :id")
    public abstract Event getEventById(String id);

    @Query("SELECT * FROM events WHERE (start>= :start AND `end` <= :end )")
    public abstract List<Event> getEventsBetweenDates(Date start, Date end);

    @Query("SELECT * FROM events WHERE (start <= :moment AND `end` >= :moment) ORDER BY `end`")
    public abstract List<Event> getOngoingEventsAt(Date moment);

    @Query("SELECT * FROM events WHERE ((`end` >= :start AND `end` <= :end) OR (start >= :start AND start <= :start)) ORDER BY `end`")
    public abstract List<Event> getOngoingEventsBetween(Date start, Date end);

    @Query("SELECT * FROM events WHERE start >= :date AND calendar_id IS NOT NULL")
    public abstract List<Event> getCalendarEventsAfter(Date date);

    @Query("SELECT * FROM events")
    public abstract List<Event> getAllEvents();
    
    @Query("DELETE FROM events")
    public abstract void deleteAllEvents();

    @Query("SELECT COUNT(*) FROM events")
    public abstract int numberOfEvents();

}
