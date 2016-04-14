package techgravy.sunshine.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.meteocons_typeface_library.Meteoconcs;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.Subscription;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

/**
 * Created by aditlal on 05/04/16.
 */

public class SettingFragment extends Fragment {

    @Bind(R.id.weather_settings_option1)
    RelativeLayout weatherNotificationLayout;
    @Bind(R.id.weather_notifications_checkbox)
    CheckBox weatherNotificationCheckbox;
    @Bind(R.id.weather_settings_option2)
    RelativeLayout weatherTempUnitsLayout;
    @Bind(R.id.weather_settings_option4)
    RelativeLayout weatherIconsLayout;
    @Bind(R.id.temperature_units_value)
    TextView tempUnitValueTextView;
    @Bind(R.id.weather_icon_value)
    TextView iconPackValueTextView;
    @Bind(R.id.weather_icon_image)
    ImageButton weatherIconImage;

    @BindString(R.string.value_units_imperial)
    String unit_imperial;
    @BindString(R.string.value_units_metric)
    String unit_metric;

    @BindString(R.string.icon_pack_default)
    String icon_default;
    @BindString(R.string.icon_pack_meteoconcs)
    String icon_meteoconcs;
    int tempUnitType = 1, iconPackType = 2;


    Subscription notificationClickSubscription, notificationCheckboxSubscription, unitsClickSubscription, iconPackClickSubscription;
    PreferenceManager preferenceManager;

    SettingsRefreshInterface settingsRefreshInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        settingsRefreshInterface = ((SettingsRefreshInterface) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Timber.tag("Settings");
        ButterKnife.bind(this, rootView);
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        weatherNotificationCheckbox.setChecked(preferenceManager.getWeatherNotificationToggle());
        notificationCheckboxSubscription =
                RxCompoundButton.checkedChanges(weatherNotificationCheckbox)
                        .subscribe(o -> preferenceManager.setWeatherNotificationToggle(weatherNotificationCheckbox.isChecked()));
        notificationClickSubscription =
                RxView.clicks(weatherNotificationLayout).subscribe(view -> weatherNotificationCheckbox.setChecked(!preferenceManager.getWeatherNotificationToggle()));
        unitsClickSubscription = RxView.clicks(weatherTempUnitsLayout).subscribe(v -> handleTemperatureUnits());
        iconPackClickSubscription = RxView.clicks(weatherIconsLayout).subscribe(v -> handleIconPack());
        if (preferenceManager.getUnit().equalsIgnoreCase(unit_imperial)) {
            tempUnitValueTextView.setText(unit_imperial);
        } else {
            tempUnitValueTextView.setText(unit_metric);
        }
        if (preferenceManager.getIconPack().equalsIgnoreCase(icon_default)) {
            iconPackValueTextView.setText(icon_default);
            weatherIconImage.setImageDrawable(new IconicsDrawable(getActivity())
                    .icon(WeatherIcons.Icon.wic_day_sunny)
                    .color(ContextCompat.getColor(getActivity(), R.color.grey_500))
                    .sizeDp(24));
        } else {
            iconPackValueTextView.setText(icon_meteoconcs);
            weatherIconImage.setImageDrawable(new IconicsDrawable(getActivity())
                    .icon(Meteoconcs.Icon.met_sun)
                    .color(ContextCompat.getColor(getActivity(), R.color.grey_500))
                    .sizeDp(24));
        }
        return rootView;
    }

    private void handleIconPack() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_option, null);
        ((RadioButton) dialogView.findViewById(R.id.unit1)).setText(icon_default);
        ((RadioButton) dialogView.findViewById(R.id.unit2)).setText(icon_meteoconcs);
        if (preferenceManager.getIconPack().equalsIgnoreCase(icon_default)) {
            ((RadioButton) dialogView.findViewById(R.id.unit1)).setChecked(true);
        } else {
            ((RadioButton) dialogView.findViewById(R.id.unit2)).setChecked(true);
        }
        MaterialStyledDialog dialog = new MaterialStyledDialog(getActivity())
                .setTitle(getString(R.string.icon_pack_heading))
                .setCustomView(dialogView)
                .setHeaderColor(R.color.accent)
                .setScrollable(true)
                .withDialogAnimation(true, Duration.NORMAL)
                .setCancelable(true)
                .setNegative("Cancel", (dialog1, which) -> dialog1.cancel())
                .setPositive("Update", (dialog1, which) -> resetValue(dialogView, iconPackType))
                .setDescription("Select the icon pack to use")
                .build();
        dialog.show();
    }

    void handleTemperatureUnits() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_option, null);
        ((RadioButton) dialogView.findViewById(R.id.unit1)).setText(unit_metric);
        ((RadioButton) dialogView.findViewById(R.id.unit2)).setText(unit_imperial);
        if (preferenceManager.getUnit().equalsIgnoreCase(unit_imperial)) {
            ((RadioButton) dialogView.findViewById(R.id.unit2)).setChecked(true);
        } else {
            ((RadioButton) dialogView.findViewById(R.id.unit1)).setChecked(true);

        }
        MaterialStyledDialog dialog = new MaterialStyledDialog(getActivity())
                .setTitle(getString(R.string.weather_units_heading))
                .setCustomView(dialogView)
                .setHeaderColor(R.color.accent)
                .setScrollable(true)
                .withDialogAnimation(true, Duration.NORMAL)
                .setCancelable(true)
                .setNegative("Cancel", (dialog1, which) -> dialog1.cancel())
                .setPositive("Update", (dialog1, which) -> resetValue(dialogView, tempUnitType))
                .setDescription("Select the temperature unit")
                .build();
        dialog.show();
    }

    void resetValue(View dialogView, int type) {
        int id = ((RadioGroup) dialogView.findViewById(R.id.unitGroup)).getCheckedRadioButtonId();
        if (id == R.id.unit1) {
            if (type == tempUnitType) {
                preferenceManager.setUnit(unit_metric.toLowerCase());
                tempUnitValueTextView.setText(unit_metric);
                settingsRefreshInterface.refreshUnit();
            } else {
                preferenceManager.setIconPack(icon_default);
                iconPackValueTextView.setText(icon_default);
                weatherIconImage.setImageDrawable(new IconicsDrawable(getActivity())
                        .icon(WeatherIcons.Icon.wic_day_sunny)
                        .color(ContextCompat.getColor(getActivity(), R.color.grey_500))
                        .sizeDp(24));
                settingsRefreshInterface.refreshIconPack();

            }
        } else {
            if (type == tempUnitType) {
                preferenceManager.setUnit(unit_imperial.toLowerCase());
                tempUnitValueTextView.setText(unit_imperial);
                settingsRefreshInterface.refreshUnit();

            } else {
                preferenceManager.setIconPack(icon_meteoconcs);
                iconPackValueTextView.setText(icon_meteoconcs);
                weatherIconImage.setImageDrawable(new IconicsDrawable(getActivity())
                        .icon(Meteoconcs.Icon.met_sun)
                        .color(ContextCompat.getColor(getActivity(), R.color.grey_500))
                        .sizeDp(24));
                settingsRefreshInterface.refreshIconPack();

            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notificationClickSubscription.unsubscribe();
        notificationCheckboxSubscription.unsubscribe();
        iconPackClickSubscription.unsubscribe();
        unitsClickSubscription.unsubscribe();
        ButterKnife.unbind(this);
    }
}