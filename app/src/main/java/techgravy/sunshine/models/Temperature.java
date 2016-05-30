package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 13/04/16.
 */
@RealmClass
public class Temperature implements RealmModel, Serializable {
    @SerializedName("min")
    private float min;
    @SerializedName("eve")
    private float eve;
    @SerializedName("max")
    private float max;
    @SerializedName("morn")
    private float morn;
    @SerializedName("night")
    private float night;
    @SerializedName("day")
    private float day;

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getEve() {
        return eve;
    }

    public void setEve(float eve) {
        this.eve = eve;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMorn() {
        return morn;
    }

    public void setMorn(float morn) {
        this.morn = morn;
    }

    public float getNight() {
        return night;
    }

    public void setNight(float night) {
        this.night = night;
    }

    public float getDay() {
        return day;
    }

    public void setDay(float day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "ClassPojo [min = " + min + ", eve = " + eve + ", max = " + max + ", morn = " + morn + ", night = " + night + ", day = " + day + "]";
    }

}