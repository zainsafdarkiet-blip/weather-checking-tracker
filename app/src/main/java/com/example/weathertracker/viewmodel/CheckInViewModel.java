package com.example.weathertracker.viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.weathertracker.entity.CheckInEntity;
import com.example.weathertracker.repository.CheckInRepository;
import com.example.weathertracker.models.WeatherResponse;
import java.util.List;
import retrofit2.Call;
public class CheckInViewModel extends AndroidViewModel {
    private final CheckInRepository repository;
    private final LiveData<List<CheckInEntity>> allCheckIns;

    public CheckInViewModel(@NonNull Application application) {
        super(application);
        repository = new CheckInRepository(application);
        allCheckIns = repository.getAllCheckIns();
    }
    public LiveData<List<CheckInEntity>> getAllCheckIns() {
        return allCheckIns;
    }
    public void insert(CheckInEntity checkIn) {
        repository.insert(checkIn);
    }
    public void delete(CheckInEntity checkIn) {
        repository.delete(checkIn);
    }
    public void deleteAll() {
        repository.deleteAll();
    }
    public Call<WeatherResponse> fetchWeatherForCoordinates(double lat, double lon, String apiKey) {
        return repository.fetchWeather(lat, lon, apiKey);
    }
}
