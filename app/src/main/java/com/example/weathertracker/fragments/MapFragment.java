package com.example.weathertracker.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.weathertracker.R;
import com.example.weathertracker.databinding.FragmentMapBinding;
import com.example.weathertracker.entity.CheckInEntity;
import com.example.weathertracker.models.WeatherResponse;
import com.example.weathertracker.preferences.PreferencesManager;
import com.example.weathertracker.viewmodel.CheckInViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private CheckInViewModel viewModel;
    private PreferencesManager preferences;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    /**
     * IMPORTANT: Replace with your actual OpenWeather API Key.
     * Do not commit your real key to public repositories.
     */
    private static final String OPENWEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY_HERE";

    private boolean shouldSaveToRoom = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        viewModel = new ViewModelProvider(this).get(CheckInViewModel.class);
        preferences = PreferencesManager.getInstance(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.fabGps.setOnClickListener(v -> retrieveCurrentGpsLocation(true));
        loadDataStoreSettings();
    }

    private void loadDataStoreSettings() {
        preferences.getSaveLocations().subscribe(save -> shouldSaveToRoom = save);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        loadHistoricalCheckIns();
        checkLocationPermissionsAndInit();

        preferences.getMapType().subscribe(type -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    switch (type) {
                        case "SATELLITE": googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
                        case "TERRAIN": googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); break;
                        case "HYBRID": googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
                        default: googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); break;
                    }
                });
            }
        });
    }

    private void checkLocationPermissionsAndInit() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            googleMap.setMyLocationEnabled(true);
            retrieveCurrentGpsLocation(false);
        }
    }

    private void retrieveCurrentGpsLocation(boolean isFabClick) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    animateCameraToLocation(currentLatLng);
                    handleLocationCheckIn(currentLatLng, isFabClick);
                } else {
                    Toast.makeText(requireContext(), "Location not found. Enable GPS.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void animateCameraToLocation(LatLng point) {
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16f), 1200, null);
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        hideWeatherCard();
        handleLocationCheckIn(point, true);
    }

    private void handleLocationCheckIn(LatLng point, boolean showCard) {
        String address = getAddressFromLatLng(point.latitude, point.longitude);
        String dateTime = new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault()).format(new Date());

        viewModel.fetchWeatherForCoordinates(point.latitude, point.longitude, OPENWEATHER_API_KEY)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        WeatherResponse weather = response.body();
                        CheckInEntity entity;
                        if (response.isSuccessful() && weather != null) {
                            String desc = weather.getWeather().get(0).getDescription();
                            String icon = weather.getWeather().get(0).getIcon();
                            double temp = weather.getMain().getTemp();
                            double feelsLike = weather.getMain().getFeelsLike();
                            int humidity = weather.getMain().getHumidity();
                            double wind = weather.getWind().getSpeed();

                            entity = new CheckInEntity(point.latitude, point.longitude, address, dateTime, temp, desc, icon, feelsLike, humidity, wind);
                            showWeatherCard(entity, icon, feelsLike, humidity, wind);
                        } else {
                            entity = new CheckInEntity(point.latitude, point.longitude, address, dateTime, 0.0, "Weather Unavailable", "", 0.0, 0, 0.0);
                            if (showCard) showWeatherCard(entity, null, 0, 0, 0);
                        }
                        
                        if (shouldSaveToRoom && showCard) {
                            viewModel.insert(entity);
                        }
                        updateMapMarker(entity);
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        CheckInEntity entity = new CheckInEntity(point.latitude, point.longitude, address, dateTime, 0.0, "Offline", "", 0.0, 0, 0.0);
                        updateMapMarker(entity);
                        if (showCard) showWeatherCard(entity, null, 0, 0, 0);
                    }
                });
    }

    private void updateMapMarker(CheckInEntity item) {
        if (googleMap == null) return;
        
        LatLng point = new LatLng(item.getLatitude(), item.getLongitude());
        
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(point)
                .title(item.getAddress())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        
        if (marker != null) {
            marker.setTag(item);
        }
    }

    private void showWeatherCard(CheckInEntity item, String iconCode, double feelsLike, int humidity, double wind) {
        binding.tvLocationName.setText(item.getAddress());
        binding.tvDateTime.setText(item.getDateTime());
        binding.tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", item.getTemperature()));
        binding.tvCondition.setText(item.getWeatherDescription().toUpperCase());
        binding.tvFeelsLike.setText(String.format(Locale.getDefault(), "Feels: %.1f°C", feelsLike));
        binding.tvHumidity.setText(String.format(Locale.getDefault(), "Hum: %d%%", humidity));
        binding.tvWindSpeed.setText(String.format(Locale.getDefault(), "Wind: %.1f m/s", wind));

        if (iconCode != null) {
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            Glide.with(this).load(iconUrl).into(binding.ivWeatherIcon);
        }

        binding.btnDeleteCheckpoint.setOnClickListener(v -> {
            viewModel.delete(item);
            hideWeatherCard();
            googleMap.clear();
            loadHistoricalCheckIns();
        });

        if (binding.weatherCard.getVisibility() != View.VISIBLE) {
            binding.weatherCard.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            binding.weatherCard.startAnimation(slideUp);
        }
    }

    private void hideWeatherCard() {
        if (binding.weatherCard.getVisibility() == View.VISIBLE) {
            Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation) {
                    binding.weatherCard.setVisibility(View.GONE);
                }
                @Override public void onAnimationRepeat(Animation animation) {}
            });
            binding.weatherCard.startAnimation(slideDown);
        }
    }

    private void loadHistoricalCheckIns() {
        viewModel.getAllCheckIns().observe(getViewLifecycleOwner(), list -> {
            if (googleMap != null && list != null) {
                for (CheckInEntity item : list) {
                    updateMapMarker(item);
                }
            }
        });
    }

    private String getAddressFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String result = address.getThoroughfare();
                if (result == null) result = address.getLocality();
                if (result == null) result = address.getCountryName();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        CheckInEntity item = (CheckInEntity) marker.getTag();
        if (item != null) {
            showWeatherCard(item, item.getIconCode(), item.getFeelsLike(), item.getHumidity(), item.getWindSpeed());
            animateCameraToLocation(marker.getPosition());
            return true;
        }
        return false;
    }
}
