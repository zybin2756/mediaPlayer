package utils;

import android.content.Context;
import android.net.TrafficStats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class ToolUtils {
    private static long lastTiemStamp = 0;
    private static long lastTotalRxBytes = 0;
    //时间格式转换
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

    /**
     * 判断是否网络资源
     */
    public static boolean isNetUri(String uri){
        boolean result = false;
        if(uri != null){
            if(uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("mms") || uri.toLowerCase().startsWith("rtsp")){
                result = true;
            }
        }
        return result;
    }

    public static String getNetSpeed(Context context){
        String rel = "0 kb/s";
        long curTiemStamp = System.currentTimeMillis();
        long curTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes()/1024);

        float speed = (curTotalRxBytes - lastTotalRxBytes)*1000 /(curTiemStamp - lastTiemStamp) ;
        if(speed < 1024f) {
            speed = Math.round(speed*100)/100;
            rel = String.valueOf(speed) + " kb/s";
        }else{
            speed/=1024f;
            speed = Math.round(speed*100)/100;
            rel = String.valueOf(speed) + " mb/s";
        }

        lastTotalRxBytes = curTotalRxBytes;
        lastTiemStamp = curTiemStamp;
        return rel;
    }

}
