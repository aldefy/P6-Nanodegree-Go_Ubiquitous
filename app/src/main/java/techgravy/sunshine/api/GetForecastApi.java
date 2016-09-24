package techgravy.sunshine.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import techgravy.sunshine.models.WeatherResponse;

/**
 * Created by aditlal on 06/04/16.
 */
public interface GetForecastApi {

    //api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=jsonl&units=metric&cnt=7
    @GET("/forecast/daily")
    Observable<WeatherResponse> getWeekForecast(@Query("q") String city, @Query("mode") String format, @Query("units") String unit, @Query("cnt") String countDays, @Query("APPID") String apiKey);

    //api.openweathermap.org/data/2.5/forecast/daily?lat={lat}&lon={lon}&cnt={cnt}
    @GET("/forecast/daily")
    Observable<WeatherResponse> getLocationForecast(@Query("lat") double lat, @Query("lon") double lon, @Query("mode") String format, @Query("units") String unit, @Query("cnt") String countDays, @Query("APPID") String apiKey);

    @GET("/weather")
    Observable<WeatherResponse> getTodaysForecast(@Query("q") String city, @Query("APPID") String apiKey);
}
