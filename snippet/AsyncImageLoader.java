import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AsyncImageLoader
 *
 * @author: onlylemi
 */
public class AsyncImageLoader {

    private static AsyncImageLoader mLoader;

    private static Context mContext;

    // 内存缓存默认 5M
    static final int MEMORY_CACHE_DEFAULT_SIZE = 5 * 1024 * 1024;
    // 文件缓存默认 50M
    static final int DISK_CACHE_DEFAULT_SIZE = 50 * 1024 * 1024;
    // 内存缓存
    private LruCache<String, Bitmap> mMemoryCache;
    // 磁盘缓存
    private DiskLruCache mDiskCache;

    public AsyncImageLoader(Context context) {
        mContext = context.getApplicationContext();

        initMemoryCache();
        initDiskCache(mContext);
    }

    public static AsyncImageLoader with(Context context) {
        if (null == mLoader) {
            mLoader = new AsyncImageLoader(context);
        }
        return mLoader;
    }

    /**
     * 初始化内存缓存
     */
    private void initMemoryCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 初始化磁盘缓存
     */
    private void initDiskCache(Context context) {
        try {
            File cacheDir = AppUtils.getDiskCacheDir(context, "img");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskCache = DiskLruCache.open(cacheDir, AppUtils.getAppVersion(App.getContext()), 1,
                    DISK_CACHE_DEFAULT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内存缓存中获取图片
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemory(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 加入到缓存中
     *
     * @param key
     * @param bitmap
     */
    private void putBitmapToMemory(String key, Bitmap bitmap) {
        if (getBitmapFromMemory(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从磁盘中获取
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromDisk(String key) {
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskCache.get(hashKeyForDisk(key));
            if (null != snapshot) {
                InputStream in = snapshot.getInputStream(0);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeStream(in, null, options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 加入到磁盘中
     *
     * @param key
     * @param bitmap
     */
    private void putBitmapToDisk(String key, Bitmap bitmap) {
        try {
            DiskLruCache.Editor editor = mDiskCache.edit(hashKeyForDisk(key));
            if (null != editor) {
                OutputStream output = editor.newOutputStream(0);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param imageView
     * @param url
     * @return 如需网络下载，则返回true
     */
    public boolean loadImage(final ImageView imageView, String url) {
        return loadImage(imageView, url, true);
    }

    /**
     * 加载图片
     *
     * @param imageView
     * @param url
     * @param isIdle    在listview中，监听listview的滚动状态，滚动为false，不滚动为true
     * @return
     */
    public boolean loadImage(final ImageView imageView, String url, boolean isIdle) {
        imageView.setTag(url);

        Bitmap bitmap = getBitmapFromMemory(url);
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap);
            return false;
        }

        // 再从磁盘中获取
       bitmap = getBitmapFromDisk(url);
       if (null != bitmap) {
           putBitmapToMemory(url, bitmap);
           imageView.setImageBitmap(bitmap);
           return false;
       }

        // 下载图片
        if (isIdle) {
            new ImageDownloadTask(imageView).execute(url);
        }

        return true;
    }

    /**
     * 异步下载图片
     */
    private class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

        private final String TAG = ImageDownloadTask.class.getSimpleName();

        private String imgUrl;
        private ImageView imageView;

        private int resId;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imgUrl = params[0];
            // 下载图片
            Bitmap bitmap = downloadBitmap(imgUrl);
            // 加入缓存
            putBitmapToMemory(imgUrl, bitmap);
            // 加入磁盘
            putBitmapToDisk(imgUrl, bitmap);

            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (null != bitmap && null != imageView) {
                // 通过 tag 来防止图片错位
                if (null != imageView.getTag() && imageView.getTag().equals(imgUrl)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        /**
         * 下载image
         *
         * @param imgUrl
         * @return
         */
        private Bitmap downloadBitmap(String imgUrl) {
            Bitmap bitmap = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(imgUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.connect();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != conn) {
                    conn.disconnect();
                }
            }
            return bitmap;
        }
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
