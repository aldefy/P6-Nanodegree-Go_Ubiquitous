package techgravy.sunshine.utils;

import android.content.Context;
import android.content.SharedPreferences;

import timber.log.Timber;


/**
 * Created by sudendra on 24/1/15.
 */
public final class PreferenceManager {

    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public PreferenceManager(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("sunshine_techgravy", Context.MODE_PRIVATE);
        Timber.tag("PreferenceManager");
    }

    private SharedPreferences getPref() {
        return mSharedPreferences;
    }

    public void setWeatherNotificationToggle(boolean notificationToggle) {
        Timber.d("notificationToggle = " + notificationToggle);
        mSharedPreferences.edit().putBoolean("notificationToggle", notificationToggle).apply();
    }

    public boolean getWeatherNotificationToggle() {
        return mSharedPreferences.getBoolean("notificationToggle", true);
    }

    public void setUnit(String tempUnit) {
        Timber.d("tempUnit = " + tempUnit);
        mSharedPreferences.edit().putString("tempUnit", tempUnit).apply();
    }

    public String getUnit() {
        return mSharedPreferences.getString("tempUnit", "metric");
    }

    public void setIconPack(String iconPack) {
        Timber.d("iconPack = " + iconPack);
        mSharedPreferences.edit().putString("iconPack", iconPack).apply();
    }

    public String getIconPack() {
        return mSharedPreferences.getString("iconPack", CommonUtils.ICON_PACK_DEFAULT);
    }
}