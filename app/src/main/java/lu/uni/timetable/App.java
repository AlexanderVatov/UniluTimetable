package lu.uni.timetable;

import android.app.Application;

public class App extends Application {

    private static App inst;
    private static Settings _settings;

    public App() {
        inst = this;
        //Initialise settings
        _settings = new Settings();
    }

    public static App getInstance() {
        return inst;
    }

    public static Settings getSettings() { return _settings;}


}
