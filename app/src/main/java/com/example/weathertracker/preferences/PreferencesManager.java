package com.example.weathertracker.preferences;
import android.content.Context;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
public class PreferencesManager {
    private static final String DATASTORE_NAME = "settings_preferences";
    private static final Preferences.Key<Boolean> KEY_SAVE_LOCATIONS = PreferencesKeys.booleanKey("save_locations");
    private static final Preferences.Key<Boolean> KEY_DARK_MODE = PreferencesKeys.booleanKey("dark_mode");
    private static final Preferences.Key<String> KEY_MAP_TYPE = PreferencesKeys.stringKey("map_type");
    private final RxDataStore<Preferences> dataStore;
    private static PreferencesManager instance;
    private PreferencesManager(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context.getApplicationContext(), DATASTORE_NAME).build();
    }
    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }
    public Flowable<Boolean> getSaveLocations() {
        return dataStore.data().map(prefs -> {
            Boolean val = prefs.get(KEY_SAVE_LOCATIONS);
            return val != null ? val : true;
        });
    }
    public void setSaveLocations(boolean save) {
        dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_SAVE_LOCATIONS, save);
            return Single.just(mutable);
        });
    }
    public Flowable<Boolean> getDarkMode() {
        return dataStore.data().map(prefs -> {
            Boolean val = prefs.get(KEY_DARK_MODE);
            return val != null ? val : false;
        });
    }
    public void setDarkMode(boolean enabled) {
        dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_DARK_MODE, enabled);
            return Single.just(mutable);
        });
    }
    public Flowable<String> getMapType() {
        return dataStore.data().map(prefs -> {
            String val = prefs.get(KEY_MAP_TYPE);
            return val != null ? val : "NORMAL";
        });
    }
    public void setMapType(String mapType) {
        dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_MAP_TYPE, mapType);
            return Single.just(mutable);
        });
    }
}
