package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aditlal on 14/04/16.
 */
public class Rain {
    @SerializedName("3h")
    private float rain3h;

    public float getRain3h() {
        return rain3h;
    }

    public void setRain3h(float rain3h) {
        this.rain3h = rain3h;
    }

    @Override
    public String toString() {
        return "ClassPojo [3h = " + rain3h + "]";
    }
}
