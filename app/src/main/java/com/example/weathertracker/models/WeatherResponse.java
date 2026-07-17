package com.example.weathertracker.models;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class WeatherResponse {
    @SerializedName("main")
    private MainData main;
    @SerializedName("weather")
    private List<WeatherDetail> weather;
    @SerializedName("wind")
    private WindData wind;
    @SerializedName("name")
    private String name;
    public MainData getMain() { return main; }
    public List<WeatherDetail> getWeather() { return weather; }
    public WindData getWind() { return wind; }
    public String getName() { return name; }
    public static class MainData {
        @SerializedName("temp")
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        @SerializedName("humidity")
        private int humidity;
        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
    }
    public static class WeatherDetail {
        @SerializedName("description")
        private String description;
        @SerializedName("icon")
        private String icon;
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }
    public static class WindData {
        @SerializedName("speed")
        private double speed;
        public double getSpeed() { return speed; }
    }
}
