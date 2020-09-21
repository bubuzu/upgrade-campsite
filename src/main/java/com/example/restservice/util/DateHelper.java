package com.example.restservice.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {
    public static Date incrementDate(Date date, int daysToAdd){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, daysToAdd);
        return c.getTime();
    }

    public static long getDifferenceInDays(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
