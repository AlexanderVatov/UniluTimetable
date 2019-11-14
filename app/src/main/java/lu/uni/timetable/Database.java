package lu.uni.timetable;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;

import java.util.Date;

@androidx.room.Database(entities = {Event.class}, version = 1, exportSchema = false)
@androidx.room.TypeConverters({Database.TypeConverters.class})
public abstract class Database extends RoomDatabase {
    public abstract EventDAO getEventDAO();

    public static Database instance() {
        if (db == null) {
            synchronized (Database.class) {
                if (db == null) {
                    db = Room.databaseBuilder(App.getInstance(),
                            Database.class, "database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return db;
    }

    private static volatile Database db;

    public static class TypeConverters {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }
}
