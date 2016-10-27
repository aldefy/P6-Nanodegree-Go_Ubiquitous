package techgravy.sunshine.api;


import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import techgravy.sunshine.models.LocationModel;

/**
 * Created by aditlal on 06/07/16.
 */

public interface ReverseGeoCodeApi {

    @GET("/json")
    Observable<LocationModel> getStateCityFromLocation(@Query("latlng") String latLng, @Query("api_key") String apiKey);

   /* @GET("/json")
    void getStateCityFromLocation(@Query("latlng") String latLng, @Query("api_key") String apiKey, Callback<LocationModel> callback);*/
}
