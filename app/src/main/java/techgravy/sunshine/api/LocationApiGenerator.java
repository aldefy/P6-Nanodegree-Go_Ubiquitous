package techgravy.sunshine.api;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import techgravy.sunshine.utils.logger.Logger;

/**
 * Created by aditlal on 06/07/16.
 */

public class LocationApiGenerator {
    public static String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    public static String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place";

    // No need to instantiate this class.
    public LocationApiGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass) {

        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        //okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);


        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        Logger.t("Retro").d(msg);
                    }
                })
                .setClient(new OkClient(okHttpClient));

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }

    public static <S> S createPlacesService(Class<S> serviceClass) {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(PLACES_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        Logger.i("Retro", msg);
                    }
                })
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }
}
