package lu.uni.timetable;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventPresenter {

    public EventPresenter(Context c, Event e) {
        this.c = c;
        this.e = e;
    }

    public void setEvent(Event e) {
        this.e = e;
        title = null;
        subject = null;
        building = null;
        room = null;
        timeRange= null;
        date = null;
        type = null;
        lecturers = null;
    }



    public String getTitle() {
        if(title == null) title = e.getTitle();
        return title;
    }

    public String getSubject() {
        if(subject == null) subject = e.getSubject();
        return subject;
    }

    public String getBuilding() {
        if(building != null) return building;
        formatBuildingRoom();
        return building;
    }

    public String getRoom() {
        if(room != null) return room;
        formatBuildingRoom();
        return room;
    }

    public String getTimeRange() {
        if(timeRange != null) return timeRange;
        formatTimeRangeDate();
        return timeRange;
    }

    public String getDate() {
        if(date != null) return date;
        formatTimeRangeDate();
        return date;
    }

    public String getType() {
        if(type != null) return type;
        type = e.getEventType();
        if (type.equals("CM")) type = c.getString(R.string.event_cm);
        else if (type.equals("TD")) type = c.getString(R.string.event_td);
        else if (type.equals("EX")) type = c.getString(R.string.event_ex);
        else if (type.equals("TP")) type = c.getString(R.string.event_tp);
        return type;
    }

    public static List<String> getLecturers(String lecturerList) {

        try {
            String[] people = lecturerList.split(",( )?");
            ArrayList<String> result = new ArrayList<>(people.length);
            for (String person: people) {
                StringBuilder firstNames = new StringBuilder(), surnames = new StringBuilder();
                boolean surnamesFinished = false;
                for (String name : person.split(" ")) {
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
                result.add((firstNames.toString() + surnames.toString())
                        .replace("d ", "d'"));

            }
            return result;
        }
        catch (Exception whatever) {
            return Collections.singletonList(lecturerList);
        }

    }

    public List<String> getLecturers() {
        if(lecturers == null) lecturers = getLecturers(e.getLecturer());
        return lecturers;
    }

    public String getBuildingCode() {
        if(buildingCode != null) return buildingCode;
        formatBuildingRoom();
        return buildingCode;
    }

    protected void formatBuildingRoom() {
        String[] locationParts = e.getRoom().replaceFirst("^[ \\t]+", "").split(" ", 2);
        try {
            buildingCode = locationParts[0];
            room = c.getString(R.string.event_room) + ' ' + locationParts[1];

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
    }

    protected void formatTimeRangeDate() {
        long startMilliseconds = e.getStart().getTime(), endMilliseconds = e.getEnd().getTime();
        if ((endMilliseconds - startMilliseconds) / (24 * 60 * 60 * 1000) == 0) {
            //If the start and end are on the same day
            timeRange = DateUtils.formatDateRange(c, startMilliseconds, endMilliseconds,
                    DateUtils.FORMAT_SHOW_TIME);
            date = DateUtils.formatDateTime(c, startMilliseconds,
                    DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE);
        } else {
            timeRange = DateUtils.formatDateRange(c, startMilliseconds, endMilliseconds,
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY);
            date = "";
        }
    }
    private Event e;
    private Context c;
    private String title, subject, building, room, timeRange, date, type;
    private List<String> lecturers;

    private String buildingCode;
}
