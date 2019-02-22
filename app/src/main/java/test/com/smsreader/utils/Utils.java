package test.com.smsreader.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class Utils {

    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);
        Date yesterday = calendar.getTime();
        return yesterday;
    }

    public static int[] splitSeconds(long seconds) {
        final int secondsPerMinute = 60;
        final int secondsPerHour = 60 * secondsPerMinute;
        final int secondsPerDay = 24 * secondsPerHour;

        int days = (int) Math.floor(seconds / secondsPerDay);
        long hourSec = seconds % secondsPerDay;
        int hours = (int) Math.floor(hourSec / secondsPerHour);
        long minSec = hourSec % secondsPerHour;
        int minutes = (int) Math.floor(minSec / secondsPerMinute);
        int remainingSeconds = (int) Math.ceil(minSec % secondsPerMinute);
        return new int[]{days, hours, minutes, remainingSeconds};
    }
}
