package techgravy.sunshine.ui.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.api.ForecastApiGenerator;
import techgravy.sunshine.api.GetForecastApi;
import techgravy.sunshine.interfaces.WeatherClickInterface;
import techgravy.sunshine.interfaces.WeatherDetailInterface;
import techgravy.sunshine.models.Temperature;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.ConnectivityUtil;
import techgravy.sunshine.utils.PreferenceManager;
import techgravy.sunshine.utils.VerticalSpaceItemDecoration;
import timber.log.Timber;

import static techgravy.sunshine.BuildConfig.API_KEY;

/**
 * Created by aditlal on 12/04/16.
 */
public class WeatherFragment extends Fragment implements WeatherClickInterface {
    private static final int WEATHER_NOTIFICATION_ID = 1040;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    GetForecastApi getForecastApi;
    WeekRVAdapter adapter;
    List<WeatherForecastModel> forecastList;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindString(R.string.forecast_empty)
    String forecastEmpty;
    private WeatherDetailInterface detailInterface;
    private Subscriber<WeatherResponse> weatherResponseSubscriber;
    //  Realm realm;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        detailInterface = ((WeatherDetailInterface) context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Timber.tag("FragmentTag").d("onCreate");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            forecastList = (ArrayList<WeatherForecastModel>) savedInstanceState.get(CommonUtils.LIST_SAVE_INSTANCE);
            Timber.d("ToDo Adapter");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_week, container, false);
        ButterKnife.bind(this, view);
        Timber.tag("FragmentTag").d("onCreateView");
        Timber.tag("Flows").d("onCreateView");
        init();
        return view;
    }

    private void init() {
        forecastList = new ArrayList<>();
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        adapter = new WeekRVAdapter(getActivity(), forecastList, preferenceManager.getUnit(), preferenceManager.getIconPack(), this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        //add ItemDecoration
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(4));
        recyclerView.setAdapter(adapter);

        getForecastApi = ForecastApiGenerator.createService(GetForecastApi.class);
        initSubscriber();
        //fetchWeatherFromServer();

        // fetchWeatherFromServer();
        if (!ConnectivityUtil.isConnected(getActivity()))
            callDBFirst();
        else
            fetchWeatherFromServer();
        checkAdapterIsEmpty(forecastEmpty);
    }

    private void callDBFirst() {
        Timber.tag("Flows").d("callDBFirst");
        List<WeatherResponse> list = new RushSearch().find(WeatherResponse.class);
        List<Temperature> temperatureList = new RushSearch().find(Temperature.class);
        Timber.tag("TempDB").d(temperatureList.toString() + "");
       /* WeatherResponse response = list.get(0);
        for (WeatherForecastModel model : response.getList()) {
            if (temperatureList != null && temperatureList.size() >= 1)
                model.setTemp(temperatureList.get(0));
        }*/
        getActivity().runOnUiThread(() -> {
            if (list != null) {
                if (list.size() > 0) {
                    Timber.tag("Flows").d("callDBFirst");
                    Timber.tag("rushGet").d(list.get(0).toString());

                    weatherResponseSubscriber.onNext(list.get(0));
                    weatherResponseSubscriber.onCompleted(); // Nothing more to emit

               /* weatherResponseSubscriber.onNext(list.get(0));
                weatherResponseSubscriber.onCompleted(); // Nothing more to emit*/
                } else {
                    Timber.tag("Flows").d("callDBFirst");
                    weatherResponseSubscriber.onError(new Throwable("No saved response"));
                    fetchWeatherFromServer();
                }
            } else {
                Timber.tag("Flows").d("callDBFirst");
                weatherResponseSubscriber.onError(new Throwable("No saved response"));
                fetchWeatherFromServer();
            }
        });

    }

    private void initSubscriber() {
        Timber.tag("initSubscriber").d("init weatherResponseSubscriber");
        Timber.tag("Flows").d("initSubscriber");

        weatherResponseSubscriber = new Subscriber<WeatherResponse>() {
            @Override
            public void onCompleted() {
                //   forecastList.clear();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error");
                checkAdapterIsEmpty("Unable to fetch data at the moment");
            }

            @Override
            public void onNext(WeatherResponse weatherResponse) {
                Timber.tag("Flows").d("initSubscriber");

                forecastList.clear();
                forecastList.addAll(weatherResponse.getList());
                adapter.notifyDataSetChanged();
                WeatherHeaderModel model = new WeatherHeaderModel();
                model.setCity(weatherResponse.getCity().getName());
                model.setHumidity(getActivity().getString(R.string.format_humidity, weatherResponse.getList().get(0).getHumidity()));
                model.setWind(CommonUtils.getFormattedWind(getActivity(), preferenceManager.getUnit(), weatherResponse.getList().get(0).getSpeed(), weatherResponse.getList().get(0).getDeg()));
                model.setPressure(getActivity().getString(R.string.format_pressure, weatherResponse.getList().get(0).getPressure()));
                int tempType = CommonUtils.calculateTimeOfDay();
                if (tempType == CommonUtils.TIME_NIGHT)
                    model.setTemp(weatherResponse.getList().get(0).getTemp() != null ? weatherResponse.getList().get(0).getTemp().getNight() : 0);
                else if (tempType == CommonUtils.TIME_MORNING)
                    model.setTemp(weatherResponse.getList().get(0).getTemp() != null ? weatherResponse.getList().get(0).getTemp().getMorn() : 0);
                else if (tempType == CommonUtils.TIME_DAY)
                    model.setTemp(weatherResponse.getList().get(0).getTemp() != null ? weatherResponse.getList().get(0).getTemp().getDay() : 0);
                else if (tempType == CommonUtils.TIME_EVE)
                    model.setTemp(weatherResponse.getList().get(0).getTemp() != null ? weatherResponse.getList().get(0).getTemp().getEve() : 0);
                else
                    model.setTemp(weatherResponse.getList().get(0).getTemp().getMax());
                model.setWeatherId(weatherResponse.getList().get(0).getWeather().get(0).getmId());
                model.setWeatherCondition(CommonUtils.getStringForWeatherCondition(getActivity(), weatherResponse.getList().get(0).getWeather().get(0).getmId()));

                if (weatherResponse.getList().get(0).getTemp() != null) {
                    String contentText =   String.format(getString(R.string.format_notification),
                            CommonUtils.getStringForWeatherCondition(getActivity(), weatherResponse.getList().get(0).getWeather().get(0).getmId()),
                            CommonUtils.formatTemperature(getActivity(), weatherResponse.getList().get(0).getTemp().getMax(), preferenceManager.getUnit()),
                            CommonUtils.formatTemperature(getActivity(), weatherResponse.getList().get(0).getTemp().getMin(), preferenceManager.getUnit()));
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getActivity())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Bangalore")
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(getActivity(), MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());
                }
                Timber.tag("Headersubscriber").d(model.toString());

                 /*   Observable<WeatherHeaderModel> quotaObservable = Observable.create(
                            new Observable.OnSubscribe<WeatherHeaderModel>() {
                                @Override
                                public void call(Subscriber<? super WeatherHeaderModel> sub) {
                                    sub.onNext(model);
                                    realm.executeTransaction(realm1 -> realm1.copyToRealm(model));
                                    sub.onCompleted();
                                }
                            }
                    );*/
                if (MainApplication.getApplication().getWeatherHeaderModelSubscriber() != null)
                    Timber.tag("WeatherHeaderSubscriber").d("not null");
                else
                    Timber.tag("WeatherHeaderSubscriber").d("null");
                Observable.just(model).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(MainApplication.getApplication().getWeatherHeaderModelSubscriber());


                //  quotaObservable
                checkAdapterIsEmpty("Unable to fetch data at the moment");

            }
        };
        MainApplication.getApplication().setWeatherResponseSubscriber(weatherResponseSubscriber);
    }

    public int getForecastNextKey() {
        //return realm.where(WeatherForecastModel.class).max("id").intValue() + 1;
        return 1;
    }

    private void fetchWeatherFromServer() {
        Timber.tag("Flows").d("fetchWeatherFromServer");
        getForecastApi.getWeekForecast(preferenceManager.getCity(), "json", "metric", "14", API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(weatherResponse -> {
                    Timber.tag("Flows").d("fetchWeatherFromServer");
                    RushCore.getInstance().clearDatabase();
                    weatherResponse.save(() -> Timber.tag("rushSaved").d("weatherResponse = " + weatherResponse.toString()));
                 /*   for (WeatherForecastModel model : weatherResponse.getList())
                        model.save(() -> Timber.tag("rushSaved").d("weatherForecast = " + model.toString()));*/
                    return weatherResponse;
                })
                .subscribe(weatherResponseSubscriber);

    }

    private void checkAdapterIsEmpty(String message) {
        if (emptyTextView != null) {
            emptyTextView.setText(message);
            if (adapter != null)
                if (adapter.getItemCount() == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        weatherResponseSubscriber.unsubscribe();
    }


    public void refreshIcons() {
        Timber.d("refreshIcons() called");
        adapter.updateIcons(preferenceManager.getIconPack());
    }

    public void refreshUnit() {
        Timber.d("refreshIcons() called");
        adapter.updateUnit(preferenceManager.getUnit());
    }

    @Override
    public void itemClicked(WeatherForecastModel model) {
        if (detailInterface != null)
            detailInterface.loadDetails(model);
    }

    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
     /*   outState.putParcelableArrayList(CommonUtils.LIST_SAVE_INSTANCE,
                (ArrayList<? extends Parcelable>) forecastList);*/
    }
}
