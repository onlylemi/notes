import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;

import java.util.Arrays;
import java.util.List;

/**
 * AppUtils
 *
 * @author: onlylemi
 */
public class AppUtils {

    private AppUtils() {

    }

    public static void share(Context context, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, "好友分享");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享 - 知乎日报"));
    }

    /**
     * list 转 string
     *
     * @param list
     * @param spaceCharacter
     * @return
     */
    public static String list2String(List<String> list, String spaceCharacter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(spaceCharacter);
            }
        }
        return sb.toString();
    }

    /**
     * string 转 list
     *
     * @param str
     * @param spaceCharacter
     * @return
     */
    public static List<String> string2List(String str, String spaceCharacter) {
        return Arrays.asList(str.split(spaceCharacter));
    }

    /**
     * dp 转 px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 获取缓存目录
     *
     * @param context
     * @param folder
     * @return
     */
    public static File getDiskCacheDir(Context context, String folder) {
        String cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable()) {
            cacheDir = context.getExternalCacheDir().getPath();
        } else {
            cacheDir = context.getCacheDir().getPath();
        }
        return new File(cacheDir + File.separator + folder);
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName
                    (), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 屏幕的width
     *
     * @param activity
     * @return
     */
    public static int screeWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 屏幕的高
     *
     * @param activity
     * @return
     */
    public static int screeHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
}
