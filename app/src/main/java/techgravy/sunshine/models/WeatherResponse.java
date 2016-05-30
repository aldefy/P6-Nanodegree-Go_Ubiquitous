package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 06/04/16.
 */
@RealmClass
public class WeatherResponse implements RealmModel, Serializable {
    private String message;

    private String cnt;

    private String cod;

    @SerializedName("list")
    private RealmList<WeatherForecastModel> list;

    @SerializedName("city")
    private WeatherForecastCity city;

    public WeatherResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public RealmList<WeatherForecastModel> getList() {
        return list;
    }

    public void setList(RealmList<WeatherForecastModel> list) {
        this.list = list;
    }

    public WeatherForecastCity getCity() {
        return city;
    }

    public void setCity(WeatherForecastCity city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", cnt = " + cnt + ", cod = " + cod + ", list = " + list + ", city = " + city + "]";
    }


}
