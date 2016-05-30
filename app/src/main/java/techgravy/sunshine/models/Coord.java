package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 13/04/16.
 */
@RealmClass
public class Coord implements RealmModel, Serializable {
    @SerializedName("lon")
    private float lon;

    @SerializedName("lat")
    private float lat;

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "ClassPojo [lon = " + lon + ", lat = " + lat + "]";
    }


}
