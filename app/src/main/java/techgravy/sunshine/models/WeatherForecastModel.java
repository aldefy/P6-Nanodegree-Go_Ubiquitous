package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by aditlal on 06/04/16.
 */
public class WeatherForecastModel implements Rush {

    @SerializedName("clouds")
    private double clouds;

    @SerializedName("dt")
    private String dt;

    @SerializedName("humidity")
    private double humidity;

    @SerializedName("pressure")
    private float pressure;

    @SerializedName("speed")
    private float speed;

    @SerializedName("deg")
    private double deg;

    @RushList(classType = Weather.class)
    @SerializedName("weather")
    private List<Weather> weather;

    private Temperature temp;


    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
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

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Temperature getTemp() {
        return temp;
    }

    public void setTemp(Temperature temp) {
        this.temp = temp;
    }

    public double getClouds() {
        return clouds;
    }

    public void setClouds(double clouds) {
        this.clouds = clouds;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    @Override
    public String toString() {
        return "ClassPojo [clouds = " + clouds + ", dt = " + dt + ", humidity = " + humidity + ", pressure = " + pressure + ", speed = " + speed + ", deg = " + deg + ", weather = " + weather + ", temp = " + temp + "]";
    }

    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }

    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    @Override
    public void delete() {
        RushCore.getInstance().delete(this);
    }

    @Override
    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    @Override
    public String getId() {
        return RushCore.getInstance().getId(this);
    }

}
