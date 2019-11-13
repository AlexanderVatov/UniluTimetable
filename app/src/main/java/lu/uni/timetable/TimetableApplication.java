package lu.uni.timetable;

import android.app.Application;

public class TimetableApplication extends Application {

    private Presenter presenter;
    private static TimetableApplication inst;

    public TimetableApplication() {
        inst = this;
        presenter = new Presenter();
    }

    public static TimetableApplication getInstance() {
        return inst;
    }

    public static Presenter getPresenter() {
        return inst.presenter;
    }



}
