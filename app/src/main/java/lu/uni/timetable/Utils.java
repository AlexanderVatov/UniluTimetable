package lu.uni.timetable;

import java.util.Calendar;
import java.util.Date;

/**
 * The Utils class contains various utility methods used in different parts of the application.
 */

public class Utils {
    enum Month {
        LAST_MONTH,
        CURRENT_MONTH,
        NEXT_MONTH
    }

    /**
     * Returns the first day of the month identified by d. For instance, if d is 26 August, 1789,
     * then this method will return the date 1 August 1789, 00:00:00h.
     *
     * @param d A util.Date used to identify a month and a year. No other date or time parameters will be retained.
     * @return A util.Date at 00:00:00h on the first day of the same month as d.
     */

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

    /**
     * Returns the first day of a month relative to the present moment. For example, if this method
     * is called on the 25th December 2019 with an argument CURRENT_MONTH, it will return the date
     * 1 December 2019, 00:00:00h.
     * @param month Either Utils.Month.LAST_MONTH, .CURRENT_MONTH, or .NEXT_MONTH, relative to the present day.
     * @return A util.Date at 00:00:00h on the first day of the month identified by d.
     */

    public static Date firstDayOfMonth(Month month) {
        Calendar c = Calendar.getInstance();
        switch (month) {
            case LAST_MONTH:
                c.add(Calendar.MONTH, -1);
                break;

            case CURRENT_MONTH:
                //c is already on current month
                break;

            case NEXT_MONTH:
                c.add(Calendar.MONTH,  +1);
                break;
        }
        return firstDayOfMonth(c.getTime());
    }

    /**
     * Returns the last day of the month identified by d. For instance, if d is 26 August, 1789,
     * then this method will return the date 31 August 1789, 23:59:59h.
     *
     * @param d A util.Date used to identify a month and a year. No other date or time parameters will be retained.
     * @return A util.Date at 23:59:59h on the last day of the same month as d.
     */

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

    /**
     * Returns the last day of a month relative to the present moment. For example, if this method
     * is called on the 25th December 2019 with an argument CURRENT_MONTH, it will return the date
     * 31 December 2019, 23:59:59h.
     * @param month Either Utils.Month.LAST_MONTH, .CURRENT_MONTH, or .NEXT_MONTH, relative to the present day.
     * @return A util.Date at 23:59:59h on the last day of the month identified by month.
     */
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

    /**
     * Returns the time 00:00:00 on a given day
     * @param day A Date object representing the given date. Its time components will be disregarded.
     * @return A Date object representing the time 00:00:00 on the given day.
     */
    public static Date startOfDay(Date day) {
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * Returns the time 23:59:59 on a given day
     * @param day A Date object representing the given date. Its time components will be disregarded.
     * @return A Date object representing the time 23:59:59 on the given day.
     */
    public static Date endOfDay(Date day) {
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * Converts a string to sentence case, i.e. with the first character of each word in uppercase
     * and subsequent characters of the same word in lowercase. Words are considered to be strings
     * of arbitrary consecutive characters not contained in wordSeparators. If a character is
     * contained in wordSeparators, it will be left unchanged. For example,
     * Utils.toSentenceCase("500,000 EU citizens r", " ,r") returns "500,000 Eu Citizens r".
     *
     * @param text Any string.
     * @param wordSeparators A sequence of separator characters, e.g. " " or ",;:"
     * @return A string with the same length as text. Null will be returned if and only if text is null.
     */
    public static String toSentenceCase(String text, String wordSeparators) {
        if (text == null || text.isEmpty() || wordSeparators == null) {
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
