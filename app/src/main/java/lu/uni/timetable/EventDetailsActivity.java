package lu.uni.timetable;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {
    private String buildingCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        System.err.println("EventDetailsActivity: just created!");
        Event e = EventIntent.getEvent(getIntent());

        EventPresenter p = new EventPresenter(this, e);
        String title = p.getTitle();
        String subject = p.getSubject();
        String building = p.getBuilding();
        String room = p.getRoom();
        String timeRange = p.getTimeRange();
        String date = p.getDate();
        String type = p.getType();
        List<String> lecturers = p.getLecturers();

        ((TextView) findViewById(R.id.titleView)).setText(title);
        if (title.equals(subject))
            findViewById(R.id.subjectView).setVisibility(View.INVISIBLE);
        else
            ((TextView) findViewById(R.id.subjectView)).setText(e.getSubject());

        if (building.isEmpty())
            findViewById(R.id.buildingView).setVisibility(View.INVISIBLE);
        else
            ((TextView) findViewById(R.id.buildingView)).setText(building);

        ((TextView) findViewById(R.id.roomView)).setText(room);
        ((TextView) findViewById(R.id.timeRangeView)).setText(timeRange);

        if (date.isEmpty()) {
            findViewById(R.id.dateView).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) findViewById(R.id.dateView)).setText(date);
        }

        ((TextView) findViewById(R.id.typeView)).setText(type);
        LinearLayout layout = findViewById(R.id.eventDetailsLayout);
        for(String lecturer: lecturers) {
            TextView t = new TextView(this);
            t.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(R.drawable.event_lecturer), null, null, null);
            t.setText(lecturer);
            t.setTextSize(20);
            t.setTextColor(Color.BLACK);
            t.setPadding(0, 0, 0, 6);
            layout.addView(t);

        }
    }



    public void showMap(View view) {
        System.err.println("EventDetailsActivity.showMap");
        if (buildingCode.isEmpty()) return; //This should never actually happen

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.buildingGoogleMapsUris.get(buildingCode)));
            intent.setPackage(Constants.GoogleMapsPackage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                intent.setPackage(null);
                intent.setData(Uri.parse(Constants.buildingFallbackMapUris.get(buildingCode)));
                if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
                System.err.println("No application with which to show map!");
            }
        } catch (NullPointerException ex) {
            System.err.println("No address is known for building \"" + buildingCode + "\"!");
        }

    }
}
