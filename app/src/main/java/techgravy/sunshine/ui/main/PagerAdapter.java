package techgravy.sunshine.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import techgravy.sunshine.ui.BaseViewPagerFragmentStatAdapter;
import techgravy.sunshine.ui.settings.SettingFragment;
import techgravy.sunshine.ui.week.WeatherWeekFragment;

/**
 * Created by aditlal on 12/04/16.
 */
public class PagerAdapter extends BaseViewPagerFragmentStatAdapter {

    String[] title;
    int numbOfTabs;

    public PagerAdapter(FragmentManager fm, int count, String[] title) {
        super(fm);
        this.numbOfTabs = count;
        this.title = title;
    }


    @Override
    public Fragment getFragmentItem(int position) {
        switch (position) {
            case 0:
                return new WeatherTodayFragment();
            case 1:
                return new WeatherWeekFragment();
            case 2:
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public void updateFragmentItem(int position, Fragment fragment) {

    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
