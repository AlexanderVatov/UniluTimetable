package lu.uni.timetable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {
    final int FIELD_PADDING_DP = 8;
    final int FIELD_DRAWABLE_PADDING_DP = 6;

    private String buildingCode = "";
    Event e;
    EventFormatter formatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        System.err.println("EventDetailsActivity: just created!");
        e = EventIntent.getEvent(getIntent());

        formatter = new EventFormatter(this, e);
        String title = formatter.getTitle();
        String subject = formatter.getSubject();
        String building = formatter.getBuilding();
        String room = formatter.getRoom();
        String roomCode = formatter.getRoomCode();
        String timeRange = formatter.getTimeRange();
        String date = formatter.getDate();
        String type = formatter.getType();
        List<String> lecturers = formatter.getLecturers();
        this.buildingCode = formatter.getBuildingCode();

        ((TextView) findViewById(R.id.titleView)).setText(title);
        if (title.equals(subject))
            findViewById(R.id.subjectView).setVisibility(View.GONE);
        else
            ((TextView) findViewById(R.id.subjectView)).setText(e.getSubject());

        if (building.isEmpty())
            findViewById(R.id.buildingView).setVisibility(View.GONE);
        else
            ((TextView) findViewById(R.id.buildingView)).setText(building);

        ((TextView) findViewById(R.id.roomView)).setText(room);
        if(!buildingCode.equals("MSA"))
            findViewById(R.id.roomMapButton).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.timeRangeView)).setText(timeRange);

        if (date.isEmpty()) {
            findViewById(R.id.dateView).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.dateView)).setText(date);
        }

        ((TextView) findViewById(R.id.typeView)).setText(type);
        LinearLayout layout = findViewById(R.id.eventDetailsLayout);
        for(String lecturer: lecturers) {
            TextView t = new TextView(this, null, R.style.EventDetails);
            t.setTextAppearance(R.style.EventDetails);
            t.setText(lecturer);
            t.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(R.drawable.event_lecturer), null, null, null);
            float density = getResources().getDisplayMetrics().density;
            t.setPadding(0,0,0, ((int) (FIELD_PADDING_DP*density)));
            t.setCompoundDrawablePadding(((int) (FIELD_DRAWABLE_PADDING_DP*density)));
            layout.addView(t);

        }
    }



    public void showBuildingMap(View view) {
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

    public void showRoomMap(View view) {
        Intent intent = new Intent(this,RoomMapActivity.class);
        intent.putExtra(EventIntent.room, formatter.getRoomCode());
        startActivity(intent);
    }
}
