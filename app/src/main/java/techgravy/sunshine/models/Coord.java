package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

/**
 * Created by aditlal on 13/04/16.
 */
public class Coord implements Rush{
    @SerializedName("lon")
    private double lon;

    @SerializedName("lat")
    private double lat;

    public double getLon ()
    {
        return lon;
    }

    public void setLon (double lon)
    {
        this.lon = lon;
    }

    public double getLat ()
    {
        return lat;
    }

    public void setLat (double lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [lon = "+lon+", lat = "+lat+"]";
    }

    @Override
    public void save() { RushCore.getInstance().save(this); }
    @Override
    public void save(RushCallback callback) { RushCore.getInstance().save(this, callback); }
    @Override
    public void delete() { RushCore.getInstance().delete(this); }
    @Override
    public void delete(RushCallback callback) { RushCore.getInstance().delete(this, callback); }
    @Override
    public String getId() { return RushCore.getInstance().getId(this); }
}
