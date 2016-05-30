package techgravy.sunshine.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ActionProvider;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

/**
 * Created by aditlal on 18/04/16.
 */
public class WeatherDetailFragment extends Fragment {


    @Bind(R.id.aboutImageBackground)
    ImageView aboutImageBackground;
    @Bind(R.id.alphaView)
    View alphaView;
    @Bind(R.id.weatherCityTextView)
    TextView weatherCityTextView;
    @Bind(R.id.weatherDateTextView)
    TextView weatherDateTextView;
    @Bind(R.id.weatherHeadlineTextView)
    TextView weatherHeadlineTextView;
    @Bind(R.id.weatherSubHeadTextView)
    TextView weatherSubHeadTextView;
    @Bind(R.id.temperatureHeading)
    TextView temperatureHeading;
    @Bind(R.id.tempDivider)
    View tempDivider;
    @Bind(R.id.morningTimeLabel)
    TextView morningTimeLabel;
    @Bind(R.id.morningTimeTemp)
    TextView morningTimeTemp;
    @Bind(R.id.afternoonTimeLabel)
    TextView afternoonTimeLabel;
    @Bind(R.id.afternoonTimeTemp)
    TextView afternoonTimeTemp;
    @Bind(R.id.eveningTimeLabel)
    TextView eveningTimeLabel;
    @Bind(R.id.eveningTimeTemp)
    TextView eveningTimeTemp;
    @Bind(R.id.nightTimeLabel)
    TextView nightTimeLabel;
    @Bind(R.id.nightTimeTemp)
    TextView nightTimeTemp;
    @Bind(R.id.weatherTemperatureLayout)
    LinearLayout weatherTemperatureLayout;
    @Bind(R.id.othersHeading)
    TextView othersHeading;
    @Bind(R.id.otherDivider)
    View otherDivider;
    @Bind(R.id.weatherHumidityTitleTextView)
    TextView weatherHumidityTitleTextView;
    @Bind(R.id.weatherHumidityValueTextView)
    TextView weatherHumidityValueTextView;
    @Bind(R.id.weatherHumidityDetailsLayout)
    LinearLayout weatherHumidityDetailsLayout;
    @Bind(R.id.weatherPressureTitleTextView)
    TextView weatherPressureTitleTextView;
    @Bind(R.id.weatherPressureValueTextView)
    TextView weatherPressureValueTextView;
    @Bind(R.id.weatherPressureDetailsLayout)
    LinearLayout weatherPressureDetailsLayout;
    @Bind(R.id.weatherWindTitleTextView)
    TextView weatherWindTitleTextView;
    @Bind(R.id.weatherWindValueTextView)
    TextView weatherWindValueTextView;
    @Bind(R.id.weatherWindDetailsLayout)
    LinearLayout weatherWindDetailsLayout;
    @Bind(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    private WeatherForecastModel forecast;
    private PreferenceManager preferenceManager;
    private ActionProvider mShareActionProvider;
    Realm realm;
    int forecastId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        handleArgs();
        setHasOptionsMenu(true);
    }

    private void handleArgs() {
        Bundle extras = getArguments();
        forecastId = extras.getInt("forecastId");
        Timber.tag("DetailsID").d(forecastId + "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        realm.executeTransaction(realm1 -> {
            Timber.tag("DetailsID").d(forecastId + "");

            forecast = realm1.where(WeatherForecastModel.class).equalTo("id", forecastId).findFirst();
            if (forecast != null) initViews();
        });
        return rootView;
    }

    private void initViews() {

        weatherDateTextView.setText(CommonUtils.formatWeekDays(forecast.getDt()));
        int tempType = CommonUtils.calculateTimeOfDay();
        if (tempType == CommonUtils.TIME_NIGHT)
            weatherHeadlineTextView.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getNight(), preferenceManager.getUnit()));

        else if (tempType == CommonUtils.TIME_MORNING)
            weatherHeadlineTextView.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getMorn(), preferenceManager.getUnit()));

        else if (tempType == CommonUtils.TIME_DAY)
            weatherHeadlineTextView.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getDay(), preferenceManager.getUnit()));

        else if (tempType == CommonUtils.TIME_EVE)
            weatherHeadlineTextView.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getEve(), preferenceManager.getUnit()));
        else
            weatherHeadlineTextView.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getMax(), preferenceManager.getUnit()));

        weatherSubHeadTextView.setText(CommonUtils.getStringForWeatherCondition(getActivity(), forecast.getWeather().get(0).getmId()));
        weatherSubHeadTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(CommonUtils.getWeatherIconFromWeather(getActivity(), forecast.getWeather().get(0).getmId(), preferenceManager.getIconPack()), null, null, null);
        morningTimeTemp.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getMorn(), preferenceManager.getUnit()));
        afternoonTimeTemp.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getDay(), preferenceManager.getUnit()));
        eveningTimeTemp.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getEve(), preferenceManager.getUnit()));
        nightTimeTemp.setText(CommonUtils.formatTemperature(getActivity(), forecast.getTemp().getNight(), preferenceManager.getUnit()));
        weatherWindValueTextView.setText(CommonUtils.getFormattedWind(getActivity(), preferenceManager.getUnit(), forecast.getSpeed(), forecast.getDeg()));
        weatherHumidityValueTextView.setText(getActivity().getString(R.string.format_humidity, forecast.getHumidity()));
        weatherPressureValueTextView.setText(getActivity().getString(R.string.format_pressure, forecast.getPressure()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    // Call to update the share intent

}
