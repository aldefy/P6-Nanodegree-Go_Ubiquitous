package techgravy.sunshine.ui.settings;

import android.location.Location;

/**
 * Created by aditlal on 13/04/16.
 */
public interface SettingsRefreshInterface {
    void refreshIconPack();

    void refreshUnit();

    void refreshLocation(Location location);
}
