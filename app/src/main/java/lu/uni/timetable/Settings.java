package lu.uni.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.util.Calendar;
import java.util.List;

/**
 * Stores and retrieves user settings
 */
public class Settings {
    public static final String PREFERENCE_FILE = "PREFERENCE_FILE";
    public static final String ENCRYPTED_PREFERENCE_FILE = "ENCRYPTED_PREFERENCE_FILE";

    public static final String VISIBLE_DAYS_LANDSCAPE = "VISIBLE_DAYS_LANDSCAPE";
    public static final String VISIBLE_DAYS_PORTRAIT = "VISIBLE_DAYS_PORTRAIT";
    public static final String MAIN_UPDATE_NEEDED = "MAIN_UPDATE_NEEDED";
    public static final String USER_LOGGED_IN = "USER_LOGGED_IN";
    public static final String USERNAME = "USERNAME"; //Stored in encryptedPreferences
    public static final String PASSWORD = "PASSWORD"; //Stored in encryptedPreferences
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
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... emptiness) {
                EventDAO dao = Database.instance().getEventDAO();
                List<Event> futureCalendarEvents = dao.getCalendarEventsAfter(Calendar.getInstance().getTime());
                CalendarSync.deleteEvents(futureCalendarEvents);

                System.err.println("Events in database: " + dao.numberOfEvents());
                dao.deleteAllEvents();
                System.err.println("Events in database: " + dao.numberOfEvents());

                return null;
            }

            @Override
            protected void onPostExecute(Void emptiness) {
                SharedPreferences.Editor e = preferences().edit();
                e.putBoolean(USER_LOGGED_IN, false);
                e.remove(STUDY_PROGRAMS);
                e.apply();

                encryptedPreferences().edit().clear().apply();
                Widget.update();
            }
        }.execute();

    }

}
