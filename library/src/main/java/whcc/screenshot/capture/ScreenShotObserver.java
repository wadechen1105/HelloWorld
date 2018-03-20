package whcc.screenshot.capture;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by wadechen on 2018/3/20.
 */

public class ScreenShotObserver {
    private static final String TAG = "ScreenShotObserver";
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap"
    };
    private Context mContext;
    private MediaContentObserver mInternalObserver;
    private MediaContentObserver mExternalObserver;
    private OnScreenshotTakenListener mListener;

    public ScreenShotObserver(Context context, OnScreenshotTakenListener listener) {
        mListener = listener;
        mContext = context.getApplicationContext();
        HandlerThread handlerThread = new HandlerThread("content_observer");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        mExternalObserver = new MediaContentObserver(handler);
        mInternalObserver = new MediaContentObserver(handler);
    }

    private class MediaContentObserver extends ContentObserver {

        public MediaContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, "onChange " + uri.toString());

            long timeThreshold = System.currentTimeMillis() - 10 * 1000; // 10 sec before take screenshot

            final String[] columns = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.DATE_TAKEN
            };

            final String selection = MediaStore.Images.Media.DATE_TAKEN + " > ?";

            Log.d(TAG, "------------timeThreshold--------- " + timeThreshold);

            Cursor cursor = null;
            try {
                cursor = mContext.getContentResolver()
                        .query(
                                uri,
                                columns,
                                selection,
                                new String[]{String.valueOf(timeThreshold)},
                                MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1");

                if (cursor != null && cursor.moveToFirst()) {
                    final String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    final String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                    final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    final long dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

                    for (String keyword : KEYWORDS) {
                        if (path.toLowerCase().contains(keyword)) {
                            // detect screenshot capture
                            mListener.onScreenshotTaken();
                            break;
                        }
                    }

                    Log.d(TAG, "screen shot added : " + fileName +
                            " | path : " + path +
                            " | dateTaken : " + dateTaken +
                            " | title = " + title);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public void register() {
        mContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                mExternalObserver
        );

        mContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                true,
                mInternalObserver
        );
    }

    public void unregister() {
        if (mInternalObserver != null) {
            try {
                mContext.getContentResolver().unregisterContentObserver(mInternalObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mInternalObserver = null;
        }
        if (mExternalObserver != null) {
            try {
                mContext.getContentResolver().unregisterContentObserver(mExternalObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mExternalObserver = null;
        }
    }


}
