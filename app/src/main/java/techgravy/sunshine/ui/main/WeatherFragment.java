package techgravy.sunshine.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
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
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.PreferenceManager;
import techgravy.sunshine.utils.VerticalSpaceItemDecoration;
import timber.log.Timber;

import static techgravy.sunshine.BuildConfig.API_KEY;

/**
 * Created by aditlal on 12/04/16.
 */
public class WeatherFragment extends Fragment implements WeatherClickInterface {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    GetForecastApi getForecastApi;
    WeekRVAdapter adapter;
    List<WeatherForecastModel> forecastList;
    @Bind(R.id.emptyTextView)
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
        // realm = Realm.getDefaultInstance();

        init();

        //NotificationHelper.expandablePictureNotification(MainActivity.this, "New Notification", "http://api.randomuser.me/portraits/women/39.jpg")
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
        fetchWeatherFromServer();
        // callDBFirst();
        checkAdapterIsEmpty(forecastEmpty);
    }

    private void callDBFirst() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmResults<WeatherHeaderModel> results1 =
                    realm1.where(WeatherHeaderModel.class).findAll();
            Observable<WeatherHeaderModel> quotaObservable = Observable.create(
                    new Observable.OnSubscribe<WeatherHeaderModel>() {
                        @Override
                        public void call(Subscriber<? super WeatherHeaderModel> sub) {
                            sub.onNext(results1.first());
                            sub.onCompleted();
                        }
                    }
            );
            quotaObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MainApplication.getApplication().getWeatherHeaderModelSubscriber());
        });
        realm.executeTransaction(realm1 -> {
            RealmResults<WeatherHeaderModel> results1 =
                    realm1.where(WeatherHeaderModel.class).findAll();

            if (results1.size() != 0) {
                Observable<WeatherHeaderModel> quotaObservable = Observable.create(
                        new Observable.OnSubscribe<WeatherHeaderModel>() {
                            @Override
                            public void call(Subscriber<? super WeatherHeaderModel> sub) {
                                sub.onNext(results1.first());
                                realm.executeTransaction(realm1 -> realm1.copyToRealm(results1.first()));
                                sub.onCompleted();
                            }
                        }
                );
                quotaObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(MainApplication.getApplication().getWeatherHeaderModelSubscriber());
            } else {
                Timber.tag("Headersubscriber").d("Size is zero");

            }
        });

        realm.executeTransaction(realm1 -> {
            RealmResults<WeatherResponse> results1 =
                    realm1.where(WeatherResponse.class).findAll();

            if (results1.size() != 0) {
                WeatherResponse weatherResponse = results1.get(0);
                weatherResponseSubscriber.onNext(weatherResponse);
                weatherResponseSubscriber.onCompleted(); // Nothing more to emit
            } else
                fetchWeatherFromServer();
        });


       /* new RushSearch().find(WeatherResponse.class, list -> {
            if (list != null)
                if (list.size() > 0) {
                    Timber.tag("rushSaved").d(list.get(0).toString());
                    weatherResponseSubscriber.onNext(list.get(0));
                    weatherResponseSubscriber.onCompleted(); // Nothing more to emit
                } else {
                    weatherResponseSubscriber.onError(new Throwable("No saved response"));
                    fetchWeatherFromServer();
                }
            else {
                weatherResponseSubscriber.onError(new Throwable("No saved response"));
                fetchWeatherFromServer();
            }
        });*/


    }

    private void initSubscriber() {
        Timber.tag("initSubscriber").d("init weatherResponseSubscriber");

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
                    model.setTemp(weatherResponse.getList().get(0).getTemp().getNight());
                else if (tempType == CommonUtils.TIME_MORNING)
                    model.setTemp(weatherResponse.getList().get(0).getTemp().getMorn());
                else if (tempType == CommonUtils.TIME_DAY)
                    model.setTemp(weatherResponse.getList().get(0).getTemp().getDay());
                else if (tempType == CommonUtils.TIME_EVE)
                    model.setTemp(weatherResponse.getList().get(0).getTemp().getEve());
                else
                    model.setTemp(weatherResponse.getList().get(0).getTemp().getMax());
                model.setWeatherId(weatherResponse.getList().get(0).getWeather().get(0).getmId());
                model.setWeatherCondition(CommonUtils.getStringForWeatherCondition(getActivity(), weatherResponse.getList().get(0).getWeather().get(0).getmId()));
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
        getForecastApi.getWeekForecast("bangalore", "json", "metric", "14", API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(weatherResponse -> {


                   /* realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();*/
                    /*List<WeatherResponse> objects = new RushSearch().find(WeatherResponse.class);
                    RushCore.getInstance().delete(objects, () -> {
                        weatherResponse.save(() -> {
                            Timber.tag("RushSaved");
                            Timber.d(weatherResponse.toString());
                        });
                    });*/
                    // Get a Realm instance for this thread
                    // All writes must be wrapped in a transaction to facilitate safe multi threading
                    for (int i = 0; i < weatherResponse.getList().size(); i++) {
                        WeatherForecastModel forecastModel = weatherResponse.getList().get(i);
                        forecastModel.setId(i);
                    }
                    /*realm.executeTransaction(realm1 -> {
                        realm1.copyToRealm(weatherResponse);
                    });*/


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
        ButterKnife.unbind(this);
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
