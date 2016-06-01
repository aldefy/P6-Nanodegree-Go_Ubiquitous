package techgravy.sunshine;

import android.app.Application;

import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Subscriber;
import techgravy.sunshine.interfaces.DaggerModuleComponent;
import techgravy.sunshine.interfaces.ModuleComponent;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.module.PrefModule;
import techgravy.sunshine.utils.LoggerTree;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

/**
 * Created by aditlal on 04/04/16.
 */
@ReportsCrashes(mailTo = "aditlal90@gmail.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
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
            ACRA.init(this);
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
        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);

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
