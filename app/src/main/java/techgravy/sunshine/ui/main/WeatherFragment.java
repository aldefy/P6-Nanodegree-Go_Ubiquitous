package techgravy.sunshine.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushSearchCallback;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
    WeatherDetailInterface detailInterface;

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
        forecastList = new ArrayList<>();

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
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        adapter = new WeekRVAdapter(getActivity(), forecastList, preferenceManager.getUnit(), preferenceManager.getIconPack(), this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        //add ItemDecoration
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(4));
        recyclerView.setAdapter(adapter);

        //NotificationHelper.expandablePictureNotification(MainActivity.this, "New Notification", "http://api.randomuser.me/portraits/women/39.jpg")
        getForecastApi = ForecastApiGenerator.createService(GetForecastApi.class);

        // callDBFirst();
        checkAdapterIsEmpty(forecastEmpty);
        if (savedInstanceState != null) {
            forecastList = (ArrayList<WeatherForecastModel>) savedInstanceState.get(CommonUtils.LIST_SAVE_INSTANCE);
            adapter.notifyDataSetChanged();
        } else
            fetchWeatherFromServer();
        return view;
    }

    private void callDBFirst() {
        Observable.create(new Observable.OnSubscribe<WeatherResponse>() {
            @Override
            public void call(Subscriber<? super WeatherResponse> subscriber) {
                new RushSearch().find(WeatherResponse.class, new RushSearchCallback<WeatherResponse>() {
                    @Override
                    public void complete(List<WeatherResponse> list) {
                        if (list.size() > 0) {
                            subscriber.onNext(list.get(0));
                            subscriber.onCompleted(); // Nothing more to emit
                        } else {
                            subscriber.onError(new Throwable("No saved response"));
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<WeatherResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error , fetching from server");
                checkAdapterIsEmpty(forecastEmpty);
                fetchWeatherFromServer();
            }

            @Override
            public void onNext(WeatherResponse weatherResponse) {
                forecastList.clear();
                forecastList.addAll(weatherResponse.getList());
                Timber.tag("DBLoaded").d(weatherResponse.getList().toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchWeatherFromServer() {
        Timber.tag("Calling").d("Calling Api");
        getForecastApi.getWeekForecast("bangalore", "json", "metric", "14", API_KEY).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               /* .map(new Func1<WeatherResponse, WeatherResponse>() {
                    @Override
                    public WeatherResponse call(WeatherResponse weatherResponse) {
                        *//* Get all objects *//*
                        WeatherResponse response = weatherResponse;
                        List<WeatherResponse> objects = new RushSearch().find(WeatherResponse.class);
                        RushCore.getInstance().delete(objects, new RushCallback() {
                            @Override
                            public void complete() {
                                response.save(new RushCallback() {
                                    @Override
                                    public void complete() {
                                        Timber.tag("RushSaved");
                                        Timber.d(response.toString());
                                    }
                                });
                            }
                        });
                        return weatherResponse;
                    }
                })*/
                .map(new Func1<WeatherResponse, WeatherResponse>() {
                    @Override
                    public WeatherResponse call(WeatherResponse weatherResponse) {
                        Observable<WeatherHeaderModel> quotaObservable = Observable.create(
                                new Observable.OnSubscribe<WeatherHeaderModel>() {
                                    @Override
                                    public void call(Subscriber<? super WeatherHeaderModel> sub) {
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
                                        Timber.tag("Header").d(model.toString());
                                        sub.onNext(model);
                                        sub.onCompleted();
                                    }
                                }
                        );
                        quotaObservable
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(MainApplication.getApplication().getSubscriber());

                        return weatherResponse;
                    }
                })
                .subscribe(new Subscriber<WeatherResponse>() {
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
                    public void onNext(WeatherResponse model) {
                        forecastList.clear();
                        forecastList.addAll(model.getList());
                        adapter.notifyDataSetChanged();
                        checkAdapterIsEmpty("Unable to fetch data at the moment");

                    }
                });
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
        outState.putParcelableArrayList(CommonUtils.LIST_SAVE_INSTANCE,
                (ArrayList<? extends Parcelable>) forecastList);
    }
}
