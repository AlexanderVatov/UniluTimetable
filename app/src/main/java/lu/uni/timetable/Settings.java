package lu.uni.timetable;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.List;

import lu.uni.avatov.guichetetudiant.Credentials;

/**
 * Stores and retrieves user settings
 */
public class Settings {
    public static final String PREFERENCE_FILE = "PREFERENCE_FILE";

    public static final String MAIN_UPDATE_NEEDED = "MAIN_UPDATE_NEEDED";
    public static final String USER_LOGGED_IN = "USER_LOGGED_IN";
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


    public static String username() {
        return Credentials.username;
    }

    public static String password() {
        return Credentials.password;
    }

    public static List<String> studyProgramIds() {
        return Collections.singletonList("0001o3032");
    }

}
