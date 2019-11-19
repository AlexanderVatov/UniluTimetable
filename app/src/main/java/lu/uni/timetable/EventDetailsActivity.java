package lu.uni.timetable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {
    private String buildingCode = "";
    private String title, subject, building, room, timeRange, date, type, lecturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        System.err.println("EventDetailsActivity: just created!");
        Event e = EventIntent.getEvent(getIntent());
        formatFields(e);


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
        ((TextView) findViewById(R.id.lecturerView)).setText(lecturer);
    }

    private void formatFields(Event e) {

        title = e.getTitle();
        subject = e.getSubject();
        
        //Format lecturer
        try {
            StringBuilder builder = new StringBuilder();
            String[] people = e.getLecturer().split(",( )?");
            for (int i = 0; i < people.length; ++i) {
                StringBuilder firstNames = new StringBuilder(), surnames = new StringBuilder();
                boolean surnamesFinished = false;
                for (String name : people[i].split(" ")) {
                    if (!surnamesFinished && name.equals(name.toUpperCase())) {
                        String titlecase = Utils.toSentenceCase(name, " -");
                        if(surnames.length() != 0) surnames.append(' ');
                        surnames.append(titlecase);
                    } else {
                        surnamesFinished = true;
                        firstNames.append(name);
                        firstNames.append(' ');
                    }
                }
                if (i != 0) builder.append(";\n");
                builder.append(firstNames.toString());
                builder.append(surnames.toString());
            }
            lecturer = builder.toString();
        }
        catch (Exception whatever) {
            lecturer = e.getLecturer();
        }
        
        //Format building and room
        String[] locationParts = e.getRoom().replaceFirst("^[ \\t]+", "").split(" ", 2);
        try {
            buildingCode = locationParts[0];
            room = "Room " + locationParts[1];

            building = Constants.buildingNames.get(buildingCode);
            if (building == null) {
                building = "";
                buildingCode = "";
                room = e.getRoom();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            building = "";
            room = e.getRoom();
        }
        
        //Format timeRange and date
        long startMilliseconds = e.getStart().getTime(), endMilliseconds = e.getEnd().getTime();
        if ((endMilliseconds - startMilliseconds) / (24 * 60 * 60 * 1000) == 0) {
            //If the start and end are on the same day
            timeRange = DateUtils.formatDateRange(this, startMilliseconds, endMilliseconds,
                    DateUtils.FORMAT_SHOW_TIME);
            date = DateUtils.formatDateTime(this, startMilliseconds,
                    DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE);
        } else {
            timeRange = DateUtils.formatDateRange(this, startMilliseconds, endMilliseconds,
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY);
            date = "";
        }

        //Format type
        type = e.getEventType();
        if (type.equals("CM")) type = getString(R.string.event_cm);
        else if (type.equals("TD")) type = getString(R.string.event_td);
        else if (type.equals("EX")) type = getString(R.string.event_ex);
        else if (type.equals("TP")) type = getString(R.string.event_tp);
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
