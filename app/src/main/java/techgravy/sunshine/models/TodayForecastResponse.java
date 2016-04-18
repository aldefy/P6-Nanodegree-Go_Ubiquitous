package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.annotations.RushList;
import timber.log.Timber;

/**
 * Created by aditlal on 14/04/16.
 */
public class TodayForecastResponse implements Rush {
    private String message;

    private String cnt;

    private String cod;

    @RushList(classType = TodayForecastModel.class)
    @SerializedName("list")
    private List<TodayForecastModel> list;

    @SerializedName("city")
    private TodayForecastCity city;

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

    public List<TodayForecastModel> getList() {
        return list;
    }

    public void setList(List<TodayForecastModel> list) {
        this.list = list;
    }

    public TodayForecastCity getCity() {
        return city;
    }

    public void setCity(TodayForecastCity city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", cnt = " + cnt + ", cod = " + cod + ", list = " + list + ", city = " + city + "]";
    }

    @Override
    public void save() {
        Timber.tag("RushSave").d(toString());
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

