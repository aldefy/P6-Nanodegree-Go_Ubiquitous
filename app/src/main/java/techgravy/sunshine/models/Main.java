package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aditlal on 14/04/16.
 */
public class Main {
    @SerializedName("temp_kf")
    private float temp_kf;
    @SerializedName("humidity")
    private double humidity;
    @SerializedName("pressure")
    private float pressure;
    @SerializedName("temp_max")
    private float temp_max;
    @SerializedName("sea_level")
    private float sea_level;
    @SerializedName("temp_min")
    private float temp_min;
    @SerializedName("temp")
    private float temp;
    @SerializedName("grnd_level")
    private float grnd_level;

    public float getTemp_kf() {
        return temp_kf;
    }

    public void setTemp_kf(float temp_kf) {
        this.temp_kf = temp_kf;
    }

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

    public float getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(float temp_max) {
        this.temp_max = temp_max;
    }

    public float getSea_level() {
        return sea_level;
    }

    public void setSea_level(float sea_level) {
        this.sea_level = sea_level;
    }

    public float getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(float temp_min) {
        this.temp_min = temp_min;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getGrnd_level() {
        return grnd_level;
    }

    public void setGrnd_level(float grnd_level) {
        this.grnd_level = grnd_level;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [temp_kf = "+temp_kf+", humidity = "+humidity+", pressure = "+pressure+", temp_max = "+temp_max+", sea_level = "+sea_level+", temp_min = "+temp_min+", temp = "+temp+", grnd_level = "+grnd_level+"]";
    }
}
