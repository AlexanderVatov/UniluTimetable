package lu.uni.timetable;

import android.app.Application;

public class App extends Application {

    private static App inst;

    public App() {
        inst = this;
    }

    public static App getInstance() {
        return inst;
    }




}
