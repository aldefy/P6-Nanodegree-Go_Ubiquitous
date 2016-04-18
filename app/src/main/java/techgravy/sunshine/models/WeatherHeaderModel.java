package techgravy.sunshine.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aditlal on 18/04/16.
 */
public class WeatherHeaderModel implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(wind);
        dest.writeString(pressure);
        dest.writeString(humidity);
        dest.writeDouble(temp);
        dest.writeString(weatherCondition);
    }

    // Creator
    public static final Parcelable.Creator<WeatherHeaderModel> CREATOR
            = new Parcelable.Creator<WeatherHeaderModel>() {
        public WeatherHeaderModel createFromParcel(Parcel in) {
            return new WeatherHeaderModel(in);
        }

        public WeatherHeaderModel[] newArray(int size) {
            return new WeatherHeaderModel[size];
        }
    };

    // "De-parcel object
    public WeatherHeaderModel(Parcel in) {
        city = in.readString();
        wind = in.readString();
        pressure = in.readString();
        humidity = in.readString();
        temp = in.readDouble();
        weatherCondition = in.readString();
    }

}
