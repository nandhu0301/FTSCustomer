package com.smiligenceTestenv.techUser.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.smiligenceTestenv.techUser.common.Constant.DATE_FORMAT_YYYYMD;


public class DateUtils
{
    static Calendar calendar = Calendar.getInstance();

    public static String fetchCurrentDate (){
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchCurrentDateAndTime (){
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_TIME_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }
    public static String fetchCurrentTime (){
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_TIME_FORMAT_NEW);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat( "hh:mm a" );
        String time = timeFormat.format ( calendar.getTime() );
        return time;
    }
    public static String fetchFormatedCurrentDate ()
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

}
