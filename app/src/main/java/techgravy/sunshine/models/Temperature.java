package techgravy.sunshine.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import timber.log.Timber;

/**
 * Created by aditlal on 13/04/16.
 */
public class Temperature implements Rush, Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(min);
        dest.writeFloat(max);
        dest.writeFloat(morn);
        dest.writeFloat(day);
        dest.writeFloat(eve);
        dest.writeFloat(night);
    }

    // Creator
    public static final Parcelable.Creator<Temperature> CREATOR
            = new Parcelable.Creator<Temperature>() {
        public Temperature createFromParcel(Parcel in) {
            return new Temperature(in);
        }

        public Temperature[] newArray(int size) {
            return new Temperature[size];
        }
    };

    // "De-parcel object
    public Temperature(Parcel in) {
        min = in.readFloat();
        max = in.readFloat();
        morn = in.readFloat();
        day = in.readFloat();
        eve = in.readFloat();
        night = in.readFloat();
    }

}