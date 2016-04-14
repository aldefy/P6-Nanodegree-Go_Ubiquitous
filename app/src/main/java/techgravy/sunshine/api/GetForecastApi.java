package techgravy.sunshine.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import techgravy.sunshine.models.WeatherResponse;

/**
 * Created by aditlal on 06/04/16.
 */
public interface GetForecastApi {

    @GET("/forecast/daily")
    Observable<WeatherResponse> getWeekForecast(@Query("q") String city, @Query("mode") String format, @Query("units") String unit,@Query("cnt")String countDays, @Query("APPID") String apiKey);

    @GET("/weather")
    Observable<WeatherResponse> getTodaysForecast(@Query("q") String city, @Query("APPID") String apiKey);
}
