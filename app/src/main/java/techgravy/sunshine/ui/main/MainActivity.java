package techgravy.sunshine.ui.main;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import techgravy.sunshine.R;
import techgravy.sunshine.ui.settings.SettingsRefreshInterface;
import techgravy.sunshine.ui.week.WeatherWeekFragment;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SettingsRefreshInterface{


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.bottomNavigation)
    BottomNavigationView bottomNavigation;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.tag("Weather");
        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        int[] tabColors = {ContextCompat.getColor(MainActivity.this, R.color.tab), ContextCompat.getColor(MainActivity.this, R.color.tab), ContextCompat.getColor(MainActivity.this, R.color.tab)};
        int[] tabImages = {R.drawable.ic_weather, R.drawable.ic_calendar,
                R.drawable.ic_settings};
        String[] title = {"Today", "Week", "Settings"};
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), 3, title);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        bottomNavigation.isWithText(true);
        bottomNavigation.setUpWithViewPager(viewPager, tabColors, tabImages);
    }


    //NotificationHelper.expandablePictureNotification(MainActivity.this, "New Notification", "http://api.randomuser.me/portraits/women/39.jpg")


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshIconPack() {
        Toast.makeText(MainActivity.this, "Updated Icon pack", Toast.LENGTH_SHORT).show();
        ((WeatherTodayFragment)  pagerAdapter.getFragmentForPos(0)).refreshIcons();
        ((WeatherWeekFragment)  pagerAdapter.getFragmentForPos(1)).refreshIcons();
    }

    @Override
    public void refreshUnit() {
        Toast.makeText(MainActivity.this, "Updated temperature unit", Toast.LENGTH_SHORT).show();
        ((WeatherTodayFragment)  pagerAdapter.getFragmentForPos(0)).refreshUnit();
        ((WeatherWeekFragment)  pagerAdapter.getFragmentForPos(1)).refreshUnit();
    }

    @Override
    public void refreshLocation(Location location) {

    }



}
