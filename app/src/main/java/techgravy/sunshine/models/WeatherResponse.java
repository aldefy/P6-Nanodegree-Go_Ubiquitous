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
public class WeatherResponse implements Rush {
    private String message;

    private String cnt;

    private String cod;

    @RushList(classType = WeatherForecastModel.class)
    @SerializedName("list")
    private List<WeatherForecastModel> list;

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

    public List<WeatherForecastModel> getList() {
        return list;
    }

    public void setList(List<WeatherForecastModel> list) {
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
