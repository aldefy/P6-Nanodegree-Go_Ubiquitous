package techgravy.sunshine.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import techgravy.sunshine.R;
import techgravy.sunshine.ui.main.MainActivity;

/**
 * Created by aditlal on 04/04/16.
 */
public class NotificationHelper {

    public static final String TAG = NotificationHelper.class.getSimpleName();

    public static void simpleTextNotification(Context mContext, String title, String message) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setContentText(message);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setAutoCancel(true);


        //Opening a Activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        Intent resultIntent = null;
        resultIntent = new Intent(mContext, MainActivity.class);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(message.hashCode(), mBuilder.build());
    }

    public static void expandableTextNotification(Context mContext, String title, String message) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message));

        //Opening a Activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        Intent resultIntent = null;
        resultIntent = new Intent(mContext, MainActivity.class);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(message.hashCode(), mBuilder.build());
    }

    public static void expandablePictureNotification(Context mContext, String title, String url) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setContentTitle(title);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setAutoCancel(true);
        // BigPictureStyle
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle();
        Picasso.with(mContext).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                s.bigPicture(bitmap);
                mBuilder.setStyle(s);
                //Opening a Activity
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                Intent resultIntent = null;
                resultIntent = new Intent(mContext, MainActivity.class);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(url.hashCode(), mBuilder.build());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                //Opening a Activity
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                Intent resultIntent = null;
                resultIntent = new Intent(mContext, MainActivity.class);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(url.hashCode(), mBuilder.build());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


    }


}
