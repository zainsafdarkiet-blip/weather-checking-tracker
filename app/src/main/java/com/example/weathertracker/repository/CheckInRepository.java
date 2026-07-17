package com.example.weathertracker.repository;
import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.weathertracker.dao.CheckInDao;
import com.example.weathertracker.database.CheckInDatabase;
import com.example.weathertracker.entity.CheckInEntity;
import com.example.weathertracker.retrofit.RetrofitClient;
import com.example.weathertracker.retrofit.WeatherApiService;
import com.example.weathertracker.models.WeatherResponse;
import java.util.List;
import retrofit2.Call;
public class CheckInRepository {
    private final CheckInDao checkInDao;
    private final LiveData<List<CheckInEntity>> allCheckIns;
    private final WeatherApiService apiService;
    public CheckInRepository(Application application) {
        CheckInDatabase db = CheckInDatabase.getDatabase(application);
        checkInDao = db.checkInDao();
        allCheckIns = checkInDao.getAllCheckIns();
        apiService = RetrofitClient.getApiService();
    }
    public LiveData<List<CheckInEntity>> getAllCheckIns() {
        return allCheckIns;
    }
    public void insert(CheckInEntity checkIn) {
        CheckInDatabase.databaseWriteExecutor.execute(() -> {
            checkInDao.insertCheckIn(checkIn);
        });
    }
    public void delete(CheckInEntity checkIn) {
        CheckInDatabase.databaseWriteExecutor.execute(() -> {
            checkInDao.deleteCheckIn(checkIn);
        });
    }
    public void deleteAll() {
        CheckInDatabase.databaseWriteExecutor.execute(() -> {
            checkInDao.deleteAllCheckIns();
        });
    }
    public Call<WeatherResponse> fetchWeather(double lat, double lon, String apiKey) {
        return apiService.getCurrentWeather(lat, lon, apiKey, "metric");
    }
}
