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

import butterknife.BindView;
import butterknife.ButterKnife;
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


    @BindView(R.id.aboutImageBackground)
    ImageView aboutImageBackground;
    @BindView(R.id.alphaView)
    View alphaView;
    @BindView(R.id.weatherCityTextView)
    TextView weatherCityTextView;
    @BindView(R.id.weatherDateTextView)
    TextView weatherDateTextView;
    @BindView(R.id.weatherHeadlineTextView)
    TextView weatherHeadlineTextView;
    @BindView(R.id.weatherSubHeadTextView)
    TextView weatherSubHeadTextView;
    @BindView(R.id.temperatureHeading)
    TextView temperatureHeading;
    @BindView(R.id.tempDivider)
    View tempDivider;
    @BindView(R.id.morningTimeLabel)
    TextView morningTimeLabel;
    @BindView(R.id.morningTimeTemp)
    TextView morningTimeTemp;
    @BindView(R.id.afternoonTimeLabel)
    TextView afternoonTimeLabel;
    @BindView(R.id.afternoonTimeTemp)
    TextView afternoonTimeTemp;
    @BindView(R.id.eveningTimeLabel)
    TextView eveningTimeLabel;
    @BindView(R.id.eveningTimeTemp)
    TextView eveningTimeTemp;
    @BindView(R.id.nightTimeLabel)
    TextView nightTimeLabel;
    @BindView(R.id.nightTimeTemp)
    TextView nightTimeTemp;
    @BindView(R.id.weatherTemperatureLayout)
    LinearLayout weatherTemperatureLayout;
    @BindView(R.id.othersHeading)
    TextView othersHeading;
    @BindView(R.id.otherDivider)
    View otherDivider;
    @BindView(R.id.weatherHumidityTitleTextView)
    TextView weatherHumidityTitleTextView;
    @BindView(R.id.weatherHumidityValueTextView)
    TextView weatherHumidityValueTextView;
    @BindView(R.id.weatherHumidityDetailsLayout)
    LinearLayout weatherHumidityDetailsLayout;
    @BindView(R.id.weatherPressureTitleTextView)
    TextView weatherPressureTitleTextView;
    @BindView(R.id.weatherPressureValueTextView)
    TextView weatherPressureValueTextView;
    @BindView(R.id.weatherPressureDetailsLayout)
    LinearLayout weatherPressureDetailsLayout;
    @BindView(R.id.weatherWindTitleTextView)
    TextView weatherWindTitleTextView;
    @BindView(R.id.weatherWindValueTextView)
    TextView weatherWindValueTextView;
    @BindView(R.id.weatherWindDetailsLayout)
    LinearLayout weatherWindDetailsLayout;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    private WeatherForecastModel forecast;
    private PreferenceManager preferenceManager;
    private ActionProvider mShareActionProvider;
    //int forecastId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  realm = Realm.getDefaultInstance();
        handleArgs();
        setHasOptionsMenu(true);
    }

    private void handleArgs() {
        Bundle extras = getArguments();
        forecast = (WeatherForecastModel) extras.getSerializable("forecast");
        Timber.tag("Details").d(forecast + "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        initViews();
       /* realm.executeTransaction(realm1 -> {
            Timber.tag("DetailsID").d(forecastId + "");

            forecast = realm1.where(WeatherForecastModel.class).equalTo("id", forecastId).findFirst();
            if (forecast != null) initViews();
        });*/
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    // Call to update the share intent

}
