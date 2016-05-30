package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 06/04/16.
 */
@RealmClass
public class WeatherForecastModel implements RealmModel, Serializable {

    @PrimaryKey
    private int id;

    @SerializedName("clouds")
    private float clouds;

    @SerializedName("dt")
    private String dt;

    @SerializedName("humidity")
    private float humidity;

    @SerializedName("pressure")
    private float pressure;

    @SerializedName("speed")
    private float speed;

    @SerializedName("deg")
    private float deg;

    @SerializedName("weather")
    private RealmList<Weather> weather;

    @SerializedName("temp")
    private Temperature temp;

    public WeatherForecastModel() {
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(RealmList<Weather> weather) {
        this.weather = weather;
    }

    public Temperature getTemp() {
        return temp;
    }

    public void setTemp(Temperature temp) {
        this.temp = temp;
    }

    public float getClouds() {
        return clouds;
    }

    public void setClouds(float clouds) {
        this.clouds = clouds;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClassPojo [clouds = " + clouds + ", dt = " + dt + ", humidity = " + humidity + ", pressure = " + pressure + ", speed = " + speed + ", deg = " + deg + ", weather = " + weather + ", temp = " + temp + "]";
    }


 /*   @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(clouds);
        dest.writeString(dt);
        dest.writeFloat(humidity);
        dest.writeFloat(pressure);
        dest.writeFloat(speed);
        dest.writeFloat(deg);
        dest.writeList(weather);
        dest.writeParcelable(temp, flags);
    }

    // Creator
    public static final Parcelable.Creator<WeatherForecastModel> CREATOR
            = new Parcelable.Creator<WeatherForecastModel>() {
        public WeatherForecastModel createFromParcel(Parcel in) {
            return new WeatherForecastModel(in);
        }

        public WeatherForecastModel[] newArray(int size) {
            return new WeatherForecastModel[size];
        }
    };

    // "De-parcel object
    public WeatherForecastModel(Parcel in) {
        clouds = in.readFloat();
        dt = in.readString();
        humidity = in.readFloat();
        pressure = in.readFloat();
        speed = in.readFloat();
        deg = in.readFloat();
        List<Weather> myList = null;
        in.readList(myList, List.class.getClassLoader());
        weather = myList;
    }*/

}
