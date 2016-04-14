package techgravy.sunshine.interfaces;

import javax.inject.Singleton;

import dagger.Component;
import techgravy.sunshine.MainApplication;
import techgravy.sunshine.module.PrefModule;
import techgravy.sunshine.utils.PreferenceManager;

/**
 * Created by aditlal on 06/04/16.
 */
@Singleton
@Component(modules = {PrefModule.class})
public interface ModuleComponent {

    void inject(MainApplication application);

    PreferenceManager providePrefManager();


}