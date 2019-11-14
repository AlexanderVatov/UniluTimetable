package lu.uni.timetable;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@androidx.room.Database(entities = {Event.class}, version = 1, exportSchema = false)
@TypeConverters({RoomTypeConverters.class})
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
}
