import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 *
 * @author qijianbin
 */
public final class TimeUtils {

    private TimeUtils() {
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前的日期（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String currentDate(){
        return timestamp2Date(currentTimestamp(), null);
    }

    /**
     * 时间戳转日期
     *
     * @param timestamp
     * @param format 若为 null，默认格式为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String timestamp2Date(long timestamp, String format) {
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
     * @param format 若为 null，默认格式为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long date2Timestamp(String date, String format) {
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
