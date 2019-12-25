package wk.doraemon.time;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TF on 2018/12/24.
 */
public class TimeUtils implements Serializable {

    /**
     * 秒数 -> 格式化时间字符串
     * */
    public static String second2Str(long seconds, String format) {
        return new SimpleDateFormat(format).format(new Date(seconds * 1000L));
    }
    public static String second2Str(long seconds) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(seconds * 1000L));
    }

    /**
     * 格式化时间字符串 -> 秒数
     * */
    public static long str2Second(String timeStr, String format) {
        long sec = 0L;
        try {
            sec = new SimpleDateFormat(format).parse(timeStr).getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return sec;
        }
    }

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date parse(String timeStr, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return date;
        }
    }

}
