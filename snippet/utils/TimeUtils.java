import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 *
 * @author: onlylemi
 */
public class TimeUtils {

    private TimeUtils() {
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static int currentTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 时间戳转日期
     *
     * @param timestamp
     * @param format
     * @return
     */
    public static String timestampToDate(long timestamp, String format) {
        if (null == format || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timestamp * 1000));
    }

    /**
     * 日期转时间戳
     *
     * @param date
     * @param format
     * @return
     */
    public static long dateToTimestamp(String date, String format) {
        if (null == format || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
