package lu.uni.timetable;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class UtilsTest {

    @Test
    public void firstDayOfMonth() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2019,12,25);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2019,12,1,0,0,0);
        Assert.assertEquals(cal2.getTime().getTime(), Utils.firstDayOfMonth(cal1.getTime()));
    }

    @Test
    public void startOfDay() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2019,12,25, 13, 30, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2019,12,25,0,0,0);
        Assert.assertEquals(cal2.getTime().getTime(), Utils.startOfDay(cal1.getTime()));
    }

    @Test
    public void toSentenceCase() {
    }
}