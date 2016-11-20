package techgravy.sunshine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import rx.Subscriber;
import rx.schedulers.Schedulers;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.api.ForecastApiGenerator;
import techgravy.sunshine.api.GetForecastApi;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

import static techgravy.sunshine.BuildConfig.API_KEY;


public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "SunshineAdapterTag";
    private static final String ACTION_DATA_UPDATED =
            "techgravy.sunshine.ACTION_DATA_UPDATED";
    private GetForecastApi getForecastApi;
    private PreferenceManager preferenceManager;
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private Context context;


    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Timber.tag(LOG_TAG).d("onPerformSync Called.");
        fetchWeatherFromServer();

    }

    private void fetchWeatherFromServer() {
        Timber.tag(LOG_TAG).d("Calling Api");
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        getForecastApi = ForecastApiGenerator.createService(GetForecastApi.class);
        Context context = getContext();
        getForecastApi.getWeekForecast("bangalore", "json", "metric", "14", API_KEY).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(weatherResponse -> {
                   // weatherResponse.save(() -> Timber.tag("rushSaved").d("Saved api sync data " + weatherResponse.hashCode()));
                    WeatherHeaderModel model = new WeatherHeaderModel();
                    model.setCity(weatherResponse.getCity().getName());
                    model.setHumidity(context.getString(R.string.format_humidity, weatherResponse.getList().get(0).getHumidity()));
                    model.setWind(CommonUtils.getFormattedWind(context, preferenceManager.getUnit(), weatherResponse.getList().get(0).getSpeed(), weatherResponse.getList().get(0).getDeg()));
                    model.setPressure(context.getString(R.string.format_pressure, weatherResponse.getList().get(0).getPressure()));
                    int tempType = CommonUtils.calculateTimeOfDay();
                    if (tempType == CommonUtils.TIME_NIGHT)
                        model.setTemp(weatherResponse.getList().get(0).getTemp().getNight());
                    else if (tempType == CommonUtils.TIME_MORNING)
                        model.setTemp(weatherResponse.getList().get(0).getTemp().getMorn());
                    else if (tempType == CommonUtils.TIME_DAY)
                        model.setTemp(weatherResponse.getList().get(0).getTemp().getDay());
                    else if (tempType == CommonUtils.TIME_EVE)
                        model.setTemp(weatherResponse.getList().get(0).getTemp().getEve());
                    else
                        model.setTemp(weatherResponse.getList().get(0).getTemp().getMax());
                    model.setMinTemp(weatherResponse.getList().get(0).getTemp().getMin());
                    model.setWeatherId(weatherResponse.getList().get(0).getWeather().get(0).getmId());
                    model.setWeatherCondition(CommonUtils.getStringForWeatherCondition(context, weatherResponse.getList().get(0).getWeather().get(0).getmId()));
                    Timber.tag(LOG_TAG).d(model.toString());
                    WearableDataService sunshineWearableConnector = new WearableDataService(getContext());
                    sunshineWearableConnector.onNotifyWearable(model.getWeatherId(),
                            CommonUtils.formatTemperature(context, model.getTemp(), preferenceManager.getUnit()),
                            CommonUtils.formatTemperature(context, weatherResponse.getList().get(0).getTemp().getMin(), preferenceManager.getUnit()));

                  /*  Observable<WeatherHeaderModel> quotaObservable = Observable.create(
                            new Observable.OnSubscribe<WeatherHeaderModel>() {
                                @Override
                                public void call(Subscriber<? super WeatherHeaderModel> sub) {

                                    sub.onNext(model);
                                  //  realm.executeTransaction(realm1 -> realm1.copyToRealm(model));
                                    sub.onCompleted();
                                }
                            }
                    );
                    quotaObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(MainApplication.getApplication().getWeatherHeaderModelSubscriber());*/

                    return weatherResponse;
                }).subscribe(new Subscriber<WeatherResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(WeatherResponse weatherResponse) {

            }
        });
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }


    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Timber.tag(LOG_TAG).d("syncImmediately Called.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        //  syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}