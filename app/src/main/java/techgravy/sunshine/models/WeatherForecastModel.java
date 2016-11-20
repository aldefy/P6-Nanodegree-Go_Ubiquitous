package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;


/**
 * Created by aditlal on 06/04/16.
 */
@RushTableAnnotation
public class WeatherForecastModel implements Serializable, Rush {


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
    @RushList(classType = Weather.class)
    private ArrayList<Weather> weather;

    @SerializedName("temp")
    private Temperature temperature;

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

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        this.weather = weather;
    }

    public Temperature getTemp() {
        return temperature;
    }

    public void setTemp(Temperature temp) {
        this.temperature = temp;
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


    @Override
    public String toString() {
        return "ClassPojo [clouds = " + clouds + ", dt = " + dt + ", humidity = " + humidity + ", pressure = " + pressure + ", speed = " + speed + ", deg = " + deg + ", weather = " + weather + ", temp = " + temperature + "]";
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
