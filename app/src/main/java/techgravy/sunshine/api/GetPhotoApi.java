package techgravy.sunshine.api;

import retrofit.http.GET;
import rx.Observable;
import techgravy.sunshine.models.PhotoResponse;

/**
 * Created by aditlal on 14/04/16.
 */
public interface GetPhotoApi {

    @GET("/search?rpp=10&sort=highest_rating&image_size=21&consumer_key=HrHfRtOrgFqOCcRpcRS7VCteUpw8OYAOjlyall8v&only=abstract")
    Observable<PhotoResponse> getPhoto();
}
