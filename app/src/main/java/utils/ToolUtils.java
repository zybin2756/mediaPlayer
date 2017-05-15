package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class ToolUtils {
    public static String timeToString(long duration){
        duration/=1000;
        long hour = duration/3600;
        duration%=3600;
        long min = duration/60;
        duration%=60;
        long sec = duration;

        StringBuilder builder = new StringBuilder();
        if(hour < 10){
            builder.append(0);
        }
        builder.append(hour);
        builder.append(':');

        if(min < 10){
            builder.append(0);
        }
        builder.append(min);
        builder.append(':');

        if(sec < 10){
            builder.append(0);
        }
        builder.append(sec);

        return builder.toString();
    }
}
