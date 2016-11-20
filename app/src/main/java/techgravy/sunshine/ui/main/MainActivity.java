package techgravy.sunshine.ui.main;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.context.IconicsLayoutInflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.api.GetPhotoApi;
import techgravy.sunshine.api.PhotoApiGenerator;
import techgravy.sunshine.helpers.PicassoCache;
import techgravy.sunshine.interfaces.WeatherDetailInterface;
import techgravy.sunshine.models.PhotoResponse;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.sync.SunshineSyncAdapter;
import techgravy.sunshine.sync.WearableDataService;
import techgravy.sunshine.ui.settings.SettingFragment;
import techgravy.sunshine.ui.settings.SettingsRefreshInterface;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

import static techgravy.sunshine.helpers.Constants.DISPLACEMENT;
import static techgravy.sunshine.helpers.Constants.FATEST_INTERVAL;
import static techgravy.sunshine.helpers.Constants.PLAY_SERVICES_RESOLUTION_REQUEST;
import static techgravy.sunshine.helpers.Constants.UPDATE_INTERVAL;

public class MainActivity extends AppCompatActivity implements SettingsRefreshInterface, WeatherDetailInterface, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {


    @BindView(R.id.aboutImageBackground)
    ImageView aboutImageBackground;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.weatherCityTextView)
    TextView weatherCityTextView;
    @BindView(R.id.weatherHeadlineTextView)
    TextView weatherHeadlineTextView;
    @BindView(R.id.weatherSubHeadTextView)
    TextView weatherSubHeadTextView;
    @BindView(R.id.weatherHumidityTitleTextView)
    TextView weatherHumidityTitleTextView;
    @BindView(R.id.weatherHumidityValueTextView)
    TextView weatherHumidityValueTextView;
    @BindView(R.id.weatherPressureTitleTextView)
    TextView weatherPressureTitleTextView;
    @BindView(R.id.weatherPressureValueTextView)
    TextView weatherPressureValueTextView;
    @BindView(R.id.weatherDetailsLayout)
    LinearLayout weatherDetailsLayout;
    @BindView(R.id.weatherWindTitleTextView)
    TextView weatherWindTitleTextView;
    @BindView(R.id.weatherWindValueTextView)
    TextView weatherWindValueTextView;
    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;
    @BindView(R.id.photoCredit)
    TextView photoCredit;

    private WeatherHeaderModel headerModel;
    private boolean isDetailLoaded, isSettingsLoaded;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private PreferenceManager preferenceManager;
    private GetPhotoApi getPhotoApi;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    WeatherFragment weatherFragment;
    WeatherDetailFragment detailFragment;
    PhotoResponse photoResponse;
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        CommonUtils.setTranslucentStatusBar(getWindow(), -1);
        setContentView(R.layout.activity_main);
        preferenceManager = MainApplication.getApplication().getPreferenceManager();
        getPhotoApi = PhotoApiGenerator.createService(GetPhotoApi.class);
        ButterKnife.bind(this);
        Timber.tag("Weather");
        if (savedInstanceState != null) {
            headerModel = savedInstanceState.getParcelable("headerModel");
            setHeaderData(headerModel);
            photoResponse = savedInstanceState.getParcelable("photoResponse");
            photoCredit.setText("Photo Credit : " + photoResponse.getPhotos().get(0).getUserModel().getFullname());
            aboutImageBackground.setTag(photoResponse.getPhotos().get(0).getUrl());
        }
        init();
        initViews();
    }

    private void init() {
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    private void initViews() {
        isDetailLoaded = false;
        isSettingsLoaded = false;
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("");
        setHeaderView();

        // Set paddingTop of toolbar to height of status bar.
        // Fixes statusbar covers toolbar issue
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        appBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Sunshine");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });

        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayout.Behavior() {
        });
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return !isDetailLoaded;
            }
        });
        fragmentManager = getSupportFragmentManager();

        openHome();

        SunshineSyncAdapter.initializeSyncAdapter(MainActivity.this);
    }


    private void setHeaderView() {
        Subscriber<WeatherHeaderModel> subscriber = new Subscriber<WeatherHeaderModel>() {
            @Override
            public void onCompleted() {
                Timber.tag("HeaderSubscriber").d("Completed");

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Timber.tag("HeaderSubscriber").e("Error : " + e.getMessage());

            }

            @Override
            public void onNext(WeatherHeaderModel weatherHeaderModel) {
                Timber.tag("HeaderSubscriber").d(weatherHeaderModel.toString());
                headerModel = weatherHeaderModel;
                setHeaderData(headerModel);
            }
        };
        MainApplication.getApplication().setWeatherHeaderModelSubscriber(subscriber);
        if (photoResponse == null)
            getHeaderImage();
        aboutImageBackground.setOnClickListener(this::open500PxPhoto);
    }

    private void open500PxPhoto(View o) {
        if (o.getTag() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Open 500px Image");
            builder.setMessage("Open the image in a browser?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://500px.com" + (String) o.getTag()));
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

        }
    }

    private void setHeaderData(WeatherHeaderModel weatherHeaderModel) {
        Timber.d(weatherHeaderModel.toString());
        weatherCityTextView.setText(weatherHeaderModel.getCity());
        weatherHeadlineTextView.setText(CommonUtils.formatTemperature(MainActivity.this, weatherHeaderModel.getTemp(), preferenceManager.getUnit()));
        weatherSubHeadTextView.setText(weatherHeaderModel.getWeatherCondition());
        weatherSubHeadTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(CommonUtils.getWeatherIconFromWeather(MainActivity.this, weatherHeaderModel.getWeatherId(), preferenceManager.getIconPack()), null, null, null);
        weatherHumidityValueTextView.setText(weatherHeaderModel.getHumidity());
        weatherPressureValueTextView.setText(weatherHeaderModel.getPressure());
        weatherWindValueTextView.setText(weatherHeaderModel.getWind());

        WearableDataService sunshineWearableConnector = new WearableDataService(MainActivity.this);
        sunshineWearableConnector.onNotifyWearable(weatherHeaderModel.getWeatherId(),
                CommonUtils.formatTemperature(MainActivity.this, weatherHeaderModel.getTemp(), preferenceManager.getUnit()),
                CommonUtils.formatTemperature(MainActivity.this, weatherHeaderModel.getMinTemp(), preferenceManager.getUnit()));
    }

    private void getHeaderImage() {
        if (preferenceManager.getImagePath().isEmpty()) {
            preferenceManager.setPhotoNumber(CommonUtils.getRandomPhoto());
            getPhotoApi.getPhoto().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<PhotoResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(PhotoResponse photoResponse) {
                    Timber.tag("PhotoUrl").d("Loaded image url onNext = " + photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getImage_url());
                    photoCredit.setText("Photo Credit : " + photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getUserModel().getFullname());
                    preferenceManager.setImageCredit(photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getUserModel().getFullname());
                    preferenceManager.setImagePath(photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getImage_url());
                    aboutImageBackground.setTag(photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getUrl());
                    PicassoCache.getPicassoInstance(MainActivity.this).load(photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getImage_url()).into(aboutImageBackground);
                    //Picasso.with(MainActivity.this).load(photoResponse.getPhotos().get(preferenceManager.getPhotoNumber()).getImage_url()).into(aboutImageBackground, new GetPalette(MainActivity.this, aboutImageBackground, getWindow()));
                }
            });
        } else {
            PicassoCache.getPicassoInstance(MainActivity.this).load(preferenceManager.getImagePath()).into(aboutImageBackground);
            photoCredit.setText("Photo Credit : " + preferenceManager.getImageCredit());
        }

    }


    //NotificationHelper.expandablePictureNotification(MainActivity.this, "New Notification", "http://api.randomuser.me/portraits/women/39.jpg")


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshIconPack() {
        openHome();
        setHeaderData(headerModel);
        Toast.makeText(MainActivity.this, "Updated Icon pack", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshUnit() {
        openHome();
        setHeaderData(headerModel);
        Toast.makeText(MainActivity.this, "Updated temperature unit", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshLocation(Location location) {

    }

    @Override
    public void refreshRandomPhoto() {
        preferenceManager.setImagePath("");
        preferenceManager.setImageCredit("");
        getHeaderImage();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                isSettingsLoaded = true;
                openSettings();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_sync:
                SunshineSyncAdapter.syncImmediately(MainActivity.this);
            default:
                return true;
        }
    }

    private void openSettings() {
        appBarLayout.setExpanded(true, true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new SettingFragment(), "settings");
        fragmentTransaction.commit();
    }

    private void openHome() {
        isDetailLoaded = false;
        isSettingsLoaded = false;
        appBarLayout.setExpanded(true, true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (fragmentManager.findFragmentByTag("weather") == null)
            weatherFragment = new WeatherFragment();
        else
            weatherFragment = (WeatherFragment) fragmentManager.findFragmentByTag("weather");

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, weatherFragment, "weather");
        fragmentTransaction.commit();

    }


    private void openDetails(WeatherForecastModel model) {
        appBarLayout.setExpanded(false, true);
        Timber.tag("DetailsID").d(model.getId() + "");
        isDetailLoaded = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentTransaction = fragmentManager.beginTransaction();
        detailFragment = new WeatherDetailFragment();
        Bundle extras = new Bundle();
        extras.putSerializable("forecast", model);
        detailFragment.setArguments(extras);
        fragmentTransaction.add(R.id.fragmentContainer, detailFragment, "details");
        fragmentTransaction.commit();

    }

    @Override
    public void loadDetails(WeatherForecastModel model) {
        openDetails(model);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable("headerModel", headerModel);
        //outState.putParcelable("photoResponse", photoResponse);
    }

    @Override
    public void onBackPressed() {
        if (!isDetailLoaded && !isSettingsLoaded)
            super.onBackPressed();
        else
            openHome();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /*
    Location Interface Methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // stopLocationUpdates();
    }

    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();
            Timber.d("Periodic location updates started!");

        } else {
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
            Timber.d("Periodic location updates stopped!");
        }
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        return true;
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            //TODO update local variable and store location

        } else {
            //TODO update local variable and store location
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //TODO update local variable and store location
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

