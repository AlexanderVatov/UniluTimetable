package lu.uni.timetable;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

/**
 * Stores and retrieves user settings
 */
public class Settings {
    public static final String PREFERENCE_FILE = "PREFERENCE_FILE";
    public static final String ENCRYPTED_PREFERENCE_FILE = "ENCRYPTED_PREFERENCE_FILE";

    public static final String MAIN_UPDATE_NEEDED = "MAIN_UPDATE_NEEDED";
    public static final String USER_LOGGED_IN = "USER_LOGGED_IN";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String STUDY_PROGRAMS = "STUDY_PROGRAMS";


    private static SharedPreferences prefs, encPrefs;
    public Settings() {
    }

    public static SharedPreferences preferences() {
        if(prefs == null) {
            prefs = App
                    .getInstance()
                    .getSharedPreferences(Settings.PREFERENCE_FILE, Context.MODE_PRIVATE);
        }

        return prefs;
    }

    public static SharedPreferences encryptedPreferences() {
        if(encPrefs == null) {
            try {
                encPrefs = EncryptedSharedPreferences.create(
                        ENCRYPTED_PREFERENCE_FILE,
                        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                        App.getInstance(),
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (Exception e) {
                System.err.println("An exception occurred while obtaining the encrypted shared preferences:");
                e.printStackTrace();
            }
        }
        return encPrefs;
    }

    public static void deleteUserData() {
        Database.instance().getEventDAO().deleteAllEvents();
    }

}
