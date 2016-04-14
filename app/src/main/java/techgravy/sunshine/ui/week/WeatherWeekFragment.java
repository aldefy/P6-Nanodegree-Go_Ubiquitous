package techgravy.sunshine.ui.week;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushSearchCallback;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.api.ApiGenerator;
import techgravy.sunshine.api.GetForecastApi;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherResponse;
import techgravy.sunshine.ui.VerticalSpaceItemDecoration;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

import static techgravy.sunshine.BuildConfig.API_KEY;

/**
 * Created by aditlal on 12/04/16.
 */
public class WeatherWeekFragment extends Fragment {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    GetForecastApi getForecastApi;
    WeekRVAdapter adapter;
    List<WeatherForecastModel> forecastList;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_week, container, false);
        ButterKnife.bind(this, view);
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        forecastList = new ArrayList<>();
        adapter = new WeekRVAdapter(getActivity(), forecastList, preferenceManager.getUnit(), preferenceManager.getIconPack());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        //add ItemDecoration
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(4));
        recyclerView.setAdapter(adapter);

        //NotificationHelper.expandablePictureNotification(MainActivity.this, "New Notification", "http://api.randomuser.me/portraits/women/39.jpg")
        getForecastApi = ApiGenerator.createService(GetForecastApi.class);
        swipeRefresh.setRefreshing(false);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchWeatherFromServer();
            }
        });
        // Configure the refreshing colors
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        callDBFirst();
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
                swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error , fetching from server");
                swipeRefresh.setRefreshing(false);
                // fetchWeatherFromServer();
            }

            @Override
            public void onNext(WeatherResponse weatherResponse) {
                forecastList.clear();
                forecastList.addAll(weatherResponse.getList());
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void fetchWeatherFromServer() {
        Timber.d("Calling Api");
        getForecastApi.getWeekForecast("bangalore", "json", "metric", "14", API_KEY).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeatherResponse, WeatherResponse>() {
                    @Override
                    public WeatherResponse call(WeatherResponse weatherResponse) {
                        /* Get all objects */
                        List<WeatherResponse> objects = new RushSearch().find(WeatherResponse.class);
                        RushCore.getInstance().delete(objects, new RushCallback() {
                            @Override
                            public void complete() {
                                weatherResponse.save();
                            }
                        });
                        return weatherResponse;
                    }
                })
                .subscribe(new Subscriber<WeatherResponse>() {
                    @Override
                    public void onCompleted() {
                        swipeRefresh.setRefreshing(false);
                        //   forecastList.clear();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error");
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(WeatherResponse model) {
                        forecastList.clear();
                        forecastList.addAll(model.getList());
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);

                    }
                });
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
}
