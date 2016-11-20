package techgravy.sunshine.ui.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.meteocons_typeface_library.Meteoconcs;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.api.LocationApiGenerator;
import techgravy.sunshine.api.ReverseGeoCodeApi;
import techgravy.sunshine.models.Address_components;
import techgravy.sunshine.models.LocationModel;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.PermissionUtils;
import techgravy.sunshine.utils.PreferenceManager;
import techgravy.sunshine.utils.logger.Logger;
import timber.log.Timber;

import static techgravy.sunshine.BuildConfig.GOOGLE_KEY;

/**
 * Created by aditlal on 05/04/16.
 */

public class SettingFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String TAG = "Settings";
    @BindView(R.id.weather_settings_option1)
    RelativeLayout weatherNotificationLayout;
    @BindView(R.id.weather_notifications_checkbox)
    CheckBox weatherNotificationCheckbox;
    @BindView(R.id.weather_settings_option2)
    RelativeLayout weatherTempUnitsLayout;
    @BindView(R.id.weather_settings_option4)
    RelativeLayout weatherIconsLayout;
    @BindView(R.id.temperature_units_value)
    TextView tempUnitValueTextView;
    @BindView(R.id.weather_icon_value)
    TextView iconPackValueTextView;
    @BindView(R.id.weather_icon_image)
    ImageButton weatherIconImage;
    @BindView(R.id.weatherSettingsHeader)
    TextView weatherSettingsHeader;
    @BindView(R.id.weather_notifications_title)
    TextView weatherNotificationsTitle;
    @BindView(R.id.divider1)
    View divider1;
    @BindView(R.id.temperature_units_heading)
    TextView temperatureUnitsHeading;
    @BindView(R.id.divider2)
    View divider2;
    @BindView(R.id.weather_location_heading)
    TextView weatherLocationHeading;
    @BindView(R.id.weather_location_value)
    TextView weatherLocationValue;
    @BindView(R.id.gpsLocationButton)
    ImageButton gpsLocationButton;
    @BindView(R.id.weather_settings_option3)
    RelativeLayout weatherSettingsOption3;
    @BindView(R.id.divider3)
    View divider3;
    @BindView(R.id.weather_icon_heading)
    TextView weatherIconHeading;
    @BindView(R.id.divider4)
    View divider4;
    @BindView(R.id.photo_heading)
    TextView photoHeading;
    @BindView(R.id.photo_setting_layout)
    RelativeLayout photoSettingLayout;
    @BindView(R.id.divider5)
    View divider5;
    @BindString(R.string.value_units_imperial)
    String unit_imperial;
    @BindString(R.string.value_units_metric)
    String unit_metric;

    @BindString(R.string.icon_pack_default)
    String icon_default;
    @BindString(R.string.icon_pack_meteoconcs)
    String icon_meteoconcs;

    private int tempUnitType = 1, iconPackType = 2;
    private Subscription notificationClickSubscription, notificationCheckboxSubscription, unitsClickSubscription, iconPackClickSubscription, changePhotoClickSubscription, locationClickSubscription;
    private PreferenceManager preferenceManager;
    private SettingsRefreshInterface settingsRefreshInterface;
    private ReverseGeoCodeApi reverseGeoCodeApi;
    private boolean isCoarseLocation, isFineLocation;

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    private ReactiveLocationProvider reactiveLocationProvider;
    private CompositeSubscription compositeSubscription;

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
        reverseGeoCodeApi = LocationApiGenerator.createService(ReverseGeoCodeApi.class);

        weatherNotificationCheckbox.setChecked(preferenceManager.getWeatherNotificationToggle());
        notificationCheckboxSubscription =
                RxCompoundButton.checkedChanges(weatherNotificationCheckbox)
                        .subscribe(o -> preferenceManager.setWeatherNotificationToggle(weatherNotificationCheckbox.isChecked()));
        notificationClickSubscription =
                RxView.clicks(weatherNotificationLayout).subscribe(view -> weatherNotificationCheckbox.setChecked(!preferenceManager.getWeatherNotificationToggle()));
        unitsClickSubscription = RxView.clicks(weatherTempUnitsLayout).subscribe(v -> handleTemperatureUnits());
        iconPackClickSubscription = RxView.clicks(weatherIconsLayout).subscribe(v -> handleIconPack());
        changePhotoClickSubscription = RxView.clicks(photoSettingLayout).subscribe(v -> handlePhotoChange());
        locationClickSubscription = RxView.clicks(weatherSettingsOption3).subscribe(v -> handleLocation());
        reactiveLocationProvider = new ReactiveLocationProvider(getActivity());
        if (preferenceManager.getUnit().equalsIgnoreCase(unit_imperial)) {
            tempUnitValueTextView.setText(unit_imperial);
        } else {
            tempUnitValueTextView.setText(unit_metric);
        }
        if (preferenceManager.getIconPack().equalsIgnoreCase(icon_default)) {
            iconPackValueTextView.setText(icon_default);
            weatherIconImage.setImageDrawable(new IconicsDrawable(getActivity())
                    .icon(WeatherIcons.Icon.wic_day_sunny)
                    .color(ContextCompat.getColor(getActivity(), R.color.white))
                    .sizeDp(24));
        } else {
            iconPackValueTextView.setText(icon_meteoconcs);
            weatherIconImage.setImageDrawable(new IconicsDrawable(getActivity())
                    .icon(Meteoconcs.Icon.met_sun)
                    .color(ContextCompat.getColor(getActivity(), R.color.white))
                    .sizeDp(24));
        }

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        return rootView;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Logger.t(TAG).i("Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    private void handleLocation() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_location, null);
        MaterialStyledDialog dialog = new MaterialStyledDialog(getActivity())
                .setTitle(getString(R.string.location_dailog_title))
                .setCustomView(dialogView)
                .setHeaderColor(R.color.primary)
                .setScrollable(true)
                .withDialogAnimation(true, Duration.NORMAL)
                .setCancelable(true)
                .setNegative("My Location", (dialog1, which) -> initMyLocation(dialog1))
                .setPositive("Search", (dialog1, which) -> searchForPlace(dialog1))
                .setDescription("Edit your location for weather updates")
                .build();
        dialog.show();
    }

    private void searchForPlace(MaterialDialog dialog1) {

    }

    private void initMyLocation(MaterialDialog dialog1) {
        dialog1.cancel();
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermissions(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
                    Logger.t("PermDexter").d("Granted " + response.getPermissionName());
                    switch (response.getPermissionName()) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            isFineLocation = true;
                            break;
                        case Manifest.permission.ACCESS_COARSE_LOCATION:
                            isCoarseLocation = true;
                            break;
                    }
                }
                for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
                    Logger.t("PermDexter").d("Denied " + response.getPermissionName());
                    switch (response.getPermissionName()) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            isFineLocation = false;
                            break;
                        case Manifest.permission.ACCESS_COARSE_LOCATION:
                            isCoarseLocation = false;
                            break;
                    }


                }

                if (isCoarseLocation && isFineLocation) {
                    //TODO location updates here
                    checkLocationSettings();

                } //else
                //   PermissionUtils.showPermissionDeniedToast(getActivity(), getString(R.string.location_unavailable));

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                PermissionUtils.showPermissionRationaleDialog(getActivity(), token, "Location Permission", getString(R.string.location_rationale));
            }
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void handlePhotoChange() {
        preferenceManager.setPhotoNumber(CommonUtils.getRandomPhoto());
        if (settingsRefreshInterface != null)
            settingsRefreshInterface.refreshRandomPhoto();
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
                        .color(ContextCompat.getColor(getActivity(), R.color.white))
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
                        .color(ContextCompat.getColor(getActivity(), R.color.white))
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
    }


    private void getLocationInfoForWeather() {
        if (mCurrentLocation != null)
            reverseGeoCodeApi.getStateCityFromLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude(), GOOGLE_KEY)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<LocationModel>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(LocationModel locationModel) {
                            String city = "";
                            List<Address_components> address_componentsList = locationModel.getResultsList().get(0).getAddress_components();
                            for (Address_components address : address_componentsList) {
                                for (String type : address.getTypes()) {
                                    if (type.equalsIgnoreCase("locality")) {
                                        city = address.getLong_name();
                                        Logger.d("citySelected", city);
                                    }
                                }
                            }
                            if (!city.isEmpty())
                                preferenceManager.setCity(city);
                            //TODO decide how to call the api all over again with new city
                        }
                    });
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.t(TAG).i("Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //TODO   updateLocationUI();
         //   Logger.t(TAG).d("Location is :" + mCurrentLocation != null ? mCurrentLocation.toString() : "Location is not available");
        } else {
            Logger.t(TAG).d("Location is :" + mCurrentLocation.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.t(TAG).i("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Logger.t(TAG).i("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Logger.t(TAG).i("All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Logger.t(TAG).i("Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Logger.t(TAG).i("PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Logger.t(TAG).i("Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        Logger.t(TAG).d("Location changed to : " + location.toString());
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //TODO updateLocationUI();
        Toast.makeText(getActivity(), getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Logger.t(TAG).i("User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Logger.t(TAG).i("User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(status -> mRequestingLocationUpdates = true);

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(status -> mRequestingLocationUpdates = false);
    }
}