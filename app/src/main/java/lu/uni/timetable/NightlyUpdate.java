package lu.uni.timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

import lu.uni.avatov.guichetetudiant.GEError;

public class NightlyUpdate extends Service {

    public static final int ALARM_JITTER = 5400000; //1.5h in milliseconds

    private final IBinder binder = new NightlyUpdate.Binder();

    public class Binder extends android.os.Binder {
        NightlyUpdate getService() {
            return NightlyUpdate.this;
        }
    }
    public NightlyUpdate() {

    }

    @Override
    public void onCreate() {
        System.err.println("Background service running. Updating...");
        try {
            Updater.update(Utils.firstDayOfMonth(Utils.Month.CURRENT_MONTH),
                    Utils.lastDayOfMonth(Utils.Month.NEXT_MONTH));
            System.err.println("Update succeeded.");
        } catch (GEError geError) {
            System.err.println("Update failed:");
            geError.printStackTrace();
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    static void setNextAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + (long)(Math.random() * ALARM_JITTER));


        App app = App.getInstance();
        AlarmManager alarmMgr = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(app, NightlyUpdate.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(app, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        System.err.println("Set alarm for: " + calendar.getTime());

    }
}
