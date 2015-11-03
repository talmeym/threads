package util;

import java.util.Date;

public class DateHelper
{
    public static String getDateStatus(Date p_date)
    {
        return getDateStatus(p_date, new Date(), " ago");
    }
    
    public static String getDateStatus(Date p_date1, Date p_date2)
    {
        return getDateStatus(p_date1, p_date2, " before");
    }
    
    private static String getDateStatus(Date p_date1, Date p_date2, String p_beforeStr)
    {
        int x_weeks = 0;
        int x_days;
        int x_hours;
        int x_mins;
        
        long x_diff = p_date1.getTime() - p_date2.getTime();
        long x_time = Math.abs(x_diff);
        
        x_days = (int) (x_time / (1000 * 60 * 60 * 24));
        
        if(x_days > 10)
        {
            x_weeks = x_days / 7;
            x_days = x_days % 7;
        }
        
        x_hours = (int) ((x_time / (1000 * 60 * 60)) % 24);        
        x_mins = (int) ((x_time / (1000 * 60)) % 60);
        
        x_mins++;
        
        StringBuffer x_buffer = new StringBuffer();
        
        if(x_weeks > 0)
        {
            x_buffer.append(x_weeks + "W ");
        }
        
        if(x_days > 0 && x_weeks < 5)
        {
            x_buffer.append(x_days + "D ");
        }

        if(x_hours > 0 && x_weeks == 0 && x_days < 5)
        {
            x_buffer.append(x_hours + "H ");
        }
        
        if(x_mins > 0 && x_weeks == 0 && x_days < 2)
        {
            x_buffer.append(x_mins + "M ");
        }
                
        if(x_diff < 0)
        {
            x_buffer.append(p_beforeStr);
        }

        return x_buffer.toString();
    }
}
