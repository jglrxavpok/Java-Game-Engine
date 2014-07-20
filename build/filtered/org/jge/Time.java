package org.jge;

import java.util.Calendar;

public class Time
{

    public static long getTime()
    {
        return System.currentTimeMillis();
    }

    public static String getTimeAsString()
    {
    	String second = ""+Calendar.getInstance().get(Calendar.SECOND);
        String minute = ""+Calendar.getInstance().get(Calendar.MINUTE);
        String hour = ""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String day = ""+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String month = ""+(Calendar.getInstance().get(Calendar.MONTH)+1);
        String year = ""+Calendar.getInstance().get(Calendar.YEAR);
        
        if(second.length() == 1)
        	second = "0"+second;
        
        if(minute.length() == 1)
        	minute = "0"+minute;
        
        if(hour.length() == 1)
        	hour = "0"+hour;
        
        if(month.length() == 1)
        	month = "0"+month;
        return year+"/"+month+"/"+day+" @ "+hour+":"+minute+":"+second;
    }
}
