# Weather Tracker Android App

A comprehensive Android application designed to help users monitor real-time weather conditions based on their GPS location.

## Features

*   **Real-time Weather**: Fetch current weather data using OpenWeather API.
*   **Interactive Map**: Integrated Google Maps to view your location and check-ins.
*   **Location Check-ins**: Save your current weather and location as a "check-in".
*   **History**: View a list of your historical check-ins stored locally using Room Database.
*   **Customizable Settings**: Choose between different map types (Normal, Satellite, Terrain, Hybrid) and toggle auto-saving of locations.
*   **Material Design**: Clean and modern user interface following Material 3 guidelines.

## Technologies Used

*   **Language**: Java
*   **UI**: XML Layouts, Material Design 3, View Binding
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Database**: Room Persistence Library
*   **Networking**: Retrofit 2, Gson
*   **Location**: Google Play Services (FusedLocationProviderClient)
*   **Maps**: Google Maps SDK for Android
*   **Image Loading**: Glide
*   **Reactive Programming**: RxJava 3 (for Preferences)

## Setup Instructions

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/weather-checking-tracker.git
    ```
2.  **API Keys**:
    *   Obtain a **Google Maps API Key** from the [Google Cloud Console](https://console.cloud.google.com/).
    *   Obtain an **OpenWeather API Key** from [OpenWeatherMap](https://openweathermap.org/api).
3.  **Configure API Keys**:
    *   Replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/AndroidManifest.xml`.
    *   Replace `YOUR_OPENWEATHER_API_KEY` in `app/src/main/java/com/example/weathertracker/fragments/MapFragment.java`.
4.  **Build and Run**:
    *   Open the project in Android Studio.
    *   Sync Gradle and run the app on an emulator or physical device.

## Screenshots

*(Add your screenshots here)*

## Author

*   **Muhammad Zain** - *FA21-BSE-024* - Air University Karachi Campus
