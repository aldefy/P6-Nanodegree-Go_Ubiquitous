package techgravy.sunshine.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.meteocons_typeface_library.Meteoconcs;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Random;

import techgravy.sunshine.R;
import timber.log.Timber;

/**
 * Created by aditlal on 31/03/16.
 */
public class CommonUtils {

    public final static String ICON_PACK_METEOCONCS = "Meteoconcs Pack";
    public final static String ICON_PACK_DEFAULT = "Default Pack";
    public final static int TIME_MORNING = 1;
    public final static int TIME_DAY = 2;
    public final static int TIME_EVE = 3;
    public final static int TIME_NIGHT = 4;
    public final static int TIME_UNKNOWN = 5;
    public final static String LIST_SAVE_INSTANCE = "forecast_list_instance";


    public static Snackbar displaySnackBar(View view, String message) {
        if (view != null && message != null) {
            return Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        }
        return null;
    }

    public static void displayToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to provide the string according to the weather
     * condition id returned by the OpenWeatherMap call.
     *
     * @param context   Android context
     * @param weatherId from OpenWeatherMap API response
     * @return string for the weather condition. null if no relation is found.
     */
    public static String getStringForWeatherCondition(Context context, int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        int stringId;
        if (weatherId >= 200 && weatherId <= 232) {
            stringId = R.string.condition_2xx;
        } else if (weatherId >= 300 && weatherId <= 321) {
            stringId = R.string.condition_3xx;
        } else switch (weatherId) {
            case 500:
                stringId = R.string.condition_500;
                break;
            case 501:
                stringId = R.string.condition_501;
                break;
            case 502:
                stringId = R.string.condition_502;
                break;
            case 503:
                stringId = R.string.condition_503;
                break;
            case 504:
                stringId = R.string.condition_504;
                break;
            case 511:
                stringId = R.string.condition_511;
                break;
            case 520:
                stringId = R.string.condition_520;
                break;
            case 531:
                stringId = R.string.condition_531;
                break;
            case 600:
                stringId = R.string.condition_600;
                break;
            case 601:
                stringId = R.string.condition_601;
                break;
            case 602:
                stringId = R.string.condition_602;
                break;
            case 611:
                stringId = R.string.condition_611;
                break;
            case 612:
                stringId = R.string.condition_612;
                break;
            case 615:
                stringId = R.string.condition_615;
                break;
            case 616:
                stringId = R.string.condition_616;
                break;
            case 620:
                stringId = R.string.condition_620;
                break;
            case 621:
                stringId = R.string.condition_621;
                break;
            case 622:
                stringId = R.string.condition_622;
                break;
            case 701:
                stringId = R.string.condition_701;
                break;
            case 711:
                stringId = R.string.condition_711;
                break;
            case 721:
                stringId = R.string.condition_721;
                break;
            case 731:
                stringId = R.string.condition_731;
                break;
            case 741:
                stringId = R.string.condition_741;
                break;
            case 751:
                stringId = R.string.condition_751;
                break;
            case 761:
                stringId = R.string.condition_761;
                break;
            case 762:
                stringId = R.string.condition_762;
                break;
            case 771:
                stringId = R.string.condition_771;
                break;
            case 781:
                stringId = R.string.condition_781;
                break;
            case 800:
                stringId = R.string.condition_800;
                break;
            case 801:
                stringId = R.string.condition_801;
                break;
            case 802:
                stringId = R.string.condition_802;
                break;
            case 803:
                stringId = R.string.condition_803;
                break;
            case 804:
                stringId = R.string.condition_804;
                break;
            case 900:
                stringId = R.string.condition_900;
                break;
            case 901:
                stringId = R.string.condition_901;
                break;
            case 902:
                stringId = R.string.condition_902;
                break;
            case 903:
                stringId = R.string.condition_903;
                break;
            case 904:
                stringId = R.string.condition_904;
                break;
            case 905:
                stringId = R.string.condition_905;
                break;
            case 906:
                stringId = R.string.condition_906;
                break;
            case 951:
                stringId = R.string.condition_951;
                break;
            case 952:
                stringId = R.string.condition_952;
                break;
            case 953:
                stringId = R.string.condition_953;
                break;
            case 954:
                stringId = R.string.condition_954;
                break;
            case 955:
                stringId = R.string.condition_955;
                break;
            case 956:
                stringId = R.string.condition_956;
                break;
            case 957:
                stringId = R.string.condition_957;
                break;
            case 958:
                stringId = R.string.condition_958;
                break;
            case 959:
                stringId = R.string.condition_959;
                break;
            case 960:
                stringId = R.string.condition_960;
                break;
            case 961:
                stringId = R.string.condition_961;
                break;
            case 962:
                stringId = R.string.condition_962;
                break;
            default:
                stringId = R.string.condition_unknown;
        }
        return context.getString(stringId);
    }

    public static String formatWeekDays(String dt) {
        DateTime givenDT = new DateTime(Long.parseLong(dt) * 1000, DateTimeZone.getDefault());
        DateTime todayDT = new DateTime(DateTimeZone.getDefault());
        if (givenDT.withTimeAtStartOfDay().equals(todayDT.withTimeAtStartOfDay())) {
            return "Today";
        } else if (givenDT.withTimeAtStartOfDay().equals(todayDT.plusDays(1).withTimeAtStartOfDay())) {
            return "Tomorrow";
        } else
            return givenDT.toString("EEEE");
    }

    public static IconicsDrawable getWeatherIconFromWeather(Context context, int weatherId, String iconPack) {
        Timber.tag("WeatherIcon");
        Timber.d(weatherId + "");
        switch (iconPack) {
            case ICON_PACK_METEOCONCS:
                return getMeteoconcsIcons(context, weatherId);
            case ICON_PACK_DEFAULT:
                return getDefaultIcons(context, weatherId);
            default:
                return new IconicsDrawable(context)
                        .icon(Meteoconcs.Icon.met_temperature)
                        .color(ContextCompat.getColor(context, R.color.white))
                        .sizeDp(24);
        }
    }

    private static IconicsDrawable getDefaultIcons(Context context, int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_storm_warning)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 300 && weatherId <= 321) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_rain)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 500 && weatherId <= 504) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_rain)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 511) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_snow)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 520 && weatherId <= 531) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_rain)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 600 && weatherId <= 622) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_forecast_io_snow)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 701 && weatherId <= 761) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_fog)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 761 || weatherId == 781) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_storm_warning)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 800) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_day_sunny_overcast)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 801) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_cloudy_windy)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 802 && weatherId <= 804) {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_cloudy)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else {
            return new IconicsDrawable(context)
                    .icon(WeatherIcons.Icon.wic_thermometer)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        }
    }

    private static IconicsDrawable getMeteoconcsIcons(Context context, int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud_flash)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 300 && weatherId <= 321) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_rain_inv)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 500 && weatherId <= 504) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_rain)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 511) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_snow)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 520 && weatherId <= 531) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_rain)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 600 && weatherId <= 622) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_snow_heavy)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 701 && weatherId <= 761) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_fog)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 761 || weatherId == 781) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud_flash_alt)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 800) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_sun)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId == 801) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_sun_inv)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else if (weatherId >= 802 && weatherId <= 804) {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        } else {
            return new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_compass)
                    .color(ContextCompat.getColor(context, R.color.white))
                    .sizeDp(24);
        }

    }


    public static String formatTemperature(Context context, double temperature, String isMetric) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";
        if (!isMetric.equalsIgnoreCase(context.getString(R.string.value_units_metric))) {
            temperature = (temperature * 1.8) + 32;
        }


        // For presentation, assume the user doesn't care about tenths of a degree.
        String returnedString = String.format(context.getString(R.string.format_temperature), temperature);
        if (!isMetric.equalsIgnoreCase(context.getString(R.string.value_units_metric))) {
            returnedString = returnedString.concat(" \u2109");//F
        } else {
            returnedString = returnedString.concat(" \u2103");//C

        }
        Timber.d("temperature=" + temperature + "->" + returnedString);
        return returnedString;
    }


    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String getFormattedWind(Context context, String isMetric, float windSpeed, float degrees) {
        int windFormat;
        if (!isMetric.equalsIgnoreCase(context.getString(R.string.value_units_metric))) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }

    public static void setTranslucentStatusBar(Window window, @ColorInt int color) {
        if (window == null) return;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(window, color);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat(window);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setTranslucentStatusBarLollipop(Window window, @ColorInt int color) {
        if (color == -1)
            window.setStatusBarColor(
                    ContextCompat.getColor(window.getContext(), R.color.primary_dark));
        else
            window.setStatusBarColor(color);
        Timber.tag("StatusBar").d(color + "");


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int calculateTimeOfDay() {
        LocalDateTime now = new LocalDateTime();

        if (now.getHourOfDay() > 0 && now.getHourOfDay() < 11) {
            return TIME_MORNING;
        } else if (now.getHourOfDay() >= 11 && now.getHourOfDay() < 18) {
            return TIME_DAY;
        } else if (now.getHourOfDay() >= 18 && now.getHourOfDay() < 20) {
            return TIME_EVE;
        } else if (now.getHourOfDay() >= 20 && now.getHourOfDay() < 11) {
            return TIME_NIGHT;
        } else {
            return TIME_UNKNOWN;
        }
    }

    public static int getRandomPhoto() {
        int max = 19;
        int min = 0;
        int diff = max - min;
        Random rn = new Random();
        int i = rn.nextInt(diff + 1);
        i += min;
        return i;
    }
}
