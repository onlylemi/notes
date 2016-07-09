import android.app.Application;
import android.content.Context;

/**
 * App
 *
 * @author: onlylemi
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
