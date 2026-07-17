package com.example.weathertracker.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.weathertracker.entity.CheckInEntity;
import java.util.List;
@Dao
public interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCheckIn(CheckInEntity checkIn);
    @Delete
    void deleteCheckIn(CheckInEntity checkIn);
    @Query("SELECT * FROM checkins ORDER BY id DESC")
    LiveData<List<CheckInEntity>> getAllCheckIns();
    @Query("DELETE FROM checkins")
    void deleteAllCheckIns();
}

