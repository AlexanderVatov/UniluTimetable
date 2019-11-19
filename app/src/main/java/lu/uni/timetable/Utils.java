package lu.uni.timetable;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    enum Month {
        LAST_MONTH,
        CURRENT_MONTH,
        NEXT_MONTH
    }
    public static int LAST_MONTH = -1;
    public static int NEXT_MONTH = -2;
    public static Date firstDayOfMonth(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                1,
                0,
                0,
                0
        );
        return c.getTime();
    }

    public static Date firstDayOfMonth(Month month) {
        Calendar c = Calendar.getInstance();
        switch (month) {
            case LAST_MONTH:
                c.roll(Calendar.MONTH, -1);
                break;

            case CURRENT_MONTH:
                //c is already on current month
                break;

            case NEXT_MONTH:
                c.roll(Calendar.MONTH,  +1);
                break;
        }
        return firstDayOfMonth(c.getTime());
    }

    public static Date lastDayOfMonth(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.getActualMaximum(Calendar.DAY_OF_MONTH),
                23,
                59,
                59
        );
        return c.getTime();
    }

    public static Date lastDayOfMonth(Month month) {
        Calendar c = Calendar.getInstance();
        switch (month) {
            case LAST_MONTH:
                c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
                break;

            case CURRENT_MONTH:
                //c is already on current month
                break;

            case NEXT_MONTH:
                c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
                break;
        }
        return lastDayOfMonth(c.getTime());
    }

    public static String toSentenceCase(String text, String wordSeparators) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean capitaliseNext = true;
        for (char ch : text.toCharArray()) {
            if (wordSeparators.indexOf(ch) != -1) {
                capitaliseNext = true;
            } else if (capitaliseNext) {
                ch = Character.toTitleCase(ch);
                capitaliseNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }
}
