package com.example.weathertracker.database;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.weathertracker.dao.CheckInDao;
import com.example.weathertracker.entity.CheckInEntity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = {CheckInEntity.class}, version = 2, exportSchema = false)
public abstract class CheckInDatabase extends RoomDatabase {
    public abstract CheckInDao checkInDao();
    private static volatile CheckInDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = 
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static CheckInDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CheckInDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CheckInDatabase.class, "checkin_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
