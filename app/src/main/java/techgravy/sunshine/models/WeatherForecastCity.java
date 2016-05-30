package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 06/04/16.
 */
@RealmClass
public class WeatherForecastCity implements RealmModel, Serializable {

    private Coord coord;

    @SerializedName("id")
    private String mId;

    private String name;

    private String population;

    private String country;

    public WeatherForecastCity() {
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ClassPojo [coord = " + coord + ", mId = " + mId + ", name = " + name + ", population = " + population + ", country = " + country + "]";
    }


}
