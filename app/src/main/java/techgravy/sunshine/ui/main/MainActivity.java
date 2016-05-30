package techgravy.sunshine.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.R;
import techgravy.sunshine.api.GetPhotoApi;
import techgravy.sunshine.api.PhotoApiGenerator;
import techgravy.sunshine.interfaces.WeatherDetailInterface;
import techgravy.sunshine.models.PhotoResponse;
import techgravy.sunshine.models.WeatherForecastModel;
import techgravy.sunshine.models.WeatherHeaderModel;
import techgravy.sunshine.sync.SunshineSyncAdapter;
import techgravy.sunshine.ui.settings.SettingFragment;
import techgravy.sunshine.ui.settings.SettingsRefreshInterface;
import techgravy.sunshine.utils.CommonUtils;
import techgravy.sunshine.utils.GetPalette;
import techgravy.sunshine.utils.PreferenceManager;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SettingsRefreshInterface, WeatherDetailInterface {


    @Bind(R.id.aboutImageBackground)
    ImageView aboutImageBackground;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    GetPhotoApi getPhotoApi;
    @Bind(R.id.weatherCityTextView)
    TextView weatherCityTextView;
    @Bind(R.id.weatherHeadlineTextView)
    TextView weatherHeadlineTextView;
    @Bind(R.id.weatherSubHeadTextView)
    TextView weatherSubHeadTextView;
    @Bind(R.id.weatherHumidityTitleTextView)
    TextView weatherHumidityTitleTextView;
    @Bind(R.id.weatherHumidityValueTextView)
    TextView weatherHumidityValueTextView;
    @Bind(R.id.weatherPressureTitleTextView)
    TextView weatherPressureTitleTextView;
    @Bind(R.id.weatherPressureValueTextView)
    TextView weatherPressureValueTextView;
    @Bind(R.id.weatherDetailsLayout)
    LinearLayout weatherDetailsLayout;
    @Bind(R.id.weatherWindTitleTextView)
    TextView weatherWindTitleTextView;
    @Bind(R.id.weatherWindValueTextView)
    TextView weatherWindValueTextView;
    @Bind(R.id.fragmentContainer)
    FrameLayout fragmentContainer;
    @Bind(R.id.photoCredit)
    TextView photoCredit;

    private WeatherHeaderModel headerModel;
    private boolean isDetailLoaded;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private PreferenceManager preferenceManager;

    WeatherFragment weatherFragment;
    SettingFragment settingFragment;
    WeatherDetailFragment detailFragment;
    PhotoResponse photoResponse;

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
        initViews();
    }

    private void initViews() {
        isDetailLoaded = false;
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("");
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

        setHeaderView();
        SunshineSyncAdapter.initializeSyncAdapter(MainActivity.this);
    }


    private void setHeaderView() {
        Subscriber<WeatherHeaderModel> subscriber = new Subscriber<WeatherHeaderModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(WeatherHeaderModel weatherHeaderModel) {
                Timber.tag("Header").d(weatherHeaderModel.toString());
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
    }

    private void getHeaderImage() {
        getPhotoApi.getPhoto().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<PhotoResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(PhotoResponse photoResponse) {
                Timber.tag("PhotoUrl").d("Loaded image url onNext = " + photoResponse.getPhotos().get(0).getImage_url());
                photoCredit.setText("Photo Credit : " + photoResponse.getPhotos().get(0).getUserModel().getFullname());
                aboutImageBackground.setTag(photoResponse.getPhotos().get(0).getUrl());
                Picasso.with(MainActivity.this).load(photoResponse.getPhotos().get(0).getImage_url()).into(aboutImageBackground, new GetPalette(MainActivity.this, aboutImageBackground, getWindow()));
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
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
        isDetailLoaded = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentTransaction = fragmentManager.beginTransaction();
        detailFragment = new WeatherDetailFragment();
        Bundle extras = new Bundle();
        extras.putParcelable("forecast", model);
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
        outState.putParcelable("headerModel", headerModel);
        outState.putParcelable("photoResponse", photoResponse);
    }

    @Override
    public void onBackPressed() {
        if (!isDetailLoaded)
            super.onBackPressed();
        else
            openHome();
    }
}

