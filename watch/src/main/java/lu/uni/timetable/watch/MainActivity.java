package lu.uni.timetable.watch;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GEEvent;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.avatov.guichetetudiant.OkHttpBackend;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        // Enables Always-on
        //setAmbientEnabled();
        update();
    }

    protected void update() {
        new FetchDataTask().execute();
    }

    protected void onUpdated(List<GEEvent> events) {
        System.err.println("MainActivity.onUpdated running...");
        StringBuilder b = new StringBuilder();
//        b.append("Classes today:\n\n");
        for(GEEvent e: events) {
            b.append(DateUtils.formatDateRange(this,
                                e.start.getTime(),
                                e.end.getTime(),
                                DateUtils.FORMAT_SHOW_TIME));
            b.append(": " + e.title + "\n");
            b.append(e.room);
            b.append("\n\n");
        }
        if(events.size() == 0) {
            b.append("-\n");
        }
        System.err.println(b.toString());
        mTextView.setText(b.toString());
    }

    class FetchDataTask extends AsyncTask<Void, Void, List<GEEvent>> {

        @Override
        protected List<GEEvent> doInBackground(Void... voids) {
            try {
                GuichetEtudiant g = new GuichetEtudiant(new OkHttpBackend());
                System.err.println("Connecting to Guichet Étudiant...");
                g.authenticate(Credentials.username, Credentials.password);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                Date today = cal.getTime();

                System.err.println("Fetching events from " + today + " to " + today + "...");
                List<GEEvent> events = g.getEvents(today,today, Collections.singletonList("0001o3032"));
                System.err.println(events.size() + " events obtained.");
                return events;
            } catch (GEError geError) {
                geError.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<GEEvent> events) {
            onUpdated(events);
        }
    }
}
