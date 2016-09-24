package techgravy.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * Created by aditlal on 23/09/16.
 */

public class WatchFaceService extends CanvasWatchFaceService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);


    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);


    private static final int MSG_UPDATE_TIME = 0;

    private String mMinTemp = "--";
    private String mMaxTemp = "--";
    private int mWeatherId = 800;
    private boolean mIsround = false;

    private GoogleApiClient mGoogleApiClient;
    private final String LOG_TAG = "onNotifyWearable";

    public static String WEATHER_DATA_PATH = "/today_weather_data";


    @Override
    public Engine onCreateEngine() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        return new Engine();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "Successfully connected to Google API client");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Failed to connect, with result: " + connectionResult);
    }


    private static class EngineHandler extends Handler {
        private final WeakReference<WatchFaceService.Engine> mWeakReference;

        public EngineHandler(WatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine
            implements DataApi.DataListener {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint mTimePaint;
        Paint mDatePaint;
        Paint mTemperaturePaint;

        boolean mAmbient;
        Time mTime;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        int mTapCount;

        float mTimeYOffset;

        float mDateYOffset;

        float mTemperatureYOffset;

        float mWeatherIconWidth;
        float mWeatherIconHeight;
        float mWeatherIconYOffset;


        SimpleDateFormat mFormatedDateNormal = new SimpleDateFormat("EEE, MMM/dd/y");
        SimpleDateFormat mFormatedDateAmbient = new SimpleDateFormat("MM/dd/y");


        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            Wearable.DataApi.addListener(mGoogleApiClient, this);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            Resources resources = WatchFaceService.this.getResources();

            mTimeYOffset = resources.getDimension(R.dimen.time_y_offset);
            mDateYOffset = resources.getDimension(R.dimen.date_y_offset);
            mTemperatureYOffset = resources.getDimension(R.dimen.temperature_y_offset);

            mWeatherIconWidth = resources.getDimension(R.dimen.weather_icon_width);
            mWeatherIconHeight = resources.getDimension(R.dimen.weather_icon_heigh);
            mWeatherIconYOffset = resources.getDimension(R.dimen.weather_icon_y_offset);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(WatchFaceService.this, R.color.background));

            mTimePaint = new Paint();
            mTimePaint = createTextPaint(ContextCompat.getColor(WatchFaceService.this, R.color.digital_text));

            mDatePaint = new Paint();
            mDatePaint = createTextPaint(ContextCompat.getColor(WatchFaceService.this, R.color.digital_text));

            mTemperaturePaint = new Paint();
            mTemperaturePaint = createTextPaint(ContextCompat.getColor(WatchFaceService.this, R.color.digital_temp));


            mTime = new Time();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            mGoogleApiClient.disconnect();
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            Resources resources = WatchFaceService.this.getResources();
            boolean isRound = insets.isRound();
            mIsround = isRound;
            float timeTextSize = resources.getDimension(isRound
                    ? R.dimen.time_text_size_round : R.dimen.time_text_size);

            float dateTextSize = resources.getDimension(isRound
                    ? R.dimen.date_text_size_round : R.dimen.date_text_size);

            float temperatureTextSize = resources.getDimension(isRound
                    ? R.dimen.temperature_text_size_round : R.dimen.temperature_text_size);

            mTimePaint.setTextSize(timeTextSize);
            mDatePaint.setTextSize(dateTextSize);
            mTemperaturePaint.setTextSize(temperatureTextSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTimePaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Resources resources = WatchFaceService.this.getResources();
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    break;
                case TAP_TYPE_TAP:
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {


            float watchWidth = bounds.width();

            Resources resources = WatchFaceService.this.getResources();


            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
                mTemperaturePaint.setColor(ContextCompat.getColor(WatchFaceService.this, R.color.digital_text));
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
                mTemperaturePaint.setColor(ContextCompat.getColor(WatchFaceService.this, R.color.digital_temp));
            }

            mTime.setToNow();

            String time = mAmbient
                    ? format("%d:%02d", mTime.hour, mTime.minute)
                    : format("%d:%02d:%02d", mTime.hour, mTime.minute, mTime.second);
            float timeTextwidth = mTimePaint.measureText(time);
            canvas.drawText(time, (watchWidth - timeTextwidth) / 2, mTimeYOffset, mTimePaint);

            String date = mAmbient
                    ? mFormatedDateAmbient.format(mTime.toMillis(true))
                    : mFormatedDateNormal.format(mTime.toMillis(true));
            float dateTextWidth = mDatePaint.measureText(date);
            canvas.drawText(date, (watchWidth - dateTextWidth) / 2, mDateYOffset, mDatePaint);

            if (!isInAmbientMode()) {
                Drawable d = ResourcesCompat.getDrawable(getResources(), getIconFromWeather(mWeatherId), null);
                int watchWidthInt = (int) watchWidth;
                int l = (watchWidthInt / 4) - ((int) mWeatherIconWidth / 2);
                int t = (int) mWeatherIconYOffset;
                int r = l + (int) mWeatherIconWidth;
                int b = (int) mWeatherIconYOffset + (int) mWeatherIconHeight;
                if (mIsround) {
                    int increment = resources.getDimensionPixelOffset(R.dimen.weather_icon_x_offset_Increment);
                    l = l + increment;
                    r = r + increment;
                }
                d.setBounds(l, t, r, b);
                d.draw(canvas);
            }


            String temperature = mMaxTemp + "  " + mMinTemp;
            float temperatureTextWidth = mTemperaturePaint.measureText(temperature);
            if (isInAmbientMode()) {
                canvas.drawText(temperature, (watchWidth - temperatureTextWidth) / 2, mTemperatureYOffset, mTemperaturePaint);
            } else {
                canvas.drawText(temperature, (watchWidth / 2) + 10, mTemperatureYOffset, mTemperaturePaint);
            }

        }


        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }


        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }


        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }


        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            Log.d(LOG_TAG, "Wear ");

            for (DataEvent dataEvent : dataEventBuffer) {
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    String path = dataEvent.getDataItem().getUri().getPath();
                    if (path.equals(WEATHER_DATA_PATH)) {
                        mWeatherId = dataMap.getInt("weatherId");
                        mMaxTemp = dataMap.getString("high", "");
                        mMinTemp = dataMap.getString("low", "");
                        Log.d(LOG_TAG,"Wear " + "weatherId: " + mWeatherId + " high: " + mMaxTemp + "low: " + mMinTemp);
                        invalidate();
                    }
                }
            }
        }
    }

    public static int getIconFromWeather(int weatherId) {

        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }
}
