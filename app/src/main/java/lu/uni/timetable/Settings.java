package lu.uni.timetable;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.util.Collections;
import java.util.List;

/**
 * Stores and retrieves user settings
 */
public class Settings {
    public static final String PREFERENCE_FILE = "PREFERENCE_FILE";

    public static final String MAIN_UPDATE_NEEDED = "MAIN_UPDATE_NEEDED";
    public static final String USER_LOGGED_IN = "USER_LOGGED_IN";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";


    private static EncryptedSharedPreferences encPrefs;
    public Settings() {
//        //Initialise settings
//        System.err.println("Settings constructor running...");
//        App app = App.getInstance();
//        System.err.println("App instance obtained: " + app);
//        //prefs = app.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
//        System.err.println("Settings constructor finished.");
    }

    public static SharedPreferences preferences() {
        return App
                .getInstance()
                .getSharedPreferences(Settings.PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public static SharedPreferences encryptedPreferences() {
        SharedPreferences p = null;
        if(encPrefs == null) {
            try {
                p = EncryptedSharedPreferences.create(
                        "secret_shared_prefs",
                        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                        App.getInstance(),
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (Exception e) {
                System.err.println("An exception occurred while obtaining the encrypted shared preferences:");
                e.printStackTrace();
            }
        }
        return p;
    }


    public static String username() {
        return Settings.encryptedPreferences().getString(Settings.USERNAME, "");
    }

    public static String password() {
        System.err.println("Decrypting password...");
        return encryptedPreferences().getString(PASSWORD, "");
    }

    public static List<String> studyProgramIds() {
        return Collections.singletonList("0001o3032");
    }

}
