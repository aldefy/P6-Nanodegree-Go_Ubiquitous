package techgravy.sunshine.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import techgravy.sunshine.R;
import techgravy.sunshine.interfaces.WeatherClickInterface;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.utils.CommonUtils;
import timber.log.Timber;

/**
 * Created by aditlal on 12/04/16.
 */
public class WeekRVAdapter extends RecyclerView.Adapter<WeekRVAdapter.WeatherViewHolder> {

    List<WeatherForecastModel> forecastList;
    Context context;
    String unit, iconPack;
    private WeatherClickInterface weatherClickInterface;

    public WeekRVAdapter(Context context, List<WeatherForecastModel> forecastList, String unit, String iconPack, WeatherClickInterface weatherClickInterface) {
        this.forecastList = forecastList;
        if (forecastList.size() > 0)
            Timber.tag("AdapterNPE").d(forecastList.toString());
        this.context = context;
        this.unit = unit;
        this.iconPack = iconPack;
        this.weatherClickInterface = weatherClickInterface;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_weather_week, parent, false);
        return new WeatherViewHolder(v, weatherClickInterface);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        WeatherForecastModel forecast = forecastList.get(position);
        Timber.tag("Model");
        Timber.d(forecast.toString());
        holder.listItemDateTextview.setText(CommonUtils.formatWeekDays(forecast.getDt()));
        holder.listItemForecastTextview.setText(CommonUtils.getStringForWeatherCondition(context, forecast.getWeather().get(0).getmId()));
        holder.listItemHighTextview.setText(CommonUtils.formatTemperature(context, forecast.getTemp().getMax(), unit));
        holder.listItemLowTextview.setText(CommonUtils.formatTemperature(context, forecast.getTemp().getMin(), unit));
        holder.listItemIcon.setImageDrawable(CommonUtils.getWeatherIconFromWeather(context, forecast.getWeather().get(0).getmId(), iconPack));
        holder.itemView.setTag(forecast);
        runEnterAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public void updateUnit(String unit) {
        this.unit = unit;
        Timber.d("updateUnit() called");
        notifyDataSetChanged();
    }


    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_item_icon)
        ImageView listItemIcon;
        @Bind(R.id.list_item_date_textview)
        TextView listItemDateTextview;
        @Bind(R.id.list_item_forecast_textview)
        TextView listItemForecastTextview;
        @Bind(R.id.list_item_high_textview)
        TextView listItemHighTextview;
        @Bind(R.id.list_item_low_textview)
        TextView listItemLowTextview;
        WeatherClickInterface weatherClickInterface;

        WeatherViewHolder(View view, WeatherClickInterface weatherClickInterface) {
            super(view);
            ButterKnife.bind(this, view);
            this.weatherClickInterface = weatherClickInterface;
            itemView.setOnClickListener(this::handleClick);
        }

        private void handleClick(View o) {
            if (weatherClickInterface != null)
                weatherClickInterface.itemClicked((WeatherForecastModel) o.getTag());
        }
    }

    public void updateIcons(String iconPack) {
        this.iconPack = iconPack;
        Timber.d("updateIcons() called");
        notifyDataSetChanged();
    }

    private void runEnterAnimation(View view) {
        view.setTranslationY(CommonUtils.getScreenHeight(context));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

}
