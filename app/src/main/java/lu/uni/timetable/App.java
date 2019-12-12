package lu.uni.timetable;

import android.app.Application;

import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.avatov.guichetetudiant.OkHttpBackend;

/**
 * This class is used to initialise global resources, such as the app settings and an instance of
 * {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant}, and to provide an Android context in
 * situations where a global context is appropriate, for example for database accesses, locale data,
 * etc. In such contexts, an instance of this class may be used directly since Application inherits
 * from Context.
 *
 * An instance of this class is created before any other classes of the application are instantiated,
 * and this instance persists through the entire lifecycle of the application.
 */

public class App extends Application {

    private static App inst;
    private static Settings _settings;
    private static GuichetEtudiant _guichet;

    public App() {
        inst = this;
        //Initialise settings
        _settings = new Settings();
    }

    /**
     * Returns the global instance of this class.
     *
     * @return A non-null reference to an App
     */
    public static App getInstance() {
        return inst;
    }

    /**
     * Returns the global instance of {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant}, creating
     * it if it does not already exist.
     *
     * @return A non-null reference to a {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant}.
     */
    public static GuichetEtudiant guichetEtudiant() {
        if (_guichet != null) _guichet = new GuichetEtudiant(new OkHttpBackend());
        return _guichet;
    }

    /**
     * Returns the global instance of {@link lu.uni.timetable.Settings}, creating it if it does not
     * already exist.
     *
     * @return A non-null reference to a {@link lu.uni.timetable.Settings}.
     */
    public static Settings getSettings() { return _settings;}


}
