package lu.uni.timetable;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to format the fields of events (date ranges, lecturers' names, etc.). As some of this is
 * locale-dependent, a Context is required. Fields are lazily generated the first time they are
 * called.
 */
public class EventFormatter {

    /**
     * Construct a new EventFormatter in a given Context and with a given {@link Event}.
     */
    public EventFormatter(Context c, Event e) {
        this.c = c;
        this.e = e;
    }

    /**
     * Reset the event. This will reset all fields
     * @param e The new event
     */
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

    /**
     * The title of the event
     * @return A non-null string
     */
    public String getTitle() {
        if(title == null) title = e.getTitle().trim();
        return title;
    }

    /**
     * The subject of the event
     * @return A non-null string
     */
    public String getSubject() {
        if(subject == null) subject = e.getSubject();
        return subject;
    }

    /**
     * The building in which the event takes place (named by {@link Constants#buildingNames}.
     * If the location provided by {@link Event#getRoom()} cannot be correctly parsed,
     * an empty string will be returned.
     * @return A non-null string
     */
    public String getBuilding() {
        if(building != null) return building;
        formatBuildingRoom();
        return building;
    }

    /**
     * The code of the building in which the event takes place (e.g. MSA, MNO, etc.).
     * For a human-readable version, use {@link #getBuilding()}.
     * If the location provided by {@link Event#getRoom()} cannot be correctly parsed,
     * an empty string will be returned instead.
     * @return A non-null string
     */
    public String getBuildingCode() {
        if(buildingCode != null) return buildingCode;
        formatBuildingRoom();
        return buildingCode;
    }

    /**
     * The room in which the event takes place.
     * If the location provided by {@link Event#getRoom()} cannot be correctly parsed,
     * the entire location will be returned instead.
     * @return A non-null string
     */
    public String getRoom() {
        if(room != null) return room;
        formatBuildingRoom();
        return room;
    }

    /**
     * The code of the room in which the event takes place (e.g. "3.370").
     * For a human-readable version, use {@link #getRoom()} ()}.
     * If the location provided by {@link Event#getRoom()} cannot be correctly parsed,
     * an empty string will be returned instead.
     * @return A non-null string
     */
    public String getRoomCode() {
        if(roomCode != null) return roomCode;
        formatBuildingRoom();
        return roomCode;
    }

    /**
     * The time range from the start to the end of the event. If the two are on the same day, no
     * date will be included (it can be obtained using {@link #getDate()};
     * if they are on different dates, a date range will be included.
     * @return A non-null string
     */
    public String getTimeRange() {
        if(timeRange != null) return timeRange;
        formatTimeRangeDate();
        return timeRange;
    }

    /**
     * The date of the event. If it spans across multiple days, an empty string will be returned
     * (and a date range will be included in {@link #getTimeRange()}.
     * @return A non-null string
     */
    public String getDate() {
        if(date != null) return date;
        formatTimeRangeDate();
        return date;
    }

    /**
     * The translated, human readable version of the event type (lecture, tutorial, etc.)
     * @return A non-null string
     */
    public String getType() {
        if(type != null) return type;
        type = e.getEventType();
        if (type.equals("CM")) type = c.getString(R.string.event_cm);
        else if (type.equals("TD")) type = c.getString(R.string.event_td);
        else if (type.equals("EX")) type = c.getString(R.string.event_ex);
        else if (type.equals("TP")) type = c.getString(R.string.event_tp);
        return type;
    }

    /**
     * Used internally.
     */
    protected static List<String> getLecturers(String lecturerList) {

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

    /**
     * A reformatted list of lecturers (first names followed by surnames). If the names could not be
     * parsed, a singleton list with the output of {@link Event#getLecturer()} will be returned.
     * @return A list containing at least one non-null string.
     */
    public List<String> getLecturers() {
        if(lecturers == null) lecturers = getLecturers(e.getLecturer());
        return lecturers;
    }

    /**
     * Used internally.
     */
    protected void formatBuildingRoom() {
        String[] locationParts = e.getRoom().replaceFirst("^[ \\t]+", "").split(" ", 2);
        try {
            buildingCode = locationParts[0];
            roomCode = locationParts[1];
            building = Constants.buildingNames.get(buildingCode);
            room = c.getString(R.string.event_room) + ' ' + roomCode;

            if (building == null) {
                building = "";
                buildingCode = "";
                room = e.getRoom();
                roomCode = "";
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            building = "";
            room = e.getRoom();
        }
    }

    /**
     * Used internally.
     */
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
    private String title, subject, building, buildingCode, room, roomCode, timeRange, date, type;
    private List<String> lecturers;
}
