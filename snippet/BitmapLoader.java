import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * BitmapLoader
 *
 * @author: onlylemi
 */
public class BitmapLoader {

    private Map<Integer, SoftReference<Bitmap>> map;

    public BitmapLoader() {
        map = new HashMap<>();
    }

    public Bitmap getBitmap(Resources res, Integer resId) {
        SoftReference<Bitmap> softBitmap = map.get(resId);
        if (null == softBitmap) {
            Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
            map.put(resId, new SoftReference<Bitmap>(bitmap));
            softBitmap = map.get(resId);
        }
        return softBitmap.get();
    }
}
