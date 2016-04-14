package techgravy.sunshine;

import android.app.Application;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;
import techgravy.sunshine.interfaces.DaggerModuleComponent;
import techgravy.sunshine.interfaces.ModuleComponent;
import techgravy.sunshine.models.Coord;
import techgravy.sunshine.models.Temperature;
import techgravy.sunshine.models.Weather;
import techgravy.sunshine.models.WeatherForecastCity;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.module.PrefModule;
import techgravy.sunshine.utils.PreferenceManager;
import techgravy.sunshine.utils.logger.LogLevel;
import techgravy.sunshine.utils.logger.Logger;
import timber.log.Timber;

/**
 * Created by aditlal on 04/04/16.
 */
public class MainApplication extends Application {


    public ModuleComponent moduleComponent;
    PreferenceManager preferenceManager;
    public static MainApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Logger.init("Sunshine").methodOffset(3).logLevel(LogLevel.FULL);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    Logger.t(tag);
                    if (t != null)
                        Logger.e(t, message);
                    else
                        Logger.d(message);

                }
            });
            Timber.tag("Sunshine");
        }
        moduleComponent = DaggerModuleComponent.builder()
                .prefModule(new PrefModule(this, 2))
                .build();
        moduleComponent.inject(this);
        preferenceManager = moduleComponent.providePrefManager();
        application = this;
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        initializeDB();
    }

    private void initializeDB() {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        // Add classes
        classes.add(Coord.class);
        classes.add(Temperature.class);
        classes.add(Weather.class);
        classes.add(WeatherForecastCity.class);
        classes.add(WeatherForecastModel.class);
        classes.add(WeatherResponse.class);
        AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
        config.setClasses(classes);
        RushCore.initialize(config);
    }

    public static MainApplication getApplication() {
        return application;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}
