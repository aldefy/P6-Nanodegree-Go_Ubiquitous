package techgravy.sunshine.models;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 18/04/16.
 */
@RealmClass
public class WeatherHeaderModel implements RealmModel {

    String city;
    String wind;
    String pressure;
    String humidity;
    double temp;
    int weatherId;
    String weatherCondition;

    public WeatherHeaderModel() {

    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "WeatherHeaderModel{" +
                "city='" + city + '\'' +
                ", wind='" + wind + '\'' +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                ", temp='" + temp + '\'' +
                ", weatherCondition='" + weatherCondition + '\'' +
                '}';
    }


}
