package techgravy.sunshine;

import android.app.Application;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;
import rx.Subscriber;
import techgravy.sunshine.interfaces.DaggerModuleComponent;
import techgravy.sunshine.interfaces.ModuleComponent;
import techgravy.sunshine.models.Coord;
import techgravy.sunshine.models.Temperature;
import techgravy.sunshine.models.Weather;
import techgravy.sunshine.models.WeatherForecastCity;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.module.PrefModule;
import techgravy.sunshine.utils.LoggerTree;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

/**
 * Created by aditlal on 04/04/16.
 */
public class MainApplication extends Application {


    public ModuleComponent moduleComponent;
    PreferenceManager preferenceManager;
    public static MainApplication application;
    private Subscriber<WeatherHeaderModel> weatherHeaderModelSubscriber;
    private Subscriber<WeatherResponse> weatherResponseSubscriber;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new LoggerTree());
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

    public Subscriber<WeatherHeaderModel> getWeatherHeaderModelSubscriber() {
        return weatherHeaderModelSubscriber;
    }

    public void setWeatherHeaderModelSubscriber(Subscriber<WeatherHeaderModel> subscriber) {
        this.weatherHeaderModelSubscriber = subscriber;
    }

    public Subscriber<WeatherResponse> getWeatherResponseSubscriber() {
        return weatherResponseSubscriber;
    }

    public void setWeatherResponseSubscriber(Subscriber<WeatherResponse> weatherResponseSubscriber) {
        this.weatherResponseSubscriber = weatherResponseSubscriber;
    }
}
