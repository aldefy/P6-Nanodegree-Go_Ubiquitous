package techgravy.sunshine.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import techgravy.sunshine.utils.PreferenceManager;

/**
 * Created by aditlal on 06/04/16.
 */
@Module
public class PrefModule {

    PreferenceManager preferenceManager;

    public PrefModule(Context context ,int count) {
        preferenceManager = new PreferenceManager(context);
    }

    @Provides
    @Singleton
    PreferenceManager providesPrefManager() {
        return preferenceManager;
    }
}
